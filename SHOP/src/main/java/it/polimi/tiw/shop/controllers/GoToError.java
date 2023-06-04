package it.polimi.tiw.shop.controllers;

import java.io.IOException;

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

@WebServlet("/Error")

public class GoToError extends HttpServlet {
	
	//Servlet che manda alla pagina di errore
	
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
       
    public GoToError() {
    	super();
    }
    
    public void init() {
    	ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		WebContext context = new WebContext(request, response, getServletContext(), request.getLocale());
		
		if(request.getAttribute("logout") == null)
			request.setAttribute("logout", true);
		
		if((Boolean)request.getAttribute("logout")) {
			session.invalidate();
			context.setVariable("logout", true);
		}else {
			context.setVariable("logout", false);
		}
		context.setVariable("error", request.getAttribute("error"));
		
		templateEngine.process("/error.html", context, response.getWriter());
		return;
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
