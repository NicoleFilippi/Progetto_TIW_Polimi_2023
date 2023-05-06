package it.polimi.tiw.shop.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.shop.beans.Order;
import it.polimi.tiw.shop.beans.User;
import it.polimi.tiw.shop.dao.PurchaseDAO;
import it.polimi.tiw.shop.utils.ConnectionHandler;

@WebServlet("/Orders")
public class GoToOrders extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
       
    public GoToOrders() {
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
		
		final WebContext context = new WebContext(request, response, getServletContext(), request.getLocale());
		
		PurchaseDAO puDAO = new PurchaseDAO(connection);
		List<Order> orders;
		try {
			orders=puDAO.getByUser((User)session.getAttribute("user"));
		}catch(Exception e) {
			//TODO handle exception
			final WebContext errcontext = new WebContext(request, response, getServletContext(), request.getLocale());
			errcontext.setVariable("error", "SQL error: " + e.getMessage());
			templateEngine.process("/orders.html", errcontext, response.getWriter());
			return;
		}
		
		context.setVariable("orders", orders);
		templateEngine.process("/orders.html", context, response.getWriter());
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
