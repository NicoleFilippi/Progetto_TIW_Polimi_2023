/**
 * 
 */

{
	let searchForm = document.getElementById("search_form");
	let homeSearchButton = document.getElementById("home_submit");
	let homeButton = document.getElementById("home_button");
	let cartButton = document.getElementById("cart_button");
	let ordersButton = document.getElementById("orders_button");
	var cart = new Cart();
	
 	window.addEventListener("load", function() {
		homeSearchButton.addEventListener("click", function(e) {
			if (searchForm.checkValidity()) {
				e.preventDefault();
				makeCall("GET", "GetResults?keyword=" + searchForm.keyword.value, null, function(req) {
	            	if (req.readyState == XMLHttpRequest.DONE) {
	            		let response = req.responseText;
		        		if (req.status === 200) {							
							loadResults(JSON.parse(response), searchForm.keyword.value);
		        		} else {
		           			//TODO HANDLE ERROR
		          		}
	      			}
	      		});
    		} else {
        		searchForm.reportValidity();
      		}
    	});
    	
    	homeButton.addEventListener("click", function(e) {
			loadHome();
    	});
    	
    	cartButton.addEventListener("click", function(e) {
			loadCart();
    	});
    	
    	ordersButton.addEventListener("click", function(e) {
			loadOrders();
    	});
    	
    	loadHome();
	});
};
