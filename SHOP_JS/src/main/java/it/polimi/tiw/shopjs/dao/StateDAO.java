package it.polimi.tiw.shopjs.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.shopjs.beans.State;


public class StateDAO {
	
	private Connection con;
	
	public StateDAO(Connection connection) {
		this.con = connection;
	}
	
	/**
	 * metodo che salva tutti gli stati dal db e li ritorna
	 * @return lista di stati
	 */
	
	public List<State> getStates() throws SQLException {	
		List<State> states = new ArrayList<>();
		
		String query = "SELECT * FROM state";
		PreparedStatement pstatement = con.prepareStatement(query);
		
		ResultSet result = pstatement.executeQuery();
		
		while(result.next()) {
			State tmp=new State();
			tmp.setIso3(result.getString("iso3"));
			tmp.setIso2(result.getString("iso2"));
			tmp.setName(result.getString("name"));
			states.add(tmp);
		}
				
		return states;	
	}
	
	/**
	 * metodo che controlla se uno stato è valido, ovvero se appartiene al db
	 * @param iso3 sigla di 3 caratteri identificativa dello stato
	 * @return true se sì, altrimenti false
	 */
	
	public boolean isValid(String iso3) throws SQLException {
		String query = "SELECT * FROM state WHERE iso3 = ?";
		PreparedStatement pstatement = con.prepareStatement(query);
		pstatement.setString(1, iso3);
		ResultSet result = pstatement.executeQuery();
		return result.next();
	}
}
