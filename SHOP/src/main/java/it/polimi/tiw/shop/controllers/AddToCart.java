package it.polimi.tiw.shop.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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

import it.polimi.tiw.shop.beans.Cart;
import it.polimi.tiw.shop.beans.ProductSupplier;
import it.polimi.tiw.shop.dao.ProductSupplierDAO;
import it.polimi.tiw.shop.utils.ConnectionHandler;

@WebServlet("/AddToCart")
public class AddToCart extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
       
    public AddToCart() {
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
		
		HttpSession session = request.getSession();
		
		int productId;
		int supplierId;
		int quantity;
		try {
			productId = Integer.parseInt(request.getParameter("productId"));
			supplierId = Integer.parseInt(request.getParameter("supplierId"));
			quantity = Integer.parseInt(request.getParameter("quantity"));
			
			if(quantity<=0)
				throw new Exception();
			
		}catch(Exception e) {
			//TODO handle exception
			final WebContext errcontext = new WebContext(request, response, getServletContext(), request.getLocale());
			errcontext.setVariable("error", "Invalid Parameters");
			templateEngine.process("/results.html", errcontext, response.getWriter());
			return;
		}
		
		ProductSupplier ps = null;
		try {
			ps = new ProductSupplierDAO(connection).getByIds(productId, supplierId);

		} catch (SQLException e) {
			//TODO handle exception
			final WebContext errcontext = new WebContext(request, response, getServletContext(), request.getLocale());
			errcontext.setVariable("error", "SQL error: "+e.getMessage());
			templateEngine.process("/results.html", errcontext, response.getWriter());
			return;
		}	
		
		if(ps==null) {
			//TODO handle exception
			final WebContext errcontext = new WebContext(request, response, getServletContext(), request.getLocale());
			errcontext.setVariable("error", "Invalid Parameters");
			templateEngine.process("/results.html", errcontext, response.getWriter());
			return;
		}
		
		((Cart)session.getAttribute("cart")).addItem(ps, quantity);
				
		String path = getServletContext().getContextPath() + "/Cart";
		response.sendRedirect(path);
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
