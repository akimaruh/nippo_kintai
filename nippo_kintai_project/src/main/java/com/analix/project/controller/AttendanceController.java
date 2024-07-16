package com.analix.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AttendanceController {
	
	@RequestMapping(path = "/attendance/regist")
	public String attendanceRegist() {
		return "/attendance/regist";
	}
}
