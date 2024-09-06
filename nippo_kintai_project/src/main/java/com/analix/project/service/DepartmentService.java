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
	 * @param name 部署名
	 * @return　
	 */
	public boolean isDepartmentExists(String name){
		Integer departmentCount = departmentMapper.departmentCountByName(name);
		return departmentCount > 0;
	}
	
	/**
	 * 「登録」ボタン押下
	 * @param newName 新部署名
	 * @return true登録成功、false登録失敗
	 */
	public boolean registDepartment(String newName) {
		
		Department department = new Department();
		department.setName(newName);

		if (isDepartmentExists(department.getName())) {
			return false; // 部署名が存在する場合は登録できない
		}
		departmentMapper.registDepartment(department);
		return true; // 登録成功
		
	}

	/**
	 * 「変更」ボタン押下
	 * @param newName 新部署名
	 * @param exsistsName 登録済の部署名
	 * @return true変更成功、false変更失敗
	 */
	public boolean updateDepartment(String newName, String exsistsName) {
		
		if (isDepartmentExists(newName)) {
			return false; // 新部署名が既に存在する場合は変更できない
		}
		
		departmentMapper.updateDepartmentName(newName, exsistsName);
		return true; // 変更成功
	}
	
	//「削除」ボタン押下(論理削除)
	public boolean deleteDepartment(String exsistsName) {
        int deleteCount = departmentMapper.deleteDepartment(exsistsName);
        return deleteCount > 0; // 行数が1以上なら成功
		
		
	}
	
}
