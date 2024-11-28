package com.analix.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.analix.project.dto.DepartmentUserDto;
import com.analix.project.entity.Department;
import com.analix.project.entity.UserDepartmentOrder;
import com.analix.project.form.DepartmentForm;

@Mapper
public interface DepartmentMapper {

	/**
	 * 部署名リスト
	 * @return
	 */
	public List<Department> findAllDepartmentName();
	
	/**
	 * ユーザごとの順序付き有効部署リストを取得
	 */
	public List<Department> findDepartmentWithOrder(@Param("userId") Integer userId);
	
	/**
	 * 部署ID全件取得
	 * @return 有効化フラグが１の部署ID全件リスト
	 */
	public List<Integer> findAllDepartmentId();

	/**
	 * 部署名の存在チェック
	 * @param name 部署名
	 * @return フラグ
	 */
	public Byte findDepartmentStatusByName(@Param("name") String name);

	/**
	 * 新部署名登録
	 * @param department
	 */
	public void registDepartment(@Param("departmentForm") DepartmentForm departmentForm);

	/**
	 * 部署名変更
	 * @param newName 新部署名
	 * @param exsistsName 登録済の部署名
	 */
	public void updateDepartmentName(@Param("newName") String newName, @Param("exsistsName") String exsistsName);

	/**
	 * 部署名削除(論理削除)
	 * @param exsistsName 登録済の部署名
	 * @return 
	 */
	public int deleteDepartment(@Param("exsistsName") String exsistsName);

	/**
	 * 無効な部署名リスト
	 * @return
	 */
	public List<Department> findInactiveDepartment();

	/**
	 * 部署名有効化(無効から有効)
	 * @param inactiveName 無効な部署名
	 */
	public Integer updateDepartmentToActive(@Param("inactiveName") String inactiveName);

	/**
	 * 部署名に紐づくユーザー数のカウント
	 * @param name 部署名
	 * @return カウント数
	 */
	public Integer getUsersCountByDepartmentName(@Param("name") String name);
	
	/**
	 * 部署IDに紐づくユーザー情報取得
	 * @param departmentId
	 * @return
	 */
	public List<DepartmentUserDto> findUsersByDepartmentId(@Param("departmentId") Integer departmentId);
	
	// 部署一覧テーブルの順序を保存
	public void saveDepartmentOrder(List<UserDepartmentOrder> orderData);

}
