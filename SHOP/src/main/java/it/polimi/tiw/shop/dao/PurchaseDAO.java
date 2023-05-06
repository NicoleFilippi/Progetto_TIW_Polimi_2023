package it.polimi.tiw.shop.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.tiw.shop.beans.Cart;
import it.polimi.tiw.shop.beans.Order;
import it.polimi.tiw.shop.beans.Product;
import it.polimi.tiw.shop.beans.ProductSupplier;
import it.polimi.tiw.shop.beans.Supplier;
import it.polimi.tiw.shop.beans.User;

public class PurchaseDAO {
	
	private Connection con;
	
	public PurchaseDAO(Connection connection) {
		this.con = connection;
	}
	
	public void addPurchase(Supplier supplier, Cart cart, User user) throws SQLException {
		
		con.setAutoCommit(false);
		try{
			String query = " INSERT INTO Purchase(total, date, supplierId, shippingPrice, userEmail, StateIso3, City, Street, CivicNumber) VALUES (?,?,?,?,?,?,?,?,?)";
			PreparedStatement pstatement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		
			Supplier cartSupplier=null;
			for(int i=0; i<cart.getSuppliers().size(); i++) {
				if(cart.getSuppliers().get(i).equals(supplier))
					cartSupplier=cart.getSuppliers().get(i);
			}
			if(cartSupplier==null)
				return;
			
			pstatement.setDouble(1, cartSupplier.getTotalCost());
			pstatement.setDate(2, new Date(System.currentTimeMillis()));
			pstatement.setInt(3, cartSupplier.getId());
			pstatement.setDouble(4, cartSupplier.getShippingCost());
			pstatement.setString(5, user.getEmail());
			pstatement.setString(6, user.getState());
			pstatement.setString(7, user.getCity());
			pstatement.setString(8, user.getStreet());
			pstatement.setString(9, user.getCivicNumber());
			
			pstatement.executeUpdate();
			
			ResultSet rs = pstatement.getGeneratedKeys();
			rs.next();
			int purchaseId = rs.getInt(1);
			
			pstatement.close();
			
			query="INSERT INTO Purchase_product VALUES (?, ?, ?, ?)";
			pstatement = con.prepareStatement(query);
			pstatement.setInt(2, purchaseId);
			
			for(int i=0; i<cart.getItems().get(cartSupplier.getId()).size(); i++) {
				pstatement.setInt(1, cart.getItems().get(cartSupplier.getId()).get(i).getProduct().getId());
				pstatement.setInt(3, cart.getQuantities().get(cart.getItems().get(cartSupplier.getId()).get(i).getId()));
				pstatement.setDouble(4, cart.getItems().get(cartSupplier.getId()).get(i).getPrice());
				pstatement.executeUpdate();
			}
			
			con.commit();
			
			cart.removeSupplier(cartSupplier);
			
		}catch(Exception e) {
			con.rollback();
			e.printStackTrace();
		}

		con.setAutoCommit(true);		
		
	}
	
	public List<Order> getByUser(User user) throws SQLException{
		List<Order> orders = new ArrayList<>();
		
		String query = " SELECT * FROM Purchase WHERE userEmail = ? ORDER BY date DESC";
		
		PreparedStatement pstatement = con.prepareStatement(query);
		
		pstatement.setString(1, user.getEmail());
		
		ResultSet result = pstatement.executeQuery();
				
		String query2="SELECT * FROM purchase_product WHERE Purchaseid = ? ";
		PreparedStatement pstatement2 = con.prepareStatement(query2);
		
		SupplierDAO sDAO = new SupplierDAO(con);
		ProductDAO pDAO = new ProductDAO(con);
		
		while(result.next()){
			
			Order o = new Order();
			Supplier s;
			o.setId(result.getInt("id"));
			o.setTotal(result.getDouble("total"));
			o.setDate(result.getDate("date"));
			s = sDAO.getById(result.getInt("supplierId"));
			o.setSupplier(s);
			o.setShippingPrice(result.getDouble("ShippingPrice"));
			o.setUserEmail(result.getString("useremail"));
			o.setStateIso3(result.getString("stateiso3"));
			o.setCity(result.getString("city"));
			o.setStreet(result.getString("street"));
			o.setCivicNumber(result.getString("civicnumber"));
			
			pstatement2.setInt(1, o.getId());
			ResultSet result2 = pstatement2.executeQuery();
			
			List<ProductSupplier> psList= new ArrayList<>();
			Map<Integer, Integer> qList = new HashMap<>();
			
			while(result2.next()){
				ProductSupplier ps = new ProductSupplier();
				ps.setSupplier(s);
				Product p = pDAO.getById(result2.getInt("productId"));
				ps.setProduct(p);
				ps.setPrice(result2.getDouble("price"));
				psList.add(ps);
				
				qList.put(result2.getInt("ProductID"), result2.getInt("quantity"));
				
			}
			o.setProducts(psList);
			o.setQuantities(qList);
			
			orders.add(o);
			
		}
		
		return orders;
	}
}
