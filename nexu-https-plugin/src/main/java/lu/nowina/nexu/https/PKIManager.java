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

import java.io.ByteArrayInputStream;
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
import java.util.Date;
import java.util.Random;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class PKIManager {

	public KeyPair createKeyPair() {

		KeyPairGenerator generator;
		try {
			generator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		KeyPair keyPair = generator.generateKeyPair();
		return keyPair;
	}

	public X509Certificate generateSelfSignedCertificate(PrivateKey pk, PublicKey p, Date membersNotBefore, Date membersNotAfter, String dn) {

		try {
			ContentSigner signer = new JcaContentSignerBuilder("SHA512withRSA").build(pk);

			X500Name name = new X500Name(dn);

			X509CertificateHolder cert = generateX509Cert(name, signer, name, new BigInteger(Long.toString(new Random().nextLong())), membersNotBefore, membersNotAfter, p, null, null);

			return toX509Certificate(cert);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	protected X509CertificateHolder generateX509Cert(X500Name issuerName, ContentSigner rootSigner, X500Name subjectNameString, BigInteger membersSerial,
			Date membersNotBefore, Date membersNotAfter, PublicKey subjectPublicKey, String ocspUrl, String crlUrl) throws Exception {

		SubjectPublicKeyInfo membersKeyInfo = SubjectPublicKeyInfo.getInstance(subjectPublicKey.getEncoded());

		X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(issuerName, membersSerial, membersNotBefore, membersNotAfter, subjectNameString,
				membersKeyInfo);

		/* Add Key Usage */
		KeyUsage keyUsage = new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign | KeyUsage.cRLSign | KeyUsage.keyEncipherment);
		certBuilder.addExtension(Extension.keyUsage, true, keyUsage);
		
		final ExtendedKeyUsage extendedKeyUsage = new ExtendedKeyUsage(new KeyPurposeId[]{
				KeyPurposeId.id_kp_clientAuth, KeyPurposeId.id_kp_serverAuth});
		certBuilder.addExtension(Extension.extendedKeyUsage, false, extendedKeyUsage);

		X509CertificateHolder membersCert = certBuilder.build(rootSigner);
		return membersCert;
	}

	private X509Certificate toX509Certificate(X509CertificateHolder holder) {
		try {
			return (X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(new ByteArrayInputStream(holder.getEncoded()));
		} catch (CertificateException | IOException e) {
			throw new RuntimeException(e);
		}
	}

}
