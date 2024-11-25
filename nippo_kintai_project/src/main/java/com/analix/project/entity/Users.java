package com.analix.project.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data
@Validated
public class Users implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String password;
	private String name;
	private String role;
	private Integer departmentId;
	private String departmentName;
	private String date;
	private LocalDate startDate;
	private Integer status; // ステータスフィールドを追加
	private String email;
	private Integer employeeCode;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Users user = (Users) o;
		return Objects.equals(id, user.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Integer getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getEmployeeCode() {
		return employeeCode;
	}

	public void setEmployeeCode(Integer employeeCode) {
		this.employeeCode = employeeCode;

	}

	//Streamで社員コード、名前の一致を確認できるメソッド作成
	public boolean isMatch(Users other) {
		return Objects.equals(this.employeeCode, other.getEmployeeCode()) &&
				Objects.equals(this.name, other.getName());
	}
}
