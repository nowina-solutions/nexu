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
import java.io.IOException;
import java.net.InetAddress;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import lu.nowina.nexu.NexuException;
import lu.nowina.nexu.jetty.AbstractJettyServer;
import lu.nowina.nexu.jetty.JettyListAwareServerConnector;

public class JettyHttpsServer extends AbstractJettyServer {

	@Override
	public Connector[] getConnectors() {
		// HTTP connector
		final HttpConfiguration http = new HttpConfiguration();
		final JettyListAwareServerConnector connector = new JettyListAwareServerConnector(getServer());
		connector.addConnectionFactory(new HttpConnectionFactory(http));
		connector.setPorts(getApi().getAppConfig().getBindingPorts());
		connector.setHost(InetAddress.getLoopbackAddress().getCanonicalHostName());

		// HTTPS connector
		final HttpConfiguration https = new HttpConfiguration();
		https.addCustomizer(new SecureRequestCustomizer());

		// Configuring SSL
		final SslContextFactory sslContextFactory = new SslContextFactory();
		// Generate keystore
		sslContextFactory.setKeyStore(generateKeyStore(getApi().getAppConfig().getNexuHome(),
				getApi().getAppConfig().getApplicationName()));
		sslContextFactory.setKeyStorePassword("password");
		sslContextFactory.setKeyManagerPassword("password");
		// Configuring the connector
		final JettyListAwareServerConnector sslConnector = new JettyListAwareServerConnector(getServer(),
				new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https));
		sslConnector.setPorts((getApi().getAppConfig()).getBindingPortsHttps());
		sslConnector.setHost(InetAddress.getLoopbackAddress().getCanonicalHostName());
		
		return new Connector[] {connector, sslConnector};
	}
	
	private KeyStore generateKeyStore(final File nexuHome, final String applicationName) {
		try {
			final File rootKeystore = new File(nexuHome, "keystore.jks");
			final KeyStore rootKS = KeyStore.getInstance("JKS");
			try(final FileInputStream fis = new FileInputStream(rootKeystore);
					final BufferedInputStream bis = new BufferedInputStream(fis)) {
				rootKS.load(bis, "password".toCharArray());
			}
			final PrivateKey rootPrivateKey = (PrivateKey) rootKS.getKey("localhost",
					"password".toCharArray()); 

			final PKIManager pki = new PKIManager();
			final X509Certificate rootCert;
			try (final FileInputStream fis =
					new FileInputStream(pki.getRootCertificate(nexuHome, applicationName));
					final BufferedInputStream bis = new BufferedInputStream(fis)) {
				final CertificateFactory cf = CertificateFactory.getInstance("X.509");
				rootCert = (X509Certificate) cf.generateCertificate(bis);
			}
			
			final KeyPair keyPair = pki.createKeyPair();
			final Calendar cal = Calendar.getInstance();
			final Date notBefore = cal.getTime();
			cal.add(Calendar.YEAR, 3);
			final long notAfterMs = cal.getTime().after(rootCert.getNotAfter()) ?
					rootCert.getNotAfter().getTime()-1 : cal.getTime().getTime();
			final Date notAfter = new Date(notAfterMs); 
			final X509Certificate cert = pki.generateCertificateForWebServer(rootPrivateKey,
					rootCert, keyPair.getPrivate(), keyPair.getPublic(), notBefore,
					notAfter, applicationName);
			
			final KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(null, null);
			keyStore.setKeyEntry("localhost", keyPair.getPrivate(), "password".toCharArray(),
					new Certificate[]{cert, rootCert});

			return keyStore;
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException |
				CertificateException | IOException e) {
			throw new NexuException(e);
		}
	}
}
