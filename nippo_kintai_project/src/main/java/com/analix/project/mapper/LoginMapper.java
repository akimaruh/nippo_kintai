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
	Users findByIdAndPassword(@Param("id") Integer id, @Param("password") String password);

}
