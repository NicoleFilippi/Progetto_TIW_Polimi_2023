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

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.shop.beans.State;
import it.polimi.tiw.shop.dao.StateDAO;
import it.polimi.tiw.shop.dao.UserDAO;
import it.polimi.tiw.shop.utils.ConnectionHandler;

@WebServlet("/AddUser")
public class AddUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
       
    public AddUser() {
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				
		final WebContext context = new WebContext(request, response, getServletContext(), request.getLocale());
		
		String name = StringEscapeUtils.escapeJava(request.getParameter("name"));
		String surname = StringEscapeUtils.escapeJava(request.getParameter("surname"));
		String email = StringEscapeUtils.escapeJava(request.getParameter("email"));
		String password = StringEscapeUtils.escapeJava(request.getParameter("password"));
		String confpwd = StringEscapeUtils.escapeJava(request.getParameter("confpwd"));
		String street = StringEscapeUtils.escapeJava(request.getParameter("street"));
		String civicNumber = StringEscapeUtils.escapeJava(request.getParameter("civicNumber"));
		String city = StringEscapeUtils.escapeJava(request.getParameter("city"));
		String state = StringEscapeUtils.escapeJava(request.getParameter("state"));
		
		if(name == null || name.equals("")) {
			setErrorParameters(request,context);
			context.setVariable("error", "Name required");
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		if(surname == null || surname.equals("")) {
			setErrorParameters(request,context);
			context.setVariable("error", "Surname required");
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		if(email == null || email.equals("")) {
			setErrorParameters(request,context);
			context.setVariable("error", "Email required");
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		if(password == null || password.equals("")) {
			setErrorParameters(request,context);
			context.setVariable("error", "Password required");
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		if(confpwd == null || confpwd.equals("")) {
			setErrorParameters(request,context);
			context.setVariable("error", "Confirm password required");
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		if(street == null || street.equals("")) {
			setErrorParameters(request,context);
			context.setVariable("error", "Street required");
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		if(civicNumber == null || civicNumber.equals("")) {
			setErrorParameters(request,context);
			context.setVariable("error", "Civic number required");
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		if(city == null || city.equals("")) {
			setErrorParameters(request,context);
			context.setVariable("error", "City required");
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		if(state == null || state.equals("")) {
			setErrorParameters(request,context);
			context.setVariable("error", "State required");
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		if(!password.equals(confpwd)) {
			setErrorParameters(request,context);
			context.setVariable("error", "Passwords are different");
			context.setVariable("prevPassword", "");
			context.setVariable("prevConfpwd", "");
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		
		UserDAO udao = new UserDAO(connection);
		StateDAO sdao = new StateDAO(connection);
		
		if(!udao.isValidEmail(email)) {
			setErrorParameters(request,context);
			context.setVariable("error", "Invalid email");
			context.setVariable("prevEmail", "");
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		
		boolean validState = false;
		boolean freeEmail = false;
		
		try {
			validState = sdao.isValid(state);
			freeEmail = udao.isFreeEmail(email);
		}catch(SQLException e) {
			setErrorParameters(request,context);
			context.setVariable("error", "SQL Error: " + e.getMessage());
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		if(!validState) {
			setErrorParameters(request,context);
			context.setVariable("error", "Invalid email");
			context.setVariable("prevState", sdao.getClientState(request));
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		if(!freeEmail) {
			setErrorParameters(request,context);
			context.setVariable("error", "An account with this email already exists");
			context.setVariable("prevEmail", "");
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		
		try {
			udao.addUser(email, name, surname, state, city, street, civicNumber, password);
		}catch(SQLException e) {
			setErrorParameters(request,context);
			context.setVariable("error", "SQL Error: " + e.getMessage());
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		
		context.setVariable("error", "New account created");
		templateEngine.process("/index.html", context, response.getWriter());
		return;
		
	}
	
	private void setErrorParameters(HttpServletRequest request, WebContext context) {
		context.setVariable("prevName", StringEscapeUtils.escapeJava(request.getParameter("name")));
		context.setVariable("prevSurname", StringEscapeUtils.escapeJava(request.getParameter("surname")));
		context.setVariable("prevEmail", StringEscapeUtils.escapeJava(request.getParameter("email")));
		context.setVariable("prevPassword", StringEscapeUtils.escapeJava(request.getParameter("password")));
		context.setVariable("prevConfpwd", StringEscapeUtils.escapeJava(request.getParameter("confpwd")));
		context.setVariable("prevStreet", StringEscapeUtils.escapeJava(request.getParameter("street")));
		context.setVariable("prevCivicNumber", StringEscapeUtils.escapeJava(request.getParameter("civicNumber")));
		context.setVariable("prevCity", StringEscapeUtils.escapeJava(request.getParameter("city")));
		context.setVariable("prevState", StringEscapeUtils.escapeJava(request.getParameter("state")));
		
		List<State> states;
		try {
			states=new StateDAO(connection).getStates();
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
				
		context.setVariable("states", states);
		
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
