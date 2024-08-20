package com.analix.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DailyReportController {
	
	@RequestMapping(path = "/dailyReport/regist")
	public String dailyReportRegist() {
		return "/dailyReport/regist";
	}
}
