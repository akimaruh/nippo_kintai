package com.analix.project.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.analix.project.dto.MonthlyAttendanceReqDto;
import com.analix.project.service.AttendanceService;

@Controller
public class AttendanceController {
	
	@Autowired
	AttendanceService attendanceService;
	
	
	@RequestMapping(path = "/attendance/regist")
	public String attendanceRegist(Model model) {
		
		List<MonthlyAttendanceReqDto> monthlyAttendanceReqList = attendanceService.getMonthlyAttendanceReq();
		model.addAttribute("monthlyAttendanceReqList", monthlyAttendanceReqList);
		
		 LocalDate today = LocalDate.now();
	        int month = today.getMonthValue();
	        int year = today.getYear();
	        
	        model.addAttribute("currentMonth", month);
	        model.addAttribute("currentYear", year);

		
		return "/attendance/regist";
	}
	

}
