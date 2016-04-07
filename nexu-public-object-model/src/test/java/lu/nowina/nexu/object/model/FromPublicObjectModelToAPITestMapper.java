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

import lu.nowina.nexu.object.model.GetIdentityInfoResponse.Gender;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * This test <code>MapStruct</code> mapper is used only to ease the maintenance
 * of the public object model of NexU.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
@Mapper(uses={UtilMappers.class})
public interface FromPublicObjectModelToAPITestMapper {

	// Get certificate
	lu.nowina.nexu.api.GetCertificateRequest mapGetCertificateRequest(GetCertificateRequest req);
	lu.nowina.nexu.api.GetCertificateResponse mapGetCertificateResponse(GetCertificateResponse resp);
	lu.nowina.nexu.api.Execution<lu.nowina.nexu.api.GetCertificateResponse> mapGetCertificateResponse(Execution<GetCertificateResponse> resp, @MappingTarget lu.nowina.nexu.api.Execution<lu.nowina.nexu.api.GetCertificateResponse> to);

	// Sign
	lu.nowina.nexu.api.SignatureRequest mapSignatureRequest(SignatureRequest req);
	lu.nowina.nexu.api.SignatureResponse mapSignatureResponse(SignatureResponse resp, @MappingTarget lu.nowina.nexu.api.SignatureResponse to);
	lu.nowina.nexu.api.Execution<lu.nowina.nexu.api.SignatureResponse> mapSignatureResponse(Execution<SignatureResponse> resp, @MappingTarget lu.nowina.nexu.api.Execution<lu.nowina.nexu.api.SignatureResponse> to);

	// Get Identity Info
	lu.nowina.nexu.api.GetIdentityInfoRequest mapGetIdentityInfoRequest(GetIdentityInfoRequest req);
	lu.nowina.nexu.api.GetIdentityInfoResponse mapGetIdentityInfoResponse(GetIdentityInfoResponse resp);
	lu.nowina.nexu.api.Execution<lu.nowina.nexu.api.GetIdentityInfoResponse> mapGetIdentityInfoResponse(Execution<GetIdentityInfoResponse> resp, @MappingTarget lu.nowina.nexu.api.Execution<lu.nowina.nexu.api.GetIdentityInfoResponse> to);

	// Authenticate
	lu.nowina.nexu.api.AuthenticateRequest mapAuthenticateRequest(AuthenticateRequest req);
	lu.nowina.nexu.api.AuthenticateResponse mapAuthenticateResponse(AuthenticateResponse resp, @MappingTarget lu.nowina.nexu.api.AuthenticateResponse to);
	lu.nowina.nexu.api.Execution<lu.nowina.nexu.api.AuthenticateResponse> mapAuthenticateResponse(Execution<AuthenticateResponse> resp, @MappingTarget lu.nowina.nexu.api.Execution<lu.nowina.nexu.api.AuthenticateResponse> to);
	
	// Util
	lu.nowina.nexu.api.CertificateFilter mapCertificateFilter(CertificateFilter certificateFilter);
	@Mapping(target="apiParameter", ignore=true)
	@Mapping(target="detected", ignore=true)
	@Mapping(target="selectedAPI", ignore=true)
	@Mapping(target="selectedCard", ignore=true)
	@Mapping(target="exception", ignore=true)
	lu.nowina.nexu.api.Feedback mapFeedback(Feedback feedback);
	lu.nowina.nexu.api.TokenId mapTokenId(TokenId tokenId);
	eu.europa.esig.dss.ToBeSigned mapToBeSigned(ToBeSigned toBeSigned);
	lu.nowina.nexu.api.FeedbackStatus mapFeedbackStatus(FeedbackStatus feedbackStatus);
	lu.nowina.nexu.api.EnvironmentInfo mapEnvironmentInfo(EnvironmentInfo environmentInfo);
	lu.nowina.nexu.api.JREVendor mapJREVendor(JREVendor jreVendor);
	lu.nowina.nexu.api.Arch mapArch(Arch arch);
	lu.nowina.nexu.api.OS mapOS(OS os);
	lu.nowina.nexu.api.Purpose mapPurpose(Purpose purpose);
	lu.nowina.nexu.api.GetIdentityInfoResponse.Gender mapGender(Gender gender);
	eu.europa.esig.dss.SignatureValue mapSignatureValue(SignatureValue signatureValue);
	lu.nowina.nexu.api.IdentityInfoSignatureData mapIndentityInfoSignatureData(IdentityInfoSignatureData iisd);
	Map<String, lu.nowina.nexu.api.IdentityInfoSignatureData> mapIndentityInfoSignatureData(Map<String, IdentityInfoSignatureData> map);
}
