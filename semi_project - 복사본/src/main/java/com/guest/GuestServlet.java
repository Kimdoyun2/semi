package com.guest;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.member.SessionInfo;
import com.util.MyServlet;
import com.util.MyUtil;

@WebServlet("/guest/*")
public class GuestServlet extends MyServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		
		String uri=req.getRequestURI();
		
		// uri에 따른 작업 구분
		if(uri.indexOf("guest.do") != -1) {
			guest(req, resp);
		} else if(uri.indexOf("guest_ok.do") != -1) {
			guestSubmit(req, resp);
		} else if(uri.indexOf("delete.do") != -1) {
			delete(req, resp);
		}
	}

	private void guest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 방명록 리스트
		GuestDAO dao = new GuestDAO();
		MyUtil util = new MyUtil();
		
		String cp = req.getContextPath();
		
		try {
			// 넘어온 페이지
			String page = req.getParameter("page");
			int current_page = 1;
			if(page != null) {
				current_page = Integer.parseInt(page);
			}
			
			int dataCount = dao.dataCount();
			
			// 전체페이지수 구하기
			int rows = 5;
			int total_page = util.pageCount(rows, dataCount);
			
			// 전체페이지보다 표시할 페이지가 큰경우
			if(total_page < current_page) {
				current_page = total_page;
			}
			
			int start = (current_page - 1) * rows + 1;
			int end = current_page * rows;
			
			// 데이터 가져오기
			List<GuestDTO> list = dao.listGuest(start, end);
			
			for(GuestDTO dto : list) {
				dto.setContent(dto.getContent().replaceAll(">", "&gt;"));
				dto.setContent(dto.getContent().replaceAll("<", "&lt;"));
				dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			}
			
			// 페이징처리
			String strUrl = cp + "/guest/guest.do";
			String paging = util.paging(current_page, total_page, strUrl);
			
			// guest.jsp에 넘겨줄 데이터
			req.setAttribute("list", list);
			req.setAttribute("page", current_page);
			req.setAttribute("total_page", total_page);
			req.setAttribute("paging", paging);
			req.setAttribute("dataCount", dataCount);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		forward(req, resp, "/WEB-INF/views/guest/guest.jsp");
	}
	
	private void guestSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 방명록 저장
		GuestDAO dao=new GuestDAO();
		
		HttpSession session=req.getSession();
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		
		String cp=req.getContextPath();

		if(info==null) { // 로그인되지 않은 경우
			resp.sendRedirect(cp+"/member/login.do");
			return;
		}
		
		try {
			GuestDTO dto=new GuestDTO();
			
			dto.setUserId(info.getUserId());
			dto.setContent(req.getParameter("content"));
			
			dao.insertGuest(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect(cp+"/guest/guest.do");
	}
	
	private void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 방명록 삭제
		GuestDAO dao=new GuestDAO();
		
		HttpSession session=req.getSession();
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		
		String cp=req.getContextPath();
		String page=req.getParameter("page");
		
		if(info==null) { // 로그인되지 않은 경우
			resp.sendRedirect(cp+"/member/login.do");
			return;
		}
		
		try {
			int num=Integer.parseInt(req.getParameter("num"));

			dao.deleteGuest(num, info.getUserId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect(cp+"/guest/guest.do?page="+page);
	}
}
