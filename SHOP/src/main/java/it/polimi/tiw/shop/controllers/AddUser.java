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
	
	//Servlet per creare un account
	
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
       
    public AddUser() {
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
		
		String name = request.getParameter("name");	
		if(name != null)									//solo se non è null per evitare NullPointerException
			name = StringEscapeUtils.escapeJava(name);		//metodo che "rende sicuro" il parametro di una richiesta HTTP
		
		String surname = request.getParameter("surname");
		if(surname != null)
			surname = StringEscapeUtils.escapeJava(surname);
		
		String email = request.getParameter("email");
		if(email != null)
			email = StringEscapeUtils.escapeJava(email);
		
		String password = request.getParameter("password");
		if(password != null)
			password = StringEscapeUtils.escapeJava(password);
		
		String confpwd = request.getParameter("confpwd");
		if(confpwd != null)
			confpwd = StringEscapeUtils.escapeJava(confpwd);
		
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
		
		//controllo che ogni parametro sia presente, altrimenti setto l'errore
		
		if(name == null || name.equals("")) {
			try {
				setErrorParameters(request,context);
			} catch (SQLException e) {
				request.setAttribute("logout",true);
				request.setAttribute("error",null);
				request.getRequestDispatcher("Error").forward(request, response);
				return;
			}
			context.setVariable("error", "Name required");
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		
		if(surname == null || surname.equals("")) {
			try {
				setErrorParameters(request,context);
			} catch (SQLException e) {
				request.setAttribute("logout",true);
				request.setAttribute("error",null);
				request.getRequestDispatcher("Error").forward(request, response);
				return;
			}
			context.setVariable("error", "Surname required");
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		
		if(email == null || email.equals("")) {
			try {
				setErrorParameters(request,context);
			} catch (SQLException e) {
				request.setAttribute("logout",true);
				request.setAttribute("error",null);
				request.getRequestDispatcher("Error").forward(request, response);
				return;
			}
			context.setVariable("error", "Email required");
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		
		if(password == null || password.equals("")) {
			try {
				setErrorParameters(request,context);
			} catch (SQLException e) {
				request.setAttribute("logout",true);
				request.setAttribute("error",null);
				request.getRequestDispatcher("Error").forward(request, response);
				return;
			}
			context.setVariable("error", "Password required");
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		
		if(confpwd == null || confpwd.equals("")) {
			try {
				setErrorParameters(request,context);
			} catch (SQLException e) {
				request.setAttribute("logout",true);
				request.setAttribute("error",null);
				request.getRequestDispatcher("Error").forward(request, response);
				return;
			}
			context.setVariable("error", "Confirm password required");
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		
		if(street == null || street.equals("")) {
			try {
				setErrorParameters(request,context);
			} catch (SQLException e) {
				request.setAttribute("logout",true);
				request.setAttribute("error",null);
				request.getRequestDispatcher("Error").forward(request, response);
				return;
			}
			context.setVariable("error", "Street required");
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		
		if(civicNumber == null || civicNumber.equals("")) {
			try {
				setErrorParameters(request,context);
			} catch (SQLException e) {
				request.setAttribute("logout",true);
				request.setAttribute("error",null);
				request.getRequestDispatcher("Error").forward(request, response);
				return;
			}
			context.setVariable("error", "Civic number required");
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		
		if(city == null || city.equals("")) {
			try {
				setErrorParameters(request,context);
			} catch (SQLException e) {
				request.setAttribute("logout",true);
				request.setAttribute("error",null);
				request.getRequestDispatcher("Error").forward(request, response);
				return;
			}
			context.setVariable("error", "City required");
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		
		if(state == null || state.equals("")) {
			try {
				setErrorParameters(request,context);
			} catch (SQLException e) {
				request.setAttribute("logout",true);
				request.setAttribute("error",null);
				request.getRequestDispatcher("Error").forward(request, response);
				return;
			}
			context.setVariable("error", "State required");
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		
		if(!password.equals(confpwd)) {
			try {
				setErrorParameters(request,context);
			} catch (SQLException e) {
				request.setAttribute("logout",true);
				request.setAttribute("error",null);
				request.getRequestDispatcher("Error").forward(request, response);
				return;
			}
			context.setVariable("error", "Wrong confirmed password");
			context.setVariable("prevPassword", "");
			context.setVariable("prevConfpwd", "");
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		
		//controllo che la mail sia sintatticamente valida
		
		UserDAO udao = new UserDAO(connection);
		StateDAO sdao = new StateDAO(connection);
		
		if(!udao.isValidEmail(email)) {
			try {
				setErrorParameters(request,context);
			} catch (SQLException e) {
				request.setAttribute("logout",true);
				request.setAttribute("error",null);
				request.getRequestDispatcher("Error").forward(request, response);
				return;
			}
			context.setVariable("error", "Invalid email");
			context.setVariable("prevEmail", "");
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		
		//controllo che lo stato sia valido e che la mail non sia già presente nel DB
		 
		boolean validState = false;
		boolean freeEmail = false;
				
		try {
			validState = sdao.isValid(state);
			freeEmail = udao.isFreeEmail(email);
		}catch(SQLException e) {
			request.setAttribute("logout",true);
			request.setAttribute("error",null);
			request.getRequestDispatcher("Error").forward(request, response);
			return;
		}
		
		if(!validState) {
			try {
				setErrorParameters(request,context);
			} catch (SQLException e) {
				request.setAttribute("logout",true);
				request.setAttribute("error",null);
				request.getRequestDispatcher("Error").forward(request, response);
				return;
			}
			context.setVariable("error", "Invalid state");
			context.setVariable("prevState", sdao.getClientState(request));
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		
		if(!freeEmail) {
			try {
				setErrorParameters(request,context);
			} catch (SQLException e) {
				request.setAttribute("logout",true);
				request.setAttribute("error",null);
				request.getRequestDispatcher("Error").forward(request, response);
				return;
			}
			context.setVariable("error", "An account with this email already exists");
			context.setVariable("prevEmail", "");
			templateEngine.process("/signup.html", context, response.getWriter());
			return;
		}
		
		//inserisco l'utente nuovo
		
		try {
			udao.addUser(email, name, surname, state, city, street, civicNumber, password);
		}catch(SQLException e) {
			request.setAttribute("logout",true);
			request.setAttribute("error",null);
			request.getRequestDispatcher("Error").forward(request, response);
			return;
		}
		
		context.setVariable("error", "New account created");
		templateEngine.process("/index.html", context, response.getWriter());
		return;
	}
	
	//metodo per mantenere i parametri precedenti inseriti dall'utente in caso di errore
	
	private void setErrorParameters(HttpServletRequest request, WebContext context) throws SQLException {
		context.setVariable("prevName", request.getParameter("name"));
		context.setVariable("prevSurname", request.getParameter("surname"));
		context.setVariable("prevEmail", request.getParameter("email"));
		context.setVariable("prevPassword", request.getParameter("password"));
		context.setVariable("prevConfpwd", request.getParameter("confpwd"));
		context.setVariable("prevStreet", request.getParameter("street"));
		context.setVariable("prevCivicNumber", request.getParameter("civicNumber"));
		context.setVariable("prevCity", request.getParameter("city"));
		context.setVariable("prevState", request.getParameter("state"));
		List<State> states = new StateDAO(connection).getStates();
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
