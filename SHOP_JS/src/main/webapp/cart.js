/**
 * Cart creator
 */


function Cart(){
	this.suppliers = [];
	this.items = new Map();
	this.quantities = new Map();
	
	this.addItem = function (ps, quantity){
		
		if(this.items.get(ps.supplier.id) == undefined) {			//se è il primo prodotto di quel fornitore
			this.suppliers.push(ps.supplier);						//si aggiunge il fornitore nella lista e si crea la lista vuota di prodotti nella mappa
			this.items.set(ps.supplier.id, []);
		}
		
		if(this.quantities.get(ps.id) == undefined) {					//se non c'è quel prodotto nella lista
			this.items.get(ps.supplier.id).push(ps);			//lo aggiungo e pongo la quantità a 0
			this.quantities.put(ps.id, 0);
		}
		
		this.quantities.set(ps.id, this.quantities.get(ps.id) + quantity);	//aggiungo la quantità nuova
	}
	
	this.removeSupplier = function (s){
		if(this.items.get(s.id) == undefined) return;
		
		for(let i=0; i < this.items.get(s.id).length; i++){
			this.quantities.set(this.items.get(s.id)[i].id, undefined);
		}
		this.items.set(s.id, undefined);
		
		for(let i=0; i < this.suppliers.length; i++){
			if(this.suppliers[i].id = s.id){
				this.suppliers.splice(i,1);
				break;
			}
		}
	}
	
	this.getProductsCost = function (s){
		if(this.items.get(s.id) == undefined) return 0;
		let productCost = 0;
		let pList = this.items.get(s.id);
		
		for(let i=0; i<pList.length; i++){
			productCost += pList[i].price * this.quantities.get(pList[i].id);
		}
		return productCost;
	}
	
	this.getProductsNumber = function (s){
		if(this.items.get(s.id) == undefined) return 0;
		let productNum = 0;
		let pList = this.items.get(s.id);
		
		for(let i=0; i<pList.length; i++){
			productNum += this.quantities.get(pList[i].id);
		}
		return productNum;
	}
	
	this.getShippingCost = function (s){
		if(this.getProductsCost(s) >= s.freeShippingThreshold) return 0;
		let prodNum = this.getProductsNumber(s);
		let min = 1;
		for(let i=0; i < s.minQuantities.length; i++){
			if(s.minQuantities[i] > min && s.minQuantities[i] <= prodNum )
				min = s.minQuantities[i];
		}
		return s.shippingPrices.get(min);
	}
	
	this.getTotalCost = function (s){
		return this.getProductsCost(s) + this.getShippingCost(s);
	}
}