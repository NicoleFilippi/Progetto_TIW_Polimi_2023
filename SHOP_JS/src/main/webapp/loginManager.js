{
	let button = messageForm.querySelector("input[type='button']");
	let alertContainer = document.getElementById("alert");
 	window.addEventListener("load", function() {
		button.addEventListener("click", function(e) {
			let form = e.target.closest("form");
			if (form.checkValidity()) {
				e.preventDefault();
				makeCall("POST", "CheckLogin", form, function(req) {
	            	if (req.readyState === 4) {
	            		let message = req.responseText;
		        		if (req.status === 200) {
		           			window.location.href="home.html";
		        		} else {
		           			alertContainer.textContent = message;
		          		}
	      			}
	      		});
    		} else {
        		alertContainer.textContent = "not valid input";
      		}
    	});
	});
};
