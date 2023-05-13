package it.polimi.tiw.shop.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.shop.beans.Cart;
import it.polimi.tiw.shop.beans.Product;
import it.polimi.tiw.shop.beans.ProductSupplier;
import it.polimi.tiw.shop.beans.Supplier;
import it.polimi.tiw.shop.beans.User;

public class ProductSupplierDAO {
	
	private Connection con;
	
	public ProductSupplierDAO(Connection connection) {
		this.con = connection;
	}
	
	/**
	 * metodo che preleva la lista di prodotto-fornitore di un certo prodotto, in ordine di prezzo
	 * @param prod il prodotto
	 * @param cart il carrello
	 * @return lista di prodotto-fornitore
	 */
	
	public List<ProductSupplier> getByProduct(Product prod, Cart cart) throws SQLException {
		List<ProductSupplier> prodsupp = new ArrayList<ProductSupplier>();
		String query = " SELECT * FROM product_supplier WHERE productid = ? ORDER BY price";
		PreparedStatement pstatement = con.prepareStatement(query);
		
		pstatement.setInt(1, prod.getId());		
		ResultSet result = pstatement.executeQuery();
		
		if(!result.next())
			return null;				
		
		//se presente almeno un fornitore che vende quel prodotto
		
		SupplierDAO suppDAO = new SupplierDAO(con);
		
		do {
			ProductSupplier tmp = new ProductSupplier();
			tmp.setProduct(prod);
			
			Supplier s = suppDAO.getById(result.getInt("supplierid"));
			tmp.setSupplier(s);
			tmp.setPrice(result.getDouble("price"));
			
			int q=0;
			double cost=0.0;
			
			//per ogni fornitore, se ho già dei prodotti nel carrello, salvo quantità e costo totale (da stampare se prodotto in dettaglio)
			
			if(cart.getItems().get(s.getId())!=null) {
				for(ProductSupplier ps : cart.getItems().get(s.getId())) {
					q += cart.getQuantities().get(ps.getId());
					cost += cart.getQuantities().get(ps.getId()) * ps.getPrice();
				}
			}
			
			tmp.getSupplier().setProductsInCartNum(q);
			tmp.getSupplier().setProductsInCartCost(cost);
			
			prodsupp.add(tmp);
			
		}while(result.next());
		
		return prodsupp;
	}
	
	/**
	 * metodo che rende visualizzato un prodotto
	 * @param u utente che visualizza
	 * @param p prodotto visualizzato
	 */
	
	public void visualizedProduct(User u, Product p) throws SQLException {
		String query = "SELECT * FROM user_product WHERE productid = ? AND useremail = ?";
		PreparedStatement pstatement = con.prepareStatement(query);
		
		pstatement.setInt(1, p.getId());
		pstatement.setString(2, u.getEmail());
		
		ResultSet result = pstatement.executeQuery();
		
		//se il prodotto è già visualizzato aggiorno il timestamp
		
		if(result.next()) {
			String update = "UPDATE user_product SET timestamp = ? WHERE productid = ? AND useremail = ?";
			pstatement = con.prepareStatement(update);
			pstatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			pstatement.setInt(2, p.getId());
			pstatement.setString(3, u.getEmail());
			pstatement.executeUpdate();
			return;
		}
		pstatement.close();
		
		query = "SELECT COUNT(*) FROM user_product WHERE useremail = ?";
		pstatement = con.prepareStatement(query);
		pstatement.setString(1, u.getEmail());
		result = pstatement.executeQuery();
		
		//se ci sono 5 (o più, per sicurezza) prodotti visualizzati, rimuovo quelli con timestamp più vecchio fino a che ne rimangono 4
		
		result.next();
		if(result.getInt("COUNT(*)")>=5) {
			
			//prelevo i 5 timestamp più recenti (e in particolare memorizzo l'ultimo)
			
			query="SELECT timestamp FROM user_product WHERE UserEmail = ? ORDER BY timestamp DESC LIMIT 5";
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, u.getEmail());
			result = pstatement.executeQuery();
			
			Timestamp ts;
			for(int i=0; i<5; i++) {
				result.next();
			}
			ts=result.getTimestamp("timestamp");			
			pstatement.close();
			
			//necessito di un'unica transazione per eliminare le entry meno recenti e inserire il nuovo prodotto visualizzato
			
			con.setAutoCommit(false);
			try{
				query="DELETE FROM user_product WHERE UserEmail = ? AND timestamp <= ?";			
				pstatement = con.prepareStatement(query);
				pstatement.setString(1, u.getEmail());
				pstatement.setTimestamp(2, ts);
				pstatement.executeUpdate();
				
				pstatement.close();
				query="INSERT INTO user_product VALUES(?,?,?)";
				pstatement = con.prepareStatement(query);
				pstatement.setString(1,u.getEmail());
				pstatement.setInt(2,p.getId());
				pstatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
				pstatement.executeUpdate();
				
				con.commit();
				
			}catch(Exception e) {
				con.rollback();
				e.printStackTrace();
			}
			
			con.setAutoCommit(true);
			
		}else {
			
			//se invece non ho ancora 5 visualizzati, semplicemente lo aggiungo
			
			pstatement.close();
			query="INSERT INTO User_product VALUES(?,?,?)";
			pstatement = con.prepareStatement(query);
			pstatement.setString(1,u.getEmail());
			pstatement.setInt(2,p.getId());
			pstatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			pstatement.executeUpdate();
		}
	}
	
	/**
	 * metodo che dati id prodotto e fornitore ritorna l'oggetto prodotto-fornitore corrispondente, se presente
	 * @param productId
	 * @param supplierId
	 * @return oggetto prodotto-fornitore
	 */
	
	public ProductSupplier getByIds(int productId, int supplierId) throws SQLException {
		ProductSupplier ps;
		String query = "SELECT * FROM product_supplier WHERE productid = ? AND supplierid = ?";
		PreparedStatement pstatement = con.prepareStatement(query);
		
		pstatement.setInt(1, productId);
		pstatement.setInt(2, supplierId);
		
		ResultSet result = pstatement.executeQuery();
		
		if(!result.next()) 
			return null;
		
		ps=new ProductSupplier();
		ps.setPrice(result.getDouble("price"));
		ps.setSupplier(new SupplierDAO(con).getById(supplierId));
		ps.setProduct(new ProductDAO(con).getById(productId));
		return ps;
	}

}
