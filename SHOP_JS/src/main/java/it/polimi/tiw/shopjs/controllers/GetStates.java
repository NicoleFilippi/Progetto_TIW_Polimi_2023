package it.polimi.tiw.shopjs.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.GsonBuilder;

import it.polimi.tiw.shopjs.beans.State;
import it.polimi.tiw.shopjs.dao.StateDAO;
import it.polimi.tiw.shopjs.utils.ConnectionHandler;

@WebServlet("/GetStates")

public class GetStates extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
    public GetStates() {
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
		
		StateDAO sDAO = new StateDAO(connection);
		List<State> result=null;
		
		try {
			result = sDAO.getStates();
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		String listJson = new GsonBuilder().create().toJson(result);
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(listJson);
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
