package it.polimi.tiw.shopjs.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.GsonBuilder;

import it.polimi.tiw.shopjs.beans.Product;
import it.polimi.tiw.shopjs.dao.ProductDAO;
import it.polimi.tiw.shopjs.utils.ConnectionHandler;

@WebServlet("/GetResults")
@MultipartConfig

public class GetResults extends HttpServlet {
	
	//Servlet che ritorna i risultati contenenti la keyword
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    public GetResults() {
        super();
    }
    
    public void init() throws ServletException {
    	try {
    		connection = ConnectionHandler.getConnection(getServletContext());
    	}catch(Exception e) {
    		connection = null;
    		e.printStackTrace();
    	}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(connection == null) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    		//TODO HANDLE ERROR
			return;
    	}
				
		//prendo la keyword
		
		String keyword = request.getParameter("keyword");
		if(keyword != null)
			keyword = StringEscapeUtils.escapeJava(keyword);
		
		//keyword non presente
		
		if(keyword==null || keyword.equals("")) {			
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			//TODO handle error
			return;
		}
		
		List<Product> prodList = null;
		ProductDAO pdao = new ProductDAO(connection);
		
		//keyword valida, cerco nel DB
		
		try{
			prodList = pdao.keywordSearch(keyword);
		} catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			//TODO handle error
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
		String listJson = new GsonBuilder().create().toJson(prodList);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(listJson);
		
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
