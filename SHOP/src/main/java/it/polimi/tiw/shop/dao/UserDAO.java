package it.polimi.tiw.shop.dao;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.shop.beans.User;

public class UserDAO {
	private Connection con;
	
	public UserDAO(Connection connection) {
		this.con = connection;
	}
	
	public User checkCredentials(String email, String password) throws SQLException {
		User user = new User();
		
		String pwQuery = "SELECT * FROM User WHERE Email = ?";
		
		ResultSet result=null;
		
		PreparedStatement pstatement = con.prepareStatement(pwQuery);
		pstatement.setString(1, email);
		result = pstatement.executeQuery();
		
		
		if (!result.isBeforeFirst()) {
			// no user has this email
			return null;
		}
			
		else {
			result.next();
			MessageDigest algorithm;
			try {
				algorithm = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e) {
				throw new SQLException(e.getMessage());
			}
			
			byte[] toDigest = password.concat(Integer.toString(result.getInt("salt"))).getBytes(StandardCharsets.UTF_8);
			byte[] newHash = algorithm.digest(toDigest);
			
			byte[] dbHash = result.getBytes("passwordHash");
			
			boolean check = true;
			
			for(int i=0; i<newHash.length && check; i++) {
				check = ( newHash[i] == dbHash[i] );
			}
			
			if( !check ) {
				return null;
			}
			
			user.setEmail(result.getString("email"));
			user.setName(result.getString("name"));
			user.setSurname(result.getString("surname"));
			user.setState(result.getString("stateIso3"));
			user.setCity(result.getString("city"));
			user.setStreet(result.getString("street"));
			user.setCivicNumber(result.getString("civicNumber"));
			return user;
		}
	}
}
