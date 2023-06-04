package it.polimi.tiw.shopjs.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.GsonBuilder;

import it.polimi.tiw.shopjs.beans.User;
import it.polimi.tiw.shopjs.dao.UserDAO;
import it.polimi.tiw.shopjs.utils.ConnectionHandler;

@WebServlet("/GetUser")

public class GetUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
    public GetUser() {
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(connection == null) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
    	}
		
		UserDAO uDAO = new UserDAO(connection);
		User result=null;
		
		try {
			result = uDAO.getByEmail((String) request.getSession().getAttribute("user") );
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		if(result == null) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		String userJson = new GsonBuilder().create().toJson(result);
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(userJson);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
