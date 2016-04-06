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

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.EncryptionAlgorithm;
import eu.europa.esig.dss.SignatureAlgorithm;
import eu.europa.esig.dss.SignatureValue;
import eu.europa.esig.dss.token.JKSSignatureToken;
import eu.europa.esig.dss.x509.CertificateToken;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;

import lu.nowina.nexu.json.GsonHelper;

/**
 * JUnit test class for JSON marshalling/unmarshalling.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class TestMarshallUnmarshallJSON {

	private static final Gson customGson = new GsonBuilder().create();

	@SuppressWarnings("serial")
	private static <T> TypeToken<Execution<T>> buildTokenType(Class<T> clas) {
		TypeToken<Execution<T>> where = new TypeToken<Execution<T>>() {}.where(new TypeParameter<T>() {}, clas);
		return where;
	}

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
	
	private void setFeedback(final lu.nowina.nexu.api.Execution<?> execution) {
		final lu.nowina.nexu.api.Feedback feedback = new lu.nowina.nexu.api.Feedback();
		
		feedback.setNexuVersion("nexuVersion");
		
		final lu.nowina.nexu.api.EnvironmentInfo environmentInfo = new lu.nowina.nexu.api.EnvironmentInfo();
		environmentInfo.setArch(lu.nowina.nexu.api.Arch.AMD64);
		environmentInfo.setJreVendor(lu.nowina.nexu.api.JREVendor.ORACLE);
		environmentInfo.setOs(lu.nowina.nexu.api.OS.LINUX);
		environmentInfo.setOsArch("osArch");
		environmentInfo.setOsName("osName");
		environmentInfo.setOsVersion("osVersion");
		feedback.setInfo(environmentInfo);
		
		feedback.setFeedbackStatus(lu.nowina.nexu.api.FeedbackStatus.SUCCESS);
		
		feedback.setStacktrace("stackTrace");
	
		feedback.setUserComment("userComment");
		
		execution.setFeedback(feedback);
	}
	
	private void assertFeedback(final Execution<?> execution) {
		Assert.assertNotNull(execution.getFeedback());
		
		Assert.assertEquals("nexuVersion", execution.getFeedback().getNexuVersion());
		
		Assert.assertNotNull(execution.getFeedback().getInfo());
		Assert.assertEquals(Arch.AMD64, execution.getFeedback().getInfo().getArch());
		Assert.assertEquals(JREVendor.ORACLE, execution.getFeedback().getInfo().getJreVendor());
		Assert.assertEquals(OS.LINUX, execution.getFeedback().getInfo().getOs());
		Assert.assertEquals("osArch", execution.getFeedback().getInfo().getOsArch());
		Assert.assertEquals("osName", execution.getFeedback().getInfo().getOsName());
		Assert.assertEquals("osVersion", execution.getFeedback().getInfo().getOsVersion());
		
		Assert.assertEquals(FeedbackStatus.SUCCESS, execution.getFeedback().getFeedbackStatus());
		
		Assert.assertEquals("stackTrace", execution.getFeedback().getStacktrace());
		
		Assert.assertEquals("userComment", execution.getFeedback().getUserComment());
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
		final CertificateToken certificate = new JKSSignatureToken(this.getClass().getResourceAsStream("/keystore.jks"), "password").getKeys().get(0).getCertificate();
		final lu.nowina.nexu.api.TokenId tokenId = new lu.nowina.nexu.api.TokenId();
		tokenId.setId("tokenId");
		final lu.nowina.nexu.api.GetCertificateResponse getCertificateResponse = new lu.nowina.nexu.api.GetCertificateResponse();
		getCertificateResponse.setCertificate(certificate);
		getCertificateResponse.setCertificateChain(new CertificateToken[]{certificate, certificate, certificate});
		getCertificateResponse.setEncryptionAlgorithm(EncryptionAlgorithm.RSA);
		getCertificateResponse.setKeyId("keyId");
		getCertificateResponse.setPreferredDigest(DigestAlgorithm.SHA256);
		getCertificateResponse.setSupportedDigests(Arrays.asList(DigestAlgorithm.SHA1, DigestAlgorithm.SHA256, DigestAlgorithm.SHA512));
		getCertificateResponse.setTokenId(tokenId);
		final lu.nowina.nexu.api.Execution<lu.nowina.nexu.api.GetCertificateResponse> respAPI = new lu.nowina.nexu.api.Execution<lu.nowina.nexu.api.GetCertificateResponse>(getCertificateResponse);
		setFeedback(respAPI);
		final String json = GsonHelper.toJson(respAPI);
		
		final Execution<GetCertificateResponse> resp = customGson.fromJson(json, buildTokenType(GetCertificateResponse.class).getType());
		assertFeedback(resp);
		Assert.assertNotNull(resp.getResponse());
		final String certificateInBase64 = Base64.encodeBase64String(certificate.getEncoded());
		Assert.assertEquals(certificateInBase64, resp.getResponse().getCertificate());
		Assert.assertArrayEquals(new String[]{certificateInBase64, certificateInBase64, certificateInBase64}, resp.getResponse().getCertificateChain());
		Assert.assertEquals("RSA", resp.getResponse().getEncryptionAlgorithm());
		Assert.assertEquals("keyId", resp.getResponse().getKeyId());
		Assert.assertEquals("SHA256", resp.getResponse().getPreferredDigest());
		Assert.assertEquals(Arrays.asList("SHA1", "SHA256", "SHA512"), resp.getResponse().getSupportedDigests());
		Assert.assertNotNull(resp.getResponse().getTokenId());
		Assert.assertEquals("tokenId", resp.getResponse().getTokenId().getId());
	}

	@Test
	public void testSignatureRequest() {
		final TokenId tokenId = new TokenId();
		tokenId.setId("tokenId");
		final ToBeSigned tbs = new ToBeSigned();
		tbs.setBytes("dG8gYmUgc2lnbmVk");
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
		Assert.assertEquals("to be signed", new String(signatureRequestAPI.getToBeSigned().getBytes(), StandardCharsets.UTF_8));
		Assert.assertNotNull(signatureRequestAPI.getTokenId());
		Assert.assertEquals("tokenId", signatureRequestAPI.getTokenId().getId());
	}

	@Test
	public void testSignatureResponse() {
		final CertificateToken certificate = new JKSSignatureToken(this.getClass().getResourceAsStream("/keystore.jks"), "password").getKeys().get(0).getCertificate();
		final lu.nowina.nexu.api.SignatureResponse signatureResponse = new lu.nowina.nexu.api.SignatureResponse(
				new SignatureValue(SignatureAlgorithm.RSA_SHA256, "to be signed".getBytes(StandardCharsets.UTF_8)),
				certificate,
				new CertificateToken[]{certificate, certificate, certificate}
		);
		final lu.nowina.nexu.api.Execution<lu.nowina.nexu.api.SignatureResponse> respAPI = new lu.nowina.nexu.api.Execution<lu.nowina.nexu.api.SignatureResponse>(signatureResponse);
		setFeedback(respAPI);
		final String json = GsonHelper.toJson(respAPI);
		
		final Execution<SignatureResponse> resp = customGson.fromJson(json, buildTokenType(SignatureResponse.class).getType());
		assertFeedback(resp);
		Assert.assertNotNull(resp.getResponse());
		final String certificateInBase64 = Base64.encodeBase64String(certificate.getEncoded());
		Assert.assertEquals(certificateInBase64, resp.getResponse().getCertificate());
		Assert.assertArrayEquals(new String[]{certificateInBase64, certificateInBase64, certificateInBase64}, resp.getResponse().getCertificateChain());
		Assert.assertEquals("RSA_SHA256", resp.getResponse().getSignatureAlgorithm());
		Assert.assertEquals("dG8gYmUgc2lnbmVk", resp.getResponse().getSignatureValue());
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
		tbs.setBytes("dG8gYmUgc2lnbmVk");
		final AuthenticateRequest authenticateRequest = new AuthenticateRequest();
		setCommonRequestFields(authenticateRequest);
		authenticateRequest.setChallenge(tbs);
		final String json = GsonHelper.toJson(authenticateRequest);
		
		final lu.nowina.nexu.api.AuthenticateRequest authenticateRequestAPI = GsonHelper.fromJson(json, lu.nowina.nexu.api.AuthenticateRequest.class);
		Assert.assertNotNull(authenticateRequestAPI);
		assertCommonRequestFields(authenticateRequestAPI);
		Assert.assertNotNull(authenticateRequestAPI.getChallenge());
		Assert.assertEquals("to be signed", new String(authenticateRequestAPI.getChallenge().getBytes(), StandardCharsets.UTF_8));
	}

	@Test
	public void testAuthenticateResponse() {
		//TODO
	}
	
	@Test
	public void testException() {
		//TODO
	}
}
