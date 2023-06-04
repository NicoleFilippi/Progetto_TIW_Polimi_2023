/**
 *	metodo che crea richiesta http da mandare ad una servlet (url) con metodo method
 */

function makeCall(method, url, formElement, cback, reset = false) {
	let req = new XMLHttpRequest();
	req.onreadystatechange = function() {
 		cback(req);
 	};
  	req.open(method, url);
  	if (formElement === null) {
		req.send();
  	} else {
    	req.send(new FormData(formElement));
  	}
  	if (formElement !== null && reset === true) {
    	formElement.reset();
  	}
}
