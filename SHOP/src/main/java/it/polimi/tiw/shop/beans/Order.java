package it.polimi.tiw.shop.beans;

import java.sql.Date;
import java.util.List;
import java.util.Map;

public class Order {
	
	//Bean Ordine
	
	private int id;					//informazioni ordine
	private double total;
	private Date date;
	private double shippingPrice;
	
	private Supplier supplier;		//informazione fornitore	
	
	private String userEmail;		//informazioni utente
	private String stateIso3;
	private String city;
	private String street;
	private String civicNumber;
	
	private List<ProductSupplier> products;		//prodotti contenuti (associati al fornitore per avere il prezzo)
	private Map<Integer, Integer> quantities;	//quantità corrispondenti
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Supplier getSupplier() {
		return supplier;
	}
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}
	public double getShippingPrice() {
		return shippingPrice;
	}
	public void setShippingPrice(double shippingPrice) {
		this.shippingPrice = shippingPrice;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getStateIso3() {
		return stateIso3;
	}
	public void setStateIso3(String stateIso3) {
		this.stateIso3 = stateIso3;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getCivicNumber() {
		return civicNumber;
	}
	public void setCivicNumber(String civicNumber) {
		this.civicNumber = civicNumber;
	}
	public List<ProductSupplier> getProducts() {
		return products;
	}
	public void setProducts(List<ProductSupplier> products) {
		this.products = products;
	}
	public Map<Integer,Integer> getQuantities() {
		return quantities;
	}
	public void setQuantities(Map<Integer,Integer> quantities) {
		this.quantities = quantities;
	}
}
