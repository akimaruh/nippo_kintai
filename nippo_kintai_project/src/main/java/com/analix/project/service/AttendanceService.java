package com.analix.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analix.project.dto.MonthlyAttendanceReqDto;
import com.analix.project.mapper.MonthlyAttendanceReqMapper;

@Service
public class AttendanceService {
	
	@Autowired
	private MonthlyAttendanceReqMapper monthlyAttendanceMapper;
	
	// 全件取得
	public List<MonthlyAttendanceReqDto> getMonthlyAttendanceReq(){
		return monthlyAttendanceMapper.findAll();
		
	}
	

}
