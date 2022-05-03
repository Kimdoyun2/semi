package com.main;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.util.MyServlet;

@WebServlet("/main.do")
public class MainServlet extends MyServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}
	
	@Override
	protected void forward(HttpServletRequest req, HttpServletResponse resp, String path) throws ServletException, IOException {
		RequestDispatcher rd = req.getRequestDispatcher(path);
		rd.forward(req, resp);
	}
	
	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		String uri = req.getRequestURI();
		
		if(uri.indexOf("main.do") != -1) {
			main(req, resp);
		}
	}
	
	protected void main(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		MainDAO dao = new MainDAO();
		
		try {
			List<MainDTO> rankHitCount = null;
			List<MainDTO> rankReg_date = null;
			
			rankHitCount = dao.rankHitCount();
			rankReg_date = dao.rankReg_date();
			
			req.setAttribute("rankHitCount", rankHitCount);
			req.setAttribute("rankReg_date", rankReg_date);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		forward(req, resp, "/WEB-INF/views/main/main.jsp");
	}
}
