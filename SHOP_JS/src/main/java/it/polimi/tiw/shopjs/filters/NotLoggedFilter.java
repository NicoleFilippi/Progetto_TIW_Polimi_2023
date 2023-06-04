package it.polimi.tiw.shopjs.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class NotLoggedFilter implements Filter {
	
	//filtro che controlla se l'utente che vuole uscire dall'area riservata non sia loggato 
	
	public NotLoggedFilter() {
		
	}
	
	public void init(FilterConfig config) throws ServletException {
		
	}

	public void destroy() {
		
	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException{		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		//manda alla home
		
		String homepath = req.getServletContext().getContextPath() + "/home.html";
		HttpSession s = req.getSession();
		if (!s.isNew() && s.getAttribute("user") != null) {
			res.sendRedirect(homepath);
			return;
		}
		
		chain.doFilter(request, response);		
	}
}
