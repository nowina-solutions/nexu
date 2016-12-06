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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lu.nowina.nexu.api.DetectedCard;
import lu.nowina.nexu.api.EnvironmentInfo;
import lu.nowina.nexu.api.OS;

/**
 * Detects smartcard.
 *
 */
@SuppressWarnings("restriction")
public class CardDetector {

	private static final Logger logger = LoggerFactory.getLogger(CardDetector.class.getSimpleName());

	private CardTerminals cardTerminals;
	
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
						//TODO : close cardTerminals
					} catch (Exception e) {
						logger.warn("Exception when closing cardTerminals", e);
					}
				}
			}
		});
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
			if((cause != null) && ("SCARD_E_SERVICE_STOPPED".equals(cause.getMessage())) && !cardTerminalsCreated) {
				logger.debug("Service stopped. Re-establish a new connection.");
				try {
					// TODO: close cardTerminals
				} catch(final Exception e1) {
					logger.warn("Exception when closing cardTerminals", e1);
				}
				this.cardTerminals = null;
				return getCardTerminals();
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
}
