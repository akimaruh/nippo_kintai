package com.analix.project.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.analix.project.entity.WorkSchedule;

@Mapper
public interface WorkScheduleMapper {
	
	/**
	 * データが存在するかどうか確認
	 * @param userId
	 * @return
	 */
	public boolean exisistsByUserId(@Param("userId") Integer userId);
	
	/**
	 * 新しいデータを登録
	 * @param workSchedule
	 */
	public boolean insertWorkSchedule(@Param("workSchedule") WorkSchedule workSchedule);
	
	/**
	 * 既存データを更新
	 * @param workSchedule
	 * @return
	 */
	public boolean updateWorkSchedule(@Param("workSchedule") WorkSchedule workSchedule);
	
	/**
	 * 設定したデータ取得
	 * @param userId
	 * @return
	 */
	public WorkSchedule findWorkSchedule(@Param("userId") Integer userId);

}
