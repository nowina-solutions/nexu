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

function nexu_get_certificates(success_callback, error_callback) {
	transmitRequest("certificates", {}, success_callback, error_callback);
}

/* function to use if we already know a certificate and its tokenId/keyId */
function nexu_sign_with_token_infos(tokenId, keyId, dataToSign, digestAlgo, success_callback, error_callback) {
	var data = '{ "tokenId":{"id":"' + tokenId + '"}, "keyId":"' + keyId + '", "toBeSigned": { "bytes": "' + dataToSign + '" } , "digestAlgorithm":"' + digestAlgo + '"}';
	transmitRequest("sign", data, success_callback, error_callback);
}

/* function to use without tokenId/keyId */
function nexu_sign(dataToSign, digestAlgo, success_callback, error_callback) {
	var data = { dataToSign:dataToSign, digestAlgo:digestAlgo };
	transmitRequest("sign", data, success_callback, error_callback);
}

function nexu_get_identity_info(success_callback, error_callback) {
	transmitRequest("identityInfo", {}, success_callback, error_callback);
}

function transmitRequest(service, data, success_callback, error_callback) {
	callUrl("${scheme}://${nexu_hostname}:${nexu_port}/rest/" + service, "POST", data, success_callback, error_callback);
}

function callUrl(url, type, data, success_callback, error_callback) {
	$.ajax({
		  type: type,
		  url: url,
		  data: data,
		  crossDomain: true, 
		  contentType: "application/json; charset=utf-8",
		  dataType: "json",
		  success: function (result) {
			  console.log(url + " : OK");
			  success_callback.call(this, result);
		  }
		}).fail(function (error) {
			console.log(url + " : KO");
			error_callback.call(this, error);
		});
} 
