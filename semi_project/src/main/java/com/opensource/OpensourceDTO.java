package com.opensource;

public class OpensourceDTO {
	private int listNum;
	private int num;
	private String userId;
	private String subject;
	private String content;
	private String reg_date;
	private String userName;
	private int hitCount;
	private int likeCount;
	
	private int fileNum;
	private String saveFilename;
	private String originalFilename;
	
	private String[] saveFiles;
	private String[] originalFiles;
	
	public int getFileNum() {
		return fileNum;
	}
	public void setFileNum(int fileNum) {
		this.fileNum = fileNum;
	}
	public String getSaveFilename() {
		return saveFilename;
	}
	public void setSaveFilename(String saveFilename) {
		this.saveFilename = saveFilename;
	}
	public String getOriginalFilename() {
		return originalFilename;
	}
	public void setOriginalFilename(String originalFilename) {
		this.originalFilename = originalFilename;
	}
	public String[] getSaveFiles() {
		return saveFiles;
	}
	public void setSaveFiles(String[] saveFiles) {
		this.saveFiles = saveFiles;
	}
	public String[] getOriginalFiles() {
		return originalFiles;
	}
	public void setOriginalFiles(String[] originalFiles) {
		this.originalFiles = originalFiles;
	}
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
	public int getHitCount() {
		return hitCount;
	}
	public void setHitCount(int hitCount) {
		this.hitCount = hitCount;
	}
	public int getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
