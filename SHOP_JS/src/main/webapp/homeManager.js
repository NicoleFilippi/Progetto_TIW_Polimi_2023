/**
 * COMPONENTS
 */

 function HomeScreen(orch){
	this.orchestrator = orch; 
	
	this.homeButton = document.getElementById("home_button");
	this.accountButton = document.getElementById("account_button");
	this.elements = document.getElementById("home_elements");
	this.table = document.getElementById("home_table");
	this.nicknameText = document.getElementById("nickname_text");
	this.lastVisualized = null;
	
	this.searchButton = document.getElementById("home_submit");
	
	let self = this;
	document.getElementById("search_form").addEventListener("submit", function(e) {
		let searchForm = document.getElementById("search_form");
		e.preventDefault();
		if (searchForm.checkValidity()) {
			let keyword = searchForm.keyword.value;			
			makeCall("GET", "GetResults?keyword=" + keyword, null, function(req) {
            	if (req.readyState == XMLHttpRequest.DONE) {
            		let response = req.responseText;
	        		if (req.status === 200) {
						let resultSet = JSON.parse(response);
						searchForm.reset();
						self.orchestrator.goToResults(resultSet,keyword);
	        		} else {
	           			self.orchestrator.handleError(req.status,response);
	          		}
      			}
      		});
		} else {
    		searchForm.reportValidity();
  		}
	});
	
	this.homeButton.addEventListener("click", function(e) {
		self.orchestrator.goToHome();
	});
	
	this.accountButton.addEventListener("click", function(e) {
		self.orchestrator.goToAccount();
	});
	
	this.loadLastVisualized = function(array){
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
		    colMinPrice.textContent = array[i].minPrice + "$";
		    row.appendChild(colMinPrice);
			let colDetails = document.createElement("td");
			let buttonDetails = document.createElement("button");
			buttonDetails.innerHTML = "Details";
			buttonDetails.addEventListener("click", function(e){					
				makeCall("GET", "GetDetails?id="+array[i].id, null, function(req){
					if (req.readyState == XMLHttpRequest.DONE) {
	            		let response = req.responseText;
		        		if (req.status === 200) {					
							let ps = JSON.parse(response);
							self.orchestrator.goToDetails(ps);
		        		} else {
							self.orchestrator.handleError(req.status,response);
		          		}
	      			}
      			});
			});
			colDetails.appendChild(buttonDetails);
			row.appendChild(colDetails);
			table.appendChild(row);
		}
	}
	
	this.updateVisualized = function(product){
		if(product != null){
			let found = 4;
			for(let j=0; j<5; j++){
				if(this.lastVisualized[j].id == product.id)
					found=j;
			}
			let tmp1 = product;
			let tmp2 = null;
			for(let i=0; i<=found; i++){
				tmp2 = this.lastVisualized[i];
				this.lastVisualized[i] = tmp1;
				tmp1 = tmp2;		
			}
		}			
	}
	
	this.load = function(){
		
		if(sessionStorage.getItem("user")==null){
			
			let req = new XMLHttpRequest();
			req.open("GET", "GetUser",false);
			req.onreadystatechange = function(){
				if(req.readyState == XMLHttpRequest.DONE){
					let response = req.responseText;
					if (req.status === 200) {							
						sessionStorage.setItem("user", response);
						sessionStorage.setItem("cart", JSON.stringify(new Cart()));
	        		} else {
	           			self.orchestrator.handleError(req.status,response);
	          		}
				}
			};
			req.send();

		}
		
		let user = JSON.parse(sessionStorage.getItem("user"));
		let self = this;
		this.homeButton.hidden = true;
		this.accountButton.hidden = false;
		this.elements.hidden = false;
		this.nicknameText.textContent = "Welcome back "+user.name+" "+user.surname+"!";
		if(this.lastVisualized==null){
			makeCall("GET", "GetLastVisualized", null, function(req){
				if (req.readyState == XMLHttpRequest.DONE) {
					let response = req.responseText;
					if (req.status === 200) {							
						self.lastVisualized = JSON.parse(response);
						self.loadLastVisualized(self.lastVisualized);
	        		} else {
	           			self.orchestrator.handleError(req.status,response);
	          		}
	      		}
			 });
		} else {
			 this.loadLastVisualized(this.lastVisualized);
		}
	}
	
	this.clear = function(){
		this.homeButton.hidden = false;
		this.elements.hidden = true;
		this.accountButton.hidden = true;
		this.nicknameText.textContent = "";
		while(this.table.rows.length>0){
			this.table.removeChild(this.table.rows.item(0));
		}
	}
	
	this.clear();
}

