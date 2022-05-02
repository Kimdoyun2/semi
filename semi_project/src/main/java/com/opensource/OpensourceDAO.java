package com.opensource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class OpensourceDAO {
	private Connection conn = DBConn.getConnection();
	
	public void insertOpensource(OpensourceDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		int seq;
		
		try {
			sql = "SELECT opensource_seq.NEXTVAL FROM dual";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			seq = 0;
			if(rs.next()) {
				seq = rs.getInt(1);
			}
			dto.setNum(seq);
			
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
			
			sql = "INSERT INTO opensource(num, userId, subject, content, reg_date, hitCount, likeCount) "
				+ " VALUES (?,?,?,?,SYSDATE,0,0)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, dto.getNum());
			pstmt.setString(2, dto.getUserId());
			pstmt.setString(3, dto.getSubject());
			pstmt.setString(4, dto.getContent());
			
			pstmt.executeUpdate();
			
			pstmt.close();
			pstmt = null;
			
			if(dto.getSaveFiles() != null) {
				sql = "INSERT INTO osfile(fileNum, num, saveFilename, originalFilename) "
					+ " VALUES (osfile_seq.NEXTVAL, ?, ?, ?)";
				pstmt = conn.prepareStatement(sql);
				
				for(int i=0; i<dto.getSaveFiles().length; i++) {
					pstmt.setInt(1, dto.getNum());
					pstmt.setString(2, dto.getSaveFiles()[i]);
					pstmt.setString(3, dto.getOriginalFiles()[i]);
					pstmt.executeUpdate();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
			
			if(pstmt != null) {
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
			sql = "SELECT COUNT(*) FROM opensource";
			pstmt = conn.prepareStatement(sql);
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
				} catch (Exception e2) {
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
	
	public int dataCount(String condition, String keyword) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = " SELECT COUNT(*) FROM opensource o "
				+ " JOIN member1 m ON m.userId = o.userId";
			if(condition.equals("all")) {
				sql += " WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ";
			} else if(condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\.|\\/)", "");
				sql += " WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ? ";
			} else {
				sql += " WHERE INSTR("+condition+", ?) >= 1 ";
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
				} catch (Exception e2) {
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
	
	public List<OpensourceDTO> listOpensource(int start, int end, String order) {
		List<OpensourceDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sql = new StringBuilder();
		
		try {
			sql.append("SELECT * FROM ( ");
			sql.append(" SELECT ROWNUM rnum, tb.* FROM ( ");
			sql.append("    SELECT num, userName, subject, content, TO_CHAR(reg_date,'YYYY-MM-DD') reg_date, ");
			sql.append("           hitCount, likeCount ");
			sql.append("    FROM opensource o");
			sql.append("    JOIN member1 m ON o.userId = m.userId ");
			if(order.equals("latest")) {
				sql.append("    ORDER BY num DESC ");
			} else {
				sql.append("    ORDER BY "+order+" DESC ");
			}
			sql.append(" ) tb WHERE ROWNUM <= ? ");
			sql.append(" ) WHERE rnum >= ? ");
			//pstmt = new LoggableStatement(conn, sql.toString());
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt(1, end);
			pstmt.setInt(2, start);
			//System.out.println( ((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				OpensourceDTO dto = new OpensourceDTO();
				dto.setNum(rs.getInt("num"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setLikeCount(rs.getInt("likeCount"));
				list.add(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
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
	
	public List<OpensourceDTO> listOpensource(int start, int end, String condition, String keyword, String order) {
		List<OpensourceDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sql = new StringBuilder();
		
		try {
			sql.append("SELECT * FROM ( ");
			sql.append(" SELECT ROWNUM rnum, tb.* FROM ( ");
			sql.append("     SELECT num, userName, subject, content, TO_CHAR(reg_date,'YYYY-MM-DD') reg_date, ");
			sql.append("           hitCount, likeCount ");
			sql.append("     FROM opensource o ");
			sql.append("     JOIN member1 m ON o.userId = m.userId ");
			if(condition.equals("all")) {
				sql.append(" WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ");
			} else if(condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sql.append(" WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ? ");
			} else {
				sql.append(" WHERE INSTR("+condition+", ?) >= 1 ");
			}
			if(order.equals("latest")) {
				sql.append("     ORDER BY num DESC ");
			} else {
				sql.append("    ORDER BY "+order+" DESC ");
			}
			sql.append(" ) tb WHERE ROWNUM <= ? ");
			sql.append(" ) WHERE rnum >= ?");
			
			pstmt = conn.prepareStatement(sql.toString());
			
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
				OpensourceDTO dto = new OpensourceDTO();
				dto.setNum(rs.getInt("num"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setLikeCount(rs.getInt("likeCount"));
				
				list.add(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
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
	
	public void updateHitCount(int num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "UPDATE opensource SET hitCount=hitCount+1 WHERE num=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.executeUpdate();
		} catch (Exception e) {
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
	
	public OpensourceDTO readOpensource(int num) {
		OpensourceDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs =  null;
		String sql;
		
		try {
			sql = " SELECT num, o.userId, userName, subject, content, reg_date, hitCount, likeCount "
				+ " FROM opensource o "
				+ " JOIN member1 m ON m.userId = o.userId "
				+ " WHERE num = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new OpensourceDTO();
				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setLikeCount(rs.getInt("likeCount"));
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
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
	
	// 로그인 유저의 게시글 좋아요 유무
	public boolean isUserOsLike(int num, String userId) {
		boolean result = false;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT num, userId FROM osLike WHERE num = ? AND userId = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.setString(2, userId);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
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
	
	public List<OpensourceDTO> listOsFile(int num) {
		List<OpensourceDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = " SELECT fileNum, saveFilename, originalFilename "
				+ " FROM osFile "
				+ " WHERE num = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				OpensourceDTO dto = new OpensourceDTO();
				dto.setFileNum(rs.getInt("fileNum"));
				dto.setSaveFilename(rs.getString("saveFilename"));
				dto.setOriginalFilename(rs.getString("originalFilename"));
				
				list.add(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
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
	
	public OpensourceDTO preReadOs(int num, String condition, String keyword) {
		OpensourceDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sql = new StringBuilder();
		
		try {
			if(keyword != null && keyword.length() != 0) {
				sql.append(" SELECT * FROM ( ");
				sql.append("   SELECT num, subject ");
				sql.append("   FROM opensource o ");
				sql.append("   JOIN member1 m ON o.userId = m.userId ");
				sql.append("   WHERE ( num > ? ) ");
				if(condition.equals("all")) {
					sql.append(" AND ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ) ");
				} else if(condition.equals("reg_date")) {
					keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
					sql.append(" AND ( TO_CHAR(reg_date, 'YYYYMMDD) = ? ) ");
				} else {
					sql.append(" AND ( INSTR("+condition+", ?) >= 1 ) ");
				}
				sql.append(" ORDER BY num ASC ");
				sql.append(" ) WHERE ROWNUM = 1 ");
				
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setInt(1, num);
				pstmt.setString(2, keyword);
				if(condition.equals("all")) {
					pstmt.setString(3, keyword);
				}
			} else {
				sql.append(" SELECT * FROM ( ");
				sql.append("   SELECT num, subject ");
				sql.append("   FROM opensource ");
				sql.append("   WHERE ( num > ? ) ");
				sql.append(" ORDER BY num ASC ");
				sql.append(" ) WHERE ROWNUM = 1 ");
				
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setInt(1, num);
			}
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new OpensourceDTO();
				dto.setNum(rs.getInt("num"));
				dto.setSubject(rs.getString("subject"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e2) {
				}
			}
			
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e2) {
				}
			}
		}
		
		return dto;
	}
	
	public OpensourceDTO nextReadOs(int num, String condition, String keyword) {
		OpensourceDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sql = new StringBuilder();
		
		try {
			if(keyword != null && keyword.length() != 0) {
				sql.append(" SELECT * FROM ( ");
				sql.append("   SELECT num, subject ");
				sql.append("   FROM opensource o ");
				sql.append("   JOIN member1 m ON o.userId = m.userId ");
				sql.append("   WHERE ( num < ? ) ");
				if(condition.equals("all")) {
					sql.append(" AND ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ) ");
				} else if(condition.equals("reg_date")) {
					keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
					sql.append(" AND ( TO_CHAR(reg_date, 'YYYYMMDD) = ? ) ");
				} else {
					sql.append(" AND ( INSTR("+condition+", ?) >= 1 ) ");
				}
				sql.append(" ORDER BY num ASC ");
				sql.append(" ) WHERE ROWNUM = 1 ");
				
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setInt(1, num);
				pstmt.setString(2, keyword);
				if(condition.equals("all")) {
					pstmt.setString(3, keyword);
				}
			} else {
				sql.append(" SELECT * FROM ( ");
				sql.append("   SELECT num, subject ");
				sql.append("   FROM opensource ");
				sql.append("   WHERE ( num < ? ) ");
				sql.append(" ORDER BY num ASC ");
				sql.append(" ) WHERE ROWNUM = 1 ");
				
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setInt(1, num);
			}
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new OpensourceDTO();
				dto.setNum(rs.getInt("num"));
				dto.setSubject(rs.getString("subject"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e2) {
				}
			}
			
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e2) {
				}
			}
		}
		
		return dto;
	}
	
	public OpensourceDTO readOsFile(int fileNum) {
		OpensourceDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = " SELECT fileNum, num, saveFilename, originalFilename "
				+ " FROM osfile "
				+ " WHERE fileNum = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, fileNum);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new OpensourceDTO();
				dto.setFileNum(rs.getInt("fileNum"));
				dto.setNum(rs.getInt("num"));
				dto.setSaveFilename(rs.getString("saveFilename"));
				dto.setOriginalFilename(rs.getString("originalFilename"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
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
	
	public void deleteOpensource(int num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "DELETE FROM opensource WHERE num=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.executeUpdate();
		} catch (Exception e) {
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
	
	public void deleteOsFile(String mode, int num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			if(mode.equals("all")) {
				sql = "DELETE FROM osfile WHERE num = ?";
			} else {
				sql = "DELETE FROM osfile WHERE fileNum = ?";
			}
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.executeUpdate();
		} catch (Exception e) {
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
	
	public void updateOpensource(OpensourceDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "UPDATE opensource SET subject=?, content=? WHERE num=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setInt(3, dto.getNum());
			pstmt.executeUpdate();
			
			pstmt.close();
			pstmt = null;
			
			if(dto.getSaveFiles() != null) {
				sql = "INSERT INTO osFile(fileNum, num, saveFilename, originalFilename) "
					+ " VALUES (osFile_seq.NEXTVAL, ?, ?, ?)";
				pstmt = conn.prepareStatement(sql);
				
				for(int i=0; i<dto.getSaveFiles().length; i++) {
					pstmt.setInt(1, dto.getNum());
					pstmt.setString(2, dto.getSaveFiles()[i]);
					pstmt.setString(3, dto.getOriginalFiles()[i]);
					
					pstmt.executeUpdate();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
	}
	
	// 게시물 좋아요 추가
	public void insertOsLike(int num, String userId) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO osLike(num, userId) VALUES(?, ?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.setString(2, userId);
	
			pstmt.executeUpdate();
			
			pstmt.close();
			pstmt = null;
			
			sql = "UPDATE opensource SET likeCount=likeCount+1 WHERE num = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.executeUpdate();
			
		} catch (Exception e) {
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
	
	// 게시물 공감 삭제
	public void deleteOsLike(int num, String userId) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "DELETE FROM osLike WHERE num = ? AND userId = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.setString(2, userId);
			pstmt.executeUpdate();
			pstmt.close();
			pstmt = null;
			
			sql = "UPDATE opensource SET likeCount=likeCount-1 WHERE num = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.executeUpdate();
			
		} catch (Exception e) {
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
	
	// 게시물 좋아요 개수
	public int countOsLike(int num) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM osLike WHERE num = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
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
				} catch (SQLException e2) {
				}
			}
			
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e2) {
				}
			}
		}
		
		return result;
	}
	
	// 게시글 댓글 추가
	public void insertReply(ReplyDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = " INSERT INTO osReply(replyNum, num, userId, content, reg_date, answer) "
				+ " VALUES (osReply_seq.NEXTVAL, ?, ?, ?, SYSDATE, ?) ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, dto.getNum());
			pstmt.setString(2, dto.getUserId());
			pstmt.setString(3, dto.getContent());
			pstmt.setInt(4, dto.getAnswer());
			pstmt.executeUpdate();
		} catch (Exception e) {
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
	
	// 게시물 댓글 개수
	public int replyCount(int num) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT NVL(COUNT(*),0) FROM osReply WHERE num = ? AND answer = 0";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
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
				} catch (SQLException e2) {
				}
			}
			
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e2) {
				}
			}
		}
		
		return result;
	}
	
	public List<ReplyDTO> listReply(int num, int start, int end) {
		List<ReplyDTO> list = new ArrayList<ReplyDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT *FROM ( "
				+ "  SELECT ROWNUM rnum th.* FROM ("
				+ "    SELECT o.replyNum, num, o.userName, content, o.reg_date, "
				+ "           NVL(answerCount, 0) answerCount "
				+ "    FROM osReply o "
				+ "    JOIN member1 m ON m.userId = o.userId "
				+ "    LEFT OUTER JOIN ( "
				+ "       SELECT answer, COUNT(*) answerCount "
				+ "       FROM osReply WHERE answer != 0 "
				+ "       GROUP BY answer "
				+ "    ) a ON o.replyNum = a.answer "
				+ "    WHERE num = ? AND o.answer = 0 "
				+ "    ORDER BY o.replyNum DESC "
				+ "  ) tb WHERE ROWNUM <= ? "
				+ " WHERE rnum >= ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.setInt(2, end);
			pstmt.setInt(3, start);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				ReplyDTO dto = new ReplyDTO();
				
				dto.setReplyNum(rs.getInt("replyNum"));
				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setAnswerCount(rs.getInt("answerCount"));
				
				list.add(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e2) {
				}
			}
			
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e2) {
				}
			}
		}
		
		return list;
	}
	
	public ReplyDTO readReply(int num) {
		ReplyDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = " SELECT replyNum, num, o.userId, userName, content, o.reg_date "
				+ " FROM osReply o "
				+ " JOIN member1 m ON m.userId = o.userId "
				+ " WHERE replyNum = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new ReplyDTO();
				
				dto.setReplyNum(rs.getInt("replyNum"));
				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e2) {
				}
			}
			
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e2) {
				}
			}
		}
		
		return dto;
	}
	
}
