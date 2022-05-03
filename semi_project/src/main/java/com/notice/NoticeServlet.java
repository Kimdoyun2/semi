package com.notice;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;
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

import org.json.JSONObject;

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
		}else if(uri.indexOf("deleteFile.do") != -1) {
			deleteFile(req, resp);
		}else if(uri.indexOf("delete.do") != -1) {
			delete(req, resp);
		}else if(uri.indexOf("deleteList.do") != -1) {
			deleteList(req, resp);
		}else if(uri.indexOf("download.do") != -1) {
			download(req, resp);
		}else if(uri.indexOf("insertBoardLike.do")!= -1) {
			// 게시물 공감 저장
			insertBoardLike(req, resp);
		}else if (uri.indexOf("insertReply.do") != -1) {
			// 댓글 추가
			insertReply(req, resp);
		} else if (uri.indexOf("listReply.do") != -1) {
			// 댓글 리스트
			listReply(req, resp);
		}else if (uri.indexOf("deleteReply.do") != -1) {
			// 댓글 삭제
			deleteReply(req, resp);
		} else if (uri.indexOf("insertReplyAnswer.do") != -1) {
			// 댓글의 답글 추가
			insertReplyAnswer(req, resp);
		} else if (uri.indexOf("listReplyAnswer.do") != -1) {
			// 댓글의 답글 리스트
			listReplyAnswer(req, resp);
		}else if (uri.indexOf("deleteReplyAnswer.do") != -1) {
			// 댓글의 답글 삭제
			deleteReplyAnswer(req, resp);
		} else if (uri.indexOf("countReplyAnswer.do") != -1) {
			// 댓글의 답글 개수
			countReplyAnswer(req, resp);
		}else if (uri.indexOf("insertReplyLike.do") != -1) {
			// 댓글 좋아요/싫어요 추가
			insertReplyLike(req, resp);
		} else if (uri.indexOf("countReplyLike.do") != -1) {
			// 댓글 좋아요/싫어요 개수
			countReplyLike(req, resp);
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
			 
			String order = req.getParameter("order");
			if(order == null) {
				order = "latest"; //아니면 기본은 최신글 순서
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
			
			List<NoticeDTO> list = null;
			if(keyword.length() == 0) {
				list = dao.listNotice(start, end, order);
			} else {
				list = dao.listNotice(start, end, condition, keyword, order);
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
				query = "condition="+condition+"&keyword="+URLEncoder.encode(keyword,"utf-8")+"&order="+order;
			
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
			req.setAttribute("order", order);
		
		
		
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
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String page = req.getParameter("page");
		String query = "page="+page;
		
		try {
			int num = Integer.parseInt(req.getParameter("num"));
			
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			String order = req.getParameter("order");
			
			
			if(condition==null) {
				condition="all";
				keyword="";
			}
			keyword = URLDecoder.decode(keyword, "utf-8");
			
			
			if(keyword.length() != 0) {
				query += "&condition="+condition
						+"&keyword="+URLEncoder.encode(keyword, "utf-8")+"&order="+order;
			}
			
			dao.updateHitCount(num);
			
			//게시물 가져오기
			NoticeDTO dto = dao.readNotice(num);
			if(dto == null) {
				resp.sendRedirect(cp+"/notice/list.do?"+query);
				return;
			}
			dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			
			// 로그인 유저의 게시글 공감 유무
			boolean isUserLike = dao.isUserBoardLike(num, info.getUserId());
			
			// 이전글 다음글
			NoticeDTO preReadNotice = dao.preReadNotice(num, condition, keyword);
			NoticeDTO nextReadNotice = dao.nextReadNotice(num, condition, keyword);
			
			// 파일
			List<NoticeDTO> listFile = dao.listNoticeFile(num);
			
			req.setAttribute("dto", dto);
			req.setAttribute("preReadNotice", preReadNotice);
			req.setAttribute("nextReadNotice", nextReadNotice);
			
			req.setAttribute("listFile", listFile);
			req.setAttribute("query", query);
			req.setAttribute("page", page);
			
			req.setAttribute("isUserLike", isUserLike);
			
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
		
		NoticeDAO dao = new NoticeDAO();
		String page = req.getParameter("page");
		
		try {
			int num = Integer.parseInt(req.getParameter("num"));
			NoticeDTO dto = dao.readNotice(num);
			if(dto == null) {
				resp.sendRedirect(cp+"/notice/list.do?page="+page);
				return;
			}
			
			// 게시물을 올린 사용자가 아니면
			if (!dto.getUserId().equals(info.getUserId())) {
				resp.sendRedirect(cp + "/bbs/list.do?page=" + page);
				return;
			}
			
			// 첨부파일 목록
			List<NoticeDTO> listFile = dao.listNoticeFile(num);
			
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
		if(req.getMethod().equalsIgnoreCase("GET")) {
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
			
			dto.setUserId(info.getUserId());
			
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
		// 삭제
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String cp = req.getContextPath();
		
		NoticeDAO dao = new NoticeDAO();

		String page = req.getParameter("page");
		String query = "page="+ page;

		try {
			int num = Integer.parseInt(req.getParameter("num"));
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			if (condition == null) {
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

			NoticeDTO dto = dao.readNotice(num);
			if (dto == null) {
				resp.sendRedirect(cp + "/notice/list.do?" + query);
				return;
			}

			// 파일삭제
			List<NoticeDTO> listFile = dao.listNoticeFile(num);
			for (NoticeDTO vo : listFile) {
				FileManager.doFiledelete(pathname, vo.getSaveFilename());
			}
			dao.deleteNoticeFile("all", num);

			// 게시글 삭제
			dao.deleteNotice(num, info.getUserId());

		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/notice/list.do?" + query);
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
	
	// 게시물 공감 저장 - AJAX:JSON
	private void insertBoardLike(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		NoticeDAO dao = new NoticeDAO();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String state = "false";
		int boardLikeCount = 0;

		try {
			int num = Integer.parseInt(req.getParameter("num"));
			String isNoLike = req.getParameter("isNoLike");

			if (isNoLike.equals("true")) {
				dao.insertBoardLike(num, info.getUserId()); // 공감
			} else {
				dao.deleteBoardLike(num, info.getUserId()); // 공감 취소
			}

			boardLikeCount = dao.countBoardLike(num);

			state = "true";
		} catch (SQLException e) {
			state = "liked";
		} catch (Exception e) {
			e.printStackTrace();
		}

		JSONObject job = new JSONObject();
		job.put("state", state);
		job.put("boardLikeCount", boardLikeCount);

		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out = resp.getWriter();
		out.print(job.toString());
	}

	// 리플 리스트 - AJAX:TEXT
	private void listReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		NoticeDAO dao = new NoticeDAO();
		MyUtil util = new MyUtil();

		try {
			int num = Integer.parseInt(req.getParameter("num"));
			String pageNo = req.getParameter("pageNo");
			int current_page = 1;
			if (pageNo != null)
				current_page = Integer.parseInt(pageNo);

			int rows = 5;
			int total_page = 0;
			int replyCount = 0;

			replyCount = dao.dataCountReply(num);
			total_page = util.pageCount(rows, replyCount);
			if (current_page > total_page) {
				current_page = total_page;
			}

			int start = (current_page - 1) * rows + 1;
			int end = current_page * rows;

			// 리스트에 출력할 데이터
			List<ReplyDTO> listReply = dao.listReply(num, start, end);

			// 엔터를 <br>
			for (ReplyDTO dto : listReply) {
				dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			}

			// 페이징 처리 : AJAX 용 - listPage : 자바스크립트 함수명
			String paging = util.pagingMethod(current_page, total_page, "listPage");

			req.setAttribute("listReply", listReply);
			req.setAttribute("pageNo", current_page);
			req.setAttribute("replyCount", replyCount);
			req.setAttribute("total_page", total_page);
			req.setAttribute("paging", paging);

			forward(req, resp, "/WEB-INF/views/notice/listReply.jsp");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendError(400);

	}

	// 리플 또는 답글 저장 - AJAX:JSON
	private void insertReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		NoticeDAO dao = new NoticeDAO();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String state = "false";
		try {
			ReplyDTO dto = new ReplyDTO();

			int num = Integer.parseInt(req.getParameter("num"));
			dto.setNum(num);
			dto.setUserId(info.getUserId());
			dto.setContent(req.getParameter("content"));
			String answer = req.getParameter("answer");
			if (answer != null) {
				dto.setAnswer(Integer.parseInt(answer));
			}

			dao.insertReply(dto);

			state = "true";
		} catch (Exception e) {
			e.printStackTrace();
		}

		JSONObject job = new JSONObject();
		job.put("state", state);

		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out = resp.getWriter();
		out.print(job.toString());
	}

	// 리플 또는 답글 삭제 - AJAX:JSON
	private void deleteReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		NoticeDAO dao = new NoticeDAO();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		String state = "false";

		try {
			int replyNum = Integer.parseInt(req.getParameter("replyNum"));

			dao.deleteReply(replyNum, info.getUserId());

			state = "true";
		} catch (Exception e) {
			e.printStackTrace();
		}

		JSONObject job = new JSONObject();
		job.put("state", state);

		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out = resp.getWriter();
		out.print(job.toString());
	}

	// 답글 저장 - AJAX:JSON
		private void insertReplyAnswer(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			insertReply(req, resp);
		}

		// 리플의 답글 리스트 - AJAX:TEXT
		private void listReplyAnswer(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			NoticeDAO dao = new NoticeDAO();

			try {
				int answer = Integer.parseInt(req.getParameter("answer"));

				List<ReplyDTO> listReplyAnswer = dao.listReplyAnswer(answer);

				// 엔터를 <br>(스타일 => style="white-space:pre;")
				for (ReplyDTO dto : listReplyAnswer) {
					dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
				}

				req.setAttribute("listReplyAnswer", listReplyAnswer);

				forward(req, resp, "/WEB-INF/views/notice/listReplyAnswer.jsp");
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}

			resp.sendError(400);
		}

		// 리플 답글 삭제 - AJAX:JSON
		private void deleteReplyAnswer(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			deleteReply(req, resp);
		}

		// 리플의 답글 개수 - AJAX:JSON
		private void countReplyAnswer(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			NoticeDAO dao = new NoticeDAO();
			int count = 0;

			try {
				int answer = Integer.parseInt(req.getParameter("answer"));
				count = dao.dataCountReplyAnswer(answer);
			} catch (Exception e) {
				e.printStackTrace();
			}

			JSONObject job = new JSONObject();
			job.put("count", count);

			resp.setContentType("text/html;charset=utf-8");
			PrintWriter out = resp.getWriter();
			out.print(job.toString());
		}

		// 댓글 좋아요 / 싫어요 저장 - AJAX:JSON
		private void insertReplyLike(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			NoticeDAO dao = new NoticeDAO();

			HttpSession session = req.getSession();
			SessionInfo info = (SessionInfo) session.getAttribute("member");

			String state = "false";
			int likeCount = 0;
			int disLikeCount = 0;

			try {
				int replyNum = Integer.parseInt(req.getParameter("replyNum"));
				int replyLike = Integer.parseInt(req.getParameter("replyLike"));

				ReplyDTO dto = new ReplyDTO();

				dto.setReplyNum(replyNum);
				dto.setUserId(info.getUserId());
				dto.setReplyLike(replyLike);

				dao.insertReplyLike(dto);

				Map<String, Integer> map = dao.countReplyLike(replyNum);

				if (map.containsKey("likeCount")) {
					likeCount = map.get("likeCount");
				}

				if (map.containsKey("disLikeCount")) {
					disLikeCount = map.get("disLikeCount");
				}

				state = "true";
			} catch (SQLException e) {
				if (e.getErrorCode() == 1) {
					state = "liked";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			JSONObject job = new JSONObject();
			job.put("state", state);
			job.put("likeCount", likeCount);
			job.put("disLikeCount", disLikeCount);

			resp.setContentType("text/html;charset=utf-8");
			PrintWriter out = resp.getWriter();
			out.print(job.toString());
		}

		// 댓글 좋아요 / 싫어요 개수 - AJAX:JSON
		private void countReplyLike(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			NoticeDAO dao = new NoticeDAO();

			int likeCount = 0;
			int disLikeCount = 0;

			try {
				int replyNum = Integer.parseInt(req.getParameter("replyNum"));
				Map<String, Integer> map = dao.countReplyLike(replyNum);

				if (map.containsKey("likeCount")) {
					likeCount = map.get("likeCount");
				}

				if (map.containsKey("disLikeCount")) {
					disLikeCount = map.get("disLikeCount");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			JSONObject job = new JSONObject();
			job.put("likeCount", likeCount);
			job.put("disLikeCount", disLikeCount);

			resp.setContentType("text/html;charset=utf-8");
			PrintWriter out = resp.getWriter();
			out.print(job.toString());
		}
	}
