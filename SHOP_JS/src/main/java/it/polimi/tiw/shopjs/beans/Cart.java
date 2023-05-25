package it.polimi.tiw.shopjs.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cart {
	
	//Oggetto Carrello
	
	private Map<Integer, Integer> quantities; 			//mappa che collega ad ogni prodotto-fornitore (id intero creato con Cantor's pairing function per semplicità) un intero che indica quantità
	
	public Cart() {
		quantities = new HashMap<>();
	}
}
