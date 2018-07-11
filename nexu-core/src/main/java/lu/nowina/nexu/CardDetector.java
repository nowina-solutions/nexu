/**
 * © Nowina Solutions, 2015-2016
 *
 * Concédée sous licence EUPL, version 1.1 ou – dès leur approbation par la Commission européenne - versions ultérieures de l’EUPL (la «Licence»).
 * Vous ne pouvez utiliser la présente œuvre que conformément à la Licence.
 * Vous pouvez obtenir une copie de la Licence à l’adresse suivante:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Sauf obligation légale ou contractuelle écrite, le logiciel distribué sous la Licence est distribué «en l’état»,
 * SANS GARANTIES OU CONDITIONS QUELLES QU’ELLES SOIENT, expresses ou implicites.
 * Consultez la Licence pour les autorisations et les restrictions linguistiques spécifiques relevant de la Licence.
 */
package lu.nowina.nexu;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.IntegerType;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;

import lu.nowina.nexu.api.DetectedCard;
import lu.nowina.nexu.api.EnvironmentInfo;
import lu.nowina.nexu.api.OS;

/**
 * Detects smartcard.
 *
 */
@SuppressWarnings("restriction")
public class CardDetector {

	private static final List<String> RESET_CONTEXT_ERRORS = Arrays.asList(
			"SCARD_E_SERVICE_STOPPED", "WINDOWS_ERROR_INVALID_HANDLE", "SCARD_E_INVALID_HANDLE", "SCARD_E_NO_SERVICE");
	
	private static final Logger logger = LoggerFactory.getLogger(CardDetector.class.getSimpleName());

	private CardTerminals cardTerminals;
	
	private final WinscardLibrary lib;
	
