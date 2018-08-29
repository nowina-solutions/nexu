package lu.nowina.nexu.generic;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.AuthProvider;
import java.security.Provider;
import java.security.Security;
import java.util.List;
import java.util.UUID;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.MaskGenerationFunction;
import eu.europa.esig.dss.SignatureValue;
import eu.europa.esig.dss.ToBeSigned;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.PasswordInputCallback;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;
import lu.nowina.nexu.CancelledOperationException;

/**
 * This adapter class allows to manage {@link CancelledOperationException}.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class Pkcs11SignatureTokenAdapter extends Pkcs11SignatureToken {

    private static final Logger logger = LoggerFactory.getLogger(Pkcs11SignatureTokenAdapter.class.getName());

    private Provider provider;

    private final int slotListIndex;

    public Pkcs11SignatureTokenAdapter(final File pkcs11lib, final PasswordInputCallback callback, final int terminalIndex) {
        super(pkcs11lib.getAbsolutePath(), callback, terminalIndex);
        this.slotListIndex = terminalIndex;
        logger.info("Lib " + pkcs11lib.getAbsolutePath());
    }

    @Override
    public void close() {
        if (this.provider != null) {
            try {
                if (this.provider instanceof AuthProvider) {
                    ((AuthProvider) this.provider).logout();
                }
            } catch (final LoginException e) {
                LOG.error("LoginException on logout of '" + this.provider.getName() + "'", e);
            }
            this.provider.clear();
            try {
                Security.removeProvider(this.provider.getName());
            } catch (final SecurityException e) {
                LOG.error("Unable to remove provider '" + this.provider.getName() + "'", e);
            } finally {
                this.provider = null;
            }
        }
    }

    @Override
    @SuppressWarnings("restriction")
    protected Provider getProvider() {
        if (this.provider == null) {
            /*
             * The smartCardNameIndex int is added at the end of the smartCard name in order to enable the successive
             * loading of multiple pkcs11 libraries
             */
            String aPKCS11LibraryFileName = this.getPkcs11Path();
            aPKCS11LibraryFileName = this.escapePath(aPKCS11LibraryFileName);

            final StringBuilder pkcs11Config = new StringBuilder();
            pkcs11Config.append("name = SmartCard").append(UUID.randomUUID().toString()).append("\n");
            pkcs11Config.append("library = \"").append(aPKCS11LibraryFileName).append("\"").append("\n");
            pkcs11Config.append("slotListIndex = ").append(this.getSlotListIndex());

            final String configString = pkcs11Config.toString();

            LOG.debug("PKCS11 Config : \n{}", configString);

            try (ByteArrayInputStream confStream = new ByteArrayInputStream(configString.getBytes("ISO-8859-1"))) {
                final sun.security.pkcs11.SunPKCS11 sunPKCS11 = new sun.security.pkcs11.SunPKCS11(confStream);
                // we need to add the provider to be able to sign later
                Security.addProvider(sunPKCS11);
                this.provider = sunPKCS11;
                return this.provider;
            } catch (final Exception e) {
                throw new DSSException("Unable to instantiate SunPKCS11", e);
            }
        }
        return this.provider;
    }

    protected String escapePath(final String pathToEscape) {
        if (pathToEscape != null) {
            return pathToEscape.replace("\\", "\\\\");
        } else {
            return "";
        }
    }

    protected int getSlotListIndex() {
        return this.slotListIndex;
    }

    @Override
    public List<DSSPrivateKeyEntry> getKeys() throws DSSException {
        try {
            return super.getKeys();
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

    @Override
    public SignatureValue sign(final ToBeSigned toBeSigned, final DigestAlgorithm digestAlgorithm, final MaskGenerationFunction mgf,
            final DSSPrivateKeyEntry keyEntry) throws DSSException {

        try {
            return super.sign(toBeSigned, digestAlgorithm, mgf, keyEntry);
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