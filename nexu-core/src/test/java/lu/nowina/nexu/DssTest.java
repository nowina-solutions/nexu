package lu.nowina.nexu;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.bouncycastle.asn1.x509.IssuerSerial;

import eu.europa.esig.dss.CertificatePolicy;
import eu.europa.esig.dss.DSSASN1Utils;
import eu.europa.esig.dss.DSSUtils;
import eu.europa.esig.dss.QCStatementOids;
import eu.europa.esig.dss.tsl.KeyUsageBit;
import eu.europa.esig.dss.x509.CertificateSourceType;
import eu.europa.esig.dss.x509.CertificateToken;

public class DssTest {

	public static void main(String args[]) {
		// CertificateToken certificateToken = DSSUtils.loadCertificate(new
		// File("C:\\Users\\landry.soules\\Dev\\Projects\\Nexu\\Tests\\Landry Soules
		// (Signature).DER"));
		CertificateToken certificateToken = DSSUtils.loadCertificate(
//				new File("C:\\Users\\landry.soules\\Dev\\Projects\\Nexu\\Tests\\Landry Soules (Authentication).DER"));
				new File("/home/landry/Dev/Projects/Nexu/Tests/landry_soules_signature.pem"));
		System.out.println("certificate token : " + certificateToken.toString());

		certificateToken.getCertificate();
		
		List<CertificatePolicy> policies = DSSASN1Utils.getCertificatePolicies(certificateToken);
		for(CertificatePolicy policy: policies) {
			System.out.println(policy.getOid() + " " + policy.getCpsUrl());
		}
		
		List<String> qcStatementsIdList = DSSASN1Utils.getQCStatementsIdList(certificateToken);
		System.out.println(qcStatementsIdList);

		for (QCStatementOids oid : QCStatementOids.values()) {

			System.out.println(oid + " " + oid.getOid() + " " + oid.getDescription());
		}

		Set<KeyUsageBit> keyUsageBits = certificateToken.getKeyUsageBits();
		for (KeyUsageBit keyUsageBit : keyUsageBits) {
			System.out.println(keyUsageBit);
		}

		System.out.println("----------");

		System.out.println("Issuer : " + certificateToken.getIssuerX500Principal().getName());
		System.out
				.println("Other data : " + DSSASN1Utils.get(certificateToken.getIssuerX500Principal()).get("2.5.4.3"));
		System.out.println("Extended key usage : " + DSSASN1Utils.getExtendedKeyUsage(certificateToken));
		Set<KeyUsageBit> kubs = certificateToken.getKeyUsageBits();
		for (KeyUsageBit kub : kubs) {
			System.out.println("Usage : " + kub.name() + " | " + kub.toString());
		}
		Set<CertificateSourceType> set = certificateToken.getSources();
		for(CertificateSourceType cst : set) {
			System.out.println(cst);
		}
	}
}