	public CardDetector(final EnvironmentInfo info) {
		if (info.getOs() == OS.LINUX) {
			logger.info("The OS is Linux, we check for Library");
			try {
				final File libFile = at.gv.egiz.smcc.util.LinuxLibraryFinder.getLibraryPath("pcsclite", "1");
				if (libFile != null) {
					logger.info("Library installed is at " + libFile.getAbsolutePath());
					System.setProperty("sun.security.smartcardio.library", libFile.getAbsolutePath());
				}
			} catch (final Exception e) {
				logger.error("Error while loading library for Linux", e);
			}
		}
		
		this.cardTerminals = null;
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if(cardTerminals != null) {
					try {
						closeCardTerminals();
					} catch (Exception e) {
						logger.warn("Exception when closing cardTerminals", e);
					}
				}
			}
		});
		
		final String libraryName = Platform.isWindows() ? WINDOWS_PATH : Platform.isMac() ? MAC_PATH : PCSC_PATH;
		this.lib = (WinscardLibrary) Native.loadLibrary(libraryName, WinscardLibrary.class);
	}

	private List<CardTerminal> getCardTerminals() {
		final boolean cardTerminalsCreated;
		if(cardTerminals == null) {
			final TerminalFactory terminalFactory = TerminalFactory.getDefault();
			cardTerminals = terminalFactory.terminals();
			cardTerminalsCreated = true;
		} else {
			cardTerminalsCreated = false;
		}
		try {
			return cardTerminals.list();
		} catch(final CardException e) {
			final Throwable cause = e.getCause();
			if((cause != null) && RESET_CONTEXT_ERRORS.contains(cause.getMessage()) && !cardTerminalsCreated) {
				logger.debug("Error class: " + cause.getClass().getName() +
						". Message: " + cause.getMessage() +
						". Re-establish a new connection.");
				try {
					closeCardTerminals();
				} catch(final Exception e1) {
					logger.warn("Exception when closing cardTerminals", e1);
				}
				try {
					establishNewContext();
				} catch(final Exception e1) {
					throw new RuntimeException(e1);
				}
				this.cardTerminals = null;
				return getCardTerminals();
			} else if((cause != null) && "SCARD_E_NO_READERS_AVAILABLE".equals(cause.getMessage())) {
				return Collections.emptyList();
			} else {
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * Detect the smartcard connected to the computer.
	 *
	 * @return a list of smartcard detection.
	 */
	public List<DetectedCard> detectCard() {
		final List<DetectedCard> listCardDetect = new ArrayList<DetectedCard>();
		int terminalIndex = 0;
		for (CardTerminal cardTerminal : getCardTerminals()) {
			// cardTerminal.isCardPresent() always returns false on MacOS, so
			// catch the CardException instead
			try {
				final DetectedCard cardDetection = new DetectedCard();
				final Card card = cardTerminal.connect("*");
				final ATR atr = card.getATR();
				cardDetection.setAtr(DetectedCard.atrToString(atr.getBytes()));
				cardDetection.setTerminalIndex(terminalIndex);
				cardDetection.setTerminalLabel(cardTerminal.getName());
				listCardDetect.add(cardDetection);
				logger.info(MessageFormat.format("Found card in terminal {0} with ATR {1}.", terminalIndex, cardDetection.getAtr()));
			} catch (CardException e) {
				// Card not present or unreadable
				logger.warn(MessageFormat.format("No card present in terminal {0}, or not readable.", Integer.toString(terminalIndex)));
			}
			terminalIndex++;
		}
		return listCardDetect;
	}

	public CardTerminal getCardTerminal(final DetectedCard detectedCard) {
		for(final CardTerminal cardTerminal : getCardTerminals()) {
			Card card = null;
			try {
				card = cardTerminal.connect("*");
				final byte[] atr = card.getATR().getBytes();
				if(((detectedCard.getTerminalLabel() == null) || cardTerminal.getName().equals(detectedCard.getTerminalLabel())) &&
				   DetectedCard.atrToString(atr).equals(detectedCard.getAtr())) {
					return cardTerminal;
				}
			} catch(final CardException e) {
				// Log exception and continue
				logger.debug("CardException on connect", e);
			} finally {
				try {
					if(card != null) {
						card.disconnect(false);
					}
				} catch (CardException e) {
					logger.warn("CardException on disconnect.", e);
				}
			}
		}
		throw new IllegalArgumentException("Cannot find CardTerminal with label " +
				detectedCard.getTerminalLabel() + " and ATR " + detectedCard.getAtr());
	}
	
	private void closeCardTerminals() throws Exception {
		final Class<?> pcscTerminalsClass = Class.forName("sun.security.smartcardio.PCSCTerminals");
        final Field contextIdField = pcscTerminalsClass.getDeclaredField("contextId");
        contextIdField.setAccessible(true);
        final long contextId = contextIdField.getLong(null);
        
        if(contextId != 0L) {
        	// Release current context
        	final Dword result = lib.SCardReleaseContext(new SCardContext(contextId));
        	if(result.longValue() != 0L) {
        		logger.warn("Error when releasing context: " + result.longValue());
        	} else {
        		logger.debug("Context was released successfully.");
        	}
        	
        	// Remove current context value
        	contextIdField.setLong(null, 0L);
        	
        	// Clear terminals
            final Field terminalsField = pcscTerminalsClass.getDeclaredField("terminals");
            terminalsField.setAccessible(true);
        	final Map<?, ?> terminals = (Map<?, ?>) terminalsField.get(null);
        	terminals.clear();
        }
	}

	private void establishNewContext() throws Exception {
		final Class<?> pcscTerminalsClass = Class.forName("sun.security.smartcardio.PCSCTerminals");
    	final Method initContextMethod = pcscTerminalsClass.getDeclaredMethod("initContext");
    	initContextMethod.setAccessible(true);
    	initContextMethod.invoke(null);
	}
	
	/***********************************************************************************************************/
	/* All following are inspired by                                                                           */
	/* https://github.com/jnasmartcardio/jnasmartcardio/blob/master/src/main/java/jnasmartcardio/Winscard.java */
	/***********************************************************************************************************/

	private static final String WINDOWS_PATH = "WinSCard.dll";
	private static final String MAC_PATH = "/System/Library/Frameworks/PCSC.framework/PCSC";
	private static final String PCSC_PATH = "libpcsclite.so.1";

	/**
	 * The winscard API, also known as PC/SC. Implementations of this API exist
	 * on Windows, OS X, and Linux, although the symbol names and sizeof
	 * parameters differs on different platforms.
	 */
	private static interface WinscardLibrary extends Library {
		Dword SCardReleaseContext(SCardContext hContext);
	}

	// Following classes are public for {@link NativeMappedConverter#defaultValue()}.
	
	/**
	 * The DWORD type used by WinSCard.h, used wherever an integer is needed in
	 * SCard functions. On Windows and OS X, this is always typedef'd to a
	 * uint32_t. In the pcsclite library on Linux, it is a long
	 * instead, which is 64 bits on 64-bit Linux.
	 */
	public static class Dword extends IntegerType {
		public static final int SIZE = Platform.isWindows() || Platform.isMac() ? 4 : NativeLong.SIZE;
		
		private static final long serialVersionUID = 1L;
		
		public Dword() {
			this(0l);
		}
		
		public Dword(long value) {
			super(SIZE, value);
		}
		
		@Override
		public String toString() {
			return Long.toString(longValue());
		}
	}
	
	/**
	 * Base class for handles used in PC/SC. On Windows, it is a handle
	 * (ULONG_PTR which cannot be dereferenced). On PCSC, it is an integer
	 * (int32_t on OS X, long on Linux).
	 */
	public static class Handle extends IntegerType {
		private static final long serialVersionUID = 1L;
		
		public static final int SIZE = Platform.isWindows() ? Pointer.SIZE : Dword.SIZE;
		
		public Handle(long value) {
			super(SIZE, value);
		}
		
		@Override
		public String toString() {
			return String.format("%s{%x}", getClass().getSimpleName(), longValue());
		}
	}
	
	/**
	 * The SCARDCONTEXT type defined in WinSCard.h, used for most SCard
	 * functions.
	 */
	public static class SCardContext extends Handle {
		private static final long serialVersionUID = 1L;
		
		/** no-arg constructor needed for {@link NativeMappedConverter#defaultValue()}*/
		public SCardContext() {
			this(0l);
		}
		
		public SCardContext(long value) {
			super(value);
		}
	}
}
