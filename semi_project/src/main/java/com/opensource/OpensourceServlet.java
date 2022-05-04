package com.opensource;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import com.member.SessionInfo;
import com.util.FileManager;
import com.util.MyUploadServlet;
import com.util.MyUtil;

@WebServlet("/opensource/*")
@MultipartConfig
public class OpensourceServlet extends MyUploadServlet {

	private static final long serialVersionUID = 1L;
	
	private String pathname;

	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		
		String uri = req.getRequestURI();
		String cp = req.getContextPath();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
				
		
		
		String root = session.getServletContext().getRealPath("/");
		pathname = root + "uploads" + File.separator + "opensource";
		
		if(uri.indexOf("list.do") != -1) {
			list(req, resp);
		} else if(info == null) {
			resp.sendRedirect(cp+"/member/login.do");
			return;
		} else if(uri.indexOf("write.do") != -1) {
			writeForm(req, resp);
		} else if(uri.indexOf("write_ok.do") != -1) {
			writeSubmit(req, resp);
		} else if(uri.indexOf("article.do") != -1) {
			article(req, resp);
		} else if(uri.indexOf("update.do") != -1) {
			updateForm(req, resp);
		} else if(uri.indexOf("update_ok.do") != -1) {
			updateSubmit(req, resp);
		} else if(uri.indexOf("delete.do") != -1) {
			delete(req, resp);
		} else if(uri.indexOf("deleteFile.do") != -1) {
			deleteFile(req, resp);
		} else if(uri.indexOf("download.do") != -1) {
			download(req, resp);
		} else if(uri.indexOf("insertOsLike.do") != -1) {
			insertOsLike(req, resp);
		}
	}
	
	private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		OpensourceDAO dao = new OpensourceDAO();
		MyUtil myutil = new MyUtil();
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
			
			String order = req.getParameter("order");
			if(order == null) {
				order = "latest";
			}
			
			if(req.getMethod().equalsIgnoreCase("GET")) {
				keyword = URLDecoder.decode(keyword, "utf-8");
			}

			int rows = 10;
			int dataCount, total_page;
			
			if(keyword.length() == 0) {
				dataCount = dao.dataCount();
			} else {
				dataCount = dao.dataCount(condition, keyword);
			}
			
			total_page = myutil.pageCount(rows, dataCount);
			if(current_page > total_page) {
				current_page = total_page;
			}
			
			int start = (current_page-1) * rows + 1;
			int end = current_page * rows;
			
			List<OpensourceDTO> list = null;
			if(keyword.length() == 0) {
				list = dao.listOpensource(start, end, order);
			} else {
				list = dao.listOpensource(start, end, condition, keyword, order);
			}
			
			int listNum, n = 0;
			for(OpensourceDTO dto : list) {
				listNum = dataCount - (start + n - 1);
				dto.setListNum(listNum);
				n++;
			}
			String query = "";
			String listUrl, articleUrl;
			
			listUrl = cp + "/opensource/list.do";
			articleUrl = cp + "/opensource/article.do?page="+current_page;
			
			if(keyword.length() != 0) {
				query = "condition="+condition+"&keyword="+URLEncoder.encode(keyword,"utf-8")+"&order="+order;
				
				listUrl += "?" + query;
				articleUrl += "&" + query;
			}
			
			String paging = myutil.paging(current_page, total_page, listUrl);
			
			req.setAttribute("list", list);
			req.setAttribute("total_page", total_page);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("page", current_page);
			req.setAttribute("order", order);
			req.setAttribute("condition", condition);
			req.setAttribute("keyword", keyword);
			req.setAttribute("articleUrl", articleUrl);
			req.setAttribute("paging", paging);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		forward(req, resp, "/WEB-INF/views/opensource/list.jsp");
	}
	
	private void writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute("mode", "write");
		forward(req, resp, "/WEB-INF/views/opensource/write.jsp");
	}
	
	private void writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String cp = req.getContextPath();
		
		OpensourceDAO dao = new OpensourceDAO();
		
		try {
			OpensourceDTO dto = new OpensourceDTO();
			dto.setUserId(info.getUserId());
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			
			Map<String, String[]> map = doFileUpload(req.getParts(), pathname);
			if(map != null) {
				String[] saveFiles = map.get("saveFilenames");
				String[] originalFiles = map.get("originalFilenames");
				
				dto.setSaveFiles(saveFiles);
				dto.setOriginalFiles(originalFiles);
			}
			
			dao.insertOpensource(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect(cp+"/opensource/list.do");
	}
	
	
	private void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글 보기
		OpensourceDAO dao = new OpensourceDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String cp = req.getContextPath();
		String page = req.getParameter("page");
		String query = "page="+page;
		
		try {
			int num = Integer.parseInt(req.getParameter("num"));
			
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			String order = req.getParameter("order");
			
			query += makeQuery(condition, keyword, order);
			dao.updateHitCount(num);
			
			OpensourceDTO dto = dao.readOpensource(num);
			if(dto == null) {
				resp.sendRedirect(cp+"/opensource/list.do?"+query);
				return;
			}
			
			dto.setContent(dto.getContent().replaceAll("&", "&amp;"));
			dto.setContent(dto.getContent().replaceAll("\"", "&quot;"));
			dto.setContent(dto.getContent().replaceAll(">", "&gt;"));
			dto.setContent(dto.getContent().replaceAll("<", "&lt;"));
			dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			dto.setContent(dto.getContent().replaceAll("\\s", "&nbsp;"));
			
			boolean isUserLike = dao.isUserOsLike(num, info.getUserId());
			
			OpensourceDTO preReadOs = dao.preReadOs(num, condition, keyword);
			OpensourceDTO nextReadOs = dao.nextReadOs(num, condition, keyword);
			
			List<OpensourceDTO> listFile = dao.listOsFile(num);
			
			req.setAttribute("dto", dto);
			req.setAttribute("preReadOs", preReadOs);
			req.setAttribute("nextReadOs", nextReadOs);
			req.setAttribute("listFile", listFile);
			req.setAttribute("query", query);
			req.setAttribute("page", page);
			req.setAttribute("isUserLike", isUserLike);
			
			forward(req, resp, "/WEB-INF/views/opensource/article.jsp");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect(cp+"/opensource/list.do?"+query);
	}
	
	private void updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 수정 폼
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		String cp = req.getContextPath();
		OpensourceDAO dao = new OpensourceDAO();
		String page = req.getParameter("page");
		String query = "page="+page;
		
		try {
			int num = Integer.parseInt(req.getParameter("num"));
			OpensourceDTO dto = dao.readOpensource(num);
			
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			if(condition == null) {
				condition = "all";
				keyword = "";
			}
			keyword = URLDecoder.decode(keyword, "utf-8");
			
			String order = req.getParameter("order");
			if(order == null) {
				order = "latest";
			}
			if(keyword.length() != 0) {
				query += "&condition=" + condition + 
						 "&keyword=" + URLEncoder.encode(keyword, "utf-8") +
						 "&order=" + order;
			}
			if(dto == null) {
				resp.sendRedirect(cp+"/opensource/list.do?"+query);
				return;
			}
			if(! (info.getUserId().equals(dto.getUserId()))) {
				resp.sendRedirect(cp+"/opensource/list.do?"+query);
				return;
			}
			
			List<OpensourceDTO> listFile = dao.listOsFile(num);
			req.setAttribute("dto", dto);
			req.setAttribute("listFile", listFile);
			req.setAttribute("query", query);
			req.setAttribute("mode", "update");
			forward(req, resp, "/WEB-INF/views/opensource/write.jsp");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		resp.sendRedirect(cp+"/opensource/list.do?"+query);
	}
	
	private void updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 수정 완료
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		String cp = req.getContextPath();
		OpensourceDAO dao = new OpensourceDAO();
		String page = req.getParameter("page");
		String query = "page="+page;
		try {
			int num = Integer.parseInt(req.getParameter("num"));
			OpensourceDTO dto = dao.readOpensource(num);
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			String order = req.getParameter("order");
			
			query += makeQuery(condition, keyword, order);
			
			if(! info.getUserId().equals(dto.getUserId())) {
				resp.sendRedirect(cp+"/opensource/list.do?"+query);
				return;
			}
			
			if(req.getMethod().equalsIgnoreCase("GET")) {
				resp.sendRedirect(cp+"/opensource/list.do?"+query);
				return;
			}
			
			try {
				dto.setNum(Integer.parseInt(req.getParameter("num")));
				dto.setSubject(req.getParameter("subject"));
				dto.setContent(req.getParameter("content"));
				
				
				Map<String, String[]> map = doFileUpload(req.getParts(), pathname);
				if(map != null) {
					String[] ss = map.get("saveFilenames");
					String[] oo = map.get("originalFilenames");
					dto.setSaveFiles(ss);
					dto.setOriginalFiles(oo);
				}
				
				dao.updateOpensource(dto);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			resp.sendRedirect(cp+"/opensource/article.do?num="+num+"&"+query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 삭제
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		OpensourceDAO dao = new OpensourceDAO();
		String page = req.getParameter("page");
		String cp = req.getContextPath();
		String query = "page=" + page;
		
		try {
			int num = Integer.parseInt(req.getParameter("num"));
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			if(condition == null) {
				condition = "all";
				keyword = "";
			}
			keyword = URLDecoder.decode(keyword, "utf-8");
			
			String order = req.getParameter("order");
			if(order == null) {
				order = "latest";
			}
			if(keyword.length() != 0) {
				query += "&condition=" + condition + 
						 "&keyword=" + URLEncoder.encode(keyword, "utf-8") +
						 "&order=" + order;
			}
			
			
			OpensourceDTO dto = dao.readOpensource(num);
			if(dto == null) {
				resp.sendRedirect(cp+"/opensource/list.do?"+query);
				return;
			}
			
			if(! (info.getUserId().equals("admin") || info.getUserId().equals(dto.getUserId()))) {
				resp.sendRedirect(cp+"/opensource/list.do?"+query);
				return;
			}
			
			List<OpensourceDTO> list = dao.listOsFile(num);
			for(OpensourceDTO vo : list) {
				FileManager.doFiledelete(pathname, vo.getSaveFilename());
			}
			
			dao.deleteOsFile("all", num);
			dao.deleteOpensource(num);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect(cp+"/opensource/list.do?" + query);
	}
	
	private void deleteFile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 파일 삭제
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		OpensourceDAO dao = new OpensourceDAO();
		String cp = req.getContextPath();
		String page = req.getParameter("page");
		String query = "page="+page;
		
		try {
			int num = Integer.parseInt(req.getParameter("num"));
			int fileNum = Integer.parseInt(req.getParameter("fileNum"));
			OpensourceDTO dto = dao.readOpensource(num);
			if(! info.getUserId().equals(dto.getUserId())) {
				resp.sendRedirect(cp+"/opensource/article.do?num="+num+"&"+query);
				System.out.println("no ID");
				return;
			}
			dto = null;
			
			dto = dao.readOsFile(fileNum);
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			String order = req.getParameter("order");
			query += makeQuery(condition, keyword, order);
			
			
			if(dto != null) {
				FileManager.doFiledelete(pathname, dto.getSaveFilename());
				
				dao.deleteOsFile("one", fileNum);
			}
			
			resp.sendRedirect(cp+"/opensource/update.do?num="+num+"&"+query);
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect(cp+"/opensource/list.do?"+query);
	}
	
	private void download(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 다운로드
		OpensourceDAO dao = new OpensourceDAO();
		boolean b = false;
		
		try {
			int fileNum = Integer.parseInt(req.getParameter("fileNum"));
			OpensourceDTO dto = dao.readOsFile(fileNum);
			
			if(dto != null) {
				b = FileManager.doFiledownload(dto.getSaveFilename(), 
							dto.getOriginalFilename(), pathname, resp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(! b) {
			resp.setContentType("text/html;charset=utf-8");
			PrintWriter out = resp.getWriter();
			out.print("<script>alert('파일다운로드가 불가능합니다.');history.back();</script>");
		}
	}
	
	private String makeQuery(String condition, String keyword, String order) {
		String query = "";
		try {
			if(condition == null) {
				condition = "all";
				keyword = "";
			}
			keyword = URLDecoder.decode(keyword, "utf-8");
		
			if(order == null) {
				order = "latest";
			}
			if(keyword.length() != 0) {
				query += "&condition=" + condition + 
						 "&keyword=" + URLEncoder.encode(keyword, "utf-8") +
						 "&order=" + order;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return query;
	}
	
	// 게시물 좋아요 저장
	private void insertOsLike(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		OpensourceDAO dao = new OpensourceDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String state = "false";
		int osLikeCount = 0;
		
		try {
			int num = Integer.parseInt(req.getParameter("num"));
			String isNoLike = req.getParameter("isNoLike");
			
			if(isNoLike.equals("true")) {
				dao.insertOsLike(num, info.getUserId());
			} else {
				dao.deleteOsLike(num, info.getUserId());
			}
			
			osLikeCount = dao.countOsLike(num);
			
			state = "true";
		} catch (SQLException e) {
			state = "liked";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject job = new JSONObject();
		job.put("state", state);
		job.put("osLikeCount", osLikeCount);
		
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out = resp.getWriter();
		out.print(job.toString());
	}
}
