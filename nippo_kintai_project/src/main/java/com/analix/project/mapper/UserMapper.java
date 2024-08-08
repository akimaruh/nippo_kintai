package com.analix.project.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.analix.project.entity.Users;

/**
 * ユーザーマッパー
 */
@Mapper
public interface UserMapper {
	
	/**
	 * ユーザー検索
	 * @param name
	 * @return Usersエンティティ
	 */
	Users findUserDataByUserName (@Param("name") String name);
	
	/**
	 * 新規ユーザーID払い出し用
	 * @return DB内ユーザーIDの最大値
	 */
	Integer createNewId ();
	
	/***
	 * ユーザーデータのカウント
	 * @param name
	 * @return nameに該当するユーザーデータ数
	 */
	Integer countUserDataById (@Param("name") String name);
	
	/**
	 * ユーザーデータ更新
	 * @param registUser
	 * @return 反映結果
	 */
	Boolean updateUserData (@Param("users") Users registUser);
	
	/**
	 * ユーザーデータ新規登録
	 * @param registUser
	 * @return 反映結果
	 */
	Boolean insertUserData (@Param("users") Users registUser);
	
	
	
}