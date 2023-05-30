package it.polimi.tiw.shopjs.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.shopjs.beans.Product;

public class ProductDAO {
	
	private Connection con;
	
	public ProductDAO(Connection connection) {
		this.con = connection;
	}
	
	/**
	 * metodo che ritorna gli ultimi prodotti visualizzati
	 * @param email mail utente
	 * @param defaultCategory è la categoria di default impostata nell'xml
	 * @return ritorna la lista di prodotti da stampare
	 */
	
	public List<Product> lastVisualized(String email, String defaultCategory) throws SQLException {
		List<Product> prodList = new ArrayList<>();
		
		String query = "SELECT * FROM product, user_product WHERE useremail = ? AND id = productid ORDER BY timestamp DESC";
		PreparedStatement pstatement = con.prepareStatement(query);
		
		String minPriceQuery = "SELECT MIN(Price) FROM product_supplier WHERE productId = ? ";
		PreparedStatement minpstatement = con.prepareStatement(minPriceQuery);
		
		pstatement.setString(1, email);
		ResultSet result = pstatement.executeQuery();
		ResultSet minPrice;
		
		//aggiungo un massimo di 5 prodotti visualizzati alla lista
		
		while(result.next() && prodList.size() < 5) {
			minpstatement.setInt(1, result.getInt("id"));		//prelevo il prezzo minore per ogni prodotto visualizzato
			minPrice = minpstatement.executeQuery();
			minPrice.next();
			
			Product tmp = new Product();
			tmp.setId(result.getInt("id"));
			tmp.setName(result.getString("name"));
			tmp.setDescription(result.getString("description"));
			tmp.setCategory(result.getString("category"));
			tmp.setPhotoPath("images/" + result.getString("PhotoPath"));
			tmp.setMinPrice(minPrice.getDouble("MIN(price)"));
			prodList.add(tmp);									
		}
		
		//se l'utente non ha visualizzato abbastanza prodotti prendo i rimanenti dalla categoria di default
		
		if(prodList.size() < 5 && defaultCategory != null) {	
			
			String defQuery = "SELECT * FROM Product WHERE category = ?";
			PreparedStatement defpstatement = con.prepareStatement(defQuery);
			defpstatement.setString(1, defaultCategory);
			ResultSet defResult = defpstatement.executeQuery();
		
			while(defResult.next() && prodList.size() < 5) {
				boolean found = false;
				
				//evito il prodotto se già presenti
				
				for(int i = 0; i < prodList.size() && !found; i++)
					found = (defResult.getInt("id") == prodList.get(i).getId());
				
				//inserimento in lista
				
				if(!found) {
					minpstatement.setInt(1, defResult.getInt("id"));
					minPrice = minpstatement.executeQuery();
					minPrice.next();
					
					Product tmp = new Product();
					tmp.setId(defResult.getInt("id"));
					tmp.setName(defResult.getString("name"));
					tmp.setDescription(defResult.getString("description"));
					tmp.setCategory(defResult.getString("category"));
					tmp.setPhotoPath("images/" + defResult.getString("PhotoPath"));
					tmp.setMinPrice(minPrice.getDouble("MIN(price)"));
					prodList.add(tmp);
				}
			}
		}
		
		return prodList;
	}
	
	/**
	 * Metodo che cerca nel db i prodotti contenenti, nel nome o nella descrizione, la parola chiave
	 * @param keyword parola chiave da cercare
	 * @return ritorna la lista di prodotti contenenti la parola chiave nel nome o nella descrizione
	 */
	
	public List<Product> keywordSearch(String keyword) throws SQLException{
		List<Product> prodList = new ArrayList<>();
		
		if(keyword == null || keyword.equals("")) {
			return prodList;
		}
		
		String query = " SELECT id,name,photopath,description,MIN(price) FROM product JOIN product_supplier ON id = productid WHERE name LIKE ? OR description LIKE ? GROUP BY id,name,photopath ORDER BY MIN(price) ";
		PreparedStatement pstatement = con.prepareStatement(query);
		
		pstatement.setString(1, "%" + keyword + "%");
		pstatement.setString(2, "%" + keyword + "%");
		
		ResultSet result = pstatement.executeQuery();
		
		while(result.next()) {
			Product tmp = new Product();
			tmp.setId(result.getInt("id"));
			tmp.setName(result.getString("name"));
			tmp.setPhotoPath("images/" + result.getString("photopath"));
			tmp.setMinPrice(result.getDouble("MIN(price)"));
			tmp.setDescription(result.getString("description"));
			prodList.add(tmp);
		}
		return prodList;	
	}
	
	/**
	 * Metodo che crea l'oggetto prodotto di un certo id prendendo i dati dal DB
	 * @param id interessato
	 * @return ritorna il prodotto richiesto
	 */
	
	public Product getById(int id)  throws SQLException {		
		String query = " SELECT id,name,category,photopath,description,MIN(price) FROM product JOIN product_supplier ON id = productid WHERE id = ? GROUP BY id,name,category,photopath,description ";
		PreparedStatement pstatement = con.prepareStatement(query);
		pstatement.setInt(1, id);
		
		ResultSet result = pstatement.executeQuery();
		Product tmp = null;
		
		if(result.next()) {
			tmp = new Product();
			tmp.setId(result.getInt("id"));
			tmp.setName(result.getString("name"));
			tmp.setCategory(result.getString("category"));
			tmp.setPhotoPath("images/" + result.getString("photopath"));
			tmp.setMinPrice(result.getDouble("MIN(price)"));
			tmp.setDescription(result.getString("description"));
		}
		return tmp;	
	}
}
