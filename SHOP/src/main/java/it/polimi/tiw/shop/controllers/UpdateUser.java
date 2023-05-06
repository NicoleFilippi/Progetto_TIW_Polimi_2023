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

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.shop.beans.State;
import it.polimi.tiw.shop.beans.User;
import it.polimi.tiw.shop.dao.StateDAO;
import it.polimi.tiw.shop.dao.UserDAO;
import it.polimi.tiw.shop.utils.ConnectionHandler;

@WebServlet("/UpdateUser")
public class UpdateUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
       
    public UpdateUser() {
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
				
		final WebContext context = new WebContext(request, response, getServletContext(), request.getLocale());
		
		List<State> states;
		try {
			states=new StateDAO(connection).getStates();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		String name = StringEscapeUtils.escapeJava(request.getParameter("name"));
		String surname = StringEscapeUtils.escapeJava(request.getParameter("surname"));
		String street = StringEscapeUtils.escapeJava(request.getParameter("street"));
		String civicNumber = StringEscapeUtils.escapeJava(request.getParameter("civicNumber"));
		String city = StringEscapeUtils.escapeJava(request.getParameter("city"));
		String state = StringEscapeUtils.escapeJava(request.getParameter("state"));
		
		HttpSession session = request.getSession();
		User user=(User)session.getAttribute("user");
		
		if(name == null || name.equals("")) {
			name=user.getName();
		}
		if(surname == null || surname.equals("")) {
			surname=user.getSurname();
		}
		if(street == null || street.equals("")) {
			street=user.getStreet();
		}
		if(civicNumber == null || civicNumber.equals("")) {
			civicNumber=user.getCivicNumber();
		}
		if(city == null || city.equals("")) {
			city=user.getCity();
		}
		if(state == null || state.equals("")) {
			state=user.getState();
		}
		
		if(name.equals(user.getName()) &&
			surname.equals(user.getSurname()) &&
			state.equals(user.getState()) &&
			street.equals(user.getStreet()) &&
			city.equals(user.getCity()) &&
			civicNumber.equals(user.getCivicNumber())){
			
			context.setVariable("error", "No parameters have changed.");
			context.setVariable("states", states);
			templateEngine.process("/account.html", context, response.getWriter());
			return;
		}
		
		UserDAO udao = new UserDAO(connection);
	
		try {
			udao.updateUser(name, surname, state, city, street, civicNumber, session);
		}catch(SQLException e) {
			context.setVariable("states", states);
			context.setVariable("error", "SQL Error: " + e.getMessage());
			templateEngine.process("/account.html", context, response.getWriter());
			return;
		}
		
		response.sendRedirect("Home");
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
