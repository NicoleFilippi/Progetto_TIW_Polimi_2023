package it.polimi.tiw.shopjs.dao;

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


import it.polimi.tiw.shopjs.beans.ClientOrder;
import it.polimi.tiw.shopjs.beans.Order;
import it.polimi.tiw.shopjs.beans.Product;
import it.polimi.tiw.shopjs.beans.ProductSupplier;
import it.polimi.tiw.shopjs.beans.Supplier;

public class PurchaseDAO {
	
	private Connection con;
	
	public PurchaseDAO(Connection connection) {
		this.con = connection;
	}
	
	/**
	 * metodo che costruisce un Ordine dai parametri inviati da client
	 * @param order oggetto ClientOrder inviato da client
	 */
	
	public Order generateOrder(ClientOrder order) throws SQLException{
		
		Order result = new Order();
		SupplierDAO sDAO = new SupplierDAO(con);
		ProductSupplierDAO psDAO = new ProductSupplierDAO(con);
		
		if(order.getQuantities().size()!=order.getProductIds().size() || order.getQuantities().size()==0);
		
		Supplier supp=null;
		List<ProductSupplier> prodSuppList = new ArrayList<>();
		Map<Integer,Integer> prodQuantities = new HashMap<>();
		double prodCost = 0;
		double shippingCost;
		int prodNum = 0;
		
		supp = sDAO.getById(order.getSupplierId());
		if(supp==null) return null;
		
		for(int i=0; i<order.getProductIds().size(); i++) {
			ProductSupplier ps = psDAO.getByIds(order.getProductIds().get(i),supp.getId());
			
			if(ps==null) return null;
			prodSuppList.add(ps);
			
			if(order.getQuantities().get(i)<=0) return null;
			prodQuantities.put(ps.getProduct().getId(),order.getQuantities().get(i));
			prodCost += order.getQuantities().get(i) * ps.getPrice();
			prodNum += order.getQuantities().get(i);
		}
		
		if(supp.getFreeShippingThreshold()>=0 && prodCost>=supp.getFreeShippingThreshold()) shippingCost = 0;
		else {
			int min = 1;
			for(int i=0; i<supp.getMinQuantities().size(); i++) {
				if(supp.getMinQuantities().get(i)>min && supp.getMinQuantities().get(i)<=prodNum)
					min = supp.getMinQuantities().get(i);
			}
			shippingCost = supp.getShippingPrices().get(min);
		}
		
		result.setSupplier(supp);
		result.setProducts(prodSuppList);
		result.setQuantities(prodQuantities);
		result.setTotal(prodCost + shippingCost);
		result.setShippingPrice(shippingCost);
		
		return result;
	}
	
	/**
	 * metodo per aggiungere un ordine
	 * @param supplier
	 * @param session oggetto sessione passato dalla servlet
	 */
	
	public void addPurchase(Order order, String user, String state, String city, String street, String civicNumber) throws SQLException {
		
		//necessità di un'unica transazione in cui si inserisce sia l'ordine che tutti i prodotti contenuti (in due tabelle diverse)
		
		con.setAutoCommit(false);
		
		try{
			String query = " INSERT INTO Purchase(total, date, supplierId, shippingPrice, userEmail, StateIso3, City, Street, CivicNumber) VALUES (?,?,?,?,?,?,?,?,?)";
			PreparedStatement pstatement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			
			pstatement.setDouble(1, order.getTotal());
			pstatement.setDate(2, new Date(System.currentTimeMillis()));
			pstatement.setInt(3, order.getSupplier().getId());
			pstatement.setDouble(4, order.getShippingPrice());
			pstatement.setString(5, user);
			pstatement.setString(6, state);
			pstatement.setString(7, city);
			pstatement.setString(8, street);
			pstatement.setString(9, civicNumber);
			
			pstatement.executeUpdate();
						
			ResultSet rs = pstatement.getGeneratedKeys();		//preleva la chiave auto-increment generata automaticamente dal DB
			rs.next();
			int purchaseId = rs.getInt(1);
			
			pstatement.close();
			
			query="INSERT INTO Purchase_product VALUES (?, ?, ?, ?)";
			pstatement = con.prepareStatement(query);
			pstatement.setInt(2, purchaseId);
			
			//inserisco tutti i prodotti
			
			for(int i=0; i<order.getProducts().size(); i++) {
				pstatement.setInt(1, order.getProducts().get(i).getProduct().getId());
				pstatement.setInt(3, order.getQuantities().get(order.getProducts().get(i).getProduct().getId()));
				pstatement.setDouble(4, order.getProducts().get(i).getPrice());
				pstatement.executeUpdate();
			}
			
			con.commit();
			
		}catch(Exception e) {
			
			con.rollback();
			e.printStackTrace();
			
		}

		con.setAutoCommit(true);		
	}
	
	/**
	 * metodo che, dato l'utente, ritorna la lista ordini con data decrescente
	 * @param user utente
	 * @return lista di ordini
	 */
	
	public List<Order> getByUser(String user) throws SQLException{
		List<Order> orders = new ArrayList<>();
		
		String query = " SELECT * FROM Purchase WHERE userEmail = ? ORDER BY date DESC,id DESC";		
		PreparedStatement pstatement = con.prepareStatement(query);
		
		pstatement.setString(1, user);		
		ResultSet result = pstatement.executeQuery();
				
		String query2="SELECT * FROM purchase_product WHERE Purchaseid = ? ";
		PreparedStatement pstatement2 = con.prepareStatement(query2);
		
		SupplierDAO sDAO = new SupplierDAO(con);
		ProductDAO pDAO = new ProductDAO(con);
		
		//per ogni ordine creo un oggetto e prelevo dal db i prodotti acquistati
		
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
