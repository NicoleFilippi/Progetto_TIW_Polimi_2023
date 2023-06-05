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

import it.polimi.tiw.shop.utils.Cart;
import it.polimi.tiw.shop.beans.Product;
import it.polimi.tiw.shop.beans.ProductSupplier;
import it.polimi.tiw.shop.dao.ProductDAO;
import it.polimi.tiw.shop.dao.ProductSupplierDAO;
import it.polimi.tiw.shop.utils.ConnectionHandler;

@WebServlet("/Results")

public class GoToResults extends HttpServlet {
	
	//Servlet che porta alla pagina Risultati
	
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
       
    public GoToResults() {
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(connection == null) {
    		request.setAttribute("logout",true);
			request.setAttribute("error",null);
			request.getRequestDispatcher("Error").forward(request, response);
			return;
    	}
		
		HttpSession session = request.getSession();		
		final WebContext context = new WebContext(request, response, getServletContext(), request.getLocale());
		
		//prendo l'eventuale keyword e l'eventuale id del prodotto di cui voglio vedere i dettagli
		
		String keyword = request.getParameter("keyword");
		if(keyword != null)
			keyword = StringEscapeUtils.escapeJava(keyword);
		
		int productId = -1;
		try {
			productId = Integer.parseInt(request.getParameter("id"));
		}catch(Exception e) {
			productId = -1;
		}
		
		//id non valido o non presente
		
		if(productId <= 0) productId = -1;
		
		//keyword non presente
		
		if(keyword == null || keyword.equals("")) keyword = null;
		
		//entrambi non validi
		
		if(keyword==null && productId < 0) {			
			final WebContext errcontext = new WebContext(request, response, getServletContext(), request.getLocale());
			errcontext.setVariable("error", "Invalid Parameters");
			templateEngine.process("/results.html", errcontext, response.getWriter());
			return;
		}
		
		List<Product> prodList = null;
		ProductDAO pdao = new ProductDAO(connection);
		
		//keyword valida, cerco nel DB
		
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
		
		List<ProductSupplier> ps = null;
		Product prodDet = null;
		
		//se id prodotto presente (voglio i dettagli di un prodotto)
		
		if(productId >= 0) {
			
			try{
				prodDet = pdao.getById(productId);
			} catch(SQLException e) {
				request.setAttribute("logout",true);
				request.setAttribute("error",null);
				request.getRequestDispatcher("Error").forward(request, response);
				return;
			}
			
			//se non esiste un prodotto con quell'id
			
			if(prodDet == null) {
				final WebContext errcontext = new WebContext(request, response, getServletContext(), request.getLocale());
				errcontext.setVariable("error", "Invalid product id");
				templateEngine.process("/results.html", errcontext, response.getWriter());
				return;
			}
			
			//se visualizzo i dettagli del prodotto, non lo voglio anche nella lista degli altri prodotti
			
			if(prodList != null) {
				for(Product p : prodList) {
					if(p.getId() == productId) {
						prodList.remove(p);
						break;
					}
				}
			}
			
			//il prodotto dettagliato è ora posto negli ultimi visualizzati
			
			ProductSupplierDAO psDAO = new ProductSupplierDAO(connection);
			
			try {
				ps = psDAO.getByProduct(prodDet,(Cart)session.getAttribute("cart"));
				psDAO.visualizedProduct((String)session.getAttribute("user"), prodDet);
			} catch (SQLException e) {
				request.setAttribute("logout",true);
				request.setAttribute("error",null);
				request.getRequestDispatcher("Error").forward(request, response);
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
