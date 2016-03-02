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