function AccountScreen(orch){
	this.orchestrator = orch;
	
	this.elements = document.getElementById("account_elements");
	this.text = document.getElementById("account_text");
	this.form = document.getElementById("account_form");
	this.logout = document.getElementById("account_logout_button");
	this.states = null;
	
	let self = this;
	this.form.addEventListener("submit", function(e){
		e.preventDefault();
		makeCall("POST", "UpdateUser", self.form, function(req){
			if (req.readyState == XMLHttpRequest.DONE) {
				let response = req.responseText;
				if (req.status === 200) {							
					if(response!=undefined && response!="") sessionStorage.setItem("user",response);
					self.orchestrator.goToHome();
        		} else {
           			self.orchestrator.handleError(req.status,response);
          		}
      		}
		});
	});
	
	this.logout.addEventListener("click", function(e){
		makeCall("POST", "Logout", null, function(req){
			if (req.readyState == XMLHttpRequest.DONE) {
				let response = req.responseText;
				if (req.status === 200) {							
						sessionStorage.removeItem("user");
						sessionStorage.removeItem("cart");
						window.location.href = "index.html";
        		} else {
           			self.orchestrator.handleError(req.status,response);
          		}
      		}
		});
	});
	
	this.loadStates = function(array,userState){
		for(let i=0; i<array.length; i++){
			let opt = document.createElement("option");
			opt.value = array[i].iso3;
			opt.text = array[i].name;
			if(array[i].iso3 == userState) opt.selected = true;
			this.form.state.appendChild(opt);
		}
	}
	
	this.load = function(){
		let user = JSON.parse(sessionStorage.getItem("user"));
		this.elements.hidden = false;
		this.form.reset();
		this.text.innerHTML = user.email;
		this.form.name.value = user.name;
		this.form.surname.value = user.surname;
		this.form.street.value = user.street;
		this.form.civicNumber.value = user.civicNumber;
		this.form.city.value = user.city;
		if(this.states==null){
			makeCall("GET", "GetStates", null, function(req){
				if (req.readyState == XMLHttpRequest.DONE) {
					let response = req.responseText;
					if (req.status === 200) {							
						self.states  = JSON.parse(response);
						self.loadStates(self.states, user.state);
	        		} else {
	           			self.orchestrator.handleError(req.status,response);
	          		}
	      		}
			});
		}else{
			this.loadStates(this.states, user.state);
		}
	}
	
	this.clear = function(){
		this.elements.hidden = true;
		this.text.innerHTML = "";
		this.text.innerHTML = "";
		this.form.name.value = "";
		this.form.surname.value = "";
		this.form.street.value = "";
		this.form.civicNumber.value = "";
		this.form.city.value = "";
		while(this.form.state.childElementCount > 0){
			this.form.state.removeChild(this.form.state.firstChild)
		}
	}
	
	this.clear();
}

