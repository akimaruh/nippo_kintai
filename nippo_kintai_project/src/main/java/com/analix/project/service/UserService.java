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
		Users userDataBySearch = userMapper.findUserDataByUserName(name);
		if (userDataBySearch != null) {
			Users userDataAfterSearch = userDataBySearch;

			return userDataAfterSearch;
		}

		Users userDataAfterSearch = new Users();
		Integer maxUserId = userMapper.createNewId();
		userDataAfterSearch.setId(maxUserId + 1);

		return userDataAfterSearch;
	}

	/**
	 * ユーザー更新
	 * @param users
	 * @return 反映結果
	 */
	public Boolean registUserData(Users users,Integer id) {
		String startDate = users.getStartDate(); 
		Users registUser = new Users();
		registUser.setPassword(users.getPassword());
		registUser.setRole(users.getRole());
		
		registUser.setStartDate(users.getStartDate());
		System.out.println("サービスクラス"+ registUser.getStartDate());
		if (id == null) {
			System.out.println("新規登録処理");
			registUser.setName(users.getName());
			return userMapper.insertUserData(registUser);

		}
		
		else if(startDate=="9999/99/99") {
			return userMapper.deleteUserData(id);
			
		}
		System.out.println(registUser.getPassword());
		
		registUser.setId(users.getId());
		return userMapper.updateUserData(registUser);
	}

}
