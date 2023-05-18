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

import it.polimi.tiw.shop.beans.Cart;
import it.polimi.tiw.shop.dao.PurchaseDAO;
import it.polimi.tiw.shop.dao.SupplierDAO;
import it.polimi.tiw.shop.utils.ConnectionHandler;

@WebServlet("/AddOrder")

public class AddOrder extends HttpServlet {
	
	//Servlet che aggiunge un ordine inviato dall'utente
	
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
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
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(connection == null) {
    		request.setAttribute("logout",true);
			request.setAttribute("error",null);
			request.getRequestDispatcher("Error").forward(request, response);
			return;
    	}
		
		HttpSession session = request.getSession();		
		int supplierId=0;
		
		try {
			supplierId = Integer.parseInt(request.getParameter("supplierId"));
		}catch(Exception e) {
			request.setAttribute("logout",false);
			request.setAttribute("error","Invalid supplier ID");
			request.getRequestDispatcher("Error").forward(request, response);
			return;
		}
		
		//gestione id fornitore non valido
		
		if(supplierId <= 0) {
			request.setAttribute("logout",false);
			request.setAttribute("error","Invalid supplier ID");
			request.getRequestDispatcher("Error").forward(request, response);
			return;
		}
		
		//gestione id fornitore non contenuto nel carrello
		
		if(((Cart)session.getAttribute("cart")).getItems().get(supplierId) == null) {
			request.setAttribute("logout",false);
			request.setAttribute("error","There are no products of this supplier in your cart");
			request.getRequestDispatcher("Error").forward(request, response);
			return;
		}
		
		//inserimento ordine
		
		PurchaseDAO puDAO = new PurchaseDAO(connection);
		try {
			puDAO.addPurchase(new SupplierDAO(connection).getById(supplierId), session);
		} catch (SQLException e) {
			request.setAttribute("logout",true);
			request.setAttribute("error",null);
			request.getRequestDispatcher("Error").forward(request, response);
			return;
		}
				
		String path = getServletContext().getContextPath() + "/Orders";
		response.sendRedirect(path);
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
