package it.polimi.tiw.shop.beans;

public class User {
	private String email;
	private String name;
	private String surname;
	private String state;
	private String city;
	private String street;
	private String civicNumber;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
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
	
}
