package com.lecture;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class LectureDAO {
	private Connection conn = DBConn.getConnection();
	
	// 게시물 추가
	public void insertLecture(LectureDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		int seq;
		
		try {
			// 다음 시퀀스값 설정
			sql = "SELECT lecture_seq.NEXTVAL FROM dual";
			
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			seq = 0;
			if (rs.next()) {
				seq = rs.getInt(1);
			}
			
			dto.setNum(seq);
			
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
			
			// lecture 테이블에 insert
			sql = "INSERT INTO lecture(num, lecture, userId, subject, content, hitCount, reg_date) "
					+ " VALUES (?, ?, ?, ?, ?, 0, SYSDATE)";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, dto.getNum());
			pstmt.setInt(2, dto.getLecture());
			pstmt.setString(3, dto.getUserId());
			pstmt.setString(4, dto.getSubject());
			pstmt.setString(5, dto.getContent());
			
			pstmt.executeUpdate();
			
			pstmt.close();
			pstmt = null;
			
			// lectureFile 테이블에 파일정보 insert
			if (dto.getSaveFiles() != null) {
				sql = "INSERT INTO lectureFile(fileNum, num, saveFilename, originalFilename) "
						+ " VALUES(lectureFile_seq.NEXTVAL, ?, ?, ?)";
				
				pstmt = conn.prepareStatement(sql);
				
				for (int i=0; i<dto.getSaveFiles().length; i++) {
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
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
	}

	// 전체 데이터 개수
	public int dataCount() {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT COUNT(*) FROM lecture";
			
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
		return result;
	}
	
	// 분류 데이터 개수
	public int dataCount(String category) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		
		try {
			sb.append("SELECT COUNT(*) FROM lecture l ");
			sb.append(" JOIN member1 m ON l.userId = m.userId ");
			sb.append(" WHERE lecture = ? ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, Integer.parseInt(category));
			
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
		return result;
	}
	
	// 검색 데이터 개수
	public int dataCount(String condition, String keyword) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append("SELECT COUNT(*) FROM lecture l ");
			sb.append(" JOIN member1 m ON l.userId = m.userId ");
			if (condition.equals("all")) {
				sb.append(" WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ");
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sb.append(" WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ? ");
			} else {
				sb.append(" WHERE INSTR(" + condition + ", ?) >= 1");
			}
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setString(1, keyword);
			if (condition.equals("all")) {
				pstmt.setString(2, keyword);
			}
			
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
		return result;
	}
	
	// 카테고리별 검색 데이터 개수
	public int dataCount(String category, String condition, String keyword) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append("SELECT COUNT(*) FROM lecture l ");
			sb.append(" JOIN member1 m ON l.userId = m.userId ");
			sb.append(" WHERE lecture = ? ");
			if (condition.equals("all")) {
				sb.append(" WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ");
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sb.append(" WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ? ");
			} else {
				sb.append(" WHERE INSTR(" + condition + ", ?) >= 1");
			}
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, Integer.parseInt(category));
			pstmt.setString(2, keyword);
			if (condition.equals("all")) {
				pstmt.setString(3, keyword);
			}
			
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
		return result;
	}
	
	// 전체 리스트
	public List<LectureDTO> listLecture(int start, int end, String order) {
		List<LectureDTO> list = new ArrayList<LectureDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append("SELECT * FROM ( ");
			sb.append(" 	SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append("			SELECT num, lecture, userName, subject, hitCount, reg_date ");
			sb.append("			FROM lecture l ");
			sb.append("			JOIN member1 m ON l.userId = m.userId ");
			if (order.equals("latest")) {
				sb.append("			ORDER BY num DESC ");
			} else {
				sb.append("			ORDER BY hitCount DESC ");
			}
			sb.append("		) tb WHERE ROWNUM <= ? ");
			sb.append(") WHERE rnum >= ?");
			
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, end);
			pstmt.setInt(2, start);
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				LectureDTO dto = new LectureDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setLecture(rs.getInt("lecture"));
				dto.setSubject(rs.getString("subject"));
				dto.setUserName(rs.getString("userName"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				
				list.add(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
		return list;
	}
	
	// 카테고리 페이지
	public List<LectureDTO> listLecture(int start, int end, String order, String category) {
		List<LectureDTO> list = new ArrayList<LectureDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append("SELECT * FROM ( ");
			sb.append(" 	SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append("			SELECT num, lecture, userName, subject, hitCount, reg_date ");
			sb.append("			FROM lecture l ");
			sb.append("			JOIN member1 m ON l.userId = m.userId ");
			sb.append("			WHERE lecture = ? ");
			if (order.equals("latest")) {
				sb.append("			ORDER BY num DESC ");
			} else {
				sb.append("			ORDER BY hitCount DESC ");
			}
			sb.append("		) tb WHERE ROWNUM <= ? ");
			sb.append(") WHERE rnum >= ?");
			
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, Integer.parseInt(category));
			pstmt.setInt(2, end);
			pstmt.setInt(3, start);
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				LectureDTO dto = new LectureDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setLecture(rs.getInt("lecture"));
				dto.setSubject(rs.getString("subject"));
				dto.setUserName(rs.getString("userName"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				
				list.add(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
		return list;
	}
	
	// 검색 페이지
	public List<LectureDTO> listLecture(int start, int end, String order, String condition, String keyword) {
		List<LectureDTO> list = new ArrayList<LectureDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append("SELECT * FROM ( ");
			sb.append(" 	SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append("			SELECT num, lecture, userName, subject, hitCount, reg_date ");
			sb.append("			FROM lecture l ");
			sb.append("			JOIN member1 m ON l.userId = m.userId ");
			if (condition.equals("all")) {
				sb.append("		WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ");
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sb.append("		WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ? ");
			} else {
				sb.append("		WHERE INSTR(" + condition + ", ?) >= 1 ");
			}
			sb.append("			ORDER BY num DESC ");
			sb.append("		) tb WHERE ROWNUM <= ? ");
			sb.append(") WHERE rnum >= ?");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			if (condition.equals("all")) {
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
			while (rs.next()) {
				LectureDTO dto = new LectureDTO();
				dto.setNum(rs.getInt("num"));
				dto.setLecture(rs.getInt("lecture"));
				dto.setSubject(rs.getString("subject"));
				dto.setUserName(rs.getString("userName"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				
				list.add(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
		return list;
	}
	
	// 카테고리별 검색 페이지
	public List<LectureDTO> listLecture(int start, int end, String order, String category, String condition, String keyword) {
		List<LectureDTO> list = new ArrayList<LectureDTO>();
		
		return list;
	}
	
	// 게시물 보기
	public LectureDTO readLecture(int num) {
		LectureDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT num, l.userId, userName, lecture, subject, content, "
					+ " reg_date, hitCount "
					+ " FROM lecture l "
					+ " JOIN member1 m ON l.userId = m.userId "
					+ " WHERE num = ?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, num);
			
			rs = pstmt.executeQuery();
			if (rs.next()) {
				dto = new LectureDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setLecture(rs.getInt("lecture"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setHitCount(rs.getInt("hitCount"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
		return dto;
	}
	
	// 이전글
	public LectureDTO preReadLecture(int num, String order, String category, String condition, String keyword) {
		LectureDTO dto = null;
		
		return dto;
	}
	
	// 다음글
	public LectureDTO nextReadLecture(int num, String order, String category, String condition, String keyword) {
		LectureDTO dto = null;
		
		return dto;
	}
	
	// 조회수 증가
	public void updateHitCount(int num) throws Exception {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "UPDATE lecture SET hitCount = hitCount+1 WHERE num = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
	}
	
	// 해당 게시물의 모든 첨부파일 가져오기
	public List<LectureDTO> listLectureFile(int num) {
		List<LectureDTO> list = new ArrayList<LectureDTO>();
		
		return list;
	}
	
	// 해당 파일번호의 정보
	public LectureDTO readLectureFile(int fileNum) {
		LectureDTO dto = null;
		
		return dto;
	}
	
	// 게시물 수정
	public void updateLecture(LectureDTO dto) throws SQLException {
		
	}
	
	// 파일 삭제
	public void deleteLectureFile(String mode, int num) throws SQLException {
		
	}
	
	// 게시물 삭제
	public void deleteLecture(int num) throws SQLException {
		
	}
}
