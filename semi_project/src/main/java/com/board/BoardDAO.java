package com.board;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class BoardDAO {
	private Connection conn = DBConn.getConnection();

	// 데이터 추가
	public void insertBoard(BoardDTO dto, String mode) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		int seq;

		try {
			sql = "SELECT board_seq.NEXTVAL FROM dual";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			seq = 0;
			if(rs.next()) {
				seq = rs.getInt(1);
			}
			dto.setBoardNum(seq);
			
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
			
			if(mode.equals("write")) {
				// 글쓰기일 때
				dto.setGroupNum(seq);
				dto.setOrderNo(0);
				dto.setDepth(0);
				dto.setParent(0);
			} else if(mode.equals("reply")) {
				// 답변일때
				updateOrderNo(dto.getGroupNum(), dto.getOrderNo());
				
				dto.setDepth(dto.getDepth() + 1);
				dto.setOrderNo(dto.getOrderNo() + 1);
			}
			
			sql = "INSERT INTO board(boardNum, userId, subject, content, "
					+ "  groupNum, depth, orderNo, parent, hitCount, reg_date) "
					+ "  VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0, SYSDATE)";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, dto.getBoardNum());
			pstmt.setString(2, dto.getUserId());
			pstmt.setString(3, dto.getSubject());
			pstmt.setString(4, dto.getContent());
			pstmt.setInt(5, dto.getGroupNum());
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

	// 답변일 경우 orderNo 변경
	public void updateOrderNo(int groupNum, int orderNo) throws SQLException {
		PreparedStatement pstmt=null;
		String sql;
		
		try {
			sql = "UPDATE board SET orderNo = orderNo+1 WHERE groupNum = ? AND orderNo > ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, groupNum);
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
	
	// 데이터 개수
	public int dataCount() {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM board";
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

	// 검색에서의 데이터 개수
	public int dataCount(String condition, String keyword) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM board b JOIN member1 m ON b.userId = m.userId ";
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

	// 게시물 리스트
	public List<BoardDTO> listBoard(int start, int end) {
		List<BoardDTO> list = new ArrayList<BoardDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT * FROM ( ");
			sb.append("     SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append("         SELECT boardNum, b.userId, userName, ");
			sb.append("               subject, groupNum, orderNo, depth, hitCount,");
			sb.append("               TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date ");
			sb.append("         FROM board b ");
			sb.append("         JOIN member1 m ON b.userId = m.userId ");
			sb.append("         ORDER BY groupNum DESC, orderNo ASC ");
			sb.append("     ) tb WHERE ROWNUM <= ? ");
			sb.append(" ) WHERE rnum >= ? ");

			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, end);
			pstmt.setInt(2, start);

			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				BoardDTO dto = new BoardDTO();

				dto.setBoardNum(rs.getInt("boardNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setGroupNum(rs.getInt("groupNum"));
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

	public List<BoardDTO> listBoard(int start, int end, String condition, String keyword) {
		List<BoardDTO> list = new ArrayList<BoardDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT * FROM ( ");
			sb.append("     SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append("         SELECT boardNum, b.userId, userName, ");
			sb.append("               subject, groupNum, orderNo, depth, hitCount,");
			sb.append("               TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date ");
			sb.append("         FROM board b ");
			sb.append("         JOIN member1 m ON b.userId = m.userId ");
			if (condition.equals("all")) {
				sb.append("     WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ");
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sb.append("     WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ?");
			} else {
				sb.append("     WHERE INSTR(" + condition + ", ?) >= 1 ");
			}
			sb.append("         ORDER BY groupNum DESC, orderNo ASC ");
			sb.append("     ) tb WHERE ROWNUM <= ? ");
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
				BoardDTO dto = new BoardDTO();

				dto.setBoardNum(rs.getInt("boardNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setGroupNum(rs.getInt("groupNum"));
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

	// 조회수 증가하기
	public void updateHitCount(int boardNum) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "UPDATE board SET hitCount=hitCount+1 WHERE boardNum=?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, boardNum);
			
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

	// 해당 게시물 보기
	public BoardDTO readBoard(int boardNum) {
		BoardDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT boardNum, b.userId, userName, subject, content, reg_date, hitCount, " 
					+ "   groupNum, depth, orderNo, parent "
					+ " FROM board b "
					+ " JOIN member1 m ON b.userId=m.userId "
					+ " WHERE boardNum = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, boardNum);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new BoardDTO();
				
				dto.setBoardNum(rs.getInt("boardNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setGroupNum(rs.getInt("groupNum"));
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

	// 이전글
	public BoardDTO preReadBoard(int groupNum, int orderNo, String condition, String keyword) {
		BoardDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			if (keyword != null && keyword.length() != 0) {
				sb.append(" SELECT * FROM ( ");
				sb.append("    SELECT boardNum, subject ");
				sb.append("    FROM board b ");
				sb.append("    JOIN member1 m ON b.userId = m.userId ");
				sb.append("    WHERE ( (groupNum = ? AND orderNo < ?) OR (groupNum > ?) ) ");
				if (condition.equals("all")) {
					sb.append("   AND ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ) ");
				} else if (condition.equals("reg_date")) {
					keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
					sb.append("   AND ( TO_CHAR(reg_date, 'YYYYMMDD') = ? ) ");
				} else {
					sb.append("   AND ( INSTR(" + condition + ", ?) >= 1 ) ");
				}
				sb.append("     ORDER BY groupNum ASC, orderNo DESC ");
				sb.append(" ) WHERE ROWNUM = 1 ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setInt(1, groupNum);
                pstmt.setInt(2, orderNo);
                pstmt.setInt(3, groupNum);
                pstmt.setString(4, keyword);
				if (condition.equals("all")) {
					pstmt.setString(5, keyword);
				}
			} else {
				sb.append(" SELECT * FROM ( ");
				sb.append("     SELECT boardNum, subject FROM board ");
				sb.append("     WHERE (groupNum = ? AND orderNo < ?) OR (groupNum > ?) ");
				sb.append("     ORDER BY groupNum ASC, orderNo DESC ");
				sb.append(" ) WHERE ROWNUM = 1 ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setInt(1, groupNum);
                pstmt.setInt(2, orderNo);
                pstmt.setInt(3, groupNum);
			}

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new BoardDTO();
				
				dto.setBoardNum(rs.getInt("boardNum"));
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
	public BoardDTO nextReadBoard(int groupNum, int orderNo, String condition, String keyword) {
		BoardDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			if (keyword != null && keyword.length() != 0) {
				sb.append(" SELECT * FROM ( ");
				sb.append("    SELECT boardNum, subject ");
				sb.append("    FROM board b ");
				sb.append("    JOIN member1 m ON b.userId = m.userId ");
				sb.append("    WHERE ( (groupNum = ? AND orderNo > ?) OR (groupNum < ?) ) ");
				if (condition.equals("all")) {
					sb.append("   AND ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ) ");
				} else if (condition.equals("reg_date")) {
					keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
					sb.append("   AND ( TO_CHAR(reg_date, 'YYYYMMDD') = ? ) ");
				} else {
					sb.append("   AND ( INSTR(" + condition + ", ?) >= 1 ) ");
				}
				sb.append("     ORDER BY groupNum DESC, orderNo ASC ");
				sb.append(" ) WHERE ROWNUM = 1 ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setInt(1, groupNum);
                pstmt.setInt(2, orderNo);
                pstmt.setInt(3, groupNum);
				pstmt.setString(4, keyword);
				if (condition.equals("all")) {
					pstmt.setString(5, keyword);
				}
			} else {
				sb.append(" SELECT * FROM ( ");
				sb.append("     SELECT boardNum, subject FROM board ");
				sb.append("     WHERE (groupNum = ? AND orderNo > ?) OR (groupNum < ?) ");
				sb.append("     ORDER BY groupNum DESC, orderNo ASC ");
				sb.append(" ) WHERE ROWNUM = 1 ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setInt(1, groupNum);
                pstmt.setInt(2, orderNo);
                pstmt.setInt(3, groupNum);
			}

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new BoardDTO();
				
				dto.setBoardNum(rs.getInt("boardNum"));
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

	// 게시물 수정
	public void updateBoard(BoardDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "UPDATE board SET subject=?, content=? WHERE boardNum=? AND userId=?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setInt(3, dto.getBoardNum());
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

	// 게시물 삭제
	public void deleteBoard(int boardNum) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "DELETE FROM board WHERE boardNum IN " 
					+ " (SELECT boardNum FROM board " 
					+ " START WITH boardNum = ? "
					+ " CONNECT BY PRIOR boardNum = parent)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, boardNum);
			
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
