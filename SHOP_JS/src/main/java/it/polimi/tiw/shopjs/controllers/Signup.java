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

import it.polimi.tiw.shopjs.dao.StateDAO;
import it.polimi.tiw.shopjs.dao.UserDAO;
import it.polimi.tiw.shopjs.utils.ConnectionHandler;

@WebServlet("/Signup")
@MultipartConfig

public class Signup extends HttpServlet {
	
	//Servlet per creare un account
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    public Signup() {
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
			response.getWriter().println("Server error, please try again later. ");
			return;
    	}		
		
		String name = request.getParameter("name");	
		if(name != null)									//solo se non è null per evitare NullPointerException
			name = StringEscapeUtils.escapeJava(name);		//metodo che "rende sicuro" il parametro di una richiesta HTTP
		
		String surname = request.getParameter("surname");
		if(surname != null)
			surname = StringEscapeUtils.escapeJava(surname);
		
		String email = request.getParameter("email");
		if(email != null)
			email = StringEscapeUtils.escapeJava(email);
		
		String password = request.getParameter("password");
		if(password != null)
			password = StringEscapeUtils.escapeJava(password);
		
		String confpwd = request.getParameter("confpwd");
		if(confpwd != null)
			confpwd = StringEscapeUtils.escapeJava(confpwd);
		
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
		
		//controllo che ogni parametro sia presente, altrimenti setto l'errore
		
		if(name == null || name.equals("")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Name required. ");
			return;
		}
		
		if(surname == null || surname.equals("")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Surname required. ");
			return;
		}
		
		if(email == null || email.equals("")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Email required. ");
			return;
		}
		
		if(password == null || password.equals("")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Password required. ");
			return;
		}
		
		if(confpwd == null || confpwd.equals("")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Confirm password required. ");
			return;
		}
		
		if(street == null || street.equals("")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Street required. ");
			return;
		}
		
		if(civicNumber == null || civicNumber.equals("")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Civic number required. ");
			return;
		}
		
		if(city == null || city.equals("")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("City required. ");
			return;
		}
		
		if(state == null || state.equals("")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("State required. ");
			return;
		}
		
		if(!password.equals(confpwd)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("The two passwords do not match. ");
			return;
		}
		
		//controllo che la mail sia sintatticamente valida
		
		UserDAO udao = new UserDAO(connection);
		StateDAO sdao = new StateDAO(connection);
		
		if(!udao.isValidEmail(email)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid email. ");
			return;
		}
		
		//controllo che lo stato sia valido e che la mail non sia già presente nel DB
		 
		boolean validState = false;
		boolean freeEmail = false;
				
		try {
			validState = sdao.isValid(state);
			freeEmail = udao.isFreeEmail(email);
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Server error, please try again later. ");
			return;
		}
		
		if(!validState) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid iso3 state. ");
			return;
		}
		
		if(!freeEmail) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("An account with this email already exists. ");
			return;
		}
		
		//inserisco l'utente nuovo
		
		try {
			udao.addUser(email, name, surname, state, city, street, civicNumber, password);
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Server error, please try again later. ");
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
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
