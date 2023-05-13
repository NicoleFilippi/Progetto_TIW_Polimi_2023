package it.polimi.tiw.shop.beans;

import java.util.List;
import java.util.Map;

public class Supplier {
	
	//Bean fornitore
	
	private int id;
	private String name;
	private int rating;
	private double freeShippingThreshold;
	
	private List<Integer> minQuantities;			//lista contenente il minimo numero di prodotti per ogni fascia
	private Map<Integer, Double> shippingPrices;    //mappa da minimo numero di prodotti a prezzo per ogni fascia (per poter iterare su thymeleaf)
	
	private int productsInCartNum;
	private double productsInCartCost;
	private double totalCost;
	private double shippingCost;
	
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
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public double getFreeShippingThreshold() {
		return freeShippingThreshold;
	}
	public void setFreeShippingThreshold(double freeShippingThreshold) {
		this.freeShippingThreshold = freeShippingThreshold;
	}
	public List<Integer> getMinQuantities() {
		return minQuantities;
	}
	public void setMinQuantities(List<Integer> minQuantities) {
		this.minQuantities = minQuantities;
	}
	public Map<Integer, Double> getShippingPrices() {
		return shippingPrices;
	}
	public void setShippingPrices(Map<Integer, Double> shippingPrices) {
		this.shippingPrices = shippingPrices;
	}
	public int getProductsInCartNum() {
		return productsInCartNum;
	}
	public void setProductsInCartNum(int productsInCartNum) {
		this.productsInCartNum = productsInCartNum;
	}
	public double getProductsInCartCost() {
		return productsInCartCost;
	}
	public void setProductsInCartCost(double productsInCartCost) {
		this.productsInCartCost = productsInCartCost;
	}
	public double getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}
	public double getShippingCost() {
		return shippingCost;
	}
	public void setShippingCost(double shippingCost) {
		this.shippingCost = shippingCost;
	}
	
	public boolean equals(Supplier s) {
		return (s.getId() == id);
	}
}
