package com.analix.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.analix.project.entity.Department;
@Mapper
public interface DepartmentMapper {
	public List<Department> findAllDepartmentName();
}
