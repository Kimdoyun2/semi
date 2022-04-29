package com.qANDa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class qANDaDAO {
	private Connection conn = DBConn.getConnection();

	//insert
	public void insertqANDa(qANDaDTO dto, String mode) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		int seq;

		try {
			sql = "SELECT qANDa_seq.NEXTVAL FROM dual";
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
			
			if(mode.equals("write")) {
				// 글쓰기일 때
				dto.setAnNum(seq);
				dto.setOrderNo(0);
				dto.setDepth(0);
				dto.setParent(0);
			} else if(mode.equals("reply")) {
				// 답변일때
				updateOrderNo(dto.getAnNum(), dto.getOrderNo());
				
				dto.setDepth(dto.getDepth() + 1);
				dto.setOrderNo(dto.getOrderNo() + 1);
			}
			
			sql = "INSERT INTO qANDa(num, userId, subject, content, "
				+ "  anNum, depth, orderNo, parent, hitCount, reg_date) "
				+ "  VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0, SYSDATE)";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, dto.getNum());
			pstmt.setString(2, dto.getUserId());
			pstmt.setString(3, dto.getSubject());
			pstmt.setString(4, dto.getContent());
			pstmt.setInt(5, dto.getAnNum());
			pstmt.setInt(6, dto.getDepth());
			pstmt.setInt(7, dto.getOrderNo());
			pstmt.setInt(8, dto.getParent());
			
			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
		}

	}
	
	// 삭제하기!
	public void deleteqANDa(int num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = " DELETE FROM qANDa WHERE Num IN " 
				+ " (SELECT Num FROM qANDa " 
				+ " START WITH Num = ? "
				+ " CONNECT BY PRIOR Num = parent)";
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
				} catch (SQLException e) {
				}
			}
		}

	}

	// orderNo
	public void updateOrderNo(int anNum, int orderNo) throws SQLException {
		PreparedStatement pstmt=null;
		String sql;
		
		try {
			sql = "UPDATE qANDa SET orderNo = orderNo+1 WHERE anNum = ? AND orderNo > ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, anNum);
			pstmt.setInt(2, orderNo);
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(pstmt!=null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}
		
	}
	
	// 개수
	public int dataCount() {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM qANDa";
			pstmt = conn.prepareStatement(sql);

			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				result = rs.getInt(1);
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

		return result;
	}

	// 개수 - 검색
	public int dataCount(String condition, String keyword) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM qANDa b JOIN member1 m ON b.userId = m.userId ";
			if (condition.equals("all")) {
				 sql += "  WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ";
			} else if (condition.equals("reg_date")) {
				 keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				 sql += "  WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ? ";
			} else {
				 sql += "  WHERE INSTR(" + condition + ", ?) >= 1 ";
			}

			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, keyword);
			if (condition.equals("all")) {
				pstmt.setString(2, keyword);
			}

			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				result = rs.getInt(1);
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

		return result;
	}
	
	// 게시물 보기
	public qANDaDTO readqANDa(int num) {
		qANDaDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT Num, b.userId, userName, subject, content, reg_date, hitCount, " 
				+ "   anNum, depth, orderNo, parent "
				+ " FROM qANDa b "
				+ " JOIN member1 m ON b.userId=m.userId "
				+ " WHERE Num = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, num);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new qANDaDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setAnNum(rs.getInt("anNum"));
				dto.setDepth(rs.getInt("depth"));
				dto.setOrderNo(rs.getInt("orderNo"));
				dto.setParent(rs.getInt("parent"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
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

	// 게시물 리스트
	public List<qANDaDTO> listqANDa(int start, int end) {
		List<qANDaDTO> list = new ArrayList<qANDaDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT * FROM ( ");
			sb.append("   SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append("             SELECT num, b.userId, userName, ");
			sb.append("                    subject, anNum, orderNo, depth, hitCount,");
			sb.append("                    TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date ");
			sb.append("   FROM qANDa b ");
			
			sb.append("       JOIN member1 m ON b.userId = m.userId ");
			sb.append("       ORDER BY anNum DESC, orderNo ASC ");
			sb.append("  ) tb WHERE ROWNUM <= ? ");
			sb.append(" ) WHERE rnum >= ? ");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, end);
			pstmt.setInt(2, start);

			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				qANDaDTO dto = new qANDaDTO();

				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setAnNum(rs.getInt("anNum"));
				dto.setDepth(rs.getInt("depth"));
				dto.setOrderNo(rs.getInt("orderNo"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				
				list.add(dto);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e2) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e2) {
				}
			}
		}

		return list;
	}

	// 검색 - 리스트
	public List<qANDaDTO> listqANDa(int start, int end, String condition, String keyword) {
		List<qANDaDTO> list = new ArrayList<qANDaDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT * FROM ( ");
			sb.append("   SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append("                 SELECT num, b.userId, userName, ");
			sb.append("                    subject, anNum, orderNo, depth, hitCount,");
			sb.append("                    TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date ");
			sb.append("   FROM qANDa b ");
			
			sb.append("         JOIN member1 m ON b.userId = m.userId ");
			if (condition.equals("all")) {
				sb.append("     WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ");
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sb.append("     WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ?");
			} else {
				sb.append("     WHERE INSTR(" + condition + ", ?) >= 1 ");
			}
			
			sb.append("         ORDER BY anNum DESC, orderNo ASC ");
			sb.append("         ) tb WHERE ROWNUM <= ? ");
			sb.append(" ) WHERE rnum >= ? ");

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
				qANDaDTO dto = new qANDaDTO();

				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setAnNum(rs.getInt("anNum"));
				dto.setDepth(rs.getInt("depth"));
				dto.setOrderNo(rs.getInt("orderNo"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));

				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e2) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e2) {
				}
			}
		}

		return list;
	}



	// 이전글
	public qANDaDTO preReadqANDa(int anNum, int orderNo, String condition, String keyword) {
		qANDaDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			if (keyword != null && keyword.length() != 0) {
				sb.append(" SELECT * FROM ( ");
				sb.append("    SELECT num, subject ");
				sb.append("    FROM qANDa b ");
				sb.append("    JOIN member1 m ON b.userId = m.userId ");
				sb.append("    WHERE ( (anNum = ? AND orderNo < ?) OR (anNum > ?) ) ");
				
				if (condition.equals("all")) {
					sb.append("   AND ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ) ");
				} else if (condition.equals("reg_date")) {
					keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
					sb.append("   AND ( TO_CHAR(reg_date, 'YYYYMMDD') = ? ) ");
				} else {
					sb.append("   AND ( INSTR(" + condition + ", ?) >= 1 ) ");
				}
				
				sb.append("     ORDER BY anNum ASC, orderNo DESC ");
				sb.append(" ) WHERE ROWNUM = 1 ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setInt(1, anNum);
                pstmt.setInt(2, orderNo);
                pstmt.setInt(3, anNum);
                pstmt.setString(4, keyword);
				if (condition.equals("all")) {
					pstmt.setString(5, keyword);
				}
			} else {
				sb.append(" SELECT * FROM ( ");
				sb.append("     SELECT num, subject FROM qANDa ");
				sb.append("     WHERE (anNum = ? AND orderNo < ?) OR (anNum > ?) ");
				sb.append("     ORDER BY anNum ASC, orderNo DESC ");
				sb.append(" ) WHERE ROWNUM = 1 ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setInt(1, anNum);
                pstmt.setInt(2, orderNo);
                pstmt.setInt(3, anNum);
			}

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new qANDaDTO();
				
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
	public qANDaDTO nextReadqANDa(int anNum, int orderNo, String condition, String keyword) {
		qANDaDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			if (keyword != null && keyword.length() != 0) {
				sb.append(" SELECT * FROM ( ");
				sb.append("    SELECT Num, subject ");
				sb.append("    FROM qANDa b ");
				sb.append("        JOIN member1 m ON b.userId = m.userId ");
				sb.append("            WHERE ( (anNum = ? AND orderNo > ?) OR (anNum < ?) ) ");
				if (condition.equals("all")) {
					sb.append("   AND ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ) ");
				} else if (condition.equals("reg_date")) {
					keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
					sb.append("   AND ( TO_CHAR(reg_date, 'YYYYMMDD') = ? ) ");
				} else {
					sb.append("   AND ( INSTR(" + condition + ", ?) >= 1 ) ");
				}
				sb.append("     ORDER BY anNum DESC, orderNo ASC ");
				sb.append(" ) WHERE ROWNUM = 1 ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setInt(1, anNum);
                pstmt.setInt(2, orderNo);
                pstmt.setInt(3, anNum);
				pstmt.setString(4, keyword);
				if (condition.equals("all")) {
					pstmt.setString(5, keyword);
				}
			} else {
				sb.append(" SELECT * FROM ( ");
				sb.append("     SELECT Num, subject FROM qANDa ");
				sb.append("     WHERE (anNum = ? AND orderNo > ?) OR (AnNum < ?) ");
				sb.append("     ORDER BY anNum DESC, orderNo ASC ");
				sb.append(" ) WHERE ROWNUM = 1 ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setInt(1, anNum);
                pstmt.setInt(2, orderNo);
                pstmt.setInt(3, anNum);
			}

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new qANDaDTO();
				
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
	
	
	// 조회수 증가하기
	public void updateHitCount(int num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "UPDATE qANDa SET hitCount=hitCount+1 WHERE Num=?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, num );
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e2) {
				}
			}
		}

	}

	// 게시물 수정
	public void updateqANDa(qANDaDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "UPDATE qANDa SET subject=?, content=? WHERE num=? AND userId=?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setInt(3, dto.getNum());
			pstmt.setString(4, dto.getUserId());
			
			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}

	}


}
