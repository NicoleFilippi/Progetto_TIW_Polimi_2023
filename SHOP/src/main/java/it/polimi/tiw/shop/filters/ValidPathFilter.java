package it.polimi.tiw.shop.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class ValidPathFilter implements Filter {
	
	//filtro che controlla se il percorso richiesto corrisponde ad una servlet
	
	public ValidPathFilter() {
		
	}
	
	public void init(FilterConfig config) throws ServletException {
		
	}

	public void destroy() {
		
	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException{		
		HttpServletRequest req = (HttpServletRequest) request;
		
		
		boolean valid = false;
		
		//cerca tra le servlet registrate una che corrisponda al percorso della richiesta
		
		for(String s : req.getServletContext().getServletRegistrations().keySet()) {
			if(req.getServletContext().getServletRegistrations().get(s).getMappings().contains(req.getServletPath())) {
				valid=true;
				break;
			}	
		}
		
		if(!valid && !"/index.html".equals(req.getServletPath()) && !"/".equals(req.getServletPath())
				&& !req.getServletPath().startsWith("/css") && !req.getServletPath().startsWith("/images")
				) {
			request.setAttribute("logout",false);
			request.setAttribute("error","The server cannot find a resource that matches your request: " + req.getServletPath());
			request.getRequestDispatcher("Error").forward(request, response);
			return;
		}
		
		chain.doFilter(request, response);		
	}
}
