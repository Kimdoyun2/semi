package com.qANDa;

public class qANDaDTO {
/*
CREATE TABLE qAnda (
   num NUMBER NOT NULL
   ,userId VARCHAR2(50) NOT NULL
   ,subject  VARCHAR2(300) NOT NULL
   ,content  VARCHAR2(4000) NOT NULL
   ,hitCount NUMBER DEFAULT 0
   ,reg_date  DATE DEFAULT SYSDATE


   ,anNum NUMBER NOT NULL
   ,depth    NUMBER(9) NOT NULL
   ,orderNo  NUMBER(9) NOT NULL
   ,parent   NUMBER NOT NULL

   ,CONSTRAINT pk_question_Num PRIMARY KEY(Num)
   , CONSTRAINT fk_question_userId FOREIGN KEY(userId)
     REFERENCES member1(userId) ON DELETE CASCADE
);
 */
	private int listNum;
	private int num;
	private String userId;
	private String subject;
	private String content;
	private String reg_date;
	private String userName;
	private int hitCount;	
	private int anNum; 
	private int depth;
	private int orderNo;
	private int parent;
	
	
	public int getListNum() {
		return listNum;
	}
	public void setListNum(int listNum) {
		this.listNum = listNum;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getReg_date() {
		return reg_date;
	}
	public void setReg_date(String reg_date) {
		this.reg_date = reg_date;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getHitCount() {
		return hitCount;
	}
	public void setHitCount(int hitCount) {
		this.hitCount = hitCount;
	}
	public int getAnNum() {
		return anNum;
	}
	public void setAnNum(int anNum) {
		this.anNum = anNum;
	}
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	public int getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}
	public int getParent() {
		return parent;
	}
	public void setParent(int parent) {
		this.parent = parent;
	}
	
	
	
}
