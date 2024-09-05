package com.analix.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analix.project.entity.Department;
import com.analix.project.mapper.DepartmentMapper;

@Service
public class DepartmentService {
	
	@Autowired
	DepartmentMapper departmentMapper;
	
	/**
	 * 「登録済の部署」リスト
	 * @return 部署リスト
	 */
	public List<Department> showDepartment(){
		List<Department> departmentList = departmentMapper.findAllDepartmentName();
		return departmentList;
	}
	
	/**
	 * 部署の存在チェック
	 * @param name
	 * @return
	 */
	public boolean isDepartmentExists(String name){
		Integer departmentCount = departmentMapper.departmentCountByName(name);
		return departmentCount > 0;
	}
	
	/**
	 * 「登録」ボタン押下
	 */
	public String registDepartment(String newName) {
		
		Department department = new Department();
		department.setName(newName);

		if (isDepartmentExists(department.getName())) {
			return "この部署名は既に登録済です";
		}
		departmentMapper.registDepartment(department);
		return "登録しました";
	}


}
