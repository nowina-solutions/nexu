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
package lu.nowina.nexu.flow.operation;

import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.SignatureValue;
import eu.europa.esig.dss.ToBeSigned;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import lu.nowina.nexu.CancelledOperationException;
import lu.nowina.nexu.api.flow.BasicOperationStatus;
import lu.nowina.nexu.api.flow.Operation;
import lu.nowina.nexu.api.flow.OperationResult;

/**
 * This {@link Operation} allows to perform a signature.
 * 
 * Expected parameters:
 * <ol>
 * <li>{@link SignatureTokenConnection}</li>
 * <li>{@link ToBeSigned}</li>
 * <li>{@link DigestAlgorithm}</li>
 * <li>{@link DSSPrivateKeyEntry}</li>
 * </ol>
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class SignOperation implements Operation<SignatureValue> {

	 private SignatureTokenConnection token;
	 private ToBeSigned toBeSigned;
	 private DigestAlgorithm digestAlgorithm;
	 private DSSPrivateKeyEntry key;

	public SignOperation() {
		super();
	}

	@Override
	public void setParams(Object... params) {
		try {
			this.token = (SignatureTokenConnection) params[0];
			this.toBeSigned = (ToBeSigned) params[1];
			this.digestAlgorithm = (DigestAlgorithm) params[2];
			this.key = (DSSPrivateKeyEntry) params[3];
		} catch(final ArrayIndexOutOfBoundsException | ClassCastException e) {
			throw new IllegalArgumentException("Expected parameters: SignatureTokenConnection, ToBeSigned, DigestAlgorithm, DSSPrivateKeyEntry");
		}
	}

	@Override
	public OperationResult<SignatureValue> perform() {
		try {
			return new OperationResult<SignatureValue>(token.sign(toBeSigned, digestAlgorithm, key));
		} catch(final CancelledOperationException e) {
			return new OperationResult<SignatureValue>(BasicOperationStatus.USER_CANCEL);
		}
	}
}
