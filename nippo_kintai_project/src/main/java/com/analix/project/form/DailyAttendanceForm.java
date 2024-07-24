package com.analix.project.form;

import java.sql.Time;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;


public class DailyAttendanceForm {
	
	private Integer id;
	private Integer userId;
	private byte status;
//	private Date date;
	private LocalDate date;
	private Time startTime;
	private Time endTime;
	private String remarks;
	private Year year;
	private Month month;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Time getStartTime() {
		return startTime;
	}

	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}

	public Time getEndTime() {
		return endTime;
	}

	public void setEndTime(Time endTime) {
		this.endTime = endTime;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public Year getYear() {
		return year;
	}
	
	public void setYear(Year year) {
		this.year = year;
	}
	
	public Month getMonth() {
		return month;
	}
	
	public void setMonth(Month month) {
		this.month = month;
	}
	
	


}