function ResultsScreen(orch){
	this.orchestrator = orch; 
	
	this.resultElements = document.getElementById("results_elements");
	this.resultText = document.getElementById("results_text");
	this.resultTable = document.getElementById("results_table");
		
	this.currProductId = -1;
	this.resultsList = null;
	this.productSuppliersList = null;
	
	this.load = function(array,keyword){
		
		this.resultsList = array;
		this.resultElements.hidden = false;
		
		if(array.length==0){
			this.resultText.textContent = "No results for the keyword: "+ keyword;
			this.resultTable.hidden = true;
		} else {
			let self = this;
			this.resultText.textContent = "Here are the results for the keyword: "+ keyword;
			this.resultTable.hidden = false;
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
			    colMinPrice.textContent = array[i].minPrice + "$";
			    row.appendChild(colMinPrice);
				let colButton = document.createElement("td");
				let detailsButton = document.createElement("button");
				detailsButton.innerHTML="Details";
				detailsButton.addEventListener("click", function(e){
					
					makeCall("GET", "GetDetails?id="+array[i].id, null, function(req){
						if (req.readyState == XMLHttpRequest.DONE) {
		            		let response = req.responseText;
			        		if (req.status === 200) {
								let ps = JSON.parse(response);
								self.orchestrator.detailsPage.load(ps);
								let product = ps[0].product;
								self.currProductId = product.id;
								//Hide the current item
								for(let i=0; i<self.resultsList.length; i++){
									if(self.resultsList[i].id == self.currProductId) self.resultTable.rows.item(i).hidden = true;
									else self.resultTable.rows.item(i).hidden = false;
								}
								
			        		} else {
			           			self.orchestrator.handleError(req.status,response);
			          		}
		      			}
					});
				});
				colButton.appendChild(detailsButton);
				row.appendChild(colButton);
				this.resultTable.appendChild(row);
			}
		}
	}
	
	this.clear = function(){		
		while(this.resultTable.rows.length>0){
			this.resultTable.removeChild(this.resultTable.rows.item(0));
		}
		this.resultElements.hidden = true;
		this.resultText.textContent = "";
		this.orchestrator.detailsPage.clear();
	}
	
	this.clear();
}

