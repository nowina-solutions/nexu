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
package lu.nowina.nexu.generic;

import java.io.File;
import java.util.List;

import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.MSCAPISignatureToken;
import eu.europa.esig.dss.token.PasswordInputCallback;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.token.mocca.MOCCASignatureTokenConnection;
import lu.nowina.nexu.api.AbstractCardProductAdapter;
import lu.nowina.nexu.api.CertificateFilter;
import lu.nowina.nexu.api.CertificateFilterHelper;
import lu.nowina.nexu.api.DetectedCard;
import lu.nowina.nexu.api.GetIdentityInfoResponse;
import lu.nowina.nexu.api.MessageDisplayCallback;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.ScAPI;

public class GenericCardAdapter extends AbstractCardProductAdapter {

    private final SCInfo info;

    public GenericCardAdapter(final SCInfo info) {
        super();
        this.info = info;
    }

    @Override
    protected boolean accept(final DetectedCard card) {
        return this.info.getAtr().equals(card.getAtr());
    }

    @Override
    protected String getLabel(final NexuAPI api, final DetectedCard card, final PasswordInputCallback callback) {
        return card.getLabel();
    }

    @Override
    protected String getLabel(final NexuAPI api, final DetectedCard card, final PasswordInputCallback callback, final MessageDisplayCallback messageCallback) {
        throw new IllegalStateException("This product adapter does not support message display callback.");
    }

    @Override
    protected boolean supportMessageDisplayCallback(final DetectedCard card) {
        return false;
    }

    @Override
    protected SignatureTokenConnection connect(final NexuAPI api, final DetectedCard card, final PasswordInputCallback callback) {
        final ConnectionInfo cInfo = this.info.getConnectionInfo(api.getEnvironmentInfo());
        final ScAPI scApi = cInfo.getSelectedApi();
        switch (scApi) {
            case MSCAPI:
                // Cannot intercept cancel and timeout for MSCAPI (too generic error).
                return new MSCAPISignatureToken();
            case PKCS_11:
                final String absolutePath = cInfo.getApiParam();
                return new Pkcs11SignatureTokenAdapter(new File(absolutePath), callback, card.getTerminalIndex());
            case MOCCA:
                return new MOCCASignatureTokenConnectionAdapter(new MOCCASignatureTokenConnection(callback), api, card);
            default:
                throw new RuntimeException("API not supported");
        }
    }

    @Override
    protected SignatureTokenConnection connect(final NexuAPI api, final DetectedCard card, final PasswordInputCallback callback,
            final MessageDisplayCallback messageCallback) {
        throw new IllegalStateException("This product adapter does not support message display callback.");
    }

    @Override
    protected boolean canReturnIdentityInfo(final DetectedCard card) {
        return false;
    }

    @Override
    public GetIdentityInfoResponse getIdentityInfo(final SignatureTokenConnection token) {
        throw new IllegalStateException("This card adapter cannot return identity information.");
    }

    @Override
    protected boolean supportCertificateFilter(final DetectedCard card) {
        return true;
    }

    @Override
    protected boolean canReturnSuportedDigestAlgorithms(final DetectedCard card) {
        return false;
    }

    @Override
    protected List<DigestAlgorithm> getSupportedDigestAlgorithms(final DetectedCard card) {
        throw new IllegalStateException("This card adapter cannot return list of supported digest algorithms.");
    }

    @Override
    protected DigestAlgorithm getPreferredDigestAlgorithm(final DetectedCard card) {
        throw new IllegalStateException("This card adapter cannot return list of supported digest algorithms.");
    }

    @Override
    public List<DSSPrivateKeyEntry> getKeys(final SignatureTokenConnection token, final CertificateFilter certificateFilter) {
        return new CertificateFilterHelper().filterKeys(token, certificateFilter);
    }
}
