package com.analix.project.service;

import org.springframework.stereotype.Service;

import com.analix.project.entity.Users;
import com.analix.project.form.RegistUserForm;
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
	public Boolean registUserData(RegistUserForm registUserForm,Integer id,String name) {

		System.out.println("サービスクラス入り");
		//		Date startDate = users.getStartDate();
		//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		//		String stringTypeStartDate = dateFormat.format(startDate); 

		System.out.println("Date処理完了");

		Users registUser = new Users();
		
		registUser.setPassword(registUserForm.getPassword());
		registUser.setRole(registUserForm.getRole());
		registUser.setName(registUserForm.getName());
		registUser.setStartDate(registUserForm.getStartDate());
		System.out.println("サービスクラス" + registUser.getStartDate());

		Boolean userCheck= userMapper.isUserDataById(id);
		System.out.println(id);
				System.out.println(userCheck);
		if (userCheck == null) {
			System.out.println("新規登録処理");
			System.out.println(name);
			
			return userMapper.insertUserData(registUser);

		}
		if (userCheck == true) {
			System.out.println("更新登録処理");
			System.out.println(registUserForm.getName());
			

			registUser.setId(id);
		}

		return userMapper.updateUserData(registUser);
	}

	/**
	 * ユーザー削除
	 * @param id
	 * @return 反映結果
	 */
	public Boolean deleteUser(Integer id) {
		return userMapper.deleteUserData(id);

	}

}
