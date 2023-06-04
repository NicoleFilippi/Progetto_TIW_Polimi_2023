package it.polimi.tiw.shopjs.beans;

import java.util.List;

public class ClientOrder{
	private int supplierId;
	private List<Integer> productIds;
	private List<Integer> quantities;
	
	public int getSupplierId() {
		return supplierId;
	}
	public List<Integer> getProductIds() {
		return productIds;
	}
	public List<Integer> getQuantities() {
		return quantities;
	}
}