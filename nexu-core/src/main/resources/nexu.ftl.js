var nexuVersion = "1.0";

function nexu_get_certificates(success_callback, error_callback) {
	callUrl("${nexuUrl}/rest/certificates", "GET", {}, success_callback, error_callback);
}

/* function to use if we already know a certificate and its tokenId/keyId */
function nexu_sign_with_token_infos(tokenId, keyId, dataToSign, digestAlgo, success_callback, error_callback) {
	var data = { tokenId:tokenId, keyId:keyId, dataToSign:dataToSign, digestAlgo:digestAlgo };
	callUrl("${nexuUrl}/rest/sign", "GET", data, success_callback, error_callback);
}

/* function to use without tokenId/keyId */
function nexu_sign(dataToSign, digestAlgo, success_callback, error_callback) {
	var data = { dataToSign:dataToSign, digestAlgo:digestAlgo };
	callUrl("${nexuUrl}/rest/sign", "GET", data, success_callback, error_callback);
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
			eval(error);
			error_callback.call(this, error);
		});
} 

callUrl("${nexuUrl}/nexu-info", "GET", {}, function(data) {
	// something responded
	console.log("server: " + data.version + " - expected: " + nexuVersion);
	if(data.version == nexuVersion) {
		// valid version
		$(".nexu-sign-button").html("Sign");
		// add hidden input to be able to check on server side
    	if ($('#nexu').length){
   	    	var hiddenInput = $('<input/>',{type:'hidden',name:'nexuDetected',value:'true'});
   	    	hiddenInput.appendTo($('#nexu'));
   	    }
	} else {
		// need update
		$(".nexu-sign-button").html("Update NexU");
		$(".nexu-sign-button").on("click", function() {
			console.log("Update NexU");
			return false;
		});
		
	}
}, function(data) {
	// no response, NexU not installed or not started
	$(".nexu-sign-button").html("Install NexU");
	$(".nexu-sign-button").on("click", function() {
		console.log("Install NexU");
		window.location = "${baseUrl}/";
		return false;
	});
});