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
import javax.servlet.http.HttpSession;

import com.google.gson.GsonBuilder;

import it.polimi.tiw.shopjs.beans.Order;
import it.polimi.tiw.shopjs.dao.PurchaseDAO;
import it.polimi.tiw.shopjs.utils.ConnectionHandler;

@WebServlet("/GetOrders")

public class GetOrders extends HttpServlet {
	
	//Servlet che preleva l'elenco di ordini
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    public GetOrders() {
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
		
		HttpSession session = request.getSession();
		
		//prende gli ordini dell'utente dal DB
		
		PurchaseDAO puDAO = new PurchaseDAO(connection);
		List<Order> orders;
		
		try {
			orders=puDAO.getByUser((String)session.getAttribute("user"));
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		//allega nella risposta il json con la lista di orders
		
		String ordJson = new GsonBuilder().create().toJson(orders);
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.getWriter().println(ordJson);
		return;
		
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
