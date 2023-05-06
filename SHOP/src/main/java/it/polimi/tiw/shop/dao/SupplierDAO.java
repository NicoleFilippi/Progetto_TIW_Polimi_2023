package it.polimi.tiw.shop.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.tiw.shop.beans.Supplier;

public class SupplierDAO {
	
	private Connection con;
	
	public SupplierDAO(Connection connection) {
		this.con = connection;
	}
	
	public Supplier getById(int id) throws SQLException {
		Supplier supp = new Supplier();
		String query = " SELECT * FROM supplier JOIN pricerange on id=supplierid WHERE id = ? ORDER BY minquantity";
		PreparedStatement pstatement = con.prepareStatement(query);
		
		pstatement.setInt(1, id);
		
		ResultSet result = pstatement.executeQuery();
		
		if(!result.next())
			return null;
		
		//SE PRESENTE
		supp.setId(id);
		supp.setName(result.getString("name"));
		supp.setRating(result.getInt("rating"));
		supp.setFreeShippingThreshold(result.getDouble("freeShippingThreshold"));
		
		List<Integer> qts = new ArrayList<>();
		Map<Integer,Double> prs = new HashMap<>();
		
		qts.add(result.getInt("minQuantity"));
		prs.put(result.getInt("minQuantity"),result.getDouble("shippingPrice"));
		
		while(result.next()) {
			qts.add(result.getInt("minQuantity"));
			prs.put(result.getInt("minQuantity"),result.getDouble("shippingPrice"));
		}
		
		supp.setMinQuantities(qts);
		supp.setShippingPrices(prs);
		
		return supp;
	}
}
