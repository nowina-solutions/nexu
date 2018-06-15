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
import java.security.KeyStore.PasswordProtection;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;

import lu.nowina.nexu.api.flow.BasicOperationStatus;
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
		TypeToken<Execution<T>> where = new TypeToken<Execution<T>>() {
		}.where(new TypeParameter<T>() {
		}, clas);
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

	private void assertSuccessExecution(final Execution<?> execution) {
		Assert.assertNull(execution.getError());
		Assert.assertNull(execution.getErrorMessage());
		Assert.assertTrue(execution.isSuccess());
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

		final lu.nowina.nexu.api.GetCertificateRequest getCertificateRequestAPI = GsonHelper.fromJson(json,
				lu.nowina.nexu.api.GetCertificateRequest.class);
		Assert.assertNotNull(getCertificateRequestAPI);
		assertCommonRequestFields(getCertificateRequestAPI);
		Assert.assertNotNull(getCertificateRequestAPI.getCertificateFilter());
		Assert.assertNull(getCertificateRequestAPI.getCertificateFilter().getCertificateSHA1());
		Assert.assertEquals(lu.nowina.nexu.api.Purpose.AUTHENTICATION,
				getCertificateRequestAPI.getCertificateFilter().getPurpose());
	}

	@Test
	public void testGetCertificateResponse() {

		try (JKSSignatureToken sigToken = new JKSSignatureToken(this.getClass().getResourceAsStream("/keystore.jks"),
				new PasswordProtection("password".toCharArray()))) {
			final CertificateToken certificate = sigToken.getKeys().get(0).getCertificate();
			final lu.nowina.nexu.api.TokenId tokenId = new lu.nowina.nexu.api.TokenId();
			tokenId.setId("tokenId");
			final lu.nowina.nexu.api.GetCertificateResponse getCertificateResponse = new lu.nowina.nexu.api.GetCertificateResponse();
			getCertificateResponse.setCertificate(certificate);
			getCertificateResponse
					.setCertificateChain(new CertificateToken[] { certificate, certificate, certificate });
			getCertificateResponse.setEncryptionAlgorithm(EncryptionAlgorithm.RSA);
			getCertificateResponse.setKeyId("keyId");
			getCertificateResponse.setPreferredDigest(DigestAlgorithm.SHA256);
			getCertificateResponse.setSupportedDigests(
					Arrays.asList(DigestAlgorithm.SHA1, DigestAlgorithm.SHA256, DigestAlgorithm.SHA512));
			getCertificateResponse.setTokenId(tokenId);
			final lu.nowina.nexu.api.Execution<lu.nowina.nexu.api.GetCertificateResponse> respAPI = new lu.nowina.nexu.api.Execution<lu.nowina.nexu.api.GetCertificateResponse>(
					getCertificateResponse);
			setFeedback(respAPI);
			final String json = GsonHelper.toJson(respAPI);

			final Execution<GetCertificateResponse> resp = customGson.fromJson(json,
					buildTokenType(GetCertificateResponse.class).getType());
			assertSuccessExecution(resp);
			assertFeedback(resp);
			Assert.assertNotNull(resp.getResponse());
			final String certificateInBase64 = Base64.encodeBase64String(certificate.getEncoded());
			Assert.assertEquals(certificateInBase64, resp.getResponse().getCertificate());
			Assert.assertArrayEquals(new String[] { certificateInBase64, certificateInBase64, certificateInBase64 },
					resp.getResponse().getCertificateChain());
			Assert.assertEquals("RSA", resp.getResponse().getEncryptionAlgorithm());
			Assert.assertEquals("keyId", resp.getResponse().getKeyId());
			Assert.assertEquals("SHA256", resp.getResponse().getPreferredDigest());
			Assert.assertEquals(Arrays.asList("SHA1", "SHA256", "SHA512"), resp.getResponse().getSupportedDigests());
			Assert.assertNotNull(resp.getResponse().getTokenId());
			Assert.assertEquals("tokenId", resp.getResponse().getTokenId().getId());
		}
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

		final lu.nowina.nexu.api.SignatureRequest signatureRequestAPI = GsonHelper.fromJson(json,
				lu.nowina.nexu.api.SignatureRequest.class);
		Assert.assertNotNull(signatureRequestAPI);
		assertCommonRequestFields(signatureRequestAPI);
		Assert.assertEquals(DigestAlgorithm.SHA1, signatureRequestAPI.getDigestAlgorithm());
		Assert.assertEquals("keyId", signatureRequestAPI.getKeyId());
		Assert.assertNotNull(signatureRequestAPI.getToBeSigned());
		Assert.assertEquals("to be signed",
				new String(signatureRequestAPI.getToBeSigned().getBytes(), StandardCharsets.UTF_8));
		Assert.assertNotNull(signatureRequestAPI.getTokenId());
		Assert.assertEquals("tokenId", signatureRequestAPI.getTokenId().getId());
	}

	@Test
	public void testSignatureResponse() {
		try (JKSSignatureToken sigToken = new JKSSignatureToken(this.getClass().getResourceAsStream("/keystore.jks"),
				new PasswordProtection("password".toCharArray()))) {
			final CertificateToken certificate = sigToken.getKeys().get(0).getCertificate();
			final lu.nowina.nexu.api.SignatureResponse signatureResponse = new lu.nowina.nexu.api.SignatureResponse(
					new SignatureValue(SignatureAlgorithm.RSA_SHA256, "to be signed".getBytes(StandardCharsets.UTF_8)),
					certificate, new CertificateToken[] { certificate, certificate, certificate });
			final lu.nowina.nexu.api.Execution<lu.nowina.nexu.api.SignatureResponse> respAPI = new lu.nowina.nexu.api.Execution<lu.nowina.nexu.api.SignatureResponse>(
					signatureResponse);
			setFeedback(respAPI);
			final String json = GsonHelper.toJson(respAPI);

			final Execution<SignatureResponse> resp = customGson.fromJson(json,
					buildTokenType(SignatureResponse.class).getType());
			assertSuccessExecution(resp);
			assertFeedback(resp);
			Assert.assertNotNull(resp.getResponse());
			final String certificateInBase64 = Base64.encodeBase64String(certificate.getEncoded());
			Assert.assertEquals(certificateInBase64, resp.getResponse().getCertificate());
			Assert.assertArrayEquals(new String[] { certificateInBase64, certificateInBase64, certificateInBase64 },
					resp.getResponse().getCertificateChain());
			Assert.assertEquals("RSA_SHA256", resp.getResponse().getSignatureAlgorithm());
			Assert.assertEquals("dG8gYmUgc2lnbmVk", resp.getResponse().getSignatureValue());
		}
	}

	@Test
	public void testGetIdentityInfoRequest() {
		final GetIdentityInfoRequest getIdentityInfoRequest = new GetIdentityInfoRequest();
		setCommonRequestFields(getIdentityInfoRequest);
		final String json = GsonHelper.toJson(getIdentityInfoRequest);

		final lu.nowina.nexu.api.GetIdentityInfoRequest getIdentityInfoRequestAPI = GsonHelper.fromJson(json,
				lu.nowina.nexu.api.GetIdentityInfoRequest.class);
		Assert.assertNotNull(getIdentityInfoRequestAPI);
		assertCommonRequestFields(getIdentityInfoRequestAPI);
	}

	@Test
	public void testGetIdentityInfoResponse() {
		try (JKSSignatureToken sigToken = new JKSSignatureToken(this.getClass().getResourceAsStream("/keystore.jks"),
				new PasswordProtection("password".toCharArray()))) {
			final CertificateToken certificate = sigToken.getKeys().get(0).getCertificate();
			final lu.nowina.nexu.api.GetIdentityInfoResponse getIdentityInfoResponse = new lu.nowina.nexu.api.GetIdentityInfoResponse();
			getIdentityInfoResponse.setAddress("address");
			getIdentityInfoResponse.setCardDeliveryAuthority("cardDeliveryAuthority");
			getIdentityInfoResponse.setCardNumber("cardNumber");
			getIdentityInfoResponse.setCardValidityDateBegin(LocalDate.now());
			getIdentityInfoResponse.setCardValidityDateEnd(LocalDate.now().plusDays(1));
			getIdentityInfoResponse.setChipNumber("chipNumber");
			getIdentityInfoResponse.setCity("city");
			getIdentityInfoResponse.setDateOfBirth(LocalDate.now().minusDays(1));
			getIdentityInfoResponse.setFirstName("firstName");
			getIdentityInfoResponse.setGender(lu.nowina.nexu.api.GetIdentityInfoResponse.Gender.MALE);
			getIdentityInfoResponse.setLastName("lastName");
			getIdentityInfoResponse.setMiddleName("middleName");
			getIdentityInfoResponse.setNationality("nationality");
			getIdentityInfoResponse.setNationalNumber("nationalNumber");
			getIdentityInfoResponse.setNobleCondition("nobleCondition");
			getIdentityInfoResponse.setPhoto("photo".getBytes(StandardCharsets.UTF_8));
			getIdentityInfoResponse.setPhotoMimeType("photoMimeType");
			getIdentityInfoResponse.setPlaceOfBirth("placeOfBirth");
			getIdentityInfoResponse.setPostalCode("postalCode");
			final Map<String, lu.nowina.nexu.api.IdentityInfoSignatureData> signatureData = new HashMap<String, lu.nowina.nexu.api.IdentityInfoSignatureData>();
			final SignatureValue signatureValue = new SignatureValue(SignatureAlgorithm.RSA_SHA512,
					"signatureValue".getBytes(StandardCharsets.UTF_8));
			final lu.nowina.nexu.api.IdentityInfoSignatureData identityInfoSignatureData = new lu.nowina.nexu.api.IdentityInfoSignatureData(
					"rawData".getBytes(StandardCharsets.UTF_8), signatureValue,
					new CertificateToken[] { certificate, certificate, certificate });
			signatureData.put("key", identityInfoSignatureData);
			getIdentityInfoResponse.setSignatureData(signatureData);
			getIdentityInfoResponse.setSpecialStatus("specialStatus");
			final lu.nowina.nexu.api.Execution<lu.nowina.nexu.api.GetIdentityInfoResponse> respAPI = new lu.nowina.nexu.api.Execution<lu.nowina.nexu.api.GetIdentityInfoResponse>(
					getIdentityInfoResponse);
			setFeedback(respAPI);
			final String json = GsonHelper.toJson(respAPI);

			final Execution<GetIdentityInfoResponse> resp = customGson.fromJson(json,
					buildTokenType(GetIdentityInfoResponse.class).getType());
			assertSuccessExecution(resp);
			assertFeedback(resp);
			Assert.assertNotNull(resp.getResponse());
			final String certificateInBase64 = Base64.encodeBase64String(certificate.getEncoded());
			Assert.assertEquals("address", resp.getResponse().getAddress());
			Assert.assertEquals("cardDeliveryAuthority", resp.getResponse().getCardDeliveryAuthority());
			Assert.assertEquals("cardNumber", resp.getResponse().getCardNumber());
			Assert.assertEquals(LocalDate.now(), resp.getResponse().getCardValidityDateBegin());
			Assert.assertEquals(LocalDate.now().plusDays(1), resp.getResponse().getCardValidityDateEnd());
			Assert.assertEquals("chipNumber", resp.getResponse().getChipNumber());
			Assert.assertEquals("city", resp.getResponse().getCity());
			Assert.assertEquals(LocalDate.now().minusDays(1), resp.getResponse().getDateOfBirth());
			Assert.assertEquals("firstName", resp.getResponse().getFirstName());
			Assert.assertEquals(GetIdentityInfoResponse.Gender.MALE, resp.getResponse().getGender());
			Assert.assertEquals("lastName", resp.getResponse().getLastName());
			Assert.assertEquals("middleName", resp.getResponse().getMiddleName());
			Assert.assertEquals("nationality", resp.getResponse().getNationality());
			Assert.assertEquals("nationalNumber", resp.getResponse().getNationalNumber());
			Assert.assertEquals("nobleCondition", resp.getResponse().getNobleCondition());
			Assert.assertEquals("cGhvdG8=", resp.getResponse().getPhoto());
			Assert.assertEquals("photoMimeType", resp.getResponse().getPhotoMimeType());
			Assert.assertEquals("placeOfBirth", resp.getResponse().getPlaceOfBirth());
			Assert.assertEquals("postalCode", resp.getResponse().getPostalCode());
			Assert.assertNotNull(resp.getResponse().getSignatureData());
			Assert.assertEquals(1, resp.getResponse().getSignatureData().size());
			Assert.assertEquals("key", resp.getResponse().getSignatureData().keySet().iterator().next());
			Assert.assertNotNull(resp.getResponse().getSignatureData().get("key"));
			Assert.assertEquals("cmF3RGF0YQ==", resp.getResponse().getSignatureData().get("key").getRawData());
			Assert.assertArrayEquals(new String[] { certificateInBase64, certificateInBase64, certificateInBase64 },
					resp.getResponse().getSignatureData().get("key").getCertificateChain());
			Assert.assertEquals("RSA_SHA512",
					resp.getResponse().getSignatureData().get("key").getSignatureValue().getAlgorithm());
			Assert.assertEquals("c2lnbmF0dXJlVmFsdWU=",
					resp.getResponse().getSignatureData().get("key").getSignatureValue().getValue());
			Assert.assertEquals("specialStatus", resp.getResponse().getSpecialStatus());
		}
	}

	@Test
	public void testAuthenticateRequest() {
		final ToBeSigned tbs = new ToBeSigned();
		tbs.setBytes("dG8gYmUgc2lnbmVk");
		final AuthenticateRequest authenticateRequest = new AuthenticateRequest();
		setCommonRequestFields(authenticateRequest);
		authenticateRequest.setChallenge(tbs);
		final String json = GsonHelper.toJson(authenticateRequest);

		final lu.nowina.nexu.api.AuthenticateRequest authenticateRequestAPI = GsonHelper.fromJson(json,
				lu.nowina.nexu.api.AuthenticateRequest.class);
		Assert.assertNotNull(authenticateRequestAPI);
		assertCommonRequestFields(authenticateRequestAPI);
		Assert.assertNotNull(authenticateRequestAPI.getChallenge());
		Assert.assertEquals("to be signed",
				new String(authenticateRequestAPI.getChallenge().getBytes(), StandardCharsets.UTF_8));
	}

	@Test
	public void testAuthenticateResponse() {
		try (JKSSignatureToken sigToken = new JKSSignatureToken(this.getClass().getResourceAsStream("/keystore.jks"),
				new PasswordProtection("password".toCharArray()))) {
			final CertificateToken certificate = sigToken.getKeys().get(0).getCertificate();

			final lu.nowina.nexu.api.AuthenticateResponse authenticateResponse = new lu.nowina.nexu.api.AuthenticateResponse(
					"keyId", certificate, new CertificateToken[] { certificate, certificate, certificate },
					new SignatureValue(SignatureAlgorithm.RSA_SHA256, "to be signed".getBytes(StandardCharsets.UTF_8)));
			final lu.nowina.nexu.api.Execution<lu.nowina.nexu.api.AuthenticateResponse> respAPI = new lu.nowina.nexu.api.Execution<lu.nowina.nexu.api.AuthenticateResponse>(
					authenticateResponse);
			setFeedback(respAPI);
			final String json = GsonHelper.toJson(respAPI);

			final Execution<AuthenticateResponse> resp = customGson.fromJson(json,
					buildTokenType(AuthenticateResponse.class).getType());
			assertSuccessExecution(resp);
			assertFeedback(resp);
			Assert.assertNotNull(resp.getResponse());
			final String certificateInBase64 = Base64.encodeBase64String(certificate.getEncoded());
			Assert.assertEquals("keyId", resp.getResponse().getKeyId());
			Assert.assertEquals(certificateInBase64, resp.getResponse().getCertificate());
			Assert.assertArrayEquals(new String[] { certificateInBase64, certificateInBase64, certificateInBase64 },
					resp.getResponse().getCertificateChain());
			Assert.assertNotNull(resp.getResponse().getSignatureValue());
			Assert.assertEquals("RSA_SHA256", resp.getResponse().getSignatureValue().getAlgorithm());
			Assert.assertEquals("dG8gYmUgc2lnbmVk", resp.getResponse().getSignatureValue().getValue());
		}
	}

	@Test
	public void testException() {
		final lu.nowina.nexu.api.Execution<?> respAPI = new lu.nowina.nexu.api.Execution<Void>(
				BasicOperationStatus.EXCEPTION);
		setFeedback(respAPI);
		final String json = GsonHelper.toJson(respAPI);

		final Execution<Void> resp = customGson.fromJson(json, buildTokenType(Void.class).getType());
		Assert.assertFalse(resp.isSuccess());
		Assert.assertNull(resp.getResponse());
		Assert.assertEquals(BasicOperationStatus.EXCEPTION.getCode(), resp.getError());
		Assert.assertEquals(BasicOperationStatus.EXCEPTION.getLabel(), resp.getErrorMessage());
		assertFeedback(resp);
	}
}
