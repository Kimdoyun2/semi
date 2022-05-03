package com.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class MainDAO {
	private Connection conn = DBConn.getConnection();
	
	// 조회수 랭킹
	public List<MainDTO> rankHitCount() {
		List<MainDTO> list = new ArrayList<MainDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append("SELECT * FROM ( ");
			sb.append("   SELECT ROWNUM rnum, tb.* FROM ( "); 
			sb.append("      SELECT 'notice' boardName, num, subject, reg_date, hitCount FROM freebbs"); 
			sb.append("      UNION ALL "); 
			sb.append("      SELECT 'opensource' boardName, num, subject, reg_date, hitCount FROM opensource"); 
			sb.append("      UNION ALL "); 
			sb.append("      SELECT 'lecture' boardName, num, subject, reg_date, hitCount FROM lecture"); 
			sb.append("      UNION ALL "); 
			sb.append("      SELECT 'study' boardName, num, subject, reg_date, hitCount FROM study"); 
			sb.append("      UNION ALL "); 
			sb.append("      SELECT 'qAndA' boardName, num, subject, reg_date, hitCount FROM qAndA"); 
			sb.append("     ORDER BY hitCount DESC ");
			sb.append("   ) tb WHERE ROWNUM <= 5 ");
			sb.append(" ) WHERE rnum >= 1 ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				MainDTO dto = new MainDTO();
				
				dto.setListNum(rs.getInt("rnum"));
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
	
	// 최근 게시물
	public List<MainDTO> rankReg_date() {
		List<MainDTO> list = new ArrayList<MainDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append("SELECT * FROM ( ");
			sb.append("   SELECT ROWNUM rnum, tb.* FROM ( "); 
			sb.append("      SELECT 'notice' boardName, num, subject, reg_date, hitCount FROM freebbs"); 
			sb.append("      UNION ALL "); 
			sb.append("      SELECT 'opensource' boardName, num, subject, reg_date, hitCount FROM opensource"); 
			sb.append("      UNION ALL "); 
			sb.append("      SELECT 'lecture' boardName, num, subject, reg_date, hitCount FROM lecture"); 
			sb.append("      UNION ALL "); 
			sb.append("      SELECT 'study' boardName, num, subject, reg_date, hitCount FROM study"); 
			sb.append("      UNION ALL "); 
			sb.append("      SELECT 'qAndA' boardName, num, subject, reg_date, hitCount FROM qAndA"); 
			sb.append("     ORDER BY reg_date DESC ");
			sb.append("   ) tb WHERE ROWNUM <= 5 ");
			sb.append(" ) WHERE rnum >= 1 ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				MainDTO dto = new MainDTO();
				
				dto.setListNum(rs.getInt("rnum"));
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
}
