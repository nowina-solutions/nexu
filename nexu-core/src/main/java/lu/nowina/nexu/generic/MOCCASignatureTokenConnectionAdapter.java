package lu.nowina.nexu.generic;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
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
import eu.europa.esig.dss.SignatureValue;
import eu.europa.esig.dss.ToBeSigned;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.token.mocca.MOCCASignatureTokenConnection;
import lu.nowina.nexu.CancelledOperationException;
import lu.nowina.nexu.api.DetectedCard;
import lu.nowina.nexu.api.NexuAPI;

/**
 * This adapter class allows to manage {@link CancelledOperationException}.
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

	public SignatureValue sign(ToBeSigned toBeSigned, DigestAlgorithm digestAlgorithm, DSSPrivateKeyEntry keyEntry) throws DSSException {
		try {
			setSignatureCard();
			return adapted.sign(toBeSigned, digestAlgorithm, keyEntry);
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