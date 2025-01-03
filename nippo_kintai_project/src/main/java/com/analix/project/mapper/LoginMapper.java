package com.analix.project.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.analix.project.entity.Users;

@Mapper
public interface LoginMapper {

	/**
	 * 全件取得
	 * @param id
	 * @param password
	 * @return ユーザー全件
	 */
	Users findByCodeAndPassword(@Param("employeeCode") Integer employeeCode, @Param("password") String password);

}
