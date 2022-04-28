package com.sphoto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class PhotoDAO {
	private Connection conn = DBConn.getConnection();

	public void insertPhoto(PhotoDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		int seq;

		try {
			sql = "SELECT sphoto_seq.NEXTVAL FROM dual";
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

			sql = "INSERT INTO sphoto ( num, userId, subject, content, reg_date ) "
					+ " VALUES (?, ?, ?, ?, SYSDATE)";
			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, dto.getNum());
			pstmt.setString(2, dto.getUserId());
			pstmt.setString(3, dto.getSubject());
			pstmt.setString(4, dto.getContent());

			pstmt.executeUpdate();

			pstmt.close();
			pstmt = null;

			if (dto.getImageFiles() != null) {
				sql = "INSERT INTO sphotoFile(fileNum, num, imageFilename) VALUES "
						+ " (sphotoFile_seq.NEXTVAL, ?, ?)";
				pstmt = conn.prepareStatement(sql);
				
				for (int i = 0; i < dto.getImageFiles().length; i++) {
					pstmt.setInt(1, dto.getNum());
					pstmt.setString(2, dto.getImageFiles()[i]);
					
					pstmt.executeUpdate();
				}
			}

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

	public int dataCount(String userId) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM sphoto WHERE userId = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, userId);
			
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

	public List<PhotoDTO> listPhoto(int start, int end, String userId) {
		List<PhotoDTO> list = new ArrayList<PhotoDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT * FROM ( ");
			sb.append("     SELECT ROWNUM rnum, tb.* FROM ( ");
			sb.append("         SELECT p.num, p.userId, userName, subject, imageFilename ");
			sb.append("         FROM sphoto p ");
			sb.append("         JOIN member1 m ON p.userId = m.userId ");
			sb.append("         LEFT OUTER JOIN ( ");
			sb.append("             SELECT fileNum, num, imageFilename FROM ( ");
			sb.append("                 SELECT fileNum, num, imageFilename, ");
			sb.append("                       ROW_NUMBER() OVER(PARTITION BY num ORDER BY fileNum ASC) rank ");
			sb.append("                 FROM sphotoFile");
			sb.append("             ) WHERE rank = 1 ");
			sb.append("         ) i ON p.num = i.num ");
			sb.append("         WHERE p.userId = ? ");
			sb.append("         ORDER BY num DESC ");
			sb.append("     ) tb WHERE ROWNUM <= ? ");
			sb.append(" ) WHERE rnum >= ? ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setString(1, userId);
			pstmt.setInt(2, end);
			pstmt.setInt(3, start);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				PhotoDTO dto = new PhotoDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setImageFilename(rs.getString("imageFilename"));
				
				list.add(dto);
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

		return list;
	}

	public PhotoDTO readPhoto(int num) {
		PhotoDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT num, p.userId, userName, subject, content, reg_date "
					+ " FROM sphoto p "
					+ " JOIN member1 m ON p.userId=m.userId  "
					+ " WHERE num = ? ";

			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, num);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new PhotoDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
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

	public void updatePhoto(PhotoDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "UPDATE sphoto SET subject=?, content=? WHERE num=?";
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setInt(3, dto.getNum());

			pstmt.executeUpdate();
			
			pstmt.close();
			pstmt = null;

			if (dto.getImageFiles() != null) {
				sql = "INSERT INTO sphotoFile(fileNum, num, imageFilename) VALUES "
						+ " (sphotoFile_seq.NEXTVAL, ?, ?)";
				pstmt = conn.prepareStatement(sql);
				
				for (int i = 0; i < dto.getImageFiles().length; i++) {
					pstmt.setInt(1, dto.getNum());
					pstmt.setString(2, dto.getImageFiles()[i]);
					
					pstmt.executeUpdate();
				}
			}

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

	public void deletePhoto(int num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "DELETE FROM sphoto WHERE num=?";
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

	public List<PhotoDTO> listPhotoFile(int num) {
		List<PhotoDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT fileNum, num, imageFilename FROM sphotoFile WHERE num = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, num);
			
			rs = pstmt.executeQuery();

			while (rs.next()) {
				PhotoDTO dto = new PhotoDTO();

				dto.setFileNum(rs.getInt("fileNum"));
				dto.setNum(rs.getInt("num"));
				dto.setImageFilename(rs.getString("imageFilename"));
				
				list.add(dto);
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

		return list;
	}

	public PhotoDTO readPhotoFile(int fileNum) {
		PhotoDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT fileNum, num, imageFilename FROM sphotoFile WHERE fileNum = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, fileNum);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new PhotoDTO();

				dto.setFileNum(rs.getInt("fileNum"));
				dto.setNum(rs.getInt("num"));
				dto.setImageFilename(rs.getString("imageFilename"));
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

	public PhotoDTO preReadPhoto(int num, String userId) {
		PhotoDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT * FROM ( ");
			sb.append("     SELECT num, subject FROM sphoto ");
			sb.append("     WHERE num > ? AND userId = ? ");
			sb.append("     ORDER BY num ASC ");
			sb.append(" ) WHERE ROWNUM = 1 ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, num);
			pstmt.setString(2, userId);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new PhotoDTO();
				
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

	public PhotoDTO nextReadPhoto(int num, String userId) {
		PhotoDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT * FROM ( ");
			sb.append("     SELECT num, subject FROM sphoto ");
			sb.append("     WHERE num < ? AND userId = ? ");
			sb.append("     ORDER BY num DESC ");
			sb.append(" ) WHERE ROWNUM = 1 ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, num);
			pstmt.setString(2, userId);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new PhotoDTO();
				
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

	public void deletePhotoFile(String mode, int num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			if (mode.equals("all")) {
				sql = "DELETE FROM sphotoFile WHERE num = ?";
			} else {
				sql = "DELETE FROM sphotoFile WHERE fileNum = ?";
			}
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
				} catch (SQLException e2) {
				}
			}
		}
	}

}
