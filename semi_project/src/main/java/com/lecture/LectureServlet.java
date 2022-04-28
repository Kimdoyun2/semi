package com.lecture;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.member.SessionInfo;
import com.util.MyUploadServlet;
import com.util.MyUtil;

@MultipartConfig
@WebServlet("/lecture/*")
public class LectureServlet extends MyUploadServlet {
	private static final long serialVersionUID = 1L;
	
	private String pathname;

	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		
		String uri = req.getRequestURI();
		String cp = req.getContextPath();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		if (uri.indexOf("list.do") == -1 && info == null) {
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}
		
		String root = session.getServletContext().getRealPath("/");
		pathname = root + "uploads" + File.separator + "lecture";
		
		if (uri.indexOf("list.do") != -1) {
			list(req, resp);
		} else if (uri.indexOf("write.do") != -1) {
			writeForm(req, resp);
		} else if (uri.indexOf("write_ok.do") != -1) {
			writeSubmit(req, resp);
		} else if (uri.indexOf("article.do") != -1) {
			article(req, resp);
		} else if (uri.indexOf("update.do") != -1) {
			updateForm(req, resp);
		} else if (uri.indexOf("update_ok.do") != -1) {
			updateSubmit(req, resp);
		} else if (uri.indexOf("delete.do") != -1) {
			delete(req, resp);
		} else if (uri.indexOf("deleteFile.do") != -1) {
			deleteFile(req, resp);
		} else if (uri.indexOf("download.do") != -1) {
			download(req, resp);
		}
	}
	
	// 리스트
	protected void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LectureDAO dao = new LectureDAO();
		MyUtil util = new MyUtil();
		String cp = req.getContextPath();
		
		try {
			String page = req.getParameter("page");
			int current_page = 1;
			if (page != null) {
				current_page = Integer.parseInt(page);
			}
			
			String category = req.getParameter("category");
			if (category == null) {
				category = "0";
			}
			
			String order = req.getParameter("order");
			if (order == null) {
				order = "latest";
			}
			
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			if (condition == null) {
				condition = "all";
				keyword = "";
			}
			
			if (req.getMethod().equalsIgnoreCase("GET")) {
				keyword = URLDecoder.decode(keyword, "utf-8");
			}
			
			int dataCount;
			if (keyword.length() == 0 && category.equals("0")) {
				dataCount = dao.dataCount();
			} else if (keyword.length() == 0 && !category.equals("0")) {
				dataCount = dao.dataCount(category);
			} else if (keyword.length() != 0 && category.equals("0")) {
				dataCount = dao.dataCount(condition, keyword);
			} else {
				dataCount = dao.dataCount(category, condition, keyword);
			}
			
			int rows = 10;
			int total_page = util.pageCount(rows, dataCount);
			if (current_page > total_page) {
				current_page = total_page;
			}
			
			int start = (current_page - 1) * rows + 1;
			int end = current_page * rows;
			
			List<LectureDTO> list = null;
			if (keyword.length() == 0 && category.equals("0")) {
				list = dao.listLecture(start, end, order);
			} else if (keyword.length() == 0 && !category.equals("0")) {
				list = dao.listLecture(start, end, order, category);
			} else if (keyword.length() != 0 && category.equals("0")) {
				list = dao.listLecture(start, end, order, condition, keyword);
			} else {
				list = dao.listLecture(start, end, order, category, condition, keyword);
			}
			
			// reg_date 시분초 잘라내기
			
			int listNum, n = 0;
			for (LectureDTO dto : list) {
				listNum = dataCount - (start + n - 1);
				dto.setListNum(listNum);
				
				dto.setReg_date(dto.getReg_date().substring(0, 10));
				
				n++;
			}
			
			String query = "order=" + order;
			if (keyword.length() != 0 && category.equals("0")) {
				query += "&condition=" + condition + "&keyword=" 
						+ URLEncoder.encode(keyword, "utf-8");
			} else if (keyword.length() != 0 && !category.equals("0")) {
				query += "&category=" + category + "&condition=" + condition + "&keyword=" 
						+ URLEncoder.encode(keyword, "utf-8");
			} else {
				query += "&category=" + category;
			}
			
			String listUrl = cp + "/lecture/list.do";
			String articleUrl = cp + "/lecture/article.do?page=" + current_page;
			if (query.length() != 0) {
				listUrl += "?" + query;
				articleUrl += "&" + query;
			}
			
			String paging = util.paging(current_page, total_page, listUrl);
			
			req.setAttribute("list", list);
			req.setAttribute("page", current_page);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("total_page", total_page);
			req.setAttribute("articleUrl", articleUrl);
			req.setAttribute("paging", paging);
			req.setAttribute("order", order);
			req.setAttribute("category", category);
			req.setAttribute("condition", condition);
			req.setAttribute("keyword", keyword);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		forward(req, resp, "/WEB-INF/views/lecture/list.jsp");
	}
	
	// 입력폼
	protected void writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute("mode", "write");
		forward(req, resp, "/WEB-INF/views/lecture/write.jsp");
	}
	
	// 게시글 저장
	protected void writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		String cp = req.getContextPath();
		
		if (req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp + "/lecture/list.do");
			return;
		}
		
		if (! info.getUserId().equals("admin")) {
			resp.sendRedirect(cp + "/lecture/list.do");
			return;
		}
		
		LectureDAO dao = new LectureDAO();
		
		try {
			LectureDTO dto = new LectureDTO();
			
			dto.setUserId(info.getUserId());
			
			dto.setLecture(Integer.parseInt(req.getParameter("category")));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			
			Map<String, String[]> map = doFileUpload(req.getParts(), pathname);
			
			if (map != null) {
				String []saveFiles = map.get("saveFilenames");
				String []originalFiles = map.get("originalFilenames");
				
				dto.setSaveFiles(saveFiles);
				dto.setOriginalFiles(originalFiles);
			}
			
			dao.insertLecture(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect(cp + "/lecture/list.do");
	}
	
	// 게시글 보기
	protected void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LectureDAO dao = new LectureDAO();
		String cp = req.getContextPath();
		
		String page = req.getParameter("page");
		String query = "page=" + page;
		
		try {
			int num = Integer.parseInt(req.getParameter("num"));
			
			String category = req.getParameter("category");
			if (category == null) {
				category = "0";
			}
			
			String order = req.getParameter("order");
			if (order == null) {
				order = "latest";
			}
			
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			if (condition == null) {
				condition = "all";
				keyword = "";
			}
			
			keyword = URLDecoder.decode(keyword, "utf-8");
			
			query += "&order=" + order;
			if (keyword.length() != 0 && category.equals("0")) {
				query += "&condition=" + condition + "&keyword=" 
						+ URLEncoder.encode(keyword, "utf-8");
			} else if (keyword.length() != 0 && !category.equals("0")) {
				query += "&category=" + category + "&condition=" + condition + "&keyword=" 
						+ URLEncoder.encode(keyword, "utf-8");
			} else {
				query += "&category=" + category;
			}
			
			dao.updateHitCount(num);
			
			LectureDTO dto = dao.readLecture(num);
			if (dto == null) {
				resp.sendRedirect(cp + "/lecture/list.do?" + query);
			}
			
			dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			
			LectureDTO preReadLecture = dao.preReadLecture(num, order, category, condition, keyword);
			LectureDTO nextReadLecture = dao.nextReadLecture(num, order, category, condition, keyword);
			
			List<LectureDTO> listFile = dao.listLectureFile(num);
			
			req.setAttribute("dto", dto);
			req.setAttribute("preReadLecture", preReadLecture);
			req.setAttribute("nextReadLecture", nextReadLecture);
			req.setAttribute("listFile", listFile);
			req.setAttribute("query", query);
			req.setAttribute("page", page);
			
			forward(req, resp, "/WEB-INF/views/lecture/article.jsp");
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect(cp + "/lecture/list.do?" + query);
	}
	
	// 수정폼
	protected void updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
	
	// 게시글 수정완료
	protected void updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
	
	// 게시글 삭제
	protected void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
	
	// 수정에서 파일 삭제
	protected void deleteFile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
	
	// 첨부파일 다운로드
	protected void download(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
}
