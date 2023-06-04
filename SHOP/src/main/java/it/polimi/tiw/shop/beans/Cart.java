package it.polimi.tiw.shop.beans;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.tiw.shop.dao.ProductSupplierDAO;

public class Cart {
	
	//Oggetto Carrello
	
	private List<Supplier> suppliers; 					//lista fornitori
	private Map<Integer, List<ProductSupplier>> items; 	//mappa che collega ad ogni fornitore (id intero) la lista di prodotti-fornitore (per memorizzare il prezzo)
	private Map<Integer, Integer> quantities; 			//mappa che collega ad ogni prodotto-fornitore (id intero creato con Cantor's pairing function per semplicità) un intero che indica quantità
	
	public Cart() {
		suppliers = new ArrayList<>();
		items = new HashMap<>();
		quantities = new HashMap<>();
	}
	
	/**
	 * metodo per aggiungere un prodotto
	 * @param ps è il prodotto associato al fornitore
	 * @param quantity è la quantità >0 
	 */
	
	public void addItem(ProductSupplier ps, int quantity) {
		if(items.get(ps.getSupplier().getId()) == null) {			//se è il primo prodotto di quel fornitore
			suppliers.add(ps.getSupplier());						//si aggiunge il fornitore nella lista e si crea la lista vuota di prodotti nella mappa
			items.put(ps.getSupplier().getId(), new ArrayList<ProductSupplier>());
		}
		if(quantities.get(ps.getId()) == null) {					//se non c'è quel prodotto nella lista
			items.get(ps.getSupplier().getId()).add(ps);			//lo aggiungo e pongo la quantità a 0
			quantities.put(ps.getId(), 0);
		}
		quantities.put(ps.getId(), quantities.get(ps.getId()) + quantity);	//aggiungo la quantità nuova
		updateCost(ps.getSupplier());										//aggiorno il costo totale per quel fornitore
	}	
	
	/**
	 * metodo per rimuovere un prodotto
	 * @param ps è il prodotto associato al fornitore
	 * @param quantity è la quantità >0 
	 */
	
	public void removeItem(ProductSupplier ps, int quantity) {
		if(items.get(ps.getSupplier().getId()) == null || quantities.get(ps.getId())==null)
			return;
		
		quantities.put(ps.getId(), quantities.get(ps.getId()) - quantity);
		if(quantities.get(ps.getId()) <= 0) {
			removeItem(ps);
		}else {
			updateCost(ps.getSupplier());
		}
	}
	
	/**metodo per rimuovere un prodotto
	 * @param ps è il prodotto associato al fornitore
	 */
	
	public void removeItem(ProductSupplier ps) {
		if(items.get(ps.getSupplier().getId()) == null || quantities.get(ps.getId())==null)
			return;
		
		quantities.remove(ps.getId());							//rimuovo la quantità corrispondente
		items.get(ps.getSupplier().getId()).remove(ps);			//rimuovo il prodotto corrispondente
		if(items.get(ps.getSupplier().getId()).size() == 0) {	//se non ci sono altri prodotti per quel fornitore
			items.remove(ps.getSupplier().getId());				//lo rimuovo sia dalla mappa di profotto-fornitori
			suppliers.remove(ps.getSupplier().getId());			//che dalla lista fornitori
		}	
		updateCost(ps.getSupplier());							//aggiorno il costo totale per quel fornitore
	}
	
	/**
	 * metodo per aggiornare il costo totale per un certo fornitore
	 * @param s è il fornitore
	 */
	
	public void reloadDBPrices(Connection connection) {
		
		ProductSupplierDAO psDAO = new ProductSupplierDAO(connection);
		
		for(Supplier sc : suppliers) {
			for(int i=0; i < items.get(sc.getId()).size(); i++) {
				try {
					ProductSupplier newps = psDAO.getByIds(items.get(sc.getId()).get(i).getProduct().getId(),sc.getId());
					if(newps!=null) items.get(sc.getId()).set(i, newps);
					else {
						items.get(sc.getId()).remove(i);
						i--;
					}
				}catch(Exception e) {}
			}
			updateCost(sc);
		}
		
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
		
		for(ProductSupplier ps : items.get(s.getId())) {			//per ogni prodotto nella mappa corrispondente al fornitore
			num += quantities.get(ps.getId());						//aggiorno il numero totale di prodotti
			cost += quantities.get(ps.getId()) * ps.getPrice();		//e il costo totale dei prodotti
		}
		
		if(cost >= s.getFreeShippingThreshold() && s.getFreeShippingThreshold()>=0) {					//se il prezzo rientra nella spedizione gratuita
			shippingCost = 0;
		}else {														//altrimenti calcolo in base al numero di pezzi il costo corretto
			int min = 1;											//a seconda delle fasce definite
			for(int i = 0; i < s.getMinQuantities().size(); i++) {
				if(s.getMinQuantities().get(i) > min && s.getMinQuantities().get(i) <= num)
					min = s.getMinQuantities().get(i);
			}
			shippingCost = s.getShippingPrices().get(min);
		}
		
		totalCost = cost + shippingCost;							//costo totale prodotti + spedizione
		
		for(Supplier cs : suppliers) {
			if(cs.equals(s)) { 
				cs.setTotalCost(totalCost);
				cs.setShippingCost(shippingCost);
			}
		}
	}
	
	/**
	 * metodo per rimuovere un fornitore e tutti i suoi prodotti associati
	 * @param s è il fornitore
	 */
	
	public void removeSupplier(Supplier s) {
		if(items.get(s.getId()) == null) 
			return;
		
		for(int i = 0; i < items.get(s.getId()).size(); i++) {
			quantities.remove(items.get(s.getId()).get(i).getId());		//rimuovo le quantità
		}
		
		items.remove(s.getId());										//rimuovo il prodotto
		
		for(Supplier supp:suppliers) {									
			if(supp.getId()==s.getId()) {
				suppliers.remove(supp);									//rimuovo il fornitore
				break;
			}
		}
	}
	
	public List<Supplier> getSuppliers(){
		return suppliers;
	}
	
	public Map<Integer, List<ProductSupplier>> getItems(){
		return items;
	}
	
	public Map<Integer, Integer> getQuantities(){
		return quantities;
	}
	
}
