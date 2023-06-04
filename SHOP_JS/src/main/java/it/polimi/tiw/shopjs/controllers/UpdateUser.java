package it.polimi.tiw.shopjs.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.GsonBuilder;

import it.polimi.tiw.shopjs.beans.User;
import it.polimi.tiw.shopjs.dao.StateDAO;
import it.polimi.tiw.shopjs.dao.UserDAO;
import it.polimi.tiw.shopjs.utils.ConnectionHandler;

@WebServlet("/UpdateUser")
@MultipartConfig

public class UpdateUser extends HttpServlet {
	
	//Servlet che modifica i parametri richiesti dall'utente
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    public UpdateUser() {
        super();
    }
    
    public void init() throws ServletException {
    	try {
    		connection = ConnectionHandler.getConnection(getServletContext());
    	}catch(Exception e) {
    		connection = null;
    		e.printStackTrace();
    	}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(connection == null) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
    	}		
		
		//prendo gli stati dal DB
		
		User user;
		try {
			user = new UserDAO(connection).getByEmail((String)request.getSession().getAttribute("user"));
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		String name = request.getParameter("name");
		if(name != null)
			name = StringEscapeUtils.escapeJava(name);
		
		String surname = request.getParameter("surname");
		if(surname != null)
			surname = StringEscapeUtils.escapeJava(surname);
		
		String street = request.getParameter("street");
		if(street != null)
			street = StringEscapeUtils.escapeJava(street);
		
		String civicNumber = request.getParameter("civicNumber");
		if(civicNumber != null)
			civicNumber = StringEscapeUtils.escapeJava(civicNumber);
		
		String city = request.getParameter("city");
		if(city != null)
			city = StringEscapeUtils.escapeJava(city);
		
		String state = request.getParameter("state");
		if(state != null)
			state = StringEscapeUtils.escapeJava(state);
		
		//se uno dei parametri che l'utente può modificare è vuoto o nullo viene sostituito con quello già presente
		
		if(name == null || name.equals("")) {
			name=user.getName();
		}
		
		if(surname == null || surname.equals("")) {
			surname=user.getSurname();
		}
		
		if(street == null || street.equals("")) {
			street=user.getStreet();
		}
		
		if(civicNumber == null || civicNumber.equals("")) {
			civicNumber=user.getCivicNumber();
		}
		
		if(city == null || city.equals("")) {
			city=user.getCity();
		}
		
		if(state == null || state.equals("")) {
			state=user.getState();
		}
		
		//se nessun parametro è variato rispetto agli originali avvisiamo l'utente senza accedere al DB inutilmente
		
		if(name.equals(user.getName()) &&
			surname.equals(user.getSurname()) &&
			state.equals(user.getState()) &&
			street.equals(user.getStreet()) &&
			city.equals(user.getCity()) &&
			civicNumber.equals(user.getCivicNumber())){
			
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		}
		
		if(!state.equals(user.getState())) {
			StateDAO sDAO = new StateDAO(connection);
			boolean valid = false;
			
			try {
				valid = sDAO.isValid(state);
			}catch(SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}
			
			if(!valid){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Invalid iso3 state. ");
				return;
			}
		}
		
		//modifica parametri
		
		UserDAO udao = new UserDAO(connection);	
		try {
			udao.updateUser(name, surname, state, city, street, civicNumber, user.getEmail());
			user = new UserDAO(connection).getByEmail((String)request.getSession().getAttribute("user"));
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		String userJson = new GsonBuilder().create().toJson(user);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(userJson);
		return;		
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
