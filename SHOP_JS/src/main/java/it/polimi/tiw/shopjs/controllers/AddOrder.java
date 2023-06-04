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

import com.google.gson.Gson;

import it.polimi.tiw.shopjs.beans.ClientOrder;
import it.polimi.tiw.shopjs.beans.Order;
import it.polimi.tiw.shopjs.dao.PurchaseDAO;
import it.polimi.tiw.shopjs.dao.StateDAO;
import it.polimi.tiw.shopjs.utils.ConnectionHandler;

@WebServlet("/AddOrder")
@MultipartConfig

public class AddOrder extends HttpServlet {
	
	//Servlet che aggiunge un ordine inviato dall'utente
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    public AddOrder() {
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
		
		String user = (String)request.getSession().getAttribute("user");
		String json = request.getParameter("order");

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
		
		if(json == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    		response.getWriter().println("Order details are missing.");
			return;
		}
		if(state == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("State is missing.");
			return;
		}
		if(city == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("City is missing.");
			return;
		}
		if(street == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Street is missing.");
			return;
		}
		if(civicNumber == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Civic number is missing.");
			return;
		}
		
		boolean valid=false;
		try {
			valid = new StateDAO(connection).isValid(state);
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		if(!valid) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("State not valid.");
			return;
		}
		
		ClientOrder corder = null;
		try {
			corder = new Gson().fromJson(json, ClientOrder.class);
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid order json.");
			return;
		}
		
		//Crea oggetto Ordine da ClientOrder
		
		PurchaseDAO puDAO = new PurchaseDAO(connection);
		Order order = null;
		
		try {
			order = puDAO.generateOrder(corder);
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		if(order == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect order data.");
			return;
		}
		
		//inserimento ordine		
		try {
			puDAO.addPurchase(order,user,state,city,street,civicNumber);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		request.getRequestDispatcher("GetOrders").forward(request, response);
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
