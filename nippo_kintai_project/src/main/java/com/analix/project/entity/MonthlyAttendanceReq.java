package com.analix.project.entity;

import java.sql.Date;

public class MonthlyAttendanceReq {

	private Integer id;
	private Integer userId;
	private Date targetYearMonth;
	private Date date;
	private byte status;

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
