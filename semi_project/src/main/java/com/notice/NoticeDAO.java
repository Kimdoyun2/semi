package com.notice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class NoticeDAO {
private Connection conn = DBConn.getConnection();
private ResultSet rs;
	
	public void insertNotice(NoticeDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		rs = null;
		String sql;
		int seq;
		
		try {
			// 다음 시퀀스값 가져오기
			sql = "SELECT notice_seq.NEXTVAL FROM dual";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
			
			seq = 0;
			if(rs.next()) {
				seq = rs.getInt(1);
			}
			dto.setNum(seq);
			
			rs.close();
			pstmt.close();
			rs=null;
			pstmt = null;
			
			//notice 테이블에 게시물 추가
			sql = "INSERT INTO notice(num, notice, userId, subject, content, hitCount, reg_date) "
					+" VALUES (?, ?, ?, ?, ?, 0,  SYSDATE)";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, dto.getNum());
			pstmt.setInt(2, dto.getNotice());
			pstmt.setString(3, dto.getUserId());
			pstmt.setString(4, dto.getSubject());
			pstmt.setString(5, dto.getContent());
			
			pstmt.executeUpdate();
			
			pstmt.close();
			pstmt = null;
			
			// noticeFile 테이블에 업로드된 파일이름 저장
			if(dto.getSaveFiles() !=null) {
				sql = "INSERT INTO noticeFile(fileNum, num, saveFilename, originalFilename) "
					+ " VALUES(noticeFile_seq.NEXTVAL, ?, ?, ?)";
				
				pstmt = conn.prepareStatement(sql);
			
			
			// 업로드한 파일 개수만큼 INSERT
			for(int i =0; i<dto.getSaveFiles().length; i++) {
				pstmt.setInt(1, dto.getNum());
				pstmt.setString(2, dto.getSaveFiles()[i]);
				pstmt.setString(3, dto.getOriginalFiles()[i]);
				
				pstmt.executeUpdate();
			}
		}	
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
			if(pstmt !=null) {
				try {
					pstmt.close();
				} catch (Exception e2) {	
				}
			}
		}
	}
	
	public int dataCount() {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT COUNT(*) FROM notice";
			
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result = rs.getInt(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
			if(pstmt!=null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
		
		return result;
	}
	
	public int dataCount(String condition, String keyword) {
		int result = 0;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT COUNT(*) FROM notice n ";
			sql += " JOIN member1 m ON n.userId = m.userId ";
			
			if(condition.equals("all")) {
				sql+= " WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?)>= 1";
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sql += " WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ?";
			} else {
				sql += " WHERE INSTR(" + condition + ", ?) >= 1";
			}
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, keyword);
			if(condition.equals("all")) {
				pstmt.setString(2, keyword);
			}
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				}catch (Exception e2) {
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
			
		}
		return result;
	}
	
	public List<NoticeDTO> listNotice(int start, int end){
		List<NoticeDTO> list = new ArrayList<NoticeDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append("SELECT * FROM ( ");
			sb.append(" SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append("  SELECT num, notice, userName, subject, hitCount, reg_date ");
			sb.append("   FROM notice n ");
			sb.append("    JOIN member1 m ON n.userId = m.userId ");
			sb.append("    ORDER BY num DESC ");
			sb.append(" ) tb WHERE ROWNUM <= ? ");
			sb.append(" ) WHERE rnum >= ?");
			
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, end);
			pstmt.setInt(2, start);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				NoticeDTO dto = new NoticeDTO();
				dto.setNum(rs.getInt("num"));
				dto.setNotice(rs.getInt("notice"));
				dto.setSubject(rs.getString("subject"));
				dto.setUserName(rs.getString("userName"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				
				list.add(dto);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				}catch (Exception e2) {
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
			
		}
		return list;
	}
	
	public List<NoticeDTO> listNotice(int start, int end, String condition, String keyword){
		List<NoticeDTO> list = new ArrayList<NoticeDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append("SELECT * FROM ( ");
			sb.append(" SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append("  SELECT num, notice, userName, subject, hitCount, reg_date ");
			sb.append("   FROM notice n ");
			sb.append("    JOIN member1 m ON n.userId = m.userId ");
			if(condition.equals("all")) {
				sb.append(" WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?)>= 1");
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sb.append(" WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ?");
			} else {
				sb.append(" WHERE INSTR(" + condition + ", ?) >= 1");
			}
			
			sb.append("    ORDER BY num DESC ");
			sb.append(" ) tb WHERE ROWNUM <= ? ");
			sb.append(" ) WHERE rnum >= ?");
			
			pstmt = conn.prepareStatement(sb.toString());
		
			if(condition.equals("all")) {
				pstmt.setString(1, keyword);
				pstmt.setString(2, keyword);
				pstmt.setInt(3, end);
				pstmt.setInt(4, start);
			} else {
				pstmt.setString(1, keyword);
				pstmt.setInt(2, end);
				pstmt.setInt(3, start);
			}
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				NoticeDTO dto = new NoticeDTO();
				dto.setNum(rs.getInt("num"));
				dto.setNotice(rs.getInt("notice"));
				dto.setSubject(rs.getString("subject"));
				dto.setUserName(rs.getString("userName"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				
				list.add(dto);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				}catch (Exception e2) {
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
			
		}
		return list;
	}
	
	// 1페이지인경우 출력할 공지 체크한 글
	public List<NoticeDTO> listNotice() {
		List<NoticeDTO> list = new ArrayList<NoticeDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT num, notice, userName, subject, hitCount, "
				+ " TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date "
				+ " FROM notice n "
				+ " JOIN member1 m ON n.userId = m.userId "
				+ " WHERE notice = 1 "
				+ " ORDER BY num DESC ";
			
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				NoticeDTO dto = new NoticeDTO();
				dto.setNum(rs.getInt("num"));
				dto.setNotice(rs.getInt("notice"));
				dto.setSubject(rs.getString("subject"));
				dto.setUserName(rs.getString("userName"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				
				list.add(dto);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				}catch (Exception e2) {
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
			
		}
		return list;
		
	}
	
	public NoticeDTO readNotice(int num) {
		NoticeDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT num, n.userId, userName, notice, subject, content, "
				+ " reg_date, hitCount "
				+ " FROM notice n "
				+ " JOIN member1 m ON n.userId = m.userId "
				+ " WHERE num = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				dto = new NoticeDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setNotice(rs.getInt("notice"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setSubject(rs.getString("subject"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setHitCount(rs.getInt("hitCount"));
			
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				}catch (Exception e2) {
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
			
		}
		
		return dto;
	}
	
	// 이전글
		public NoticeDTO preReadNotice(int num, String condition, String keyword) {
			NoticeDTO dto = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			StringBuilder sb = new StringBuilder();

			try {
				if (keyword != null && keyword.length() != 0) {
					sb.append(" SELECT * FROM ( ");
					sb.append("    SELECT num, subject ");
					sb.append("    FROM notice n ");
					sb.append("    JOIN member1 m ON n.userId = m.userId ");
					sb.append("    WHERE ( num > ? ) ");
					if (condition.equals("all")) {
						sb.append("   AND ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ) ");
					} else if (condition.equals("reg_date")) {
						keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
						sb.append("   AND ( TO_CHAR(reg_date, 'YYYYMMDD') = ? ) ");
					} else {
						sb.append("   AND ( INSTR(" + condition + ", ?) >= 1 ) ");
					}
					sb.append("     ORDER BY num ASC ");
					sb.append(" ) WHERE ROWNUM = 1 ");

					pstmt = conn.prepareStatement(sb.toString());
					
					pstmt.setInt(1, num);
					pstmt.setString(2, keyword);
					if (condition.equals("all")) {
						pstmt.setString(3, keyword);
					}
				} else {
					sb.append(" SELECT * FROM ( ");
					sb.append("     SELECT num, subject FROM notice ");
					sb.append("     WHERE num > ? ");
					sb.append("     ORDER BY num ASC ");
					sb.append(" ) WHERE ROWNUM = 1 ");

					pstmt = conn.prepareStatement(sb.toString());
					
					pstmt.setInt(1, num);
				}

				rs = pstmt.executeQuery();

				if (rs.next()) {
					dto = new NoticeDTO();
					dto.setNum(rs.getInt("num"));
					dto.setSubject(rs.getString("subject"));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
					}
				}

				if (pstmt != null) {
					try {
						pstmt.close();
					} catch (SQLException e) {
					}
				}
			}

			return dto;
		}

		// 다음글
		public NoticeDTO nextReadNotice(int num, String condition, String keyword) {
			NoticeDTO dto = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			StringBuilder sb = new StringBuilder();

			try {
				if (keyword != null && keyword.length() != 0) {
					sb.append(" SELECT * FROM ( ");
					sb.append("    SELECT num, subject ");
					sb.append("    FROM notice n ");
					sb.append("    JOIN member1 m ON n.userId = m.userId ");
					sb.append("    WHERE ( num < ? ) ");
					if (condition.equals("all")) {
						sb.append("   AND ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ) ");
					} else if (condition.equals("reg_date")) {
						keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
						sb.append("   AND ( TO_CHAR(reg_date, 'YYYYMMDD') = ? ) ");
					} else {
						sb.append("   AND ( INSTR(" + condition + ", ?) >= 1 ) ");
					}
					sb.append("     ORDER BY num DESC ");
					sb.append(" ) WHERE ROWNUM = 1 ");

					pstmt = conn.prepareStatement(sb.toString());
					
					pstmt.setInt(1, num);
					pstmt.setString(2, keyword);
					if (condition.equals("all")) {
						pstmt.setString(3, keyword);
					}
				} else {
					sb.append(" SELECT * FROM ( ");
					sb.append("     SELECT num, subject FROM notice ");
					sb.append("     WHERE num < ? ");
					sb.append("     ORDER BY num DESC ");
					sb.append(" ) WHERE ROWNUM = 1 ");

					pstmt = conn.prepareStatement(sb.toString());
					
					pstmt.setInt(1, num);
				}

				rs = pstmt.executeQuery();

				if (rs.next()) {
					dto = new NoticeDTO();
					dto.setNum(rs.getInt("num"));
					dto.setSubject(rs.getString("subject"));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
					}
				}

				if (pstmt != null) {
					try {
						pstmt.close();
					} catch (SQLException e) {
					}
				}
			}

			return dto;
		}

	public void updateHitCount(int num) throws Exception {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql ="UPDATE notice SET hitCount=hitCount+1 WHERE num = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}finally {
			if(pstmt!=null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
	}
	
	public List<NoticeDTO> listnoticeFile(int num){
		// 해당 게시물의 모든 첨부파일 리스트 가져오기
		List<NoticeDTO> list = new ArrayList<NoticeDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT fileNum, saveFilename, originalFilename FROM noticeFile "
					+ " WHERE num = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			
			rs = pstmt.executeQuery();
			while(rs.next()) { 
				NoticeDTO dto = new NoticeDTO();
				dto.setFileNum(rs.getInt("filenum"));
				dto.setSaveFilename(rs.getString("saveFilename"));
				dto.setOriginalFilename(rs.getString("originalFilename"));
				list.add(dto);
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
			
			if(pstmt!=null) {
				try {
					pstmt.close();
				} catch (Exception e2) {	
				}
			}
		}
		
		return list;
	}
	
	public NoticeDTO readNoticeFile(int fileNum) {
		// 파일번호에 해당하는 noticeFile 테이블의 내용
		NoticeDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT fileNum, num, saveFilename, originalFilename FROM noticeFile "
					+ " WHERE fileNum = ? "; 
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, fileNum);
			
			rs = pstmt.executeQuery();
			if(rs.next()) { 
				dto = new NoticeDTO();
				dto.setFileNum(rs.getInt("filenum"));
				dto.setNum(rs.getInt("num"));
				dto.setSaveFilename(rs.getString("saveFilename"));
				dto.setOriginalFilename(rs.getString("originalFilename"));
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
			
			if(pstmt!=null) {
				try {
					pstmt.close();
				} catch (Exception e2) {	
				}
			}
		}
		return dto;
	}
	

	public void updateNotice(NoticeDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "UPDATE notice SET notice=?, subject=?, content=?, WHERE num=?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, dto.getNotice());
			pstmt.setString(2, dto.getSubject());
			pstmt.setString(3, dto.getContent());
			pstmt.setInt(4, dto.getNum());
			
			pstmt.executeUpdate();
			
			pstmt.close();
			pstmt = null;
			
			//첨부 파일이 존재하는 경우 첨부파일을 INSERT 
			if(dto.getSaveFiles() != null) {
				sql = "INSERT INTO noticeFile(fileNum, num, saveFilename, originalFilename) "
						+ " VALUES (noticeFile_seq.NEXTVAL, ?, ?, ?)";
			pstmt = conn.prepareStatement(sql);
			
			for(int i =0; i<dto.getSaveFiles().length; i++) {
				pstmt.setInt(1, dto.getNum());
				pstmt.setString(2, dto.getSaveFiles()[i]);
				pstmt.setString(3, dto.getOriginalFiles()[i]);
				
				pstmt.executeUpdate();
			}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
						}
			}
		}
	}
	
	public void deleteNoticeFile(String mode, int num) throws SQLException {
		// 파일 테이블 삭제
		PreparedStatement pstmt = null;
		String sql;		
		
		try {
			if(mode.equals("all")) { // 게시글이 삭제된 경우 게시글의 모든 첨부 파일 삭제
				sql = "DELETE FROM noticeFile WHERE num=?";
			}else { // 해당 첨부 파일만 삭제
				sql = "DELETE FROM noticeFile WHERE fileNum=?";
			}
		
			pstmt = conn.prepareStatement(sql);
		
			pstmt.setInt(1, num);
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
	}
		
		
		public void deleteNotice(int num) throws SQLException {
			PreparedStatement pstmt = null;
			String sql;
			
			try {
				sql = "DELETE FROM notice WHERE num =?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, num);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
				throw e;
			} finally {
				if(pstmt!=null) {
					try {
						pstmt.close();
					} catch (Exception e) {
					}
				}
			}
		}
	
	
	}


