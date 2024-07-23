package com.analix.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.analix.project.dto.MonthlyAttendanceReqDto;

@Mapper
public interface MonthlyAttendanceReqMapper {
	
	// 全件取得
	List<MonthlyAttendanceReqDto> findAll();

}
