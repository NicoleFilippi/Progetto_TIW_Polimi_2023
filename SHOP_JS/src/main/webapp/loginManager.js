{ // avoid variables ending up in the global scope
  // page components
  let messageForm = document.getElementById("login_form" );
  let submitButton = messageForm.querySelector("input[type='button']");
  let alertContainer = document.getElementById("alert");
  
	 window.addEventListener("load", function() {
	  submitButton.addEventListener("click", function(e) { // called when one clicks the button
	      let form = e.target.closest("form"); // example of DOM navigation from event object
	      if (form.checkValidity()) {
	        e.preventDefault();
	        makeCall("POST", "CheckLogin", form,
	          function(req) { // callback of the POST HTTP request
	            if (req.readyState === 4) { // response has arrived
	              let message = req.responseText; // get the body of the response
	              if (req.status === 200) { // if no errors
                window.location.href="home.html";
              } else {
                alertContainer.textContent = message; // report the error contained in the response body
              }
          }
          }
	        );
	      } else {
	        alertContainer.textContent = "no";
	      }
	    });
	  });
}; // end of main block
