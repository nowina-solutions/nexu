package lu.nowina.nexu.jetty;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinNT.TOKEN_INFORMATION_CLASS;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Implementation of {@link UserByPIDStrategy} using JNA and dedicated to
 * Win32 platforms.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
class Win32JNAUserByPIDStrategy implements UserByPIDStrategy {

	public Win32JNAUserByPIDStrategy() {
		super();
	}

	@Override
	public String getUserForPID(int pid) {
		try(final CloseableHandle processHandle =
				new CloseableHandle(Kernel32.INSTANCE.OpenProcess(WinNT.PROCESS_QUERY_INFORMATION, false, pid))) {
			if(processHandle.getHandle() == null) {
				if(Kernel32.INSTANCE.GetLastError() == WinError.ERROR_ACCESS_DENIED) {
					return "Process is not owned by current user";
				}
				throw new IllegalStateException("OpenProcess failed: " + Kernel32.INSTANCE.GetLastError());
			}
			try(final CloseableHandle tokenHandle = new CloseableHandle(openProcessToken(processHandle.getHandle()))) {
				final IntByReference tokenInformationRequiredSize = new IntByReference();
				// Do not test returned value here because it will be insufficient buffer size (error code 122)
				Advapi32.INSTANCE.GetTokenInformation(tokenHandle.getHandle(),
						TOKEN_INFORMATION_CLASS.TokenUser, null, 0, tokenInformationRequiredSize);
				final WinNT.TOKEN_USER tokenInformation = new WinNT.TOKEN_USER(tokenInformationRequiredSize.getValue());
				if(!Advapi32.INSTANCE.GetTokenInformation(tokenHandle.getHandle(),
						TOKEN_INFORMATION_CLASS.TokenUser, tokenInformation, tokenInformationRequiredSize.getValue(),
						tokenInformationRequiredSize)) {
					throw new IllegalStateException("GetTokenInformation failed: " + Kernel32.INSTANCE.GetLastError());
				}
				final char[] userName = new char[256];
				final IntByReference userNameSize = new IntByReference(Native.getNativeSize(userName.getClass(), userName));
				final char[] domainName = new char[256];
				final IntByReference domainNameSize = new IntByReference(Native.getNativeSize(domainName.getClass(), domainName));
				final PointerByReference typeOfAccount = new PointerByReference();
				if(!Advapi32.INSTANCE.LookupAccountSid(null, tokenInformation.User.Sid,
						userName, userNameSize, domainName, domainNameSize, typeOfAccount)) {
					throw new IllegalStateException("LookupAccountSid failed: " + Kernel32.INSTANCE.GetLastError());
				}
				return new String(userName, 0, userNameSize.getValue());
			}
		}
	}
	
	private HANDLE openProcessToken(final HANDLE processHandle) {
		final HANDLEByReference tokenHandle = new HANDLEByReference();
		if(!Advapi32.INSTANCE.OpenProcessToken(processHandle, WinNT.TOKEN_QUERY, tokenHandle)) {
			throw new IllegalStateException("OpenProcessToken failed: " + Kernel32.INSTANCE.GetLastError());
		}
		return tokenHandle.getValue();
	}
	
	private static class CloseableHandle implements AutoCloseable {
		private final HANDLE handle;

		public CloseableHandle(HANDLE handle) {
			this.handle = handle;
		}

		public HANDLE getHandle() {
			return handle;
		}

		@Override
		public void close() {
			if((handle != null) && !Kernel32.INSTANCE.CloseHandle(handle)) {
				throw new IllegalStateException("CloseHandle failed: " + Kernel32.INSTANCE.GetLastError());
			}
		}
	}
}
