package com.analix.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analix.project.entity.WorkSchedule;
import com.analix.project.mapper.WorkScheduleMapper;

@Service
public class WorkScheduleService {
	
	@Autowired
	private WorkScheduleMapper workScheduleMapper;
	
	// 勤務時間設定
	public void scheduleSaveOrUpdate(WorkSchedule workSchedule) {
		workScheduleMapper.registWorkSchedule(workSchedule);
	}

}
