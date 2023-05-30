package it.polimi.tiw.shopjs.beans;

import it.polimi.tiw.shopjs.beans.ProductSupplier;

public class ProductSupplier {
	
	//Bean prodotto-fornitore
	
	private Product product;
	private Supplier supplier;
	private double price;
	private int id;
	
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
		
		//associa un id univoco ad ogni coppia prodotto-fornitore utilizzando la funzione di accoppiamento di Cantor
		
		if(supplier!=null) {
			id = (product.getId() + supplier.getId()) * (product.getId() + supplier.getId() + 1) / 2 + supplier.getId();
		}
	}
	public Supplier getSupplier() {
		return supplier;
	}
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
		
		//associa un id univoco ad ogni coppia prodotto-fornitore utilizzando la funzione di accoppiamento di Cantor
		
		if(product!=null) {
			id = (product.getId() + supplier.getId()) * (product.getId() + supplier.getId() + 1) / 2 + supplier.getId();
		}
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public boolean equals(ProductSupplier ps) {
		return (ps.getSupplier().equals(supplier) && ps.getProduct().equals(product));
	}
	public int getId() {
		return id;
	}
}
