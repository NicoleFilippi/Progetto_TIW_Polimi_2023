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
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

	public GoToHome() {
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// Redirect to the login if the user is not logged in
		String loginpath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		
		//Load last seen products
		ServletContext servletContext = getServletContext();
		User user = (User) session.getAttribute("user");
		ProductDAO pdao = new ProductDAO(connection);
		List<Product> prodList = null;
		try {
			prodList = pdao.lastVisualized(user.getEmail(), servletContext.getInitParameter("defaultCategory"));
		} catch(Exception e) {
			// TODO handle exception
			final WebContext context = new WebContext(request, response, servletContext, request.getLocale());
			context.setVariable("error", "SQL error: " + e.getMessage());
			templateEngine.process("/home.html", context, response.getWriter());
			return;
		}
		
		// Load the Home page
		
		final WebContext context = new WebContext(request, response, servletContext, request.getLocale());
		
		context.setVariable("productList", prodList);
		
		//context.setVariable("prod1", servletContext.getInitParameter("defaultCategory"));
		
		templateEngine.process("/home.html", context, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
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
