package com.analix.project.entity;

import java.sql.Date;

public class MonthlyAttendanceReq {

	private int id;
	private int userId;
	private Date targetYearMonth;
	private Date date;
	private byte status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Date getTargetYearMonth() {
		return targetYearMonth;
	}

	public void setTargetYearMonth(Date targetYearMonth) {
		this.targetYearMonth = targetYearMonth;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

}
