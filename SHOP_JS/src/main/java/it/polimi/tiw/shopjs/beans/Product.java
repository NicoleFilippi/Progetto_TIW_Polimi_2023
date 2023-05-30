package it.polimi.tiw.shopjs.beans;

import it.polimi.tiw.shopjs.beans.Product;

public class Product {
	
	//Bean Prodotto
	
	private int id;
	private String name;
	private String description;
	private String category;
	private String photoPath;
	private double minPrice;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getPhotoPath() {
		return photoPath;
	}
	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}
	public double getMinPrice() {
		return minPrice;
	}
	public void setMinPrice(double minPrice) {
		this.minPrice = minPrice;
	}
	

	public boolean equals(Product p) {
		return (p.getId()==id);
	}
}
