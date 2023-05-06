package it.polimi.tiw.shop.dao;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import it.polimi.tiw.shop.beans.User;

public class UserDAO {
	private Connection con;
	
	public UserDAO(Connection connection) {
		this.con = connection;
	}
	
	public User checkCredentials(String email, String password) throws SQLException {
		
		User user = new User();
		
		String pwQuery = "SELECT * FROM User WHERE Email = ?";
		
		PreparedStatement pstatement = con.prepareStatement(pwQuery);
		pstatement.setString(1, email);
		ResultSet result = pstatement.executeQuery();
		
		
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
	
	public boolean isValidEmail(String email) {
		if (email == null)
			return false;
		Pattern pat = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
		return pat.matcher(email).matches();
	}
	
	public boolean isFreeEmail(String email) throws SQLException {
		if (email == null)
			return false;
		String query = "SELECT * FROM User WHERE Email = ?";
		PreparedStatement pstatement = con.prepareStatement(query);
		pstatement.setString(1, email);
		ResultSet result = pstatement.executeQuery();
		return !result.next();
	}
	
	public void addUser(String email, String name, String surname, String iso3, String city, String street, String civicNumber, String password) throws SQLException {
		
		String query = "INSERT INTO user VALUES (?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstatement = con.prepareStatement(query);
		pstatement.setString(1, email);
		pstatement.setString(2, name);
		pstatement.setString(3, surname);
		pstatement.setString(4, iso3);
		pstatement.setString(5, city);
		pstatement.setString(6, street);
		pstatement.setString(7, civicNumber);
		
		int salt = (int)(Math.random()*900) + 100;
		
		MessageDigest algorithm;
		try {
			algorithm = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new SQLException(e.getMessage());
		}
		byte[] toDigest = password.concat(Integer.toString(salt)).getBytes(StandardCharsets.UTF_8);
		byte[] newHash = algorithm.digest(toDigest);
		pstatement.setBytes(8, newHash);
		pstatement.setInt(9, salt);
		
		pstatement.executeUpdate();
		
	}
	
	public void updateUser(String name, String surname, String iso3, String city, String street, String civicNumber, HttpSession session) throws SQLException {
		
		User u =(User)session.getAttribute("user");
		
		String query = "UPDATE user SET name=?, surname=?, stateiso3=?, city=?, street=?, civicnumber=? WHERE email=?";
		PreparedStatement pstatement = con.prepareStatement(query);
		pstatement.setString(1, name);
		pstatement.setString(2, surname);
		pstatement.setString(3, iso3);
		pstatement.setString(4, city);
		pstatement.setString(5, street);
		pstatement.setString(6, civicNumber);	
		pstatement.setString(7, u.getEmail());
		
		pstatement.executeUpdate();
		
		u.setName(name);
		u.setSurname(surname);
		u.setState(iso3);
		u.setCity(city);
		u.setStreet(street);
		u.setCivicNumber(civicNumber);
				
	}
}
