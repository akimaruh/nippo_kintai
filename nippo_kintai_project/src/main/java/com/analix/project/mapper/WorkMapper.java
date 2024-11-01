package com.analix.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.analix.project.entity.Work;

@Mapper
public interface WorkMapper {
	/**
	 * 作業名リスト
	 * @return
	 */
	public List<Work> findAllWorkName();
	

}
