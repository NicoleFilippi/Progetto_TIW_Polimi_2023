/**
 * manda parametri login al server e gestisce la risposta
 */

{
	let loginForm = document.getElementById("login_form");
	let button = loginForm.querySelector("input[type='button']");
	let alertContainer = document.getElementById("alert");
 	window.addEventListener("load", function() {
		button.addEventListener("click", function(e) {
			if (loginForm.checkValidity()) {
				e.preventDefault();
				makeCall("POST", "CheckLogin", loginForm, function(req) {
	            	if (req.readyState == XMLHttpRequest.DONE) {
	            		let response = req.responseText;
		        		if (req.status === 200) {							
							sessionStorage.setItem("user", response);							
		           			window.location.href="home.html";
		        		} else {
		           			alertContainer.textContent = response;
		          		}
	      			}
	      		});
    		} else {
        		loginForm.reportValidity();
      		}
    	});
	});
};
