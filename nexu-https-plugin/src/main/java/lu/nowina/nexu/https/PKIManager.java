/**
 * © Nowina Solutions, 2015-2017
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class PKIManager {

	public KeyPair createKeyPair() {
		try {
			final KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(2048);
			return generator.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public X509Certificate generateRootSelfSignedCertificate(final PrivateKey pk, final PublicKey p, final Date membersNotBefore,
			final Date membersNotAfter, final String applicationName) {
		try {
			final ContentSigner signer = new JcaContentSignerBuilder("SHA512withRSA").build(pk);
			final X500Name name = this.getX500SubjectForRoot(applicationName);
			final SubjectPublicKeyInfo membersKeyInfo = SubjectPublicKeyInfo.getInstance(p.getEncoded());

			final X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(name,
					new BigInteger(Long.toString(new Random().nextLong())),
					membersNotBefore, membersNotAfter, name, membersKeyInfo);
			
			/* Add Key Usage */
			final KeyUsage keyUsage = new KeyUsage(KeyUsage.keyCertSign | KeyUsage.cRLSign);
			certBuilder.addExtension(Extension.keyUsage, true, keyUsage);

			/* Add Basic constraints */
			certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(1));
			
			final X509CertificateHolder cert = certBuilder.build(signer);
			return toX509Certificate(cert);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public X509Certificate generateCertificateForWebServer(final PrivateKey rootPk, final X509Certificate rootCert,
			final PrivateKey pk, final PublicKey p, final Date membersNotBefore, final Date membersNotAfter,
			final String applicationName) {
		try {
			final ContentSigner signer = new JcaContentSignerBuilder("SHA512withRSA").build(rootPk);
			final X500Name rootX500Name = new JcaX509CertificateHolder(rootCert).getSubject();
			final X500Name subjectX500Name = this.getX500SubjectForWebServer(applicationName);
			final SubjectPublicKeyInfo membersKeyInfo = SubjectPublicKeyInfo.getInstance(p.getEncoded());

			final X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(rootX500Name,
					new BigInteger(Long.toString(new Random().nextLong())),
					membersNotBefore, membersNotAfter, subjectX500Name, membersKeyInfo);

			/* Add Key Usage */
			final KeyUsage keyUsage = new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment);
			certBuilder.addExtension(Extension.keyUsage, true, keyUsage);

			/* Add Extended Key Usage */
			final ExtendedKeyUsage extendedKeyUsage = new ExtendedKeyUsage(new KeyPurposeId[]{
					KeyPurposeId.id_kp_clientAuth, KeyPurposeId.id_kp_serverAuth});
			certBuilder.addExtension(Extension.extendedKeyUsage, false, extendedKeyUsage);

			/* Add subjectAltName */
			final GeneralNames names = new GeneralNames(new GeneralName(GeneralName.dNSName, "localhost"));
			certBuilder.addExtension(Extension.subjectAlternativeName, false, names);
			
			/* Add basic constraints */
			certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));
			
			final X509CertificateHolder cert = certBuilder.build(signer);
			return toX509Certificate(cert);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
    private X509Certificate toX509Certificate(X509CertificateHolder holder) {
		try {
			return (X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(
					new ByteArrayInputStream(holder.getEncoded()));
		} catch (CertificateException | IOException e) {
			throw new RuntimeException(e);
		}
	}

    private X500Name getX500SubjectForRoot(final String applicationName) {
        final X500NameBuilder builder = new X500NameBuilder();
        builder.addRDN(BCStyle.CN, "localhost");
        builder.addRDN(BCStyle.O, applicationName);
        builder.addRDN(BCStyle.C, "LU");
        return builder.build();
    }

    private X500Name getX500SubjectForWebServer(final String applicationName) {
        final X500NameBuilder builder = new X500NameBuilder();
        builder.addRDN(BCStyle.CN, "localhost");
        builder.addRDN(BCStyle.O, applicationName);
        builder.addRDN(BCStyle.OU, "webserver");
        builder.addRDN(BCStyle.C, "LU");
        return builder.build();
    }
    
	public File getRootCertificate(final File nexuHome, final String applicationName) {
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
}
