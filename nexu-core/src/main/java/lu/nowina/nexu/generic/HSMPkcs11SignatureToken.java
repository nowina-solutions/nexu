package lu.nowina.nexu.generic;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStoreException;
import java.security.Provider;
import java.security.ProviderException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.token.AbstractSignatureTokenConnection;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.KSPrivateKeyEntry;
import eu.europa.esig.dss.token.PasswordInputCallback;

public class HSMPkcs11SignatureToken extends AbstractSignatureTokenConnection {

	private Provider _pkcs11Provider;
	private final String _pkcs11Path;
	private KeyStore _keyStore;
	private PasswordInputCallback callback;
	private int slotIndex;

	private static int hsmNameIndex = 0;

	public HSMPkcs11SignatureToken(String pkcs11AbsolutePath, PasswordInputCallback passwordInputCallback,
			int slotIndex) {
		this._pkcs11Path = pkcs11AbsolutePath;
		this.callback = passwordInputCallback;
		this.slotIndex = slotIndex;
	}

	private Provider getProvider() {
		try {
			if (_pkcs11Provider == null) {
				// check if the provider already exists
				final Provider[] providers = Security.getProviders();
				if (providers != null) {
					for (final Provider provider : providers) {
						final String providerInfo = provider.getInfo();
						if (providerInfo.contains(getPkcs11Path())) {
							_pkcs11Provider = provider;
							return provider;
						}
					}
				}
				// provider not already installed

				installProvider();
			}
			return _pkcs11Provider;
		} catch (ProviderException | UnsupportedEncodingException ex) {
			throw new DSSException("Not a PKCS#11 library", ex);
		}
	}

	@SuppressWarnings("restriction")
	private void installProvider() throws UnsupportedEncodingException {

		/*
		 * The smartCardNameIndex int is added at the end of the smartCard name in order
		 * to enable the successive loading of multiple pkcs11 libraries
		 */
		String aPKCS11LibraryFileName = getPkcs11Path();

		// Remove unnecessary symbols from url
		aPKCS11LibraryFileName = URLDecoder.decode(aPKCS11LibraryFileName, "UTF-8");
		aPKCS11LibraryFileName = aPKCS11LibraryFileName.replace("file:/", "");

		// Escape slashes
		aPKCS11LibraryFileName = escapePath(aPKCS11LibraryFileName);

		String pkcs11ConfigSettings = "name = HSM" + hsmNameIndex + "\n" + "library = \"" + aPKCS11LibraryFileName
				+ "\"\nslotListIndex = " + slotIndex;

		byte[] pkcs11ConfigBytes = pkcs11ConfigSettings.getBytes();
		ByteArrayInputStream confStream = new ByteArrayInputStream(pkcs11ConfigBytes);

		sun.security.pkcs11.SunPKCS11 pkcs11 = new sun.security.pkcs11.SunPKCS11(confStream);
		_pkcs11Provider = pkcs11;

		Security.addProvider(_pkcs11Provider);
		hsmNameIndex++;
	}

	private String escapePath(String pathToEscape) {
		if (pathToEscape != null) {
			return pathToEscape.replace("\\", "\\\\");
		} else {
			return "";
		}
	}

	@SuppressWarnings("restriction")
	private KeyStore getKeyStore() throws KeyStoreException {

		if (_keyStore == null) {
			_keyStore = KeyStore.getInstance("PKCS11", getProvider());
			try {
				_keyStore.load(new KeyStore.LoadStoreParameter() {

					@Override
					public ProtectionParameter getProtectionParameter() {
						return new KeyStore.CallbackHandlerProtection(new CallbackHandler() {

							@Override
							public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
								for (Callback c : callbacks) {
									if (c instanceof PasswordCallback) {
										((PasswordCallback) c).setPassword(callback.getPassword());
										return;
									}
								}
								throw new RuntimeException("No password callback");
							}
						});
					}
				});
			} catch (Exception e) {
				if (e instanceof sun.security.pkcs11.wrapper.PKCS11Exception) {
					if ("CKR_PIN_INCORRECT".equals(e.getMessage())) {
						throw new DSSException("Bad password for PKCS11", e);
					}
				}
				throw new KeyStoreException("Can't initialize Sun PKCS#11 security provider. Reason: " + e.getMessage(),
						e);
			}
		}
		return _keyStore;
	}

	protected String getPkcs11Path() {
		return _pkcs11Path;
	}

	@Override
	public void close() {
		if (_pkcs11Provider != null) {
			try {
				Security.removeProvider(_pkcs11Provider.getName());
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
			}
		}
		this._pkcs11Provider = null;
		this._keyStore = null;
	}

	@Override
	public List<DSSPrivateKeyEntry> getKeys() throws DSSException {

		final List<DSSPrivateKeyEntry> list = new ArrayList<DSSPrivateKeyEntry>();

		try {
			final KeyStore keyStore = getKeyStore();

			final Enumeration<String> aliases = keyStore.aliases();
			System.out.println(aliases.hasMoreElements());

			while (aliases.hasMoreElements()) {
				final String alias = aliases.nextElement();
				if (keyStore.isKeyEntry(alias)) {
					final PrivateKeyEntry entry = (PrivateKeyEntry) keyStore.getEntry(alias, null);
					list.add(new KSPrivateKeyEntry(alias, entry));
				}
				System.out.println(alias);
			}

		} catch (Exception e) {
			throw new DSSException("Can't initialize Sun PKCS#11 security " + "provider. Reason: " + e.getMessage(), e);
		}
		return list;
	}
}
