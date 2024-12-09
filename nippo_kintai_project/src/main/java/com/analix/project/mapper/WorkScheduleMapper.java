package com.analix.project.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.analix.project.entity.WorkSchedule;

@Mapper
public interface WorkScheduleMapper {
	
	// 勤務時間登録(既存のデータがあれば更新)
	public void registWorkSchedule(@Param("workSchedule") WorkSchedule workSchedule);

}
