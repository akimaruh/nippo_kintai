package com.analix.project.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AttendanceController {
	
	@RequestMapping(path = "/attendance/regist")
	public String attendanceRegist(Model model) {
		 LocalDate today = LocalDate.now();
	        int month = today.getMonthValue();
	        int year = today.getYear();
	        
	        model.addAttribute("currentMonth", month);
	        model.addAttribute("currentYear", year);

		
		return "/attendance/regist";
	}
	
	
	
}
