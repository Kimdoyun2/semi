package com.mypage;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.member.SessionInfo;
import com.util.MyUtil;

@WebServlet("/mypage/*")
public class MypageServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}
	
	protected void forward(HttpServletRequest req, HttpServletResponse resp, String path) throws ServletException, IOException {
		RequestDispatcher rd = req.getRequestDispatcher(path);
		rd.forward(req, resp);
	}
	
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		String cp = req.getContextPath();
		
		if(info==null) {
			resp.sendRedirect(cp+"/member/login.do");
			return;
		} 
		
		// uri에 따른 작업 구분
		String uri = req.getRequestURI();
		if(uri.indexOf("mypage.do") != -1) {
			mypage(req, resp);
		}
	}
	
	protected void mypage(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 마이페이지 폼
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		MypageDAO dao = new MypageDAO();
		
		// 내정보 가져오는거
		MypageDTO dto = dao.readMypage(info.getUserId());
		int boardCount = dao.readNumber(info.getUserId());
	
		req.setAttribute("dto", dto);
		req.setAttribute("boardCount", boardCount);
		
		MyUtil myUtil = new MyUtil();
		String cp = req.getContextPath();
		
		try {
			String page = req.getParameter("page");
			int current_page = 1;
			if(page != null) {
				current_page = Integer.parseInt(page);
			}
			
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			if(condition == null) {
				condition = "all";
				keyword = "";
			}
			
			if(req.getMethod().equalsIgnoreCase("GET")) {
				keyword = URLDecoder.decode(keyword, "utf-8");
			}
			
			int dataCount = dao.readNumber(info.getUserId());
			
			int rows = 5;
			int total_page = myUtil.pageCount(rows, dataCount);
			if(current_page > total_page) {
				current_page = total_page;
			}
			
			int start = (current_page - 1) * rows + 1;
			int end = current_page * rows;
			
			List<MypageDTO> list = null;
			if(keyword.length() == 0) {
			list = dao.listNotice(info.getUserId(), start, end);

			String query = "";
			String listUrl, articleUrl;
			String boardName = dto.getBoardName();
			listUrl = cp+"/mypage/mypage.do";
			articleUrl = cp + "/"+boardName+ "/article.do?page=" + current_page;
			
			if(keyword.length() != 0) {
				query = "condition=" + condition + "&keyword="
						+ URLEncoder.encode(keyword, "utf-8");
				listUrl += "?" + query;
				articleUrl += "&" + query;
			}
			
			String paging = myUtil.paging(current_page, total_page, listUrl);

			req.setAttribute("list", list);
			req.setAttribute("page", current_page);
			req.setAttribute("total_page", total_page);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("paging", paging);
			req.setAttribute("articleUrl", articleUrl);
			req.setAttribute("condition", condition);
			req.setAttribute("keyword", keyword);
			
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		forward(req, resp, "/WEB-INF/views/mypage/mypage.jsp");
	
	}
}