package com.analix.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.analix.project.dto.UserSearchDto;
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
	Users findUserDataByEmployeeCode(@Param("employeeCode") Integer employeeCode);

	/**
	 * 表示用ユーザー情報取得
	 * @param id
	 * @return 社員番号,ユーザー名,部署名格納のエンティティ
	 */
	Users findUserDataForDisplay(@Param("id") Integer id);

	/**
	 * 新規ユーザーID払い出し用
	 * @return DB内ユーザーIDの最大値
	 */
	Integer createNewEmployeeCode();

	/***
	 * ユーザーデータの存在チェック
	 * @param employeeCode
	 * @return employeeCodeが true：既に存在する false:存在しない 
	 */
	boolean userExsistByEmployeeCode(@Param("employeeCode") Integer employeeCode);
	
	
	/**
	 * ユーザーデータ更新
	 * @param registUser
	 * @return 反映結果
	 */
	Boolean updateUserData(@Param("users") Users registUser);

	/**
	 * ユーザーデータ新規登録
	 * @param registUser
	 * @return 反映結果
	 */
	Boolean insertUserData(@Param("users") Users registUser);

	/**
	 * 権限ごとのユーザー抽出
	 * @param role
	 * @return 該当のユーザーエンティティリスト
	 */
	List<Users> findUserListByRole(@Param("role") String role);

	/***
	 * ユーザーのメールアドレス抽出
	 * @param userId
	 * @return email
	 */
	String findEmailByUserId(@Param("userId") Integer userId);

	/**
	 * ユーザー名またはユーザーIDであいまい検索
	 * @param userKeyword
	 * @return 検索されたユーザーエンティティリスト
	 */
	List<Users> searchForUserNameAndEmployeeCode(@Param("userKeyword") String userKeyword);

	/**
	 * ユーザー一括登録
	 * @param insertList
	 * @return 反映結果
	 */
	public boolean batchInsertUsers(@Param("insertList") List<Users> insertList);

	/**
	 * ユーザー一括更新
	 * @param insertList
	 * @return 反映結果
	 */
	public boolean batchUpdateUsers(@Param("updateList") List<Users> updateList);

	/**
	 * インポート用既存ユーザーID取得 
	 * @param employeeCode
	 * @return 社員番号、ID
	 */
	List<Users> findIdByEmployeeCodeAndName(@Param("usersList") List<Users> usersList);
	/**
	 * インポート用既存ユーザーID取得 
	 * @param employeeCode
	 * @return ID、名前、社員番号
	 */
	List<Users> findIdByEmployeeCode(@Param("usersList") List<Users> usersList);

	/**
	 * インポート用既存ユーザーID取得 
	 * @param employeeCode
	 * @return ID、名前、社員番号
	 */
	List<Users> findIdByName(@Param("usersList") List<Users> usersList);
	
	/**
	 * パスワードを忘れた方用ユーザーID検索
	 * @param employeeCode
	 * @param email
	 * @return id
	 */
	Integer findIdByEmployeeCodeAndEmail(@Param("employeeCode") Integer employeeCode,@Param("email") String email);
	
	/**
	 * パスワード更新
	 * @param id
	 * @param password
	 * @return 反映結果
	 */
	boolean updatePassword(@Param("id") Integer id , @Param("password") String password);
	
	/**
	 * ユーザー一覧画面表示用（全件）
	 * @return
	 */
	public List<Users> findUserList();
	

	/**
	 * ユーザー一覧画面表示用（キーワード検索 + 詳細検索）
	 * @param userSearchDto
	 * @return
	 */
	public List<Users> searchUsersByKeyword(UserSearchDto userSearchDto);

}