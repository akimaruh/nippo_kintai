package com.analix.project.service;

import org.springframework.stereotype.Service;

import com.analix.project.entity.Users;
import com.analix.project.mapper.UserMapper;

@Service
public class UserService {
	
	private final UserMapper userMapper;
	
	public UserService(UserMapper userMapper) {
		this.userMapper = userMapper;
	}
	
	/**
	 * ユーザー名で検索
	 * @param name
	 * @return userエンティティ
	 */
	public Users getUserDataByUserName(String name) {
		Users userData = userMapper.findUserDataByUserName(name);
		return userData;
	}
	
	/**
	 * ユーザー更新
	 * @param users
	 * @return 反映結果
	 */
	public Boolean updateUserData(Users users) {
		return userMapper.updateUserData(users);
	}
	
	/**
	 * ユーザー登録
	 * @param users
	 * @return 反映結果
	 */
	public Boolean insertUserData(Users users) {
		return userMapper.insertUserData(users);
	}

}