function DetailsScreen(orch){
	this.orchestrator = orch;

	this.detailsElements = document.getElementById("details_elements");
	this.detailsTable = document.getElementById("details_table");
	this.supplierTable = document.getElementById("supplier_table");
	this.popup = document.getElementById("popup_area");
	this.popupTable = document.getElementById("popup_table");
	
	let self = this;
	this.load = function(ps){
		self.popup.hidden = true;
		self.detailsElements.hidden = false;
		self.productSuppliersList = ps;	
		let product = ps[0].product;
		self.orchestrator.homePage.updateVisualized(product);
		
		while(self.detailsTable.rows.length>1){
			self.detailsTable.removeChild(self.detailsTable.rows.item(1));
		}
		
		while(self.supplierTable.rows.length>1){
			self.supplierTable.removeChild(self.supplierTable.rows.item(1));
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
		self.detailsTable.appendChild(row);
	
		for(let i=0; i<ps.length; i++){
			let row = document.createElement("tr");
		    let colName = document.createElement("td");
		    colName.textContent = ps[i].supplier.name;
		    colName.className="column";
		    row.appendChild(colName);
		    let colPrice = document.createElement("td");
			colPrice.textContent = ps[i].price + "$";
			colPrice.className="column";
		    row.appendChild(colPrice);
		    let colRating = document.createElement("td");
		    colRating.textContent = ps[i].supplier.rating+"/5";
		    colRating.className="column";
		    row.appendChild(colRating);
		    let colFST = document.createElement("td");
		    if(ps[i].supplier.freeShippingThreshold>=0)
		    	colFST.textContent = ps[i].supplier.freeShippingThreshold+"$";
		    else
		    	colFST.textContent = "N.D.";
		    colFST.className="column";
		    row.appendChild(colFST);
		    let colSPR = document.createElement("td");
		    let ulSPR = document.createElement("ul");
		    for(let j=0; j<ps[i].supplier.minQuantities.length; j++){
				let liSPR = document.createElement("li");
				liSPR.textContent = ps[i].supplier.minQuantities[j] + ' products: ' + ps[i].supplier.shippingPrices[ps[i].supplier.minQuantities[j]]+' $';
				ulSPR.appendChild(liSPR);
			}
			colSPR.className="column";
			colSPR.appendChild(ulSPR);
			row.appendChild(colSPR);
			let colItemsNum = document.createElement("td");
			colItemsNum.textContent = getProductsNumberFromCart(ps[i].supplier);
			colItemsNum.addEventListener("mouseenter", function(e){
				if(getCartItems()[ps[i].supplier.id]!=undefined){
					for(let j=0; j<getCartItems()[ps[i].supplier.id].length; j++){
						let popupPs = getCartItems()[ps[i].supplier.id][j]; 
						let popupRow = document.createElement("tr");
						let popupColId = document.createElement("td");
					    popupColId.textContent = popupPs.product.id;
					    popupRow.appendChild(popupColId);
					    let popupColName = document.createElement("td");
					    popupColName.textContent = popupPs.product.name;
					    popupRow.appendChild(popupColName);
					    let popupColPrice = document.createElement("td");
					    popupColPrice.textContent = popupPs.price + "$";
					    popupRow.appendChild(popupColPrice);
					    let popupColQuantity = document.createElement("td");
					    popupColQuantity.textContent = getCartQuantities()[popupPs.id];
					    popupRow.appendChild(popupColQuantity);
						self.popupTable.appendChild(popupRow);
					}
					let mouseX = e.pageX;
					let mouseY = e.pageY;
					
					// Posiziona il popup in corrispondenza delle coordinate del mouse
					self.popup.style.left = mouseX + "px";
					self.popup.style.top = mouseY + "px";				
					self.popup.hidden = false;
				}
			});
			colItemsNum.addEventListener("mousemove", function(e){
				if(getCartItems()[ps[i].supplier.id]!=undefined){
					let mouseX = e.pageX;
					let mouseY = e.pageY;
					// Posiziona il popup in corrispondenza delle coordinate del mouse
					self.popup.style.left = mouseX + "px";
					self.popup.style.top = mouseY + "px";				
					self.popup.hidden = false;
				}
			});
			colItemsNum.addEventListener("mouseleave", function(e){
				self.popup.hidden = true;
				while(self.popupTable.rows.length>1){
					self.popupTable.removeChild(self.popupTable.rows.item(1));
				}
			});
			colItemsNum.className="column";
			row.appendChild(colItemsNum);
			let colItemsCost = document.createElement("td");
			colItemsCost.textContent = getProductsCostFromCart(ps[i].supplier)+"$";
			colItemsCost.className="column";
			row.appendChild(colItemsCost);
			
			let colPutInCart = document.createElement("td");
			let formPutInCart = document.createElement("form");
			let labelQuantity = document.createElement("label");
			labelQuantity.innerHTML = "Quantity: ";
			labelQuantity.className="label";
			let inputQuantity = document.createElement("input");
			inputQuantity.type = "number";
			inputQuantity.min = 1;
			inputQuantity.defaultValue = 1;
			inputQuantity.className="input";
			inputQuantity.required = true;
			let inputButton = document.createElement("input");
			inputButton.type = "button";
			inputButton.value = "Add to cart";
			inputButton.className="button";
			inputButton.addEventListener("click", function(e){
				e.preventDefault();
				if (inputQuantity.checkValidity() && inputQuantity.valueAsNumber > 0) {
					addItemToCart(ps[i], inputQuantity.valueAsNumber);
					self.orchestrator.goToCart();
				} else {
		    		inputQuantity.reportValidity();
		  		}
			});
			formPutInCart.action = "#";
			formPutInCart.appendChild(labelQuantity);
			formPutInCart.appendChild(inputQuantity);
			formPutInCart.appendChild(inputButton);
			colPutInCart.className = "column";
			colPutInCart.appendChild(formPutInCart);
			row.appendChild(colPutInCart);			
			
			self.supplierTable.appendChild(row);
		}
	}
	
	this.clear = function(){
		while(this.detailsTable.rows.length>1){
			this.detailsTable.removeChild(this.detailsTable.rows.item(1));
		}
		while(this.supplierTable.rows.length>1){
			this.supplierTable.removeChild(this.supplierTable.rows.item(1));
		}
		
		this.detailsElements.hidden = true;
		this.popup.hidden = true;
	}
}

function CartScreen(orch){
	this.orchestrator = orch; 
	
	this.cartButton = document.getElementById("cart_button");
	this.elements = document.getElementById("cart_elements");
	this.tables = document.getElementById("cart_tables");
	this.cartText = document.getElementById("cart_text");
	
	let self = this;
	this.cartButton.addEventListener("click", function(e) {
		self.orchestrator.goToCart();
	});
	
	this.load = function(){
		this.cartButton.hidden = true;
		this.elements.hidden = false;
		
		if(isCartEmpty()){
			this.cartText.textContent = "Your cart is empty";
			this.tables.hidden = true;
		}else{
			this.cartText.textContent = "Your cart:";
			
			for(let i=0; i<getCartSuppliers().length; i++){
				let supplier = getCartSuppliers()[i];
				let suppTable = document.createElement("table");
				suppTable.className = "table";
				let rowName = document.createElement("tr");
				let colName = document.createElement("td");
				colName.textContent = supplier.name;
				colName.className = "column bold";
				colName.colSpan = 4;
				rowName.appendChild(colName);
				suppTable.appendChild(rowName);
				let rowDesc = document.createElement("tr");
				let colDescName = document.createElement("td");
				colDescName.textContent = "Name: ";
				colDescName.className = "column bold";
				let colDescPrice = document.createElement("td");
				colDescPrice.textContent = "Price: ";
				colDescPrice.className = "column bold";
				let colDescQuantity = document.createElement("td");
				colDescQuantity.textContent = "Quantity: ";
				colDescQuantity.className = "column bold";
				let colDescTotal = document.createElement("td");
				colDescTotal.textContent = "Total: ";
				colDescTotal.className = "column bold";
				rowDesc.appendChild(colDescName);
				rowDesc.appendChild(colDescPrice);
				rowDesc.appendChild(colDescQuantity);
				rowDesc.appendChild(colDescTotal);
				suppTable.appendChild(rowDesc);
				for(let j=0; j<getCartItems()[supplier.id].length; j++){
					let prodSupp = getCartItems()[supplier.id][j];
					let rowItem = document.createElement("tr");
					let colName = document.createElement("td");
					colName.className = "column";
					colName.textContent = prodSupp.product.name;
					let colPrice = document.createElement("td");
					colPrice.textContent = prodSupp.price+"$";
					colPrice.className = "column";
					let colQuantity = document.createElement("td");
					colQuantity.textContent = getCartQuantities()[prodSupp.id];
					colQuantity.className = "column";
					let colTotal = document.createElement("td");
					colTotal.textContent = getCartQuantities()[prodSupp.id]*prodSupp.price+"$";
					colTotal.className = "column";
					rowItem.appendChild(colName);
					rowItem.appendChild(colPrice);
					rowItem.appendChild(colQuantity);
					rowItem.appendChild(colTotal);
					suppTable.appendChild(rowItem);
				}
				let rowShippingCost = document.createElement("tr");
				let colShippingCostDesc = document.createElement("td");
				colShippingCostDesc.textContent = "Shipping cost: ";
				colShippingCostDesc.className = "column bold";
				colShippingCostDesc.colSpan = 1;
				let colShippingCostValue = document.createElement("td");
				colShippingCostValue.textContent = getShippingCostFromCart(supplier)+"$";
				colShippingCostValue.className = "column";
				colShippingCostValue.colSpan = 3;
				rowShippingCost.appendChild(colShippingCostDesc);
				rowShippingCost.appendChild(colShippingCostValue);
				suppTable.appendChild(rowShippingCost);
				let rowTotalCost = document.createElement("tr");
				let colTotalCostDesc = document.createElement("td");
				colTotalCostDesc.textContent = "Total cost: ";
				colTotalCostDesc.className = "column bold";
				colTotalCostDesc.colSpan = 1;
				let colTotalCostValue = document.createElement("td");
				colTotalCostValue.textContent = getTotalCostFromCart(supplier)+"$";
				colTotalCostValue.className="column";
				colTotalCostValue.colSpan = 3;
				rowTotalCost.appendChild(colTotalCostDesc);
				rowTotalCost.appendChild(colTotalCostValue);
				suppTable.appendChild(rowTotalCost);
				let rowButton = document.createElement("tr");
				let colButton = document.createElement("td");
				let button = document.createElement("button");
				button.innerHTML="Order";
				button.addEventListener("click", function(e){
					e.preventDefault();
					let order = createClientOrder(supplier);
					if(order != null){
						
						let form = document.createElement("form");
						let user = JSON.parse(sessionStorage.getItem("user"));
						//Add order json
					  	let hidInput = document.createElement("input");
					  	hidInput.type = "hidden";
					  	hidInput.name = "order";
					  	hidInput.value = order;
					  	form.appendChild(hidInput);
					  	//Add civic number
					  	hidInput = document.createElement("input");
					  	hidInput.type = "hidden";
					  	hidInput.name = "civicNumber";
					  	hidInput.value = user.civicNumber;
					  	form.appendChild(hidInput);
					  	//Add street
					  	hidInput = document.createElement("input");
					  	hidInput.type = "hidden";
					  	hidInput.name = "street";
					  	hidInput.value = user.street;
					  	form.appendChild(hidInput);
					  	//Add city
					  	hidInput = document.createElement("input");
					  	hidInput.type = "hidden";
					  	hidInput.name = "city";
					  	hidInput.value = user.city;
					  	form.appendChild(hidInput);
					  	//Add state
					  	hidInput = document.createElement("input");
					  	hidInput.type = "hidden";
					  	hidInput.name = "state";
					  	hidInput.value = user.state;
					  	form.appendChild(hidInput);
						
						makeCall("POST", "AddOrder", form, function(req){
							if (req.readyState == XMLHttpRequest.DONE) {
								let response = req.responseText;
								if (req.status === 200) {
									removeSupplierFromCart(supplier);							
									self.orchestrator.goToOrders(JSON.parse(response));
				        		} else {
				           			self.orchestrator.handleError(req.status,response);
				          		}
				      		}
						});
					}else{
						self.orchestrator.handleError(req.status,response);
					}
				});
				button.className = "button";
				colButton.appendChild(button);
				colButton.className = "column";
				colButton.colSpan = 2;
				let colButton2 = document.createElement("td");
				let button2 = document.createElement("button");
				button2.innerHTML="Remove from cart";
				button2.addEventListener("click", function(e){
					e.preventDefault();
					removeSupplierFromCart(supplier);
					self.tables.removeChild(suppTable);
					if(isCartEmpty()){
						self.cartText.textContent = "Your cart is empty";
						self.tables.hidden = true;
					}
				});
				button2.className = "button";
				colButton2.appendChild(button2);
				colButton2.className = "column";
				colButton2.colSpan = 2;
				rowButton.appendChild(colButton);
				rowButton.appendChild(colButton2);
				suppTable.appendChild(rowButton);
				self.tables.appendChild(suppTable);
				self.tables.appendChild(document.createElement("br"));
			}
			
			this.tables.hidden = false;
		}
	}
	
	this.clear = function(){
		this.cartButton.hidden = false;
		this.elements.hidden = true;
		this.cartText.textContent = "";
		while(this.tables.childElementCount > 0){
			this.tables.removeChild(this.tables.firstChild);
		}
	}
	
	this.clear();
}

function OrdersScreen(orch){
	this.orchestrator = orch; 
	
	this.ordersButton = document.getElementById("orders_button");
	this.elements = document.getElementById("orders_elements");
	this.tables = document.getElementById("orders_tables");
	this.ordersText = document.getElementById("orders_text");
	
	let self = this;
	this.ordersButton.addEventListener("click", function(e) {
		makeCall("GET", "GetOrders", null, function(req){
			if(req.readyState == XMLHttpRequest.DONE){
				let response = req.responseText;
				if (req.status === 200) {							
					self.orchestrator.goToOrders(JSON.parse(response));
        		} else {
           			self.orchestrator.handleError(req.status,response);
          		}
			}
		});
	});
	
	this.load = function(ordersList){
		this.ordersButton.hidden = true;
		this.elements.hidden = false;
		
		if(ordersList.length <= 0){
			this.ordersText.textContent = "You have no orders yet";
			this.tables.hidden = true;
		}else{
			this.ordersText.textContent = "Your orders:";
			
			for(let i=0; i<ordersList.length; i++){
				let orderTable = document.createElement("table");
				orderTable.className = "table";
				let rowInfo = document.createElement("tr");
				let colNumber = document.createElement("td");
				colNumber.textContent = "Order id: " + ordersList[i].id;
				colNumber.className = "column bold";
				colNumber.colSpan=1;
				let colSupp = document.createElement("td");
				colSupp.textContent = ordersList[i].supplier.name;
				colSupp.className = "column bold";
				colSupp.colSpan=3;
				rowInfo.appendChild(colNumber);
				rowInfo.appendChild(colSupp);
				orderTable.appendChild(rowInfo);
				let rowDesc = document.createElement("tr");
				let colDescName = document.createElement("td");
				colDescName.textContent = "Name: ";
				colDescName.className = "column bold";
				let colDescPrice = document.createElement("td");
				colDescPrice.textContent = "Price: ";
				colDescPrice.className = "column bold";
				let colDescQuantity = document.createElement("td");
				colDescQuantity.textContent = "Quantity: ";
				colDescQuantity.className = "column bold";
				let colDescTotal = document.createElement("td");
				colDescTotal.textContent = "Total: ";
				colDescTotal.className = "column bold";
				rowDesc.appendChild(colDescName);
				rowDesc.appendChild(colDescPrice);
				rowDesc.appendChild(colDescQuantity);
				rowDesc.appendChild(colDescTotal);
				orderTable.appendChild(rowDesc);
				for(let j=0; j<ordersList[i].products.length; j++){
					let prod = ordersList[i].products[j];
					let rowItem = document.createElement("tr");
					let colName = document.createElement("td");
					colName.textContent = prod.product.name;
					colName.className = "column";
					let colPrice = document.createElement("td");
					colPrice.textContent = prod.price+"$";
					colPrice.className = "column";
					let colQuantity = document.createElement("td");
					colQuantity.textContent = ordersList[i].quantities[prod.product.id];
					colQuantity.className = "column";
					let colTotal = document.createElement("td");
					colTotal.textContent = ordersList[i].quantities[prod.product.id]*prod.price+"$";
					colTotal.className = "column";
					rowItem.appendChild(colName);
					rowItem.appendChild(colPrice);
					rowItem.appendChild(colQuantity);
					rowItem.appendChild(colTotal);
					orderTable.appendChild(rowItem);
				}
				let rowShippingCost = document.createElement("tr");
				let colShippingCostDesc = document.createElement("td");
				colShippingCostDesc.textContent = "Shipping cost: ";
				colShippingCostDesc.className = "column bold";
				colShippingCostDesc.colSpan=1;
				let colShippingCostValue = document.createElement("td");
				colShippingCostValue.textContent = ordersList[i].shippingPrice+"$";
				colShippingCostValue.className = "column";
				colShippingCostValue.colSpan=3;
				rowShippingCost.appendChild(colShippingCostDesc);
				rowShippingCost.appendChild(colShippingCostValue);
				orderTable.appendChild(rowShippingCost);
				let rowTotalCost = document.createElement("tr");
				let colTotalCostDesc = document.createElement("td");
				colTotalCostDesc.textContent = "Total cost: ";
				colTotalCostDesc.className = "column bold";
				colTotalCostDesc.colSpan=1;
				let colTotalCostValue = document.createElement("td");
				colTotalCostValue.textContent = ordersList[i].total+"$";
				colTotalCostValue.className = "column";
				colTotalCostValue.colSpan=3;
				rowTotalCost.appendChild(colTotalCostDesc);
				rowTotalCost.appendChild(colTotalCostValue);
				orderTable.appendChild(rowTotalCost);
				let rowDate = document.createElement("tr");
				let colDateDesc = document.createElement("td");
				colDateDesc.textContent = "Date: ";
				colDateDesc.className = "column bold";
				colDateDesc.colSpan=1;
				let colDate = document.createElement("td");
				colDate.textContent = ordersList[i].date;
				colDate.className = "column";
				colDate.colSpan=3;
				rowDate.appendChild(colDateDesc);
				rowDate.appendChild(colDate);
				orderTable.appendChild(rowDate);
				let rowAddress = document.createElement("tr");
				let colAddressDesc = document.createElement("td");
				colAddressDesc.textContent = "Shipping address: ";
				colAddressDesc.className = "column bold";
				colAddressDesc.colSpan=1;
				let colAddress = document.createElement("td");
				colAddress.textContent = ordersList[i].street+" "+ordersList[i].civicNumber+", "+ordersList[i].city+", "+ordersList[i].stateIso3;
				colAddress.className="column";
				colAddress.colSpan=3;
				rowAddress.appendChild(colAddressDesc);
				rowAddress.appendChild(colAddress);
				orderTable.appendChild(rowAddress);
				
				self.tables.appendChild(orderTable);
				self.tables.appendChild(document.createElement("br"));
			}
			
			this.tables.hidden = false;
		}
	}
	
	this.clear = function(){
		this.ordersButton.hidden = false;
		this.elements.hidden = true;
		this.ordersText.textContent = "";
		while(this.tables.childElementCount > 0){
			this.tables.removeChild(this.tables.firstChild);
		}
	}
	
	this.clear();
}

function ErrorScreen(orch){
	this.orchestrator = orch; 
	
	this.menuButtons = document.getElementById("menu_buttons");
	this.elements = document.getElementById("error_elements");
	this.errorText = document.getElementById("error_text");
	this.login_rb = document.getElementById("login_redirect_button");
	this.home_rb = document.getElementById("home_redirect_button");
	
	let self = this;
	this.login_rb.addEventListener("click", function(e) {
		window.location.href="index.html";
	});
	
	this.home_rb.addEventListener("click", function(e) {
		self.orchestrator.goToHome();
	});
	
	this.load = function(text, logout){
		document.body.style.backgroundColor="#ebccd1";
		this.menuButtons.hidden = true;
		this.login_rb.hidden = true;
		this.home_rb.hidden = true;
		this.elements.hidden = false;
		if(text==undefined){
			this.errorText.innerHTML="Please try again later";
		}else{
			this.errorText.innerHTML="Error: " + text;
		}
		if(!logout){
			this.home_rb.hidden = false;
		}else{
			sessionStorage.removeItem("user");
			sessionStorage.removeItem("cart");
			this.login_rb.hidden = false;
		}
	}
	
	this.clear = function(){
		document.body.style.backgroundColor="#edd2e9";
		this.menuButtons.hidden = false;
		this.elements.hidden = true;
		this.errorText.innerHTML = "";
		this.login_rb.hidden = true;
		this.home_rb.hidden = true;
		
	}
	
	this.clear();
}

function Orchestrator(){
	this.currentPage = null;
	
	this.homePage = new HomeScreen(this);
	this.accountPage = new AccountScreen(this);
	this.detailsPage = new DetailsScreen(this);
	this.resultsPage = new ResultsScreen(this);	
	this.cartPage = new CartScreen(this);
	this.ordersPage = new OrdersScreen(this);
	this.errorPage = new ErrorScreen(this);
	
	this.goToHome = function(){
		this.currentPage.clear();
		this.currentPage=this.homePage;
		this.homePage.load();
	}
	
	this.goToAccount = function(){
		this.currentPage.clear();
		this.currentPage=this.accountPage;
		this.accountPage.load();
	}
	
	this.goToResults = function(array,keyword){
		this.currentPage.clear();
		this.currentPage=this.resultsPage;
		this.resultsPage.load(array,keyword);
	}
	
	this.goToDetails = function(ps){
		this.currentPage.clear();
		this.currentPage=this.detailsPage;
		this.detailsPage.load(ps);
	}
	
	this.goToCart = function(){
		this.currentPage.clear();
		this.currentPage=this.cartPage;
		this.cartPage.load();
	}
	
	this.goToOrders = function(ordersList){
		this.currentPage.clear();
		this.currentPage=this.ordersPage;
		this.ordersPage.load(ordersList);
	}
	
	this.goToError = function(text, logout){
		this.currentPage.clear();
		this.currentPage=this.errorPage;
		this.errorPage.load(text, logout);
	}
	
	this.handleError = function(status,text){
		if(status == 500){
			this.goToError(undefined,true);
			return;
		}
		if(status == 401){
			sessionStorage.removeItem("user");
			sessionStorage.removeItem("cart");
			window.location.href = "index.html";
			return;
		}
		this.goToError(text,false);
	}
	
	this.start = function(){
		this.currentPage = this.homePage;
		this.homePage.load();
	}
}

{
	new Orchestrator().start();
}