package it.polimi.tiw.shopjs.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/Logout")

public class Logout extends HttpServlet {
	
	//Servlet che effettua il logout dell'utente
	
	private static final long serialVersionUID = 1L;
       
    public Logout() {
        super();
    }
    
    public void init() throws ServletException {

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session =request.getSession();
		if(session.getAttribute("user")!=null)
			session.removeAttribute("user");
		session.invalidate();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void destroy() {
		
	}
}
