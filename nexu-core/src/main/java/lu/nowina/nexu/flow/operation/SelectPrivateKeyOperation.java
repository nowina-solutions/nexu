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

import java.util.Iterator;
import java.util.List;

import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import lu.nowina.nexu.CancelledOperationException;
import lu.nowina.nexu.api.CertificateFilter;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.Product;
import lu.nowina.nexu.api.ProductAdapter;
import lu.nowina.nexu.api.flow.BasicOperationStatus;
import lu.nowina.nexu.api.flow.OperationResult;
import lu.nowina.nexu.view.core.UIOperation;

/**
 * This {@link CompositeOperation} allows to retrieve a private key for a given {@link SignatureTokenConnection}
 * and optional <code>certificate filter</code> and/or <code>key filter</code>.
 *
 * Expected parameters:
 * <ol>
 * <li>{@link SignatureTokenConnection}</li>
 * <li>{@link NexuAPI}</li>
 * <li>{@link Product} (optional)</li>
 * <li>{@link ProductAdapter} (optional)</li>
 * <li>{@link CertificateFilter} (optional)</li>
 * <li>Key filter (optional): {@link String}</li>
 * </ol>
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class SelectPrivateKeyOperation extends AbstractCompositeOperation<DSSPrivateKeyEntry> {

    private SignatureTokenConnection token;
    private NexuAPI api;
    private Product product;
    private ProductAdapter productAdapter;
    private CertificateFilter certificateFilter;
    private String keyFilter;

    public SelectPrivateKeyOperation() {
        super();
    }

    @Override
    public void setParams(final Object... params) {
        try {
            this.token = (SignatureTokenConnection) params[0];
            this.api = (NexuAPI) params[1];
            if(params.length > 2) {
                this.product = (Product) params[2];
            }
            if(params.length > 3) {
                this.productAdapter = (ProductAdapter) params[3];
            }
            if(params.length > 4) {
                this.certificateFilter = (CertificateFilter) params[4];
            }
            if(params.length > 5) {
                this.keyFilter = (String) params[5];
            }
        } catch(final ClassCastException | ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Expected parameters: SignatureTokenConnection, NexuAPI, Product (optional), ProductAdapter (optional), CertificateFilter (optional), key filter (optional)");
        }
    }

    @Override
    public OperationResult<DSSPrivateKeyEntry> perform() {
        final List<DSSPrivateKeyEntry> keys;

        try {
            if((this.productAdapter != null) && (this.product != null) && this.productAdapter.supportCertificateFilter(this.product) && (this.certificateFilter != null)) {
                keys = this.productAdapter.getKeys(this.token, this.certificateFilter);
            } else {
                keys = this.token.getKeys();
            }
        } catch(final CancelledOperationException e) {
            return new OperationResult<DSSPrivateKeyEntry>(BasicOperationStatus.USER_CANCEL);
        }

        DSSPrivateKeyEntry key = null;

        final Iterator<DSSPrivateKeyEntry> it = keys.iterator();
        while (it.hasNext()) {
            final DSSPrivateKeyEntry e = it.next();
            if ("CN=Token Signing Public Key".equals(e.getCertificate().getCertificate().getIssuerDN().getName())) {
                it.remove();
            }
        }

        if (keys.isEmpty()) {
            return new OperationResult<DSSPrivateKeyEntry>(CoreOperationStatus.NO_KEY);
        } else if (keys.size() == 1) {
            key = keys.get(0);
            if((this.keyFilter != null) && !key.getCertificate().getDSSIdAsString().equals(this.keyFilter)) {
                return new OperationResult<DSSPrivateKeyEntry>(CoreOperationStatus.CANNOT_SELECT_KEY);
            } else {
                return new OperationResult<DSSPrivateKeyEntry>(key);
            }
        } else {
            if (this.keyFilter != null) {
                for (final DSSPrivateKeyEntry k : keys) {
                    if (k.getCertificate().getDSSIdAsString().equals(this.keyFilter)) {
                        key = k;
                        break;
                    }
                }
                if(key == null) {
                    return new OperationResult<DSSPrivateKeyEntry>(CoreOperationStatus.CANNOT_SELECT_KEY);
                }
            } else if(this.api.getAppConfig().isEnablePopUps()) {
                @SuppressWarnings("unchecked")
                final OperationResult<DSSPrivateKeyEntry> op =
                this.operationFactory.getOperation(UIOperation.class, "/fxml/key-selection.fxml", new Object[]{keys, this.api.getAppConfig().getApplicationName(), this.api.getAppConfig().isDisplayBackButton()}).perform();
                if(op.getStatus().equals(CoreOperationStatus.BACK)) {
                    return new OperationResult<DSSPrivateKeyEntry>(CoreOperationStatus.BACK);
                }
                if(op.getStatus().equals(BasicOperationStatus.USER_CANCEL)) {
                    return new OperationResult<DSSPrivateKeyEntry>(BasicOperationStatus.USER_CANCEL);
                }
                key = op.getResult();
                if(key == null) {
                    return new OperationResult<DSSPrivateKeyEntry>(CoreOperationStatus.NO_KEY_SELECTED);
                }
            } else {
                return new OperationResult<DSSPrivateKeyEntry>(CoreOperationStatus.CANNOT_SELECT_KEY);
            }
            return new OperationResult<DSSPrivateKeyEntry>(key);
        }
    }
}
