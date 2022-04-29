package com.notice;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.member.SessionInfo;
import com.util.FileManager;
import com.util.MyUploadServlet;
import com.util.MyUtil;


@MultipartConfig
@WebServlet("/notice/*")
public class NoticeServlet extends MyUploadServlet {
	private static final long serialVersionUID = 1L;
	
	private String pathname;
	
	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		
		String uri = req.getRequestURI();
		String cp = req.getContextPath();
		
		// 로그인 정보
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		// 주소가 게시글 리스트가 아니면서 로그인이 되어 있지 않은 경우에는 로그인 화면으로 이동
		if(uri.indexOf("list.do") != -1 && info == null) {
			resp.sendRedirect(cp+"/member/login.do");
			return;
		}
		
		// 파일을 저장할 경로
		String root = session.getServletContext().getRealPath("/");
		pathname = root + "uploads" + File.separator + "notice";
		
		// 주소에 따른 작업 구분 
		if(uri.indexOf("list.do") != -1) {
			list(req, resp);
		} else if(uri.indexOf("write.do") != -1) {
			writeForm(req, resp);
		}else if(uri.indexOf("write_ok.do") != -1) {
			writeSubmit(req, resp);
		}else if(uri.indexOf("article.do") != -1) {
			article(req, resp);
		}else if(uri.indexOf("update.do") != -1) {
			updateForm(req, resp);
		}else if(uri.indexOf("update_ok.do") != -1) {
			updateSubmit(req, resp);
		}else if(uri.indexOf("delete.do") != -1) {
			delete(req, resp);
		}else if(uri.indexOf("deleteFile.do") != -1) {
			deleteFile(req, resp);
		}else if(uri.indexOf("deleteList.do") != -1) {
			deleteList(req, resp);
		}else if(uri.indexOf("download.do") != -1) {
			download(req, resp);
		}
	}
	
	protected void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 리스트
		NoticeDAO dao = new NoticeDAO();
		MyUtil util = new MyUtil();
		String cp  = req.getContextPath();
				
		try {
			String page =  req.getParameter("page");
			int current_page = 1;
			if(page != null) {
				current_page = Integer.parseInt(page);
			
			}
			
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			if(condition == null) {
				condition ="all";
				keyword = "";
			}
			if(req.getMethod().equalsIgnoreCase("GET")) {
				keyword = URLDecoder.decode(keyword, "utf-8");
			}
			
			int rows = 10;
			int dataCount, total_page;
			
			if(keyword.length() == 0)
			{
				dataCount = dao.dataCount();
			} else {
				dataCount = dao.dataCount(condition, keyword);
			}
			total_page = util.pageCount(rows, dataCount);
			if(current_page > total_page) {
				current_page = total_page;
			}
			
			int start = (current_page - 1)* rows+ 1;
			int end = current_page*rows;
			
			List<NoticeDTO> list;
			if(keyword.length() == 0) {
				list = dao.listNotice(start, end);
			} else {
				list = dao.listNotice(start, end, condition, keyword);
			}
			
			// 공지글
			List<NoticeDTO> listNotice =null;
			if(current_page ==1) {
				listNotice = dao.listNotice();
			}
			listNotice = dao.listNotice();
			
			
			long gap;
			Date curDate = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
			
			
			int listNum, n = 0;
			for(NoticeDTO dto : list) {
				listNum = dataCount - (start + n - 1);
				dto.setListNum(listNum);
				
				Date date = sdf.parse(dto.getReg_date());
				gap = (curDate.getTime() - date.getTime()) /(1000*60*60); // 시간
				dto.setGap(gap);
				
				dto.setReg_date(dto.getReg_date().substring(0, 10));
				
				n++;
			}
			
			String query = "";
			String listUrl, articleUrl;
			
			listUrl = cp + "/notice/list.do";
			articleUrl = cp + "/notice/article.do?page="+current_page;
			
			if(keyword.length() != 0) {
				query = "condition="+condition+"&keyword="+URLEncoder.encode(keyword,"utf-8");
			
				listUrl += "?" + query;
				articleUrl += "&" + query;
			
			}
			
			String paging = util.paging(current_page, total_page, listUrl);
		
			//포워딩할 jsp에 전달할 값
			req.setAttribute("list", list);
			req.setAttribute("listNotice", listNotice);
			req.setAttribute("page", current_page);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("total_page", total_page);
			req.setAttribute("paging", paging);
			req.setAttribute("condition", condition);
			req.setAttribute("keyword", keyword);
			req.setAttribute("articleUrl", articleUrl);
		
		
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		forward(req, resp, "/WEB-INF/views/notice/list.jsp");
	
	}
	
	protected void writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//게시글 입력 폼
		req.setAttribute("mode", "write");
		forward(req, resp, "/WEB-INF/views/notice/write.jsp");
	}
	
	protected void writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//게시글 저장
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String cp = req.getContextPath();
		
		if(req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp+"/notice/list.do");
			return;
		}
		
		//admin만 글을 등록
		if(! info.getUserId().equals("admin")) {
			resp.sendRedirect(cp+"/notice/list.do");
			return;
		}

		NoticeDAO dao = new NoticeDAO();
		
		try {
			NoticeDTO dto = new NoticeDTO();
			
			dto.setUserId(info.getUserId()); // 세션에저장된 userId
			
			dto.setSubject(req.getParameter("subject"));
			if(req.getParameter("notice") != null) {
				dto.setNotice(Integer.parseInt(req.getParameter("notice")));
			}
			dto.setContent(req.getParameter("content"));
			
			// 파일
			Map<String, String[]> map = doFileUpload(req.getParts(), pathname);
			if(map != null) {
				String []saveFiles =map.get("saveFilenames");
				String []originalFiles = map.get("originalFilenames");
				
				dto.setSaveFiles(saveFiles);
				dto.setOriginalFiles(originalFiles);
				
			}
			dao.insertNotice(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect(cp+"/notice/list.do");
	}
	
	protected void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//게시글 보기
		NoticeDAO dao = new NoticeDAO();
		String cp = req.getContextPath();
		
		String page = req.getParameter("page");
		String query = "page="+page;
		
		try {
			int num = Integer.parseInt(req.getParameter("num"));
			
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			if(condition==null) {
				condition="all";
				keyword="";
			}
			keyword = URLDecoder.decode(keyword, "utf-8");
			if(keyword.length() != 0) {
				query += "&condition="+condition
						+"&keyword="+URLEncoder.encode(keyword, "utf-8");
			}
			
			dao.updateHitCount(num);
			
			NoticeDTO dto = dao.readNotice(num);
			if(dto == null) {
				resp.sendRedirect(cp+"/notice/list.do?"+query);
				return;
			}
			dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			
			// 이전글 다음글
			NoticeDTO preReadNotice = dao.preReadNotice(num, condition, keyword);
			NoticeDTO nextReadNotice = dao.nextReadNotice(num, condition, keyword);
			
			// 파일
			List<NoticeDTO> listFile = dao.listnoticeFile(num);
			
			req.setAttribute("dto", dto);
			req.setAttribute("preReadNotice", preReadNotice);
			req.setAttribute("nextReadNotice", nextReadNotice);
			
			req.setAttribute("listFile", listFile);
			req.setAttribute("query", query);
			req.setAttribute("page", page);
			
			forward(req, resp, "/WEB-INF/views/notice/article.jsp");
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		resp.sendRedirect(cp+"/notice/list.do?"+query);
	}
	
	protected void updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//게시글 수정 폼
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		String cp = req.getContextPath();
		
		if(! info.getUserId().equals("admin")) {
			resp.sendRedirect(cp+"/notice/list.do");
			return;
		}
		
		NoticeDAO dao = new NoticeDAO();
		String page = req.getParameter("page");
		
		try {
			int num = Integer.parseInt(req.getParameter("num"));
			NoticeDTO dto = dao.readNotice(num);
			if(dto == null) {
				resp.sendRedirect(cp+"/notice/list.do?page="+page);
				return;
			}
			
			// 첨부파일 목록
			List<NoticeDTO> listFile = dao.listnoticeFile(num);
			
			req.setAttribute("dto", dto);
			req.setAttribute("listFile", listFile);
			req.setAttribute("page", page);
			req.setAttribute("mode", "update");
			
			forward(req, resp, "/WEB-INF/views/notice/write.jsp");
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp+"/notice/list.do?page="+page);
		
	}
	
	protected void updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//게시글 수정 완료
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		String cp = req.getContextPath();
		
		if(! info.getUserId().equals("admin")) {
			resp.sendRedirect(cp+"/notice/list.do");
			return;
		}
		
		if(! req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp+"/notice/list.do");
			return;
		}
		NoticeDAO dao = new NoticeDAO();
		String page = req.getParameter("page");
		
		try {
			NoticeDTO dto = new NoticeDTO();
			
			dto.setNum(Integer.parseInt(req.getParameter("num")));
			if(req.getParameter("notice")!=null) {
				dto.setNotice(Integer.parseInt(req.getParameter("notice")));
			}
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			
			Map<String, String[]> map = doFileUpload(req.getParts(),pathname);
			if(map != null) {
				String[] ss = map.get("saveFilenames");
				String[] oo = map.get("originalFilenames");
				dto.setSaveFiles(ss);
				dto.setOriginalFiles(oo);
			}
			
			dao.updateNotice(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		resp.sendRedirect(cp+"/notice/list.do?page="+page);
		

	}
	
	protected void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//게시글 삭제
		
	}
	
	protected void deleteFile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//수정에서 파일 삭제
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String cp = req.getContextPath();
		if(! info.getUserId().equals("admin")) {
			resp.sendRedirect(cp+"/notice/list.do");
			return;
		}
		
		NoticeDAO dao = new NoticeDAO();
		
		String page = req.getParameter("page");
		try {
			int num = Integer.parseInt(req.getParameter("num"));
			int fileNum = Integer.parseInt(req.getParameter("fileNum"));
			
			// 삭제할 파일 정보
			NoticeDTO dto = dao.readNoticeFile(fileNum);
			
			if(dto != null) {
				 // 실제 파일 삭제
				FileManager.doFiledelete(pathname, dto.getSaveFilename());
				
				// 테이블 테이터 삭제
				dao.deleteNoticeFile("one", fileNum);
			}
			
			// 다시 수정 화면으로
			resp.sendRedirect(cp+"/notice/update.do?num"+num+"&page="+page);
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		resp.sendRedirect(cp+"/notice/list.do?page="+page);
	}
	
	protected void deleteList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//리스트에서 여러 게시글 삭제

	}
	
	protected void download(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//첨부파일 다운로드

		NoticeDAO dao = new NoticeDAO();
		boolean b = false;
		
		try {
			int fileNum = Integer.parseInt(req.getParameter("fileNum"));
			NoticeDTO dto = dao.readNoticeFile(fileNum);
			
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

}
