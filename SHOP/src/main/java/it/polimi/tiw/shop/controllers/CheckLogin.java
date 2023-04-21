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

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.shop.beans.User;
import it.polimi.tiw.shop.dao.UserDAO;
import it.polimi.tiw.shop.utils.ConnectionHandler;

@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

    public CheckLogin() {
    	super();
    }
    
    public void init() throws ServletException {
    	connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email = StringEscapeUtils.escapeJava(request.getParameter("email"));
		String password = StringEscapeUtils.escapeJava(request.getParameter("password"));
		
		if(email == null || password == null || email.isEmpty() || password.isEmpty()) {
			//TODO GESTIONE ERRORE DI LOGIN
			
			final WebContext context = new WebContext(request, response, getServletContext(), request.getLocale());
			context.setVariable("errorMex", "Insert email and password");
			context.setVariable("prevEmail", email);
			context.setVariable("prevPassword", password);
			templateEngine.process("/index.html", context, response.getWriter());
			return;
		}
		
		UserDAO userDao = new UserDAO(connection);
		User user=null;
		
		try {
			user = userDao.checkCredentials(email, password);
		} catch (SQLException e) {
			//TODO HANDLE EXCEPTION
			
			
		}
		
		if(user == null) {
			// LOGIN ERRATO
			final WebContext context = new WebContext(request, response, getServletContext(), request.getLocale());
			context.setVariable("errorMex", "Incorrect credentials ");
			context.setVariable("prevEmail", email);
			context.setVariable("prevPassword", password);
			templateEngine.process("/index.html", context, response.getWriter());
			return;
			
		}
		else {
			request.getSession().setAttribute("user", user);
			
			//TODO GESTIONE LOGIN CORRETTO

			final WebContext context = new WebContext(request, response, getServletContext(), request.getLocale());
			context.setVariable("errorMex", user.getName() + " " + user.getSurname() + ": " + user.getStreet() + " " + user.getCivicNumber()  + ", " + user.getCity() + ", " + user.getState());
			
			templateEngine.process("/index.html", context, response.getWriter());
			return;
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
