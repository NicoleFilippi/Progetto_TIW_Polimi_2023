package it.polimi.tiw.shop.beans;

public class ProductSupplier {
	
	//Bean prodotto-fornitore
	
	private Product product;
	private Supplier supplier;
	private double price;
	
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public Supplier getSupplier() {
		return supplier;
	}
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
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
	
	//metodo che associa un id univoco ad ogni coppia prodotto-fornitore utilizzando la funzione di accoppiamento di Cantor
	
	public int getId() {
		return (product.getId() + supplier.getId()) * (product.getId() + supplier.getId() + 1) / 2 + supplier.getId();
	}
}
