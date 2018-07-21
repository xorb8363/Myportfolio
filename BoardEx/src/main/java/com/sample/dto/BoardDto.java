package com.sample.dto;

import java.sql.Timestamp;
import org.springframework.web.multipart.MultipartFile;

public class BoardDto {

	private int bNum;
	private String bWriter;
	private String bTitle;
	private String bContents;
	private Timestamp bRegDate;
	private int bReadCount;
	private String bImage;
	private MultipartFile fileName;
	public int getbNum() {
		return bNum;
	}
	public void setbNum(int bNum) {
		this.bNum = bNum;
	}
	public String getbWriter() {
		return bWriter;
	}
	public void setbWriter(String bWriter) {
		this.bWriter = bWriter;
	}
	public String getbTitle() {
		return bTitle;
	}
	public void setbTitle(String bTitle) {
		this.bTitle = bTitle;
	}
	public String getbContents() {
		return bContents;
	}
	public void setbContents(String bContents) {
		this.bContents = bContents;
	}
	public Timestamp getbRegDate() {
		return bRegDate;
	}
	public void setbRegDate(Timestamp bRegDate) {
		this.bRegDate = bRegDate;
	}
	public int getbReadCount() {
		return bReadCount;
	}
	public void setbReadCount(int bReadCount) {
		this.bReadCount = bReadCount;
	}
	public String getbImage() {
		return bImage;
	}
	public void setbImage(String bImage) {
		this.bImage = bImage;
	}
	public MultipartFile getFileName() {
		return fileName;
	}
	public void setFileName(MultipartFile fileName) {
		this.fileName = fileName;
	}
	
	
}
