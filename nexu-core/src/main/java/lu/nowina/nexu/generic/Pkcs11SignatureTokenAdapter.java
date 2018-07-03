package lu.nowina.nexu.generic;

import java.util.List;

import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.MaskGenerationFunction;
import eu.europa.esig.dss.SignatureValue;
import eu.europa.esig.dss.ToBeSigned;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import lu.nowina.nexu.CancelledOperationException;

/**
 * This adapter class allows to manage {@link CancelledOperationException}.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class Pkcs11SignatureTokenAdapter implements SignatureTokenConnection {

	private SignatureTokenConnection adapted;

	public Pkcs11SignatureTokenAdapter(SignatureTokenConnection adapted) {
		super();
		this.adapted = adapted;
	}

	public void close() {
		adapted.close();
	}

	public List<DSSPrivateKeyEntry> getKeys() throws DSSException {
		try {
			return adapted.getKeys();
		} catch (final Exception e) {
			Throwable t = e;
			while (t != null) {
				if ("CKR_CANCEL".equals(t.getMessage()) || "CKR_FUNCTION_CANCELED".equals(t.getMessage())) {
					throw new CancelledOperationException(e);
				} else if (t instanceof CancelledOperationException) {
					throw (CancelledOperationException) t;
				}
				t = t.getCause();
			}
			// Rethrow exception as is.
			throw e;
		}
	}

	@Deprecated
	public SignatureValue sign(ToBeSigned toBeSigned, DigestAlgorithm digestAlgorithm, DSSPrivateKeyEntry keyEntry)
			throws DSSException {
		return sign(toBeSigned, digestAlgorithm, null, keyEntry);
	}

	@Override
	public SignatureValue sign(ToBeSigned toBeSigned, DigestAlgorithm digestAlgorithm, MaskGenerationFunction mgf,
			DSSPrivateKeyEntry keyEntry) throws DSSException {

		try {
			return adapted.sign(toBeSigned, digestAlgorithm, mgf, keyEntry);
		} catch (final Exception e) {
			Throwable t = e;
			while (t != null) {
				if ("CKR_CANCEL".equals(t.getMessage()) || "CKR_FUNCTION_CANCELED".equals(t.getMessage())) {
					throw new CancelledOperationException(e);
				} else if (t instanceof CancelledOperationException) {
					throw (CancelledOperationException) t;
				}
				t = t.getCause();
			}
			// Rethrow exception as is.
			throw e;
		}
	}

}