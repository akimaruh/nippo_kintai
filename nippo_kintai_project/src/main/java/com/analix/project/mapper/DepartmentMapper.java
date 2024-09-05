package com.analix.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.analix.project.entity.Department;
@Mapper
public interface DepartmentMapper {
	
	/**
	 * 部署名リスト
	 * @return
	 */
	public List<Department> findAllDepartmentName();
	
	/**
	 * 部署の検索
	 * @param name
	 * @return
	 */
	public Integer departmentCountByName(@Param("name") String name);
	
	/**
	 * 新部署名登録
	 */
	public void registDepartment(@Param("department") Department department);
}
