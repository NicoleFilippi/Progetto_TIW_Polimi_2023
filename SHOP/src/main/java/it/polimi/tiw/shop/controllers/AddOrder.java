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
import it.polimi.tiw.shop.beans.User;
import it.polimi.tiw.shop.dao.PurchaseDAO;
import it.polimi.tiw.shop.dao.SupplierDAO;
import it.polimi.tiw.shop.utils.ConnectionHandler;

@WebServlet("/AddOrder")
public class AddOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
       
    public AddOrder() {
        super();
    }
    
    public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
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
		
		if(supplierId<=0) {
			request.setAttribute("logout",false);
			request.setAttribute("error","Invalid supplier ID");
			request.getRequestDispatcher("Error").forward(request, response);
			return;
		}
		
		if(((Cart)session.getAttribute("cart")).getItems().get(supplierId)==null) {
			request.setAttribute("logout",false);
			request.setAttribute("error","There are no products by this supplier in your cart");
			request.getRequestDispatcher("Error").forward(request, response);
			return;
		}
		
		PurchaseDAO puDAO = new PurchaseDAO(connection);
		try {
			puDAO.addPurchase(new SupplierDAO(connection).getById(supplierId), (Cart)session.getAttribute("cart"), (User)session.getAttribute("user"));
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
