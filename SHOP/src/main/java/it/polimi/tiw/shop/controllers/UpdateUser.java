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
	
	//Servlet che modifica i parametri richiesti dall'utente
	
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
       
    public UpdateUser() {
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
		
		final WebContext context = new WebContext(request, response, getServletContext(), request.getLocale());
		
		//prendo gli stati dal DB
		
		List<State> states;
		try {
			states=new StateDAO(connection).getStates();
		} catch (SQLException e) {
			request.setAttribute("logout",true);
			request.setAttribute("error",null);
			request.getRequestDispatcher("Error").forward(request, response);
			return;
		}
		
		//prelevo i parametri
		
		String name = request.getParameter("name");
		if(name != null)
			name = StringEscapeUtils.escapeJava(name);
		
		String surname = request.getParameter("surname");
		if(surname != null)
			surname = StringEscapeUtils.escapeJava(surname);
		
		String street = request.getParameter("street");
		if(street != null)
			street = StringEscapeUtils.escapeJava(street);
		
		String civicNumber = request.getParameter("civicNumber");
		if(civicNumber != null)
			civicNumber = StringEscapeUtils.escapeJava(civicNumber);
		
		String city = request.getParameter("city");
		if(city != null)
			city = StringEscapeUtils.escapeJava(city);
		
		String state = request.getParameter("state");
		if(state != null)
			state = StringEscapeUtils.escapeJava(state);
		
		HttpSession session = request.getSession();
		User user;
		try {
			user = new UserDAO(connection).getByEmail((String)session.getAttribute("user"));
		}catch(SQLException e) {
			request.setAttribute("logout",true);
			request.setAttribute("error",null);
			request.getRequestDispatcher("Error").forward(request, response);
			return;
		}
		
		//se uno dei parametri che l'utente può modificare è vuoto o nullo viene sostituito con quello già presente
		
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
		
		//se nessun parametro è variato rispetto agli originali avvisiamo l'utente senza accedere al DB inutilmente
		
		if(name.equals(user.getName()) &&
			surname.equals(user.getSurname()) &&
			state.equals(user.getState()) &&
			street.equals(user.getStreet()) &&
			city.equals(user.getCity()) &&
			civicNumber.equals(user.getCivicNumber())){
			
			response.sendRedirect("Home");
			return;
		}
		
		if(!state.equals(user.getState())) {
			StateDAO sDAO = new StateDAO(connection);
			boolean valid = false;
			
			try {
				valid = sDAO.isValid(state);
			}catch(SQLException e) {
				request.setAttribute("logout",true);
				request.setAttribute("error",null);
				request.getRequestDispatcher("Error").forward(request, response);
				return;
			}
			
			if(!valid){
				context.setVariable("error", "Invalid iso3 state.");
				try {
					context.setVariable("user", new UserDAO(connection).getByEmail((String)request.getSession().getAttribute("user")));
				}catch(SQLException e) {
					request.setAttribute("logout",true);
					request.setAttribute("error",null);
					request.getRequestDispatcher("Error").forward(request, response);
					return;
				}
				context.setVariable("states", states);
				templateEngine.process("/account.html", context, response.getWriter());
				return;
			}
		}
		
		//modifica parametri
		
		UserDAO udao = new UserDAO(connection);	
		try {
			udao.updateUser(name, surname, state, city, street, civicNumber, session);
		}catch(SQLException e) {
			request.setAttribute("logout",true);
			request.setAttribute("error",null);
			request.getRequestDispatcher("Error").forward(request, response);
			return;
		}
		
		response.sendRedirect("Home");
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
