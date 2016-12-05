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

import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jnasmartcardio.Smartcardio;
import jnasmartcardio.Smartcardio.JnaCardTerminals;
import jnasmartcardio.Smartcardio.JnaPCSCException;
import lu.nowina.nexu.api.DetectedCard;

/**
 * Detects smartcard.
 *
 */
@SuppressWarnings("restriction")
public class CardDetector {

	private static final Logger logger = LoggerFactory.getLogger(CardDetector.class.getSimpleName());

	static {
		Security.insertProviderAt(new Smartcardio(), 1);
	}
	
	private JnaCardTerminals cardTerminals;
	
	public CardDetector() {
		this.cardTerminals = null;
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if(cardTerminals != null) {
					try {
						cardTerminals.close();
					} catch (JnaPCSCException e) {
						logger.warn("Exception when closing JnaCardTerminals", e);
					}
				}
			}
		});
	}

	private List<CardTerminal> getCardTerminals() {
		if(cardTerminals == null) {
			final TerminalFactory terminalFactory;
			try {
				terminalFactory = TerminalFactory.getInstance("PC/SC", null);
			} catch(final NoSuchAlgorithmException e) {
				// Should never happen as we give the provider
				throw new IllegalStateException(e);
			}
			cardTerminals = (JnaCardTerminals) terminalFactory.terminals();
		}
		try {
			return cardTerminals.list();
		} catch(final CardException e) {
			final Throwable cause = e.getCause();
			if((cause != null) && ("SCARD_E_SERVICE_STOPPED".equals(cause.getMessage()))) {
				logger.debug("Service stopped. Re-establish a new connection.");
				try {
					this.cardTerminals.close();
				} catch(final Exception e1) {
					logger.warn("Exception when closing JnaCardTerminals", e1);
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
				if(cardTerminal.getName().equals(detectedCard.getTerminalLabel()) &&
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
