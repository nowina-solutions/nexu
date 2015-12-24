package lu.nowina.nexu.json.pojo;

import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.x509.CertificateToken;

public class TestWithCertificate {
	
	private CertificateToken token;
	
	private CertificateToken[] chain;

	public TestWithCertificate() {
	}
	
	public TestWithCertificate(DSSPrivateKeyEntry key) {
		this.token = key.getCertificate();
		this.chain = key.getCertificateChain();
	}
	
	public CertificateToken getToken() {
		return token;
	}

	public void setToken(CertificateToken token) {
		this.token = token;
	}

	public CertificateToken[] getChain() {
		return chain;
	}

	public void setChain(CertificateToken[] chain) {
		this.chain = chain;
	}

}
