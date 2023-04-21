package it.polimi.tiw.shop.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.shop.beans.Product;

public class ProductDAO {
private Connection con;
	
	public ProductDAO(Connection connection) {
		this.con = connection;
	}
	
	public List<Product> lastVisualized(String email, String defaultCategory) throws SQLException {
		List<Product> prodList = new ArrayList<>();
		
		String query = "SELECT * FROM product, user_product WHERE useremail = ? AND id = productid ORDER BY timestamp DESC";
		PreparedStatement pstatement = con.prepareStatement(query);
		
		String minPriceQuery = "SELECT MIN(Price) FROM product_supplier WHERE productId = ? ";
		PreparedStatement minpstatement = con.prepareStatement(minPriceQuery);
		
		pstatement.setString(1, email);
		ResultSet result = pstatement.executeQuery();
		ResultSet minPrice;
		
		while(result.next() && prodList.size() < 5) {
			minpstatement.setInt(1, result.getInt("id"));
			minPrice = minpstatement.executeQuery();
			minPrice.next();
			
			Product tmp = new Product();
			tmp.setId(result.getInt("id"));
			tmp.setName(result.getString("name"));
			tmp.setDescription(result.getString("description"));
			tmp.setCategory(result.getString("category"));
			tmp.setPhotoPath(result.getString("PhotoPath"));
			tmp.setMinPrice(minPrice.getDouble("MIN(price)"));
			prodList.add(tmp);
		}
		
		if( prodList.size() < 5 && defaultCategory!= null) {
			
			String defQuery = "SELECT * FROM Product WHERE category = ?";
			PreparedStatement defpstatement = con.prepareStatement(defQuery);
			defpstatement.setString(1, defaultCategory);
			ResultSet defResult = defpstatement.executeQuery();
		
			while(defResult.next() && prodList.size() < 5) {
				boolean found = false;
				for(int i = 0; i < prodList.size() && !found; i++) found = (defResult.getInt("id") == prodList.get(i).getId());
				if(!found) {
					minpstatement.setInt(1, defResult.getInt("id"));
					minPrice = minpstatement.executeQuery();
					minPrice.next();
					
					Product tmp = new Product();
					tmp.setId(defResult.getInt("id"));
					tmp.setName(defResult.getString("name"));
					tmp.setDescription(defResult.getString("description"));
					tmp.setCategory(defResult.getString("category"));
					tmp.setPhotoPath(defResult.getString("PhotoPath"));
					tmp.setMinPrice(minPrice.getDouble("MIN(price)"));
					prodList.add(tmp);
				}
			}
		}
		
		if(prodList.size()==0) throw new SQLException("Empty");
		
		return prodList;
	}
}
