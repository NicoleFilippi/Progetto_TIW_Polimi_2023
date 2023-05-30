/**
 * 
 */

function emptyAll(){	
	document.getElementById("home_button").style.display="block";
	document.getElementById("cart_button").style.display="block";
	document.getElementById("orders_button").style.display="block";
	
	document.getElementById("home_elements").style.display="none";
	document.getElementById("details_elements").style.display="none";
	document.getElementById("results_elements").style.display="none";
	document.getElementById("cart_elements").style.display="none";
	document.getElementById("orders_elements").style.display="none";
	
	let homeTab = document.getElementById("home_table");
	let detailsTab = document.getElementById("details_table");
	let sellerTab = document.getElementById("supplier_table");
	let resultsTab = document.getElementById("results_table");
	let cartTabs = document.getElementById("cart_tables");
	let ordersTabs = document.getElementById("orders_tables");
	
	while(homeTab.rows.length>0){
		homeTab.removeChild(homeTab.firstChild);
	}
	
	while(detailsTab.rows.length>1){
		detailsTab.removeChild(detailsTab.rows.item(1));
	}
	
	while(sellerTab.rows.length>1){
		sellerTab.removeChild(sellerTab.rows.item(1));
	}
	
	while(resultsTab.rows.length>0){
		resultsTab.removeChild(resultsTab.firstChild);
	}
	
	while(cartTabs.childElementCount>0){
		cartTabs.removeChild(cartTabs.firstChild);
	}
	
	while(ordersTabs.childElementCount>0){
		ordersTabs.removeChild(ordersTabs.firstChild);
	}
}


function loadHome(){
	emptyAll();
	let user = JSON.parse(sessionStorage.getItem("user"));
	
	document.getElementById("home_button").style.display="none"; 
	 
	document.getElementById("home_elements").style.display="block";
	 
	document.getElementById("nickname_text").textContent = "Welcome back "+user.name+" "+user.surname+"!";
	if(sessionStorage.getItem("lastVisualized")==undefined){
		makeCall("GET", "GetLastVisualized", null, function(req){
			if (req.readyState == XMLHttpRequest.DONE) {
				let response = req.responseText;
				if (req.status === 200) {							
					sessionStorage.setItem("lastVisualized", response);							
					loadLastVisualized(JSON.parse(sessionStorage.getItem("lastVisualized")));
        		} else {
           			//TODO HANDLE ERROR
          		}
      		}
		 });
	} else {
		 loadLastVisualized(JSON.parse(sessionStorage.getItem("lastVisualized")));
	}
};
 
function loadLastVisualized(array){
	let table = document.getElementById("home_table");
	for(let i=0; i<5; i++){
		let row = document.createElement("tr");
	    let colId = document.createElement("td");
	    colId.textContent = array[i].id;
	    row.appendChild(colId);
	    let colImage = document.createElement("td");
	    let image=document.createElement("img");
	    image.src=array[i].photoPath;
	    image.width=80;
	    image.height=80;
	    row.appendChild(colImage);
	    colImage.appendChild(image);
	    let colName = document.createElement("td");
	    colName.textContent = array[i].name;
	    row.appendChild(colName);
	    let colMinPrice = document.createElement("td");
	    colMinPrice.textContent = array[i].minPrice;
	    row.appendChild(colMinPrice);
		//TODO details button
		table.appendChild(row);
	}
}

