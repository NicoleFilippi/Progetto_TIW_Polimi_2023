package it.polimi.tiw.shopjs.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.shopjs.beans.Product;
import it.polimi.tiw.shopjs.beans.User;
import it.polimi.tiw.shopjs.dao.ProductDAO;
import it.polimi.tiw.shopjs.utils.ConnectionHandler;

@WebServlet("/GetLastVisualized")
@MultipartConfig

public class GetLastVisualized extends HttpServlet {
	
	//Servlet che prende gli ultimi prodotti visualizzati dal database
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    public GetLastVisualized() {
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
    		//TODO error
			return;
    	}
		
		HttpSession session = request.getSession();		
				
		List<Product> prodList = null;
		ProductDAO pdao = new ProductDAO(connection);	
		
		User user = (User)session.getAttribute("user");
		
		try {
			prodList = pdao.lastVisualized(user.getEmail(), getServletContext().getInitParameter("defaultCategory"));
		}catch(SQLException e){
			//TODO ERROR
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		String listJson = new GsonBuilder().create().toJson(prodList);
		
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
