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
	 * 部署名の存在チェック
	 * @param name 部署名
	 * @return 部署名が存在する数
	 */
	public Byte findDepartmentStatusByName(@Param("name") String name);
	
	/**
	 * 新部署名登録
	 * @param department
	 */
	public void registDepartment(@Param("department") Department department);
	
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
	
}
