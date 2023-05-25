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

import it.polimi.tiw.shopjs.beans.Cart;
import it.polimi.tiw.shopjs.beans.User;
import it.polimi.tiw.shopjs.dao.UserDAO;
import it.polimi.tiw.shopjs.utils.ConnectionHandler;

@WebServlet("/CheckLogin")
@MultipartConfig

public class CheckLogin extends HttpServlet {

	// Servlet che verifica se i dati del login sono corretti

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

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (connection == null) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("no db");
			return;
		}

		HttpSession session = request.getSession();
		String email = request.getParameter("email");
		String psw = request.getParameter("password");
		if(email != null)
			email = StringEscapeUtils.escapeJava(email);
		
		if(psw != null)
			psw = StringEscapeUtils.escapeJava(psw);
		
		// se un parametro è nullo o vuoto -> errore

		if (email == null || psw == null || email.isEmpty() || psw.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("no param");
		}

		// Controllo credenziali

		UserDAO userDao = new UserDAO(connection);
		User user = null;

		try {
			user = userDao.checkCredentials(email, psw);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("no db");
			return;
		}

		// errore login errato

		if (user == null) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("no cred");
			return;

		} else {
			response.setStatus(HttpServletResponse.SC_OK);
			session.setAttribute("user", user);
			if (session.getAttribute("cart") == null)
				session.setAttribute("cart", new Cart());
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
