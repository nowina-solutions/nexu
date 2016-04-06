/**
 * © Nowina Solutions, 2015-2016
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
package lu.nowina.nexu.object.model;

import org.junit.Test;

import eu.europa.esig.dss.DigestAlgorithm;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import lu.nowina.nexu.json.GsonHelper;

/**
 * JUnit test class for JSON marshalling/unmarshalling.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class TestMarshallUnmarshallJSON {

	public TestMarshallUnmarshallJSON() {
		super();
	}

	private void setCommonRequestFields(final NexuRequest request) {
		request.setExternalId("externalId");
		request.setNonce("nonce");
		request.setRequestSeal("requestSeal");
		request.setUserLocale("userLocale");
	}
	
	private void assertCommonRequestFields(final lu.nowina.nexu.api.NexuRequest request) {
		Assert.assertEquals("externalId", request.getExternalId());
		Assert.assertEquals("nonce", request.getNonce());
		Assert.assertEquals("requestSeal", request.getRequestSeal());
		Assert.assertEquals("userLocale", request.getUserLocale());
	}
	
	@Test
	public void testGetCertificateRequest() {
		final CertificateFilter certFilter = new CertificateFilter();
		certFilter.setPurpose(Purpose.AUTHENTICATION);
		final GetCertificateRequest getCertificateRequest = new GetCertificateRequest(certFilter);
		setCommonRequestFields(getCertificateRequest);
		final String json = GsonHelper.toJson(getCertificateRequest);
		
		final lu.nowina.nexu.api.GetCertificateRequest getCertificateRequestAPI = GsonHelper.fromJson(json, lu.nowina.nexu.api.GetCertificateRequest.class);
		Assert.assertNotNull(getCertificateRequestAPI);
		assertCommonRequestFields(getCertificateRequestAPI);
		Assert.assertNotNull(getCertificateRequestAPI.getCertificateFilter());
		Assert.assertNull(getCertificateRequestAPI.getCertificateFilter().getCertificateSHA1());
		Assert.assertEquals(lu.nowina.nexu.api.Purpose.AUTHENTICATION, getCertificateRequestAPI.getCertificateFilter().getPurpose());
	}

	@Test
	public void testGetCertificateResponse() {
		//TODO
	}

	@Test
	public void testSignatureRequest() {
		final TokenId tokenId = new TokenId();
		tokenId.setId("tokenId");
		final ToBeSigned tbs = new ToBeSigned();
		tbs.setBytes("dG90bw==");
		final SignatureRequest signatureRequest = new SignatureRequest();
		setCommonRequestFields(signatureRequest);
		signatureRequest.setDigestAlgorithm("SHA1");
		signatureRequest.setKeyId("keyId");
		signatureRequest.setToBeSigned(tbs);
		signatureRequest.setTokenId(tokenId);
		final String json = GsonHelper.toJson(signatureRequest);

		final lu.nowina.nexu.api.SignatureRequest signatureRequestAPI = GsonHelper.fromJson(json, lu.nowina.nexu.api.SignatureRequest.class);
		Assert.assertNotNull(signatureRequestAPI);
		assertCommonRequestFields(signatureRequestAPI);
		Assert.assertEquals(DigestAlgorithm.SHA1, signatureRequestAPI.getDigestAlgorithm());
		Assert.assertEquals("keyId", signatureRequestAPI.getKeyId());
		Assert.assertNotNull(signatureRequestAPI.getToBeSigned());
		Assert.assertEquals("toto", new String(signatureRequestAPI.getToBeSigned().getBytes(), StandardCharsets.UTF_8));
		Assert.assertNotNull(signatureRequestAPI.getTokenId());
		Assert.assertEquals("tokenId", signatureRequestAPI.getTokenId().getId());
	}

	@Test
	public void testSignatureResponse() {
		//TODO
	}

	@Test
	public void testGetIdentityInfoRequest() {
		final GetIdentityInfoRequest getIdentityInfoRequest = new GetIdentityInfoRequest();
		setCommonRequestFields(getIdentityInfoRequest);
		final String json = GsonHelper.toJson(getIdentityInfoRequest);
		
		final lu.nowina.nexu.api.GetIdentityInfoRequest getIdentityInfoRequestAPI = GsonHelper.fromJson(json, lu.nowina.nexu.api.GetIdentityInfoRequest.class);
		Assert.assertNotNull(getIdentityInfoRequestAPI);
		assertCommonRequestFields(getIdentityInfoRequestAPI);
	}

	@Test
	public void testGetIdentityInfoResponse() {
		//TODO
	}

	@Test
	public void testAuthenticateRequest() {
		final ToBeSigned tbs = new ToBeSigned();
		tbs.setBytes("dG90bw==");
		final AuthenticateRequest authenticateRequest = new AuthenticateRequest();
		setCommonRequestFields(authenticateRequest);
		authenticateRequest.setChallenge(tbs);
		final String json = GsonHelper.toJson(authenticateRequest);
		
		final lu.nowina.nexu.api.AuthenticateRequest authenticateRequestAPI = GsonHelper.fromJson(json, lu.nowina.nexu.api.AuthenticateRequest.class);
		Assert.assertNotNull(authenticateRequestAPI);
		assertCommonRequestFields(authenticateRequestAPI);
		Assert.assertNotNull(authenticateRequestAPI.getChallenge());
		Assert.assertEquals("toto", new String(authenticateRequestAPI.getChallenge().getBytes(), StandardCharsets.UTF_8));
	}

	@Test
	public void testAuthenticateResponse() {
		//TODO
	}
}
