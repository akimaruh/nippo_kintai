package com.analix.project.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analix.project.dto.DepartmentUserDto;
import com.analix.project.entity.Department;
import com.analix.project.entity.UserDepartmentOrder;
import com.analix.project.form.DepartmentForm;
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
	 * 「有効な部署」リスト(ユーザごとの順序付き)
	 * @param userId
	 * @return
	 */
	public List<Department> showDepartmentWithOrder(Integer userId){
		List<Department> departmentOrderList = departmentMapper.findDepartmentWithOrder(userId);
		return departmentOrderList;
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
	public Byte getDepartmentStatus(String name){
		return departmentMapper.findDepartmentStatusByName(name);
	}
	
	/**
	 * 「登録」ボタン押下
	 * @param newName 新部署名
	 * @return true登録成功、false登録失敗
	 */
	public boolean registDepartment(String name) {
		Byte status = getDepartmentStatus(name);

		if (status == null) {
			DepartmentForm departmentForm = new DepartmentForm();
			departmentForm.setName(name);
			departmentMapper.registDepartment(departmentForm);
			return true; // 登録成功
		} else if (status == 0) {
			return false; // 登録失敗(無効化)
		} else {
			return false; // 登録失敗(既に存在する)
		}
	}

	/**
	 * 「変更」ボタン押下
	 * @param newName 新部署名
	 * @param exsistsName 登録済の部署名
	 * @return true変更成功、false変更失敗
	 */
	public boolean updateDepartment(String newName, String exsistsName) {
		Byte status = getDepartmentStatus(newName);

		if (status == null) {
			departmentMapper.updateDepartmentName(newName, exsistsName);
			return true; // 変更成功
		} else if (status == 0) {
			return false; // 変更失敗(無効化)
		} else
			return false; // 変更失敗(既に存在する)
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
//	public boolean validationForm(Department department, boolean checkLength, boolean requiredNameCheck, 
//			boolean requiredNewNameCheck, boolean requiredExsistsNameCheck, boolean requiredInactiveNameCheck, BindingResult result) {
//		String name = department.getName();
//		String newName = department.getNewName();
//		String exsistsName = department.getExsistsName();
//		String inactiveName = department.getInactiveName();
//
//		// 文字数チェック
//		if (checkLength && name != null && name.length() > 100) {
//			result.addError(
//					new FieldError("department", "name", "100文字以内で入力してください。"));
//		}
//
//		// 新部署名の必須チェック
//		if (requiredNameCheck && (name == null || name.isEmpty())) {
//			result.addError(
//					new FieldError("department", "name", "新部署名を入力してください。"));
//		}
//		
//		// 文字数チェック
//		if (checkLength && newName != null && newName.length() > 100) {
//			result.addError(
//					new FieldError("department", "newName", "100文字以内で入力してください。"));
//		}
//
//		// 新部署名の必須チェック
//		if (requiredNewNameCheck && (newName == null || newName.isEmpty())) {
//			result.addError(
//					new FieldError("department", "newName", "新部署名を入力してください。"));
//		}
//		
//		// 登録済の部署名の必須チェック
//		if (requiredExsistsNameCheck && (exsistsName == null || exsistsName.isEmpty())) {
//			result.addError(
//					new FieldError("department", "exsistsName", "登録済の部署名を選択してください。"));
//		}
//		
//		// 削除済の部署名の必須チェック
//		if (requiredInactiveNameCheck && (inactiveName == null || inactiveName.isEmpty())) {
//			result.addError(
//					new FieldError("department", "inactiveName", "削除済の部署名を選択してください。"));
//		}
//
//		return true;
//	}

	/**
	 * 部署IDに紐づくユーザー数のカウント
	 * @param departmentId 部署ID
	 * @return カウント数
	 */
//	public Integer userDepartment(Integer departmentId) {
//		return departmentMapper.getUsersCountByDepartmentId(departmentId);
//	}
	
	public Integer getUserCountByDepartmentId(Integer departmentId) {
		List<DepartmentUserDto> usersList = departmentMapper.findUsersByDepartmentId(departmentId);
		return usersList.size(); // size()でユーザー数を取得
	}
	
	/**
	 * 部署に所属しているユーザー取得
	 * @param departmentId 部署ID
	 * @return
	 */
	public List<DepartmentUserDto> getUsersByDepartmentId(Integer departmentId) {
		return departmentMapper.findUsersByDepartmentId(departmentId);
	}
	
	/**
	 * 部署IDを全件取得
	 * @return 部署ID全件
	 */
	public List<String> getAllDepartmentId() {
		List<Integer> allDepartmentIdList = departmentMapper.findAllDepartmentId();
		/*		Integer型のListをStreamを生成→中間操作(List内の要素をString.valueOf()でString型に加工)
		  →終端操作(IntStream型からCollection型に変換)*/
 
		List<String> allDepartmentIdStringList = allDepartmentIdList.stream().map(String::valueOf)
				.collect(Collectors.toList());
		return allDepartmentIdStringList;
	}
	
	// 部署一覧テーブルの順序を保存
	public void saveDepartmentOrder(List<UserDepartmentOrder> orderData) {
		departmentMapper.saveDepartmentOrder(orderData);
	}
	

}
