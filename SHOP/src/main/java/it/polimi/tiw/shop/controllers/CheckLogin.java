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

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.shop.utils.Cart;
import it.polimi.tiw.shop.beans.User;
import it.polimi.tiw.shop.dao.UserDAO;
import it.polimi.tiw.shop.utils.ConnectionHandler;

@WebServlet("/CheckLogin")

public class CheckLogin extends HttpServlet {
	
	//Servlet che verifica se i dati del login sono corretti
	
	private static final long serialVersionUID = 1L;	
	private Connection connection = null;
	private TemplateEngine templateEngine;

    public CheckLogin() {
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
		String email = request.getParameter("email");
		if(email != null)
			email = StringEscapeUtils.escapeJava(email);
		
		String password = request.getParameter("password");
		if(password != null)
			password = StringEscapeUtils.escapeJava(password);
		
		//se un parametro è nullo o vuoto -> errore
		
		if(email == null || password == null || email.isEmpty() || password.isEmpty()) {
			final WebContext context = new WebContext(request, response, getServletContext(), request.getLocale());
			context.setVariable("error", "Insert email and password");
			context.setVariable("prevEmail", email);
			context.setVariable("prevPassword", password);
			templateEngine.process("/index.html", context, response.getWriter());
			return;
		}
		
		//Controllo credenziali
		
		UserDAO userDao = new UserDAO(connection);
		User user=null;
		
		try {
			user = userDao.checkCredentials(email, password);
		} catch (SQLException e) {
			request.setAttribute("logout",true);
			request.setAttribute("error",null);
			request.getRequestDispatcher("Error").forward(request, response);
			return;
		}
		
		//errore login errato
		
		if(user == null) {
			final WebContext context = new WebContext(request, response, getServletContext(), request.getLocale());
			context.setVariable("error", "Incorrect credentials ");
			context.setVariable("prevEmail", email);
			context.setVariable("prevPassword", password);
			templateEngine.process("/index.html", context, response.getWriter());
			return;
			
		}
		else {
			session.setAttribute("user", user);
			session.setAttribute("cart", new Cart());

			String path = getServletContext().getContextPath() + "/Home";
			response.sendRedirect(path);
		}
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
