package com.mypage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class MypageDAO {
	private Connection conn = DBConn.getConnection();
	
	// 내정보보기 이름, 등급, 가입일
	public MypageDTO readMypage(String userId) {
		MypageDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append("SELECT userName, ");
			sb.append(" CASE WHEN userid = 'admin' THEN '관리자' ");
			sb.append(" ELSE '회원'");
			sb.append(" END AS grade, TO_CHAR(register_date, 'YYYY-MM-DD') register_date");
			sb.append("  FROM member1");
			sb.append("  WHERE userId = ?");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setString(1, userId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new MypageDTO();
				
				dto.setUserName(rs.getString("userName"));
				dto.setGrade(rs.getString("grade"));
				dto.setRegister_date(rs.getString("register_date"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
				
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}
		
		return dto;
	}	
	
	// 쓴 글 갯수
	public int readNumber(String userId) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
	try {
		sql = "SELECT COUNT(*) FROM ("
			+ " SELECT 'freebbs' tbname, subject FROM freebbs WHERE userid = ?"
			+ " UNION ALL "
			+ " SELECT 'opensource' tbname, subject FROM opensource WHERE userid = ?"
			+ " UNION ALL "
			+ " SELECT 'lecture' tbname, subject FROM lecture WHERE userid = ?"
			+ " UNION ALL "
			+ " SELECT 'study' tbname, subject FROM study WHERE userid = ?"
			+ " UNION ALL "
			+ " SELECT 'qAnda' tbname, subject FROM qAnda WHERE userid = ?"
			+ " ) ";
		
		pstmt = conn.prepareStatement(sql);
		
		pstmt.setString(1, userId);
		pstmt.setString(2, userId);
		pstmt.setString(3, userId);
		pstmt.setString(4, userId);
		pstmt.setString(5, userId);
		
		rs = pstmt.executeQuery();
		
		if(rs.next()) {
			result = rs.getInt(1);
		}
		
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
			}
		}
			
		if(pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
			}
		}
	}
		return result;
	}
	
	
	public List<MypageDTO> listNotice(String userId, int start, int end) {
		List<MypageDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append("SELECT * FROM ( ");
			sb.append("   SELECT ROWNUM rnum, tb.* FROM ( "); 
			sb.append("      SELECT 'notice' boardName, num, subject, reg_date, hitCount FROM freebbs WHERE userId = ?"); 
			sb.append("      UNION ALL "); 
			sb.append("      SELECT 'opensource' boardName, num, subject, reg_date, hitCount FROM opensource WHERE userId = ?"); 
			sb.append("      UNION ALL "); 
			sb.append("      SELECT 'lecture' boardName, num, subject, reg_date, hitCount FROM lecture WHERE userId = ?"); 
			sb.append("      UNION ALL "); 
			sb.append("      SELECT 'study' boardName, num, subject, reg_date, hitCount FROM study WHERE userId = ?"); 
			sb.append("      UNION ALL "); 
			sb.append("      SELECT 'qAnda' boardName, num, subject, reg_date, hitCount FROM qAndA WHERE userId = ?"); 
			sb.append("     ORDER BY reg_date DESC ");
			sb.append("   ) tb WHERE ROWNUM <= ? ");
			sb.append(" ) WHERE rnum >= ? "); 
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setString(1, userId);
			pstmt.setString(2, userId);
			pstmt.setString(3, userId);
			pstmt.setString(4, userId);
			pstmt.setString(5, userId);
			pstmt.setInt(6, end);
			pstmt.setInt(7, start);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				MypageDTO dto = new MypageDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setBoardName(rs.getString("boardName"));
				dto.setSubject(rs.getString("subject"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setHitCount(rs.getInt("hitCount"));
				
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

}
