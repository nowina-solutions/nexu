/**
 * © Nowina Solutions, 2015-2015
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

	public CardDetector(EnvironmentInfo info) {
		try {
			if (info.getOs() == OS.LINUX) {
				logger.info("The OS is Linux, we check for Library");
				File libFile = at.gv.egiz.smcc.util.LinuxLibraryFinder.getLibraryPath("pcsclite", "1");
				if (libFile != null) {
					logger.info("Library installed is " + libFile.getAbsolutePath());
					System.setProperty("sun.security.smartcardio.library", libFile.getAbsolutePath());
				}
			}
		} catch (Exception e) {
			logger.error("Error while loading library for Linux", e);
		}
	}

	/**
	 * Detect the smartcard connected to the computer.
	 *
	 * @return a list of smartcard detection.
	 */
	public List<DetectedCard> detectCard() {
		List<DetectedCard> listCardDetect = null;
		listCardDetect = new ArrayList<DetectedCard>();
		final TerminalFactory terminalFactory = TerminalFactory.getDefault();

		final List<CardTerminal> listCardTerminal;
		try {
			listCardTerminal = terminalFactory.terminals().list();
		} catch (Exception e) {
			// on MacOS and Linux there is an exception when there are no
			// terminals connected
			// but it is OK to continue with the intitalisation - user can
			// connect + refresh
			logger.debug("Error listing the terminals", e);
			logger.info("No terminals found.");
			return listCardDetect;
		}
		int terminalIndex = 0;
		for (CardTerminal cardTerminal : listCardTerminal) {
			// cardTerminal.isCardPresent() always returns false on MacOS, so
			// catch the CardException instead
			try {
				final DetectedCard cardDetection = new DetectedCard();
				final Card card = cardTerminal.connect("*");
				final ATR atr = card.getATR();
				cardDetection.setAtr(DetectedCard.atrToString(atr.getBytes()));
				cardDetection.setTerminalIndex(terminalIndex);
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

}
