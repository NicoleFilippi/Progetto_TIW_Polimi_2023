package it.polimi.tiw.shop.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.shop.utils.Cart;
import it.polimi.tiw.shop.beans.Supplier;
import it.polimi.tiw.shop.dao.SupplierDAO;
import it.polimi.tiw.shop.utils.ConnectionHandler;

@WebServlet("/RemoveFromCart")

public class RemoveFromCart extends HttpServlet {
	
	//Servlet che rimuove i prodotti di un fornitore dal carrello
	
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
       
    public RemoveFromCart() {
        super();
    }
    
    public void init() throws ServletException {
    	try {
    		connection = ConnectionHandler.getConnection(getServletContext());
    	}catch(Exception e) {
    		connection = null;
    		e.printStackTrace();
    	}
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	if(connection == null) {
    		request.setAttribute("logout",true);
			request.setAttribute("error",null);
			request.getRequestDispatcher("Error").forward(request, response);
			return;
    	}
    	
    	//controllo validità parametro
    	
		HttpSession session = request.getSession();		
		int supplierId;
		
		try {
			supplierId = Integer.parseInt(request.getParameter("supplierId"));
		}catch(Exception e) {
			request.setAttribute("logout",false);
			request.setAttribute("error","Your request has invalid or missing parameters");
			request.getRequestDispatcher("Error").forward(request, response);
			return;
		}
		
		Supplier s = null;
		
		try {
			s = new SupplierDAO(connection).getById(supplierId);
		} catch (SQLException e) {
			request.setAttribute("logout",true);
			request.setAttribute("error",null);
			request.getRequestDispatcher("Error").forward(request, response);
			return;
		}
		
		//rimozione dal carrello
				
		((Cart)session.getAttribute("cart")).removeSupplier(s);
				
		String path = getServletContext().getContextPath() + "/Cart";
		response.sendRedirect(path);
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
