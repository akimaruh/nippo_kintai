package com.analix.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.analix.project.dto.MonthlyAttendanceReqDto;

@Mapper
public interface MonthlyAttendanceReqMapper {
	
	/*
	 * 全件取得
	 */
	public List<MonthlyAttendanceReqDto> findAllMonthlyAttendanceReq();
	
	/*
	 * status更新 承認
	 */
	public void updateStatusApprove(@Param("id") Integer id);
	
	/*
	 * status更新 却下
	 */
	public void updateStatusReject(@Param("id") Integer id);
	

}
