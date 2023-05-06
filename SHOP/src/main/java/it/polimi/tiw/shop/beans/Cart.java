package it.polimi.tiw.shop.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cart {
	
	private List<Supplier> suppliers;
	private Map<Integer,List<ProductSupplier>> items;
	private Map<Integer,Integer> quantities; 
	
	public Cart() {
		suppliers = new ArrayList<>();
		items = new HashMap<>();
		quantities = new HashMap<>();
	}
	
	public void addItem(ProductSupplier ps, int quantity) {
		if(items.get(ps.getSupplier().getId())==null) {
			suppliers.add(ps.getSupplier());
			items.put(ps.getSupplier().getId(), new ArrayList<ProductSupplier>());
		}
		if(quantities.get(ps.getId())==null) {
			items.get(ps.getSupplier().getId()).add(ps);
			quantities.put(ps.getId(),0);
		}
		quantities.put(ps.getId(),quantities.get(ps.getId())+quantity);
		updateCost(ps.getSupplier());
	}
	
	public void removeItem(ProductSupplier ps, int quantity) {
		quantities.put(ps.getId(),quantities.get(ps.getId())-quantity);
		if(quantities.get(ps.getId())<=0) {
			removeItem(ps);
		}else {
			updateCost(ps.getSupplier());
		}
	}
	
	public void removeItem(ProductSupplier ps) {
		quantities.remove(ps.getId());
		items.get(ps.getSupplier().getId()).remove(ps);
		if(items.get(ps.getSupplier().getId()).size()==0) {
			items.remove(ps.getSupplier().getId());
			suppliers.remove(ps.getSupplier().getId());
		}
		updateCost(ps.getSupplier());
	}
	
	public List<Supplier> getSuppliers(){
		return suppliers;
	}
	
	public Map<Integer,List<ProductSupplier>> getItems(){
		return items;
	}
	
	public Map<Integer,Integer> getQuantities(){
		return quantities;
	}
	
	private void updateCost(Supplier s) {
		double totalCost = 0;
		double shippingCost = 0;
		double cost = 0;
		int num = 0;
		
		boolean found=false;
		for(Supplier sc : suppliers) {
			if(s.equals(sc)) {
				found = true;
				break;
			}
		}
		if(!found) return;
		
		for(ProductSupplier ps : items.get(s.getId())) {
			num += quantities.get(ps.getId());
			cost += quantities.get(ps.getId())*ps.getPrice();
		}
		if(cost >= s.getFreeShippingThreshold()) {
			shippingCost = 0;
		}else {
			int min = 1;
			for(int i=0;i<s.getMinQuantities().size();i++) {
				if(s.getMinQuantities().get(i)>min && s.getMinQuantities().get(i) <= num)
					min = s.getMinQuantities().get(i);
			}
			shippingCost = s.getShippingPrices().get(min);
		}
		
		totalCost = cost + shippingCost;
		
		for(Supplier cs : suppliers) {
			if(cs.equals(s)) { 
				cs.setTotalCost(totalCost);
				cs.setShippingCost(shippingCost);
			}
		}
	}
	
	public void removeSupplier(Supplier s) {
		if(items.get(s.getId())==null) 
			return;
		
		for(int i=0; i<items.get(s.getId()).size();i++) {
			quantities.remove(items.get(s.getId()).get(i).getId());
		}
		
		items.remove(s.getId());
		
		for(Supplier supp:suppliers) {
			if(supp.getId()==s.getId())
				suppliers.remove(supp);
		}
	}
}
