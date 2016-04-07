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

import java.util.Map;

import org.mapstruct.Mapper;

import lu.nowina.nexu.object.model.GetIdentityInfoResponse.Gender;

/**
 * This test <code>MapStruct</code> mapper is used only to ease the maintenance
 * of the public object model of NexU.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
@Mapper(uses={UtilMappers.class})
public interface FromAPIToPublicObjectModelTestMapper {

	// Get certificate
	GetCertificateRequest mapGetCertificateRequest(lu.nowina.nexu.api.GetCertificateRequest req);
	GetCertificateResponse mapGetCertificateResponse(lu.nowina.nexu.api.GetCertificateResponse resp);
	Execution<GetCertificateResponse> mapGetCertificateResponse(lu.nowina.nexu.api.Execution<lu.nowina.nexu.api.GetCertificateResponse> resp);

	// Sign
	SignatureRequest mapSignatureRequest(lu.nowina.nexu.api.SignatureRequest req);
	SignatureResponse mapSignatureResponse(lu.nowina.nexu.api.SignatureResponse resp);
	Execution<SignatureResponse> mapSignatureResponse(lu.nowina.nexu.api.Execution<lu.nowina.nexu.api.SignatureResponse> resp);

	// Get Identity Info
	GetIdentityInfoRequest mapGetIdentityInfoRequest(lu.nowina.nexu.api.GetIdentityInfoRequest req);
	GetIdentityInfoResponse mapGetIdentityInfoResponse(lu.nowina.nexu.api.GetIdentityInfoResponse resp);
	Execution<GetIdentityInfoResponse> mapGetIdentityInfoResponse(lu.nowina.nexu.api.Execution<lu.nowina.nexu.api.GetIdentityInfoResponse> resp);

	// Authenticate
	AuthenticateRequest mapAuthenticateRequest(lu.nowina.nexu.api.AuthenticateRequest req);
	AuthenticateResponse mapAuthenticateResponse(lu.nowina.nexu.api.AuthenticateResponse resp);
	Execution<AuthenticateResponse> mapAuthenticateResponse(lu.nowina.nexu.api.Execution<lu.nowina.nexu.api.AuthenticateResponse> resp);
	
	// Util
	CertificateFilter mapCertificateFilter(lu.nowina.nexu.api.CertificateFilter certificateFilter);
	Feedback mapFeedback(lu.nowina.nexu.api.Feedback feedback);
	TokenId mapTokenId(lu.nowina.nexu.api.TokenId tokenId);
	ToBeSigned mapToBeSigned(eu.europa.esig.dss.ToBeSigned toBeSigned);
	FeedbackStatus mapFeedbackStatus(lu.nowina.nexu.api.FeedbackStatus feedbackStatus);
	EnvironmentInfo mapEnvironmentInfo(lu.nowina.nexu.api.EnvironmentInfo environmentInfo);
	JREVendor mapJREVendor(lu.nowina.nexu.api.JREVendor jreVendor);
	Arch mapArch(lu.nowina.nexu.api.Arch arch);
	OS mapOS(lu.nowina.nexu.api.OS os);
	Purpose mapPurpose(lu.nowina.nexu.api.Purpose purpose);
	Gender mapGender(lu.nowina.nexu.api.GetIdentityInfoResponse.Gender gender);
	SignatureValue mapSignatureValue(eu.europa.esig.dss.SignatureValue signatureValue);
	IdentityInfoSignatureData mapIndentityInfoSignatureData(lu.nowina.nexu.api.IdentityInfoSignatureData iisd);
	Map<String, IdentityInfoSignatureData> mapIndentityInfoSignatureData(Map<String, lu.nowina.nexu.api.IdentityInfoSignatureData> map);
}
