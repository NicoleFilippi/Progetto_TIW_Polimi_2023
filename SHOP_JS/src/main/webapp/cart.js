//oggetto carrello della sessione client

function Cart(){
	this.suppliers = []; //array fornitori
	this.items = new Map(); //mappa fornitore.id->product-supplier
	this.quantities = new Map(); //mappa product-supplier.id-quantità
}

function ClientOrder(){
	this.supplierId = -1;
	this.productIds = [];
	this.quantities = [];
}

//ritorna true se vuoto

function isCartEmpty(){
	let cart = JSON.parse(sessionStorage.getItem("cart"));
	if(cart.suppliers.length>0)
		return false;
	return true;
}

//ritorna lista di fornitori nel carrello

function getCartSuppliers(){
	let cart = JSON.parse(sessionStorage.getItem("cart"));
	return cart.suppliers;
}

//ritorna tutti i prodotti nel carrello (mappati per fornitore)

function getCartItems(){
	let cart = JSON.parse(sessionStorage.getItem("cart"));
	return cart.items;
}

//ritorna tutte le quantità (mappate per product-supplier)

function getCartQuantities(){
	let cart = JSON.parse(sessionStorage.getItem("cart"));
	return cart.quantities;
}

//aggiunge al carrello

function addItemToCart(ps, quantity){
	let cart = JSON.parse(sessionStorage.getItem("cart"));
	if(cart.items[ps.supplier.id] == undefined) {			//se è il primo prodotto di quel fornitore
		cart.suppliers.push(ps.supplier);					//si aggiunge il fornitore nella lista e si crea la lista vuota di prodotti nella mappa
		cart.items[ps.supplier.id] = [];
	}
	
	if(cart.quantities[ps.id] == undefined) {				//se non c'è quel prodotto nella lista
		cart.items[ps.supplier.id].push(ps);				//lo aggiungo e pongo la quantità a 0
		cart.quantities[ps.id] = 0;
	}
	
	cart.quantities[ps.id] = cart.quantities[ps.id] + quantity;	//aggiungo la quantità nuova
	
	//aggiorno elemento sessione
	
	sessionStorage.setItem("cart", JSON.stringify(cart));
}

//rimuove dal carrello
	
function removeSupplierFromCart(s){
	let cart = JSON.parse(sessionStorage.getItem("cart"));
	if(cart.items[s.id] == undefined) return;
	
	for(let i=0; i < cart.items[s.id].length; i++){
		cart.quantities[cart.items[s.id][i].id] = undefined;
	}
	cart.items[s.id] = undefined;
	
	for(let i=0; i < cart.suppliers.length; i++){
		if(cart.suppliers[i].id = s.id){
			cart.suppliers.splice(i,1);
			break;
		}
	}
	
	//aggiorno elemento sessione
	
	sessionStorage.setItem("cart",JSON.stringify(cart));
}

//prende il costo dei prodotti dal carrello di un certo fornitore
	
function getProductsCostFromCart(s){
	let cart = JSON.parse(sessionStorage.getItem("cart"));
	if(cart.items[s.id] == undefined)
		return 0;
	let productCost = 0;
	let pList = cart.items[s.id];
	
	for(let i=0; i<pList.length; i++){
		productCost += pList[i].price * cart.quantities[pList[i].id];
	}
	return productCost;
}

//prende numero di elementi di un certo fornitore nel carrello

function getProductsNumberFromCart(s){
	let cart = JSON.parse(sessionStorage.getItem("cart"));
	if(cart.items[s.id] == undefined)
		return 0;
	let productNum = 0;
	let pList = cart.items[s.id];
	
	for(let i=0; i<pList.length; i++){
		productNum += cart.quantities[pList[i].id];
	}
	return productNum;
}

//prende costo di spedizione di un fornitore dal carrello

function getShippingCostFromCart(s){
	if(getProductsCostFromCart(s) >= s.freeShippingThreshold && s.freeShippingThreshold>=0)
		return 0;
	let prodNum = getProductsNumberFromCart(s);
	let min = 1;
	for(let i=0; i < s.minQuantities.length; i++){
		if(s.minQuantities[i] > min && s.minQuantities[i] <= prodNum )
			min = s.minQuantities[i];
	}
	return s.shippingPrices[min];
}
	
//prende il costo totale per un certo fornitore

function getTotalCostFromCart(s){
	return getProductsCostFromCart(s) + getShippingCostFromCart(s);
}

//crea client-order dato fornitore

function createClientOrder(s){	
	let cart = JSON.parse(sessionStorage.getItem("cart"));
	
	if(s==null || s.id == undefined)
		return null;
		
	if(cart.items[s.id]==undefined)
		return null;
	
	let order = new ClientOrder();
	
	order.supplierId = s.id;
	
	for(let i=0; i < cart.items[s.id].length; i++){
		order.productIds.push(cart.items[s.id][i].product.id);		//inserisce nella lista il prodotto
		order.quantities.push(cart.quantities[cart.items[s.id][i].id]);		//inserisce nella lista la quantità
	}
	
	return JSON.stringify(order);	//ritorna json
}