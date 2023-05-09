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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.shop.beans.Cart;
import it.polimi.tiw.shop.beans.Product;
import it.polimi.tiw.shop.beans.ProductSupplier;
import it.polimi.tiw.shop.beans.User;
import it.polimi.tiw.shop.dao.ProductDAO;
import it.polimi.tiw.shop.dao.ProductSupplierDAO;
import it.polimi.tiw.shop.utils.ConnectionHandler;

@WebServlet("/Results")
public class GoToResults extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
       
    public GoToResults() {
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
		
		final WebContext context = new WebContext(request, response, getServletContext(), request.getLocale());
		
		String keyword = request.getParameter("keyword");
		String productIdStr = request.getParameter("id");
		int productId = -1;
		try {
			productId = Integer.valueOf(productIdStr);
		}catch(Exception e) {
			productId = -1;
		}
		if(productId<=0) productId = -1;
		if(keyword==null || keyword.equals("")) keyword=null;
		
		if(keyword==null && productId < 0) {
			
			final WebContext errcontext = new WebContext(request, response, getServletContext(), request.getLocale());
			errcontext.setVariable("error", "Invalid Parameters");
			templateEngine.process("/results.html", errcontext, response.getWriter());
			return;
		}
		
		List<Product> prodList = null;
		ProductDAO pdao = new ProductDAO(connection);
		
		if(keyword!=null) {
			try{
				prodList = pdao.keywordSearch(keyword);
			} catch(SQLException e) {
				request.setAttribute("logout",true);
				request.setAttribute("error",null);
				request.getRequestDispatcher("Error").forward(request, response);
				return;
			}
			context.setVariable("keyword", keyword);
			context.setVariable("productList", prodList);
		}
		
		List<ProductSupplier> ps=null;
		Product prodDet=null;
		
		if(productId>=0) {
			
			try{
				prodDet = pdao.getById(productId);
			} catch(SQLException e) {
				request.setAttribute("logout",true);
				request.setAttribute("error",null);
				request.getRequestDispatcher("Error").forward(request, response);
				return;
			}
			
			if(prodDet == null) {
				
				//TODO handle error
				final WebContext errcontext = new WebContext(request, response, getServletContext(), request.getLocale());
				errcontext.setVariable("error", "Invalid product id");
				templateEngine.process("/results.html", errcontext, response.getWriter());
				return;
			}
			
			if(prodList!=null) {
				for(Product p : prodList) {
					if(p.getId()==productId) {
						prodList.remove(p);
						break;
					}
				}
			}
			
			
			ProductSupplierDAO psDAO = new ProductSupplierDAO(connection);
			
			try {
				ps = psDAO.getByProduct(prodDet,(Cart)session.getAttribute("cart"));
				psDAO.visualizedProduct((User)session.getAttribute("user"), prodDet);
			} catch (SQLException e) {
				
				//TODO handle exception
				final WebContext errcontext = new WebContext(request, response, getServletContext(), request.getLocale());
				errcontext.setVariable("error", "SQL error: " + e.getMessage());
				templateEngine.process("/results.html", errcontext, response.getWriter());
				return;
			}
			
			context.setVariable("supplierList", ps);
			context.setVariable("detailedProduct", prodDet);
		}
		
		templateEngine.process("/results.html", context, response.getWriter());
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
