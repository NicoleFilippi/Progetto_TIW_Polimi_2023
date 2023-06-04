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

public class NotLoggedJSFilter implements Filter {
	
	//filtro che controlla se l'utente che vuole accedere a risorse fuori dall'area riservata non sia loggato 
	
	public NotLoggedJSFilter() {
		
	}
	
	public void init(FilterConfig config) throws ServletException {
		
	}

	public void destroy() {
		
	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException{		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		//invia errore
		
		HttpSession s = req.getSession();
		if (!s.isNew() && s.getAttribute("user") != null) {
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		chain.doFilter(request, response);		
	}
}
