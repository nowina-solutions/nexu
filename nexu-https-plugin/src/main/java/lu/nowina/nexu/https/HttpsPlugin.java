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
package lu.nowina.nexu.https;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lu.nowina.nexu.NexuException;
import lu.nowina.nexu.api.EnvironmentInfo;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.plugin.InitializationMessage;
import lu.nowina.nexu.api.plugin.InitializationMessage.MessageType;
import lu.nowina.nexu.api.plugin.NexuPlugin;
import net.lingala.zip4j.core.ZipFile;

/**
 * NexU plugin that will perform all initialization tasks required by NexU to perform HTTPS.
 * 
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class HttpsPlugin implements NexuPlugin {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpsPlugin.class.getName());
	
	public HttpsPlugin() {
		super();
	}

	@Override
	public List<InitializationMessage> init(String pluginId, NexuAPI api) {
		final ResourceBundle resourceBundle = ResourceBundle.getBundle("bundles/https");
		final ResourceBundle baseResourceBundle = ResourceBundle.getBundle("bundles/nexu");
		
		LOGGER.info("Verify if keystore is ready");
		File nexuHome = api.getAppConfig().getNexuHome();
		File keyStoreFile = new File(nexuHome, "keystore.jks");
		final File caCert;
		if (!keyStoreFile.exists()) {
			caCert = createKeystore(nexuHome, api.getAppConfig().getApplicationName());
		} else {
			final File testCaCert = getKeystore(nexuHome, api.getAppConfig().getApplicationName());
			try {
				if(!hasSubjectAltNames(testCaCert)) {
					// Re-create key store if certificate does not have a subject alt name (NOW-122).
					caCert = createKeystore(nexuHome, api.getAppConfig().getApplicationName());
				} else {
					caCert = testCaCert;
				}
			} catch(final IOException | CertificateException e) {
				LOGGER.warn("Exception when trying to determine if certificate has subject alternative names", e);
				return Arrays.asList(new InitializationMessage(
						MessageType.CONFIRMATION,
						resourceBundle.getString("warn.install.cert.title"),
						MessageFormat.format(resourceBundle.getString("warn.install.cert.header"), api.getAppConfig().getApplicationName(), "subjectAltNames"),
						baseResourceBundle.getString("provide.feedback"),
						true,
						e
					)
				);
			}
		}
		return installCaCert(api, caCert, resourceBundle, baseResourceBundle);
	}

	private boolean hasSubjectAltNames(final File caCert) throws IOException, CertificateException {
		try (
				final FileInputStream fis = new FileInputStream(caCert);
				final BufferedInputStream bis = new BufferedInputStream(fis);
				) {
			final CertificateFactory cf = CertificateFactory.getInstance("X.509");
			final X509Certificate cert = (X509Certificate) cf.generateCertificate(bis);
			final Collection<List<?>> subjectAltNames = cert.getSubjectAlternativeNames();
			if((subjectAltNames != null) && !subjectAltNames.isEmpty()) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * Create a keystore in the directory given in parameters
	 * 
	 * @param nexuHome
	 * 
	 * @return A file containing the CA cert of the keystore
	 */
	File createKeystore(final File nexuHome, final String applicationName) {

		try {
			File keyStoreFile = new File(nexuHome, "keystore.jks");
			LOGGER.info("Creating keystore " + keyStoreFile.getAbsolutePath());

			PKIManager pki = new PKIManager();
			KeyPair pair = pki.createKeyPair();

			Calendar cal = Calendar.getInstance();
			Date notBefore = cal.getTime();
			cal.add(Calendar.YEAR, 10);
			Date notAfter = cal.getTime();

			X509Certificate cert = pki.generateSelfSignedCertificate(pair.getPrivate(), pair.getPublic(),
					notBefore, notAfter, "cn=localhost, O=" + applicationName + ", C=LU");

			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(null, null);
			FileOutputStream output = new FileOutputStream(keyStoreFile);
			keyStore.setKeyEntry("localhost", pair.getPrivate(), "password".toCharArray(), new Certificate[] { cert });
			keyStore.store(output, "password".toCharArray());
			output.close();

			File caCert = new File(nexuHome, applicationName + "-" + notBefore.getTime() + ".crt");
			FileOutputStream caOutput = new FileOutputStream(caCert);
			caOutput.write(cert.getEncoded());
			caOutput.close();

			return caCert;
		} catch (Exception e) {
			throw new RuntimeException("Cannot create keystore", e);
		}

	}

	private File getKeystore(final File nexuHome, final String applicationName) {
		final String[] files = nexuHome.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(applicationName + "-") && name.endsWith(".crt");
			}
		});
		if(files.length > 0) {
			Arrays.sort(files);
			return new File(nexuHome, files[files.length - 1]);
		} else {
			// Backward compatibility
			return new File(nexuHome, "ca-cert.crt");
		}
	}
	
	private List<InitializationMessage> installCaCert(final NexuAPI api, final File caCert, final ResourceBundle resourceBundle, final ResourceBundle baseResourceBundle) {
		final List<InitializationMessage> messages = new ArrayList<>();
		final EnvironmentInfo envInfo = EnvironmentInfo.buildFromSystemProperties(System.getProperties());
		switch(envInfo.getOs()) {
		case WINDOWS:
			messages.addAll(installCaCertInFirefoxForWindows(api, caCert, resourceBundle, baseResourceBundle));
			messages.addAll(installCaCertInWindowsStore(api, caCert, resourceBundle, baseResourceBundle));
			break;
		case MACOSX:
			messages.addAll(installCaCertInFirefoxForMac(api, caCert, resourceBundle, baseResourceBundle));
			messages.addAll(installCaCertInMacUserKeychain(api, caCert, resourceBundle, baseResourceBundle));
			break;
		case LINUX:
			messages.addAll(installCaCertInLinuxFFChromeStores(api, caCert, resourceBundle, baseResourceBundle));
			break;
		case NOT_RECOGNIZED:
			LOGGER.warn("Automatic installation of CA certficate is not yet supported for NOT_RECOGNIZED.");
			break;
		default:
			throw new IllegalArgumentException("Unhandled value: " + envInfo.getOs());
		}
		return messages;
	}
	
	/**
	 * Install the CA Cert in Firefox for Windows.
	 */
	private List<InitializationMessage> installCaCertInFirefoxForWindows(final NexuAPI api, final File caCert, final ResourceBundle resourceBundle, final ResourceBundle baseResourceBundle) {
		Path tempDirPath = null;
		try {
			// 1. Copy and unzip firefox_add-certs-nowina-1.1.zip
			tempDirPath = Files.createTempDirectory("NexU-Firefox-Add_certs");
			final File tempDirFile = tempDirPath.toFile();
			final File zipFile = new File(tempDirFile, "firefox_add-certs-nowina-1.1.zip");
			FileUtils.copyURLToFile(this.getClass().getResource("/firefox_add-certs-nowina-1.1.zip"), zipFile);
			new ZipFile(zipFile).extractAll(tempDirPath.toString());
			
			// 2. Install caCert into <unzipped_folder>/cacert
			final File unzippedFolder = new File(tempDirFile.getAbsolutePath() + File.separator +
					"firefox_add-certs-nowina-1.1");
			final File caCertDestDir = new File(unzippedFolder, "cacert");
			FileUtils.copyFile(caCert, new File(caCertDestDir, caCert.getName()));
			
			// 3. Run add-certs.cmd
			final ProcessBuilder pb = new ProcessBuilder(unzippedFolder + File.separator + "add-certs.cmd");
			pb.redirectErrorStream(true);
			final Process p = pb.start();
			if(!p.waitFor(180, TimeUnit.SECONDS)) {
				throw new NexuException("Timeout occurred when trying to install CA certificate in Firefox");
			}
			if(p.exitValue() == 1) {
				LOGGER.info("Mozilla Firefox not installed.");
			} else if(p.exitValue() != 0) {
				final String output = IOUtils.toString(p.getInputStream());
				throw new NexuException("Batch script returned " + p.exitValue() + " when trying to install CA certificate in Firefox. Output: " + output);
			}
			return Collections.emptyList();
		} catch(Exception e) {
			LOGGER.warn("Exception when trying to install certificate in Firefox", e);
			return Arrays.asList(new InitializationMessage(
					MessageType.CONFIRMATION,
					resourceBundle.getString("warn.install.cert.title"),
					MessageFormat.format(resourceBundle.getString("warn.install.cert.header"), api.getAppConfig().getApplicationName(), "FireFox"),
					baseResourceBundle.getString("provide.feedback"),
					true,
					e
				)
			);
		} finally {
			if(tempDirPath != null) {
				try {
					FileUtils.deleteDirectory(tempDirPath.toFile());
				} catch (IOException e) {
					LOGGER.error("IOException when deleting " + tempDirPath.toString() + ": " + e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Install the CA Cert in Firefox for Mac.
	 */
	private List<InitializationMessage> installCaCertInFirefoxForMac(final NexuAPI api, final File caCert,
			final ResourceBundle resourceBundle, final ResourceBundle baseResourceBundle) {
		Path tempDirPath = null;
		try {
			// 1. Copy and unzip firefox_add-certs-mac-1.0.zip
			tempDirPath = Files.createTempDirectory("NexU-Firefox-Add_certs");
			final File tempDirFile = tempDirPath.toFile();
			final File zipFile = new File(tempDirFile, "firefox_add-certs-mac-1.0.zip");
			FileUtils.copyURLToFile(this.getClass().getResource("/firefox_add-certs-mac-1.0.zip"), zipFile);
			new ZipFile(zipFile).extractAll(tempDirPath.toString());
			
			// 2. Run add_certs.sh
			final ProcessBuilder pb = new ProcessBuilder("/bin/bash", "add_certs.sh", api.getAppConfig().getApplicationName(),
					caCert.getAbsolutePath());
			pb.directory(new File(tempDirFile.getAbsolutePath() + File.separator +
					"firefox_add-certs-mac-1.0" + File.separator + "bin"));
			pb.redirectErrorStream(true);
			final Process p = pb.start();
			if(!p.waitFor(180, TimeUnit.SECONDS)) {
				throw new NexuException("Timeout occurred when trying to install CA certificate in Firefox");
			}
			if(p.exitValue() != 0) {
				final String output = IOUtils.toString(p.getInputStream());
				throw new NexuException("Batch script returned " + p.exitValue() + " when trying to install CA certificate in Firefox. Output: " + output);
			}
			return Collections.emptyList();
		} catch(Exception e) {
			LOGGER.warn("Exception when trying to install certificate in Firefox", e);
			return Arrays.asList(new InitializationMessage(
					MessageType.CONFIRMATION,
					resourceBundle.getString("warn.install.cert.title"),
					MessageFormat.format(resourceBundle.getString("warn.install.cert.header"), api.getAppConfig().getApplicationName(), "FireFox"),
					baseResourceBundle.getString("provide.feedback"),
					true,
					e
				)
			);
		} finally {
			if(tempDirPath != null) {
				try {
					FileUtils.deleteDirectory(tempDirPath.toFile());
				} catch (IOException e) {
					LOGGER.error("IOException when deleting " + tempDirPath.toString() + ": " + e.getMessage(), e);
				}
			}
		}
	}
	
	/**
	 * Installs the CA certificate in Windows Store (used by Chrome, IE and Edge amongst others).
	 */
	private List<InitializationMessage> installCaCertInWindowsStore(final NexuAPI api, final File caCert, final ResourceBundle resourceBundle, final ResourceBundle baseResourceBundle) {
		try (
				final FileInputStream fis = new FileInputStream(caCert);
				final BufferedInputStream bis = new BufferedInputStream(fis);
				) {
			final KeyStore keyStore = KeyStore.getInstance("Windows-ROOT");
			keyStore.load(null);

			final CertificateFactory cf = CertificateFactory.getInstance("X.509");
			final Certificate cert = cf.generateCertificate(bis);

			if(keyStore.getCertificateAlias(cert) == null) {
				keyStore.setCertificateEntry(api.getAppConfig().getApplicationName() + "-localhost", cert); 
			}
			return Collections.emptyList();
		} catch(final KeyStoreException e) {
			LOGGER.warn("KeyStoreException when trying to install certificate in Windows Store", e);
			// Unfortunately there is no particular exception thrown in this case
			return Arrays.asList(new InitializationMessage(
					MessageType.CONFIRMATION,
					resourceBundle.getString("warn.install.cert.title"),
					MessageFormat.format(resourceBundle.getString("warn.install.cert.header"), api.getAppConfig().getApplicationName(), "Windows Store"),
					resourceBundle.getString("warn.install.cert.windows.registry") + "\n\n" + baseResourceBundle.getString("provide.feedback"),
					true,
					e
				)
			);
		} catch(final Exception e) {
			LOGGER.warn("Exception when trying to install certificate in Windows Store", e);
			return Arrays.asList(new InitializationMessage(
					MessageType.CONFIRMATION,
					resourceBundle.getString("warn.install.cert.title"),
					MessageFormat.format(resourceBundle.getString("warn.install.cert.header"), api.getAppConfig().getApplicationName(), "Windows Store"),
					baseResourceBundle.getString("provide.feedback"),
					true,
					e
				)
			);
		}
	}
	
	/**
	 * Installs the CA certificate in Mac user keychain (used by Safari amongst others).
	 */
	private List<InitializationMessage> installCaCertInMacUserKeychain(final NexuAPI api, final File caCert,
			final ResourceBundle resourceBundle, final ResourceBundle baseResourceBundle) {
		Path tempFilePath = null;
		try {
			// 1. Copy mac_user_keychain_add-certs.sh
			tempFilePath = Files.createTempFile("mac_user_keychain_add-certs", "sh");
			final File tempFile = tempFilePath.toFile();
			FileUtils.copyURLToFile(this.getClass().getResource("/mac_user_keychain_add-certs.sh"), tempFile);
			
			// 2. Run mac_user_keychain_add-certs.sh
			final ProcessBuilder pb = new ProcessBuilder("/bin/bash", tempFile.getAbsolutePath(),
					caCert.getAbsolutePath());
			pb.redirectErrorStream(true);
			final Process p = pb.start();
			if(!p.waitFor(180, TimeUnit.SECONDS)) {
				throw new NexuException("Timeout occurred when trying to install CA certificate in Mac user keychain");
			}
			if(p.exitValue() != 0) {
				final String output = IOUtils.toString(p.getInputStream());
				throw new NexuException("Batch script returned " + p.exitValue() + " when trying to install CA certificate in Mac user keychain. Output: " + output);
			}
			return Collections.emptyList();
		} catch(Exception e) {
			LOGGER.warn("Exception when trying to install certificate in Mac user keychain", e);
			return Arrays.asList(new InitializationMessage(
					MessageType.CONFIRMATION,
					resourceBundle.getString("warn.install.cert.title"),
					MessageFormat.format(resourceBundle.getString("warn.install.cert.header"), api.getAppConfig().getApplicationName(), "Mac user keychain"),
					baseResourceBundle.getString("provide.feedback"),
					true,
					e
				)
			);
		} finally {
			if(tempFilePath != null) {
				try {
					Files.delete(tempFilePath);
				} catch (IOException e) {
					LOGGER.error("IOException when deleting " + tempFilePath.toString() + ": " + e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Installs the CA certificate in Linux FF and Chrome/Chromium stores.
	 */
	private List<InitializationMessage> installCaCertInLinuxFFChromeStores(final NexuAPI api, final File caCert,
			final ResourceBundle resourceBundle, final ResourceBundle baseResourceBundle) {
		Path tempFilePath = null;
		try {
			// 1. Copy linux_add-certs.sh
			tempFilePath = Files.createTempFile("linux_add-certs.sh", "sh");
			final File tempFile = tempFilePath.toFile();
			FileUtils.copyURLToFile(this.getClass().getResource("/linux_add-certs.sh"), tempFile);
			
			// 2. Run linux_add-certs.sh
			final ProcessBuilder pb = new ProcessBuilder("/bin/bash", tempFile.getAbsolutePath(),
					api.getAppConfig().getApplicationName(), caCert.getAbsolutePath());
			pb.redirectErrorStream(true);
			final Process p = pb.start();
			if(!p.waitFor(180, TimeUnit.SECONDS)) {
				throw new NexuException("Timeout occurred when trying to install CA certificate in Linux FF and Chrome/Chromium stores.");
			}
			if(p.exitValue() != 0) {
				final String output = IOUtils.toString(p.getInputStream());
				throw new NexuException("Batch script returned " + p.exitValue() + " when trying to install CA certificate in Linux FF and Chrome/Chromium stores. Output: " + output);
			}
			return Collections.emptyList();
		} catch(Exception e) {
			LOGGER.warn("Exception when trying to install certificate in Linux FF and Chrome/Chromium stores", e);
			return Arrays.asList(new InitializationMessage(
					MessageType.CONFIRMATION,
					resourceBundle.getString("warn.install.cert.title"),
					MessageFormat.format(resourceBundle.getString("warn.install.cert.header"), api.getAppConfig().getApplicationName(), "Linux Firefox & Chrome/Chromium stores"),
					baseResourceBundle.getString("provide.feedback"),
					true,
					e
				)
			);
		} finally {
			if(tempFilePath != null) {
				try {
					Files.delete(tempFilePath);
				} catch (IOException e) {
					LOGGER.error("IOException when deleting " + tempFilePath.toString() + ": " + e.getMessage(), e);
				}
			}
		}
	}
}
