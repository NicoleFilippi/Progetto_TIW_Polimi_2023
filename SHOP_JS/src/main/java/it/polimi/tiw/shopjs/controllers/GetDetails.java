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

import it.polimi.tiw.shopjs.beans.Product;
import it.polimi.tiw.shopjs.beans.ProductSupplier;
import it.polimi.tiw.shopjs.dao.ProductDAO;
import it.polimi.tiw.shopjs.dao.ProductSupplierDAO;
import it.polimi.tiw.shopjs.utils.ConnectionHandler;

@WebServlet("/GetDetails")

public class GetDetails extends HttpServlet {
	
	//Servlet che prende i dettagli del prodotto interessato
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    public GetDetails() {
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
		
		//prendo id del prodotto di cui voglio vedere i dettagli
		
		int productId = -1;
		try {
			productId = Integer.parseInt(request.getParameter("id"));
		}catch(Exception e) {
			productId = -1;
		}
		
		//id non valido o non presente
		
		if(productId <= 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid product.");
			return;			
		}
		
		List<ProductSupplier> ps = null;
		Product prodDet = null;
		ProductDAO pdao = new ProductDAO(connection);
		
		try{
			prodDet = pdao.getById(productId);
		} catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		//se non esiste un prodotto con quell'id
		
		if(prodDet == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid product.");
			return;
		}
		
		//il prodotto dettagliato è ora posto negli ultimi visualizzati
		
		ProductSupplierDAO psDAO = new ProductSupplierDAO(connection);
		
		try {
			ps = psDAO.getByProduct(prodDet);
			psDAO.visualizedProduct((String)session.getAttribute("user"), prodDet);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		
		//mando al client la lista dei product-supplier che di quel prodotto
		
		String listJson = new GsonBuilder().create().toJson(ps);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(listJson);
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
