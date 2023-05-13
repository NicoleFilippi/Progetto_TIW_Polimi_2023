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

import it.polimi.tiw.shop.beans.Product;
import it.polimi.tiw.shop.beans.User;
import it.polimi.tiw.shop.dao.ProductDAO;
import it.polimi.tiw.shop.utils.ConnectionHandler;

@WebServlet("/Home")

public class GoToHome extends HttpServlet {
	
	//Servlet che manda alla home page
	
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

	public GoToHome() {
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
    	
		HttpSession session = request.getSession();		
		ServletContext servletContext = getServletContext();
		User user = (User) session.getAttribute("user");
		ProductDAO pdao = new ProductDAO(connection);
		List<Product> prodList = null;
		
		//Carica gli ultimi prodotti 5 visualizzati (o quelli di default)
		
		try {
			prodList = pdao.lastVisualized(user.getEmail(), servletContext.getInitParameter("defaultCategory"));
		} catch(Exception e) {
			request.setAttribute("logout",true);
			request.setAttribute("error",null);
			request.getRequestDispatcher("Error").forward(request, response);
			return;
		}
		
		// Carica la Home page
		
		final WebContext context = new WebContext(request, response, servletContext, request.getLocale());		
		context.setVariable("productList", prodList);		
		templateEngine.process("/home.html", context, response.getWriter());
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