function loadResults(array, keyword){
	emptyAll();
	 
	document.getElementById("results_elements").style.display="block"; 
	
	if(array.length==0){
		document.getElementById("results_text").textContent = "No results for the keyword: "+ keyword;
		document.getElementById("results_table").style.display="none";
	} else {
		document.getElementById("results_text").textContent = "Here are the results for the keyword: "+ keyword;
		let table = document.getElementById("results_table");
		table.style.display="block";
		for(let i=0; i<array.length; i++){
			let row = document.createElement("tr");
		    let colId = document.createElement("td");
		    colId.textContent = array[i].id;
		    row.appendChild(colId);
		    let colImage = document.createElement("td");
		    let image=document.createElement("img");
		    image.src=array[i].photoPath;
		    image.width=80;
		    image.height=80;
		    row.appendChild(colImage);
		    colImage.appendChild(image);
		    let colName = document.createElement("td");
		    colName.textContent = array[i].name;
		    row.appendChild(colName);
		    let colMinPrice = document.createElement("td");
		    colMinPrice.textContent = array[i].minPrice;
		    row.appendChild(colMinPrice);
			let colButton = document.createElement("td");
			let detailsButton = document.createElement("button");
			detailsButton.innerHTML="Details";
			detailsButton.addEventListener("click", function(e){
				makeCall("GET", "GetDetails?id="+array[i].id, null, function(req){
					if (req.readyState == XMLHttpRequest.DONE) {
	            		let response = req.responseText;
		        		if (req.status === 200) {							
							document.getElementById("details_elements").style.display="block";			
							
							let ps = JSON.parse(response);
							let product = ps[0].product;
							
							let table = document.getElementById("details_table");
							
							while(table.rows.length>1){
								table.removeChild(table.rows.item(1));
							}
							
							let sellerTab = document.getElementById("supplier_table");
							while(sellerTab.rows.length>1){
								sellerTab.removeChild(sellerTab.rows.item(1));
							}
							
							let row = document.createElement("tr");
						    let colId = document.createElement("td");
						    colId.textContent = product.id;
						    row.appendChild(colId);
						    let colImage = document.createElement("td");
						    let image=document.createElement("img");
						    image.src=product.photoPath;
						    image.width=80;
						    image.height=80;
						    row.appendChild(colImage);
						    colImage.appendChild(image);
						    let colName = document.createElement("td");
						    colName.textContent = product.name;
						    row.appendChild(colName);
						    let colCategory = document.createElement("td");
						    colCategory.textContent = product.category;
						    row.appendChild(colCategory);
						    let colDescription = document.createElement("td");
						    colDescription.textContent = product.description;
						    row.appendChild(colDescription);
							table.appendChild(row);
							
							let suppTable = document.getElementById("supplier_table");
							for(let i=0; i<ps.length; i++){
								let row = document.createElement("tr");
							    let colName = document.createElement("td");
							    colName.textContent = ps[i].supplier.name;
							    row.appendChild(colName);
							    let colPrice = document.createElement("td");
								colPrice.textContent = ps[i].price;
							    row.appendChild(colPrice);
							    let colRating = document.createElement("td");
							    colRating.textContent = ps[i].supplier.rating;
							    row.appendChild(colRating);
							    let colFST = document.createElement("td");
							    colFST.textContent = ps[i].supplier.freeShippingThreshold;
							    row.appendChild(colFST);
							    let colSPR = document.createElement("td");
							    let ulSPR = document.createElement("ul");
							    for(let j=0; j<ps[i].supplier.minQuantities.length; j++){
									let liSPR = document.createElement("li");
									liSPR.textContent = ps[i].supplier.minQuantities[j] + ' products: ' + ps[i].supplier.shippingPrices[ps[i].supplier.minQuantities[j]]+' $';
									ulSPR.appendChild(liSPR);
								}
								colSPR.appendChild(ulSPR);
								row.appendChild(colSPR);
								let colItemsNum = document.createElement("td");
								colItemsNum.textContent = cart.getProductsNumber(ps[i].supplier);
								row.appendChild(colItemsNum);
								let colItemsCost = document.createElement("td");
								colItemsCost.textContent = cart.getProductsCost(ps[i].supplier);
								row.appendChild(colItemsCost);							
								
								suppTable.appendChild(row);
							}
							let resTable = document.getElementById("results_table");
							for(let i=0; i<resTable.rows.length; i++){
								if(resTable.rows.item(i).cells.item(0).textContent==product.id)
									resTable.rows.item(i).style.display="none";
								else
									resTable.rows.item(i).style.display="block";
							}
							
		        		} else {
		           			//TODO HANDLE ERROR
		          		}
	      			}
				});
			});
			colButton.appendChild(detailsButton);
			row.appendChild(colButton);
			table.appendChild(row);
		}
	}
} 


function loadDetails(product){
	emptyAll();
	 
	document.getElementById("details_elements").style.display="block";
	
	if(product==undefined){
		//TODO handle error
	} else {
		let table = document.getElementById("details_table");
		
		let row = document.createElement("tr");
	    let colId = document.createElement("td");
	    colId.textContent = product.id;
	    row.appendChild(colId);
	    let colImage = document.createElement("td");
	    let image=document.createElement("img");
	    image.src=product.photoPath;
	    image.width=80;
	    image.height=80;
	    row.appendChild(colImage);
	    colImage.appendChild(image);
	    let colName = document.createElement("td");
	    colName.textContent = product.name;
	    row.appendChild(colName);
	    let colCategory = document.createElement("td");
	    colCategory.textContent = product.category;
	    row.appendChild(colCategory);
	    let colDescription = document.createElement("td");
	    colDescription.textContent = product.description
	    row.appendChild(colDescription);
		table.appendChild(row);
	}	 
}