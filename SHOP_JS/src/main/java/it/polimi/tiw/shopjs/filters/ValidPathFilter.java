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

public class ValidPathFilter implements Filter {
	
	//filtro che controlla se il percorso richiesto corrisponde ad una servlet
	
	public ValidPathFilter() {}
	
	public void init(FilterConfig config) throws ServletException {}

	public void destroy() {}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException{		
		HttpServletRequest req = (HttpServletRequest) request;	
		HttpServletResponse resp = (HttpServletResponse) response;	
		boolean valid = false;
		
		//cerca tra le servlet registrate una che corrisponda al percorso della richiesta
		
		for(String s : req.getServletContext().getServletRegistrations().keySet()) {
			if(req.getServletContext().getServletRegistrations().get(s).getMappings().contains(req.getServletPath())) {
				valid=true;
				break;
			}	
		}
		
		if(!valid && !"/index.html".equals(req.getServletPath()) && !"/".equals(req.getServletPath()) && !req.getServletPath().endsWith(".js")
				&& !req.getServletPath().startsWith("/css")  && !"/home.html".equals(req.getServletPath()) && !req.getServletPath().startsWith("/images")) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().println("The server cannot find a resource that matches your request: " + req.getServletPath());
			return;
		}
		
		chain.doFilter(request, response);		
	}
}
