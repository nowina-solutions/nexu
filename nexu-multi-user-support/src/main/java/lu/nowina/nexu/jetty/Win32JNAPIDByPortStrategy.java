package lu.nowina.nexu.jetty;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Structure;

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.platform.win32.WinDef.DWORD;

/**
 * Implementation of {@link PIDByPortStrategy} using JNA and dedicated to
 * Win32 platforms.
 *
 * @author Martin Schleyer (schleyer@oszimt.de)
 */
class Win32JNAPIDByPortStrategy implements PIDByPortStrategy {

	public Win32JNAPIDByPortStrategy() {
		super();
	}

	// API interface required up to JNA 5.2.0
	public static interface IPHlpAPI extends StdCallLibrary {
		int GetExtendedTcpTable(MIB_TCPTABLE_OWNER_PID pTcpTable, IntByReference pdwSize, boolean bOrder, long ulAf,
				int table, long reserved);
	}

	// Structs and statics
	public static interface Socket {
		long AF_INET = 2; // Internet IP Protocol.
		long AF_INET6 = 23; // IP version 6.
	}

	public static interface MIB_TCP_STATE {
		int MIB_TCP_STATE_CLOSED = 1;
		int MIB_TCP_STATE_LISTEN = 2;
		int MIB_TCP_STATE_SYN_SENT = 3;
		int MIB_TCP_STATE_SYN_RCVD = 4;
		int MIB_TCP_STATE_ESTAB = 5;
		int MIB_TCP_STATE_FIN_WAIT1 = 6;
		int MIB_TCP_STATE_FIN_WAIT2 = 7;
		int MIB_TCP_STATE_CLOSE_WAIT = 8;
		int MIB_TCP_STATE_CLOSING = 9;
		int MIB_TCP_STATE_LAST_ACK = 10;
		int MIB_TCP_STATE_TIME_WAIT = 11;
		int MIB_TCP_STATE_DELETE_TCB = 12;
	}

	public static class MIB_TCPROW_OWNER_PID extends Structure {
		public DWORD dwState;
		public DWORD dwLocalAddr;
		public DWORD dwLocalPort;
		public DWORD dwRemoteAddr;
		public DWORD dwRemotePort;
		public DWORD dwOwningPid;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList(new String[] { "dwState", "dwLocalAddr", "dwLocalPort", "dwRemoteAddr", "dwRemotePort",
					"dwOwningPid" });
		}

	}

	public static class MIB_TCPTABLE_OWNER_PID extends Structure {
		public MIB_TCPTABLE_OWNER_PID(final Memory ptr) {
            super(ptr);
        }

        public DWORD dwNumEntries;
        public MIB_TCPROW_OWNER_PID[] table = new MIB_TCPROW_OWNER_PID[1];

        public MIB_TCPTABLE_OWNER_PID() {
        }

        public MIB_TCPTABLE_OWNER_PID(final int size) {
            this.dwNumEntries = new DWORD(size);
            this.table = (MIB_TCPROW_OWNER_PID[]) new MIB_TCPROW_OWNER_PID().toArray(size);
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "dwNumEntries", "table" });
        }

    }

    private static int ERROR_INSUFFICIENT_BUFFER = 122;

    @Override
    public int getPIDForPort(final int port) {
        final int pid = -1;

        try {
            // Load JNA
            final IPHlpAPI IPHLPAPI = (IPHlpAPI) Native.loadLibrary("iphlpapi", IPHlpAPI.class);

            // Prepare data structure
            MIB_TCPTABLE_OWNER_PID table = new MIB_TCPTABLE_OWNER_PID();
            final IntByReference psize = new IntByReference(table.size());

            // Get IPv4 connections
            int status = IPHLPAPI.GetExtendedTcpTable(table, psize, false, Socket.AF_INET, 5, 0);

            if (status == ERROR_INSUFFICIENT_BUFFER) {
                table = new MIB_TCPTABLE_OWNER_PID((psize.getValue() - 4) / table.table[0].size());
                psize.setValue(table.size());

                // get table with correct size
                status = IPHLPAPI.GetExtendedTcpTable(table, psize, false, Socket.AF_INET, 5, 0);

                // Iterate through table
                for (final MIB_TCPROW_OWNER_PID e : table.table) {
                    // Check if state is MIB_TCP_STATE_ESTAB
                    if (e.dwState.intValue() == MIB_TCP_STATE.MIB_TCP_STATE_ESTAB)
                        // Check if local port is given port, i.e. connection established from
                        // this port
                        if (port == getPort(e.dwLocalPort)) {
                            // PID found
                            return e.dwOwningPid.intValue();
                        }
                }
            }
        } catch (final Exception e) {
            System.err.println("Port lookup failed!");
        }
        return pid;
    }

    // Change network byte order of DWORD to proper int value
    private int getPort(final DWORD d) {
        final int b1 = (d.intValue() & 0x0000FF00) >> 8;
        final int b2 = d.intValue() & 0x000000FF;
		return (b2 << 8) + b1;
	}

}
