var app_url = "http://localhost:9095/decode";

function decode() {
	var public_key = document.getElementById("pubKey").value;
	var access_token = document.getElementById("accessToken").value;
	if (!access_token) {
		alert("Please provide access token");
		return;
	}
	var data = {
		"pubKey": public_key,
		"accessToken": access_token
	}
	$.ajax({
		beforeSend: function (request) {
			request.setRequestHeader("Content-type", "application/json; charset=UTF-8");
		},
		type: "GET",
		url: app_url,
		data: data,
		dataType: "json",
		success: successResponse,
		error: failureResponse
	});
}

function successResponse(data, status, jqXHR) {
	clearResponse();
	window.onerror = function (e) {
		alert("Invalid Access Token");
		return;
	};
	if (data["message"]) {
		alert(data["message"]);
		return;
	}
	var payload = JSON.parse(data["payload"]);
	document.getElementById("header").innerHTML = JSON.stringify(JSON.parse(data["header"]), undefined, 2);
	document.getElementById("payload").innerHTML = JSON.stringify(JSON.parse(data["payload"]), undefined, 2);
	document.getElementById("exp").innerHTML = new Date(payload["exp"]*1000);
}

function failureResponse(data, status, jqXHR) {
	alert(data);
}


function enablePublicKeyTextArea(pubKeyToggleBtn){
	var pubKeyTextArea = document.getElementById("pubKey");
	if (pubKeyToggleBtn.value == "yes") {
		pubKeyTextArea.removeAttribute("disabled");
	} else {
		pubKeyTextArea.setAttribute("disabled", "disabled");
		document.getElementById("pubKey").value = "";
	}
}

function clearResponse() {
	document.getElementById("header").innerHTML = "";
	document.getElementById("payload").innerHTML = "";
	document.getElementById("exp").innerHTML = "";
}

function clearAll() {
	document.getElementById("accessToken").value = "";
	document.getElementById("pubKey").value = "";
	document.getElementById("header").innerHTML = "";
	document.getElementById("payload").innerHTML = "";
	document.getElementById("exp").innerHTML = "";
}