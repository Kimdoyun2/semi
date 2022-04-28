package com.photo;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.member.SessionInfo;
import com.util.FileManager;
import com.util.MyUploadServlet;
import com.util.MyUtil;

@MultipartConfig
@WebServlet("/photo/*")
public class PhotoServlet extends MyUploadServlet {
	private static final long serialVersionUID = 1L;

	private String pathname;

	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		
		String uri = req.getRequestURI();
		String cp = req.getContextPath();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		if (info == null) { // 로그인 되지 않은 경우
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}

		// 이미지를 저장할 경로(pathname)
		String root = session.getServletContext().getRealPath("/");
		pathname = root + "uploads" + File.separator + "photo";
		
		// uri에 따른 작업 구분
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
		}
	}

	private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시물 리스트
		PhotoDAO dao = new PhotoDAO();
		MyUtil util = new MyUtil();

		String cp = req.getContextPath();
		
		try {
			String page = req.getParameter("page");
			int current_page = 1;
			if (page != null) {
				current_page = Integer.parseInt(page);
			}

			// 전체데이터 개수
			int dataCount = dao.dataCount();

			// 전체페이지수
			int rows = 12;
			int total_page = util.pageCount(rows, dataCount);
			if (current_page > total_page) {
				current_page = total_page;
			}

			// 게시물 가져올 시작과 끝위치
			int start = (current_page - 1) * rows + 1;
			int end = current_page * rows;

			// 게시물 가져오기
			List<PhotoDTO> list = dao.listPhoto(start, end);

			// 페이징 처리
			String listUrl = cp + "/photo/list.do";
			String articleUrl = cp + "/photo/article.do?page=" + current_page;
			String paging = util.paging(current_page, total_page, listUrl);

			// 포워딩할 list.jsp에 전달할 값
			req.setAttribute("list", list);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("articleUrl", articleUrl);
			req.setAttribute("page", current_page);
			req.setAttribute("total_page", total_page);
			req.setAttribute("paging", paging);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		forward(req, resp, "/WEB-INF/views/photo/list.jsp");
	}

	private void writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글쓰기 폼
		req.setAttribute("mode", "write");
		forward(req, resp, "/WEB-INF/views/photo/write.jsp");
	}

	private void writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시물 저장
		PhotoDAO dao = new PhotoDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String cp = req.getContextPath();
		if (req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp + "/photo/list.do");
			return;
		}

		try {
			PhotoDTO dto = new PhotoDTO();

			dto.setUserId(info.getUserId());
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));

			String filename = null;
			Part p = req.getPart("selectFile");
			Map<String, String> map = doFileUpload(p, pathname);
			if (map != null) {
				filename = map.get("saveFilename");
			}

			if (filename != null) {
				dto.setImageFilename(filename);
				dao.insertPhoto(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/photo/list.do");
	}

	private void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시물 보기
		PhotoDAO dao = new PhotoDAO();
		
		String cp = req.getContextPath();
		String page = req.getParameter("page");

		try {
			int num = Integer.parseInt(req.getParameter("num"));

			PhotoDTO dto = dao.readPhoto(num);
			if (dto == null) {
				resp.sendRedirect(cp + "/photo/list.do?page=" + page);
				return;
			}

			dto.setContent(dto.getContent().replaceAll("\n", "<br>"));

			req.setAttribute("dto", dto);
			req.setAttribute("page", page);

			forward(req, resp, "/WEB-INF/views/photo/article.jsp");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/photo/list.do?page=" + page);
	}

	private void updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 수정 폼
		PhotoDAO dao = new PhotoDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String cp = req.getContextPath();
		String page = req.getParameter("page");

		try {
			int num = Integer.parseInt(req.getParameter("num"));
			PhotoDTO dto = dao.readPhoto(num);

			if (dto == null) {
				resp.sendRedirect(cp + "/photo/list.do?page=" + page);
				return;
			}

			// 게시물을 올린 사용자가 아니면
			if (!dto.getUserId().equals(info.getUserId())) {
				resp.sendRedirect(cp + "/photo/list.do?page=" + page);
				return;
			}

			req.setAttribute("dto", dto);
			req.setAttribute("page", page);

			req.setAttribute("mode", "update");

			forward(req, resp, "/WEB-INF/views/photo/write.jsp");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/photo/list.do?page=" + page);
	}

	private void updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 수정 완료
		PhotoDAO dao = new PhotoDAO();
		
		String cp = req.getContextPath();
		if (req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp + "/photo/list.do");
			return;
		}

		String page = req.getParameter("page");

		try {
			PhotoDTO dto = new PhotoDTO();
			
			dto.setNum(Integer.parseInt(req.getParameter("num")));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));

			String imageFilename = req.getParameter("imageFilename");
			dto.setImageFilename(imageFilename);

			Part p = req.getPart("selectFile");
			Map<String, String> map = doFileUpload(p, pathname);
			if (map != null) { // 이미지 파일을 업로드 한 경우
				String filename = map.get("saveFilename");
				// 기존 이미지 파일 지우기
				FileManager.doFiledelete(pathname, imageFilename);
				dto.setImageFilename(filename);
			}

			dao.updatePhoto(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/photo/list.do?page=" + page);
	}

	private void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 삭제 완료
		PhotoDAO dao = new PhotoDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String cp = req.getContextPath();
		String page = req.getParameter("page");

		try {
			int num = Integer.parseInt(req.getParameter("num"));

			PhotoDTO dto = dao.readPhoto(num);
			if (dto == null) {
				resp.sendRedirect(cp + "/photo/list.do?page=" + page);
				return;
			}

			// 게시물을 올린 사용자나 admin이 아니면
			if (!dto.getUserId().equals(info.getUserId()) && !info.getUserId().equals("admin")) {
				resp.sendRedirect(cp + "/photo/list.do?page=" + page);
				return;
			}

			// 이미지 파일 지우기
			FileManager.doFiledelete(pathname, dto.getImageFilename());

			// 테이블 데이터 삭제
			dao.deletePhoto(num);
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/photo/list.do?page=" + page);
	}
}
