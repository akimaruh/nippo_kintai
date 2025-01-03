package com.analix.project.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.analix.project.entity.TemporaryPassword;

@Mapper
public interface TemporaryPasswordMapper {

	/**
	 * 仮パスワード新規作成
	 * @param userId
	 * @param temporaryPassword
	 * @return 反映結果
	 */
	boolean insertTemporaryPassword(@Param("temporaryPassword") TemporaryPassword temporaryPassword);

	/**
	 * 仮パスワード更新
	 * @param id
	 * @param temporaryPassword
	 * @return 反映結果
	 */
	boolean updateTemporaryPassword(@Param("temporaryPassword") TemporaryPassword temporaryPassword);
	
	/**
	 * 仮パスワードテーブルの存在チェック
	 * @param userId
	 * @return true:存在する false:存在しない
	 */
	boolean exsistTemporaryPasswordTable (@Param("userId") Integer userId);
}
