package com.analix.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DepartmentController {
	
	@RequestMapping(path = "/department/regist")
	public String departmentRegist() {
		return "/department/regist";
	}
}
