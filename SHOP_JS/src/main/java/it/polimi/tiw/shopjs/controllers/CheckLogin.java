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
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.GsonBuilder;

import it.polimi.tiw.shopjs.beans.User;
import it.polimi.tiw.shopjs.dao.UserDAO;
import it.polimi.tiw.shopjs.utils.ConnectionHandler;

@WebServlet("/CheckLogin")
@MultipartConfig

public class CheckLogin extends HttpServlet {
	
	//servlet che controlla effettua il login dell'utente

	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CheckLogin() {
		super();
	}

	public void init() throws ServletException {
		try {
			connection = ConnectionHandler.getConnection(getServletContext());
		} catch (Exception e) {
			connection = null;
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (connection == null) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Server error, please try again later.");
			return;
		}

		HttpSession session = request.getSession();
		String email = request.getParameter("email");
		String psw = request.getParameter("password");
		
		//prelevo parametri dalla richiesta
		
		if(email != null)
			email = StringEscapeUtils.escapeJava(email);
		
		if(psw != null)
			psw = StringEscapeUtils.escapeJava(psw);
		
		//controllo se sono nulli o vuoti e mando errore

		if (email == null || psw == null || email.isEmpty() || psw.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Empty credentials.");
			return;
		}

		UserDAO userDao = new UserDAO(connection);
		User user = null;

		//Controllo se l'utente esiste
		
		try {
			user = userDao.checkCredentials(email, psw);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Server error, please try again later.");
			return;
		}

		if (user == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect credentials.");
			return;

		}
		
		//se esiste memorizzo nella sessione lato server la mail dell'utente e mando al client l'intero utente
		
		else {
			session.setAttribute("user", user.getEmail());
			String userJson = new GsonBuilder().create().toJson(user);
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println(userJson);
		}
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
