package com.analix.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

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
	 * 「無効な部署」リスト
	 * @return 無効な部署リスト
	 */
	public List<Department> showInactiveDepartment(){
		List<Department> inactiveDepartmentList = departmentMapper.findInactiveDepartment();

		return inactiveDepartmentList;
	}
	
	/**
	 * 部署の存在チェック
	 * @param name 部署名
	 * @return ture存在する false存在しない
	 */
	public boolean isDepartmentExists(String name){
		Integer departmentCount = departmentMapper.departmentCountByName(name);
		return departmentCount > 0; // 1以上なら存在する
	}
	
	/**
	 * 「登録」ボタン押下
	 * @param newName 新部署名
	 * @return true登録成功、false登録失敗
	 */
	public boolean registDepartment(String newName) {
		if (isDepartmentExists(newName)) {
			return false; // 部署名が存在する場合は登録できない
		}
		
		Department department = new Department();
		department.setName(newName);
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
	
	/**
	 * 「削除」ボタン押下(論理削除)
	 * @param exsistsName 登録済の部署名
	 * @return true削除成功、false削除失敗
	 */
	public boolean deleteDepartment(String exsistsName) {
        int deleteCount = departmentMapper.deleteDepartment(exsistsName);
        return deleteCount > 0; // 行数が1以上なら成功
	}
	
	/**
	 * 「有効化」ボタン押下
	 * @param inactiveName 無効な部署名
	 * @return true有効化成功、false有効化失敗
	 */
	public boolean updateDepartmentToActive(String inactiveName) {
		int updateCount = departmentMapper.updateDepartmentToActive(inactiveName);
		return updateCount > 0;
	}

	/**
	 * 入力チェック
	 * @param department
	 * @param checkLength 新部署名文字数
	 * @param requiredNewNameCheck 新部署名
	 * @param requiredExsistsNameCheck 登録済の部署名
	 * @param requiredInactiveNameCheck 削除済部署名
	 * @param result
	 * @return
	 */
	public boolean validationForm(Department department, boolean checkLength, boolean requiredNewNameCheck,
			boolean requiredExsistsNameCheck, boolean requiredInactiveNameCheck, BindingResult result) {
		String newName = department.getNewName();
		String exsistsName = department.getExsistsName();
		String inactiveName = department.getInactiveName();

		if (checkLength && newName.length() > 10) {
			result.addError(
					new FieldError("department", "newName", "10文字以内で入力してください。"));
		}

		if (requiredNewNameCheck && newName == "") {
			result.addError(
					new FieldError("department", "newName", "新部署名を入力してください。"));
		}

		if (requiredExsistsNameCheck && exsistsName == "") {
			result.addError(
					new FieldError("department", "exsistsName", "登録済の部署名を選択してください。"));
		}

		if (requiredInactiveNameCheck && inactiveName == "") {
			result.addError(
					new FieldError("department", "inactiveName", "削除済の部署名を選択してください。"));
		}

		return true;
	}

}
