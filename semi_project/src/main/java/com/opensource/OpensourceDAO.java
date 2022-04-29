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
	
}
