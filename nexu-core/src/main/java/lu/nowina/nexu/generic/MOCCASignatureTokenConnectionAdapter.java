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
package lu.nowina.nexu.generic;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.gv.egiz.smcc.CancelledException;
import at.gv.egiz.smcc.CardNotSupportedException;
import at.gv.egiz.smcc.SignatureCard;
import at.gv.egiz.smcc.SignatureCardFactory;
import at.gv.egiz.smcc.TimeoutException;
import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.MaskGenerationFunction;
import eu.europa.esig.dss.SignatureValue;
import eu.europa.esig.dss.ToBeSigned;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.token.mocca.MOCCASignatureTokenConnection;
import lu.nowina.nexu.CancelledOperationException;
import lu.nowina.nexu.api.DetectedCard;
import lu.nowina.nexu.api.NexuAPI;

/**
 * This adapter class allows to manage {@link CancelledOperationException} and {@link SignatureCard}.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
@SuppressWarnings("restriction")
public class MOCCASignatureTokenConnectionAdapter implements SignatureTokenConnection {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MOCCASignatureTokenConnectionAdapter.class.getSimpleName());
	
	private final MOCCASignatureTokenConnection adapted;
	private final NexuAPI api;
	private final DetectedCard card;

	public MOCCASignatureTokenConnectionAdapter(final MOCCASignatureTokenConnection adapted,
			final NexuAPI api, final DetectedCard card) {
		super();
		this.adapted = adapted;
		this.api = api;
		this.card = card;
	}

	public void close() {
		adapted.close();
	}

	private List<SignatureCard> getSignatureCard() {
		final SignatureCardFactory factory = SignatureCardFactory.getInstance();
		final CardTerminal cardTerminal = api.getCardTerminal(card);
		Card card;
		try {
			card = cardTerminal.connect("*");
		} catch (CardException e) {
			// Same way to manage exception than in at.gv.egiz.smcc.util.SmartCardIO.
			card = null;
			LOGGER.debug("Failed to connect to card.", e);
		}
		try {
			final ArrayList<SignatureCard> result = new ArrayList<>(1);
			result.add(factory.createSignatureCard(card, cardTerminal));
			return result;
		} catch (CardNotSupportedException e) {
			// Here we must support the card or throw an exception (we have just
			// one card).
			throw new IllegalArgumentException(e);
		}
	}
	
	private void setSignatureCard() {
		final Field field;
		try {
			field = MOCCASignatureTokenConnection.class.getDeclaredField("_signatureCards");
		} catch (final NoSuchFieldException | SecurityException e) {
			// Should never hapenn
			throw new IllegalStateException(e);
		}
		field.setAccessible(true);
		try {
			field.set(adapted, getSignatureCard());
		} catch (final IllegalAccessException e) {
			// Should never hapenn
			throw new IllegalStateException(e);
		}
	}
	
	public List<DSSPrivateKeyEntry> getKeys() throws DSSException {
		try {
			setSignatureCard();
			return adapted.getKeys();
		} catch(final Exception e) {
			Throwable t = e;
			while(t != null) {
				if((t instanceof CancelledException) ||
						(t instanceof TimeoutException)) {
					throw new CancelledOperationException(e);
				} else if(t instanceof CancelledOperationException) {
					throw (CancelledOperationException) t;
				}
				t = t.getCause();
			}
			// Rethrow exception as is.
			throw e;
		}
	}

	@Deprecated
	public SignatureValue sign(ToBeSigned toBeSigned, DigestAlgorithm digestAlgorithm, DSSPrivateKeyEntry keyEntry) throws DSSException {
		return sign(toBeSigned, digestAlgorithm,null, keyEntry);
	}

	@Override
	public SignatureValue sign(ToBeSigned toBeSigned, DigestAlgorithm digestAlgorithm, MaskGenerationFunction mgf,
			DSSPrivateKeyEntry keyEntry) throws DSSException {
		try {
			setSignatureCard();
			return adapted.sign(toBeSigned, digestAlgorithm, mgf,keyEntry);
		} catch(final Exception e) {
			Throwable t = e;
			while(t != null) {
				if((t instanceof CancelledException) ||
						(t instanceof TimeoutException)) {
					throw new CancelledOperationException(e);
				} else if(t instanceof CancelledOperationException) {
					throw (CancelledOperationException) t;
				}
				t = t.getCause();
			}
			// Rethrow exception as is.
			throw e;
		}
	}
}