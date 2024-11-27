package com.analix.project.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.analix.project.dto.UserCsvInputDto;
import com.analix.project.entity.Department;
import com.analix.project.entity.Users;
import com.analix.project.form.RegistUserForm;
import com.analix.project.mapper.DepartmentMapper;
import com.analix.project.mapper.UserMapper;
import com.analix.project.util.Constants;
import com.analix.project.util.CustomDateUtil;
import com.analix.project.util.PasswordUtil;

@Service
public class UserService {

	private final UserMapper userMapper;
	private final DepartmentMapper departmentMapper;
	private final CustomDateUtil customDateUtil;
	private final PasswordUtil passwordUtil;

	public UserService(UserMapper userMapper, DepartmentMapper departmentMapper, CustomDateUtil customDateUtil,PasswordUtil passwordUtil) {
		this.userMapper = userMapper;
		this.departmentMapper = departmentMapper;
		this.customDateUtil = customDateUtil;
		this.passwordUtil = passwordUtil;
	}

	/**
	 * ユーザー名で検索
	 * @param name
	 * @return registUserForm
	 */
	public RegistUserForm getUserDataByEmployeeCode(Integer inputEmployeeCode) {

		//DBでユーザー検索
		Users userDataBySearch = userMapper.findUserDataByEmployeeCode(inputEmployeeCode);
		RegistUserForm registUserForm = new RegistUserForm();
		
		if (userDataBySearch != null) {
			
			registUserForm.setId(userDataBySearch.getId());
			registUserForm.setName(userDataBySearch.getName());
//			registUserForm.setPassword(userDataBySearch.getPassword());
			registUserForm.setRole(userDataBySearch.getRole());
			registUserForm.setDepartmentId(userDataBySearch.getDepartmentId());
			registUserForm.setEmail(userDataBySearch.getEmail());
			registUserForm.setDepartmentName(userDataBySearch.getDepartmentName());
			registUserForm.setEmployeeCode(userDataBySearch.getEmployeeCode());
			registUserForm.setStartDate(getStringStartDate(userDataBySearch.getStartDate()));
		} else {
			//ユーザーが存在しない場合新しいユーザーIDを払い出し
			Integer NextEmployeeCode = userMapper.createNewEmployeeCode();
			registUserForm.setEmployeeCode(NextEmployeeCode + 1);
			registUserForm.setInsertFlg(Constants.INSERT_FLG);
		}
		return registUserForm;
	}

	/**
	 * 利用開始日をLocalDate型から画面表示用に変換
	 * @param startDate
	 * @return String型startDate
	 */
	private String getStringStartDate(LocalDate startDate) {
		//LocalDate型(yyyy-MM-dd)からString型(yyyy/MM/dd)へ変換
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		String stringStartDate = startDate.format(dateTimeFormatter);

		//DB保存の"9999-12-31"を外部表示用"9999/99/99"に戻す
		if (stringStartDate.equals("9999/12/31")) {
			stringStartDate = "9999/99/99";
		}
		return stringStartDate;
	}

	/**
	 * ユーザー更新
	 * @param users
	 * @return 反映結果
	 */
	public String registUserData(RegistUserForm registUserForm) {
		System.out.println(registUserForm.getName());
		Users registUser = new Users();
		Integer id = registUserForm.getId();
		String startDate = registUserForm.getStartDate();
		String userName = registUserForm.getName();
		Integer employeeCode = registUserForm.getEmployeeCode();
		String employeeCodeString = String.valueOf(registUserForm.getEmployeeCode());
		//"利用開始日に9999/99/99が入力されている場合
		if (startDate.equals("9999/99/99")) {
			//DBで保存できる最大日付へ変更
			startDate = "9999/12/31";
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		LocalDate startDateLoalDate = LocalDate.parse(startDate, formatter);

//		registUser.setPassword(passwordUtil.getSaltedAndStrechedPassword(registUserForm.getPassword(), employeeCodeString));
		registUser.setRole(registUserForm.getRole());
		registUser.setName(userName);
		registUser.setStartDate(startDateLoalDate);
		registUser.setEmail(registUserForm.getEmail());
		registUser.setDepartmentId(registUserForm.getDepartmentId());
		registUser.setEmployeeCode(employeeCode);
		System.out.println(employeeCode);

		//ユーザー登録処理
		if (registUserForm.getInsertFlg() == Constants.INSERT_FLG) {
			if (!userMapper.userExsistByEmployeeCode(employeeCode)) {
				return "社員番号:" + employeeCode + "は既に登録されています。";

			} else {
				registUser.setPassword(passwordUtil.getSaltedAndStrechedPassword(Constants.FIRST_PASS, employeeCodeString));
				boolean updateCheck = userMapper.insertUserData(registUser);
				return updateCheck ? userName + "を登録しました。" : userName + "の登録が失敗しました。";
			}
			//ユーザー更新処理
		} else {
			if (!employeeCode.equals(registUserForm.getBeforeEmployeeCode())
					&& userMapper.userExsistByEmployeeCode(employeeCode)) {
				return "社員番号:" + employeeCode + "は既に登録されています。";
			}
			registUser.setId(id);
			boolean updateCheck = userMapper.updateUserData(registUser);
			return updateCheck ? userName + "を更新しました。" : userName + "の更新が失敗しました。";
		}
	}

	/**
	 * 部署入力プルダウン用リスト
	 * @return 部署リスト
	 */
	public Map<String, Integer> pulldownDepartment() {
		List<Department> departmentList = departmentMapper.findAllDepartmentName();
		Map<String, Integer> departmentMap = new LinkedHashMap<>();

		for (Department row : departmentList) {
			String departmentName = row.getName();
			Integer departmentId = row.getDepartmentId();
			departmentMap.put(departmentName, departmentId);
		}
		return departmentMap;
	}

	public List<Department> getAllDepartmentId() {

		return departmentMapper.findAllDepartmentName();

	}

	/**
	 * 名前または社員番号でユーザー検索
	 * @param userKeyword
	 * @return 該当のユーザー名・社員番号MapList
	 */
	public Map<Integer, Users> searchForUserNameAndEmployeeCode(String userKeyword) {
		System.out.println("サービス通った:" + userKeyword);
		List<Users> userNameAndEmployeeCodeList = userMapper.searchForUserNameAndEmployeeCode(userKeyword);
		System.out.println("マッパー返ってきた：" + userNameAndEmployeeCodeList);
		Map<Integer, Users> userNameAndEmployeeCodeMap = new LinkedHashMap<>();
		for (Users user : userNameAndEmployeeCodeList) {
			userNameAndEmployeeCodeMap.put(user.getEmployeeCode(), user);
		}
		System.out.println(userNameAndEmployeeCodeMap);
		return userNameAndEmployeeCodeMap;
	}

	/**
	 * インポートしたファイルをユーザーリスト化
	 * @param file
	 * @return インポートされたユーザーリスト
	 */
	public List<UserCsvInputDto> showImportList(MultipartFile file) {
		try (InputStream inputStream = file.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			List<UserCsvInputDto> insertList = new ArrayList<>();
			//読み取ったCSVの行を入れるための変数を作成
			String line;
			//ヘッダーレコードを飛ばすためにあらかじめ１行だけ読み取っておく（ない場合は不要）
			line = br.readLine();
			//行がNULL（CSVの値がなくなる）になるまで処理を繰り返す
			lineLabel: while ((line = br.readLine()) != null) {
				System.out.println("line:" + line);
				//Stringのsplitメソッドを使用してカンマごとに分割して配列にいれる
				String[] csvSplit = line.split(",", Constants.USER_COLUMN_LENGTH);
				for (int i = 0; i < csvSplit.length; i++) {
					//社員番号を入力していない行は飛ばす
					if (csvSplit[0] == "") {
						continue lineLabel;
					}
				}
				//分割した値をセットして登録
				UserCsvInputDto user = new UserCsvInputDto();
				user.setEmployeeCode(csvSplit[0]);
//				user.setPassword(csvSplit[1]);
				user.setName(csvSplit[1]);
				user.setRole(csvSplit[2]);
				user.setDepartmentId(csvSplit[3]);
				user.setStartDate(csvSplit[4]);
				if (csvSplit[5].isEmpty()) {
					user.setEmail(null);
				} else {
					user.setEmail(csvSplit[5]);
				}
				insertList.add(user);
			}
			br.close();
			List<String> duplicationList = duplicationCheck(insertList);
			System.out.println(duplicationList);
			if (!duplicationList.isEmpty()) {

				throw new IllegalStateException("社員番号が重複しています。社員番号" + duplicationList.toString());
			}
			return insertList;
		} catch (IOException e) {
			throw new IllegalStateException("CSVの読み込みに失敗しました。");

		}
	}

	/**
	 * CSV入力社員番号の重複チェック
	 * @param insertList
	 * @return
	 */
	public List<String> duplicationCheck(List<UserCsvInputDto> insertList) {
		List<String> duplicationEmployeeList = new ArrayList<>();
		List<String> list = new ArrayList<>();
		for (UserCsvInputDto user : insertList) {
			System.out.println(list);
			String employeeCode = user.getEmployeeCode();
			if (!list.contains(employeeCode)) {
				list.add(employeeCode);
			} else {
				duplicationEmployeeList.add(employeeCode);
			}
		}
		System.out.println(duplicationEmployeeList);
		return duplicationEmployeeList;
	}

	/**
	 * CSV入力フロー管理
	 * @param userCsvInputDtoList
	 * @return  処理結果
	 */
	public boolean handleCsvImport(List<UserCsvInputDto> userCsvInputDtoList) {
		//String型から各プロパティの型に変換
		List<Users> usersList = entityString(userCsvInputDtoList);
		//社員番号からユーザーを検索
		List<Users> findIdByEmployeeCodeList = userMapper.findIdByEmployeeCode(usersList);
		//入力データの妥当性チェック
		validateIntegrity(usersList, findIdByEmployeeCodeList);
		//新規登録処理と更新登録処理に分ける
		Map<String, List<Users>> classifiedLists = classifyUsers(usersList, findIdByEmployeeCodeList);
		//処理開始
		return importUsers(classifiedLists.get("insert"), classifiedLists.get("update"));
	}

	/**
	 * DTOからエンティティへ格納
	 * @param userCsvInputDtoList
	 * @return DTOのデータを格納したエンティティリスト
	 */
	public List<Users> entityString(List<UserCsvInputDto> userCsvInputDtoList) {
		List<Users> usersList = new ArrayList<>();
		for (UserCsvInputDto userCsvInputDto : userCsvInputDtoList) {
			Users user = new Users();
			user.setEmployeeCode(Integer.parseInt(userCsvInputDto.getEmployeeCode()));
//			user.setPassword(passwordUtil.getSaltedAndStrechedPassword(Constants.FIRST_PASS, userCsvInputDto.getEmployeeCode()));
			user.setName(userCsvInputDto.getName());
			user.setRole(userCsvInputDto.getRole());
			user.setDepartmentId(Integer.parseInt(userCsvInputDto.getDepartmentId()));
			user.setStartDate(customDateUtil.formatDate(userCsvInputDto.getStartDate()));
			user.setEmail(userCsvInputDto.getEmail());
			usersList.add(user);
		}
		return usersList;
	}

	/**
	 * 入力データの妥当性チェック
	 * @param usersList
	 * @param findIdByEmployeeCodeList
	 */
	public void validateIntegrity(List<Users> usersList, List<Users> findIdByEmployeeCodeList) {
		//CSV入力データから社員番号、名前をマップ化
		Map<Integer, String> usersCodeNameMap = usersList.stream()
				.collect(Collectors.toMap(Users::getEmployeeCode, Users::getName));
		//DBの社員番号抽出データから社員番号、名前をマップ化
		Map<Integer, String> nameByEmployeeCodeMap = findIdByEmployeeCodeList.stream()
				.collect(Collectors.toMap(Users::getEmployeeCode, Users::getName));
		//２つのマップで一致しないバリューがあったらエラーを出して終了
		for (Map.Entry<Integer, String> nameByEmployeeCodeEntry : nameByEmployeeCodeMap.entrySet()) {
			System.out.println("nameByEmployeeCode" + nameByEmployeeCodeEntry);
			for (Map.Entry<Integer, String> usersCodeNameEntry : usersCodeNameMap.entrySet()) {
				System.out.println("usersCodeName" + usersCodeNameEntry);
				if (usersCodeNameEntry.getKey().equals(nameByEmployeeCodeEntry.getKey())) {
					if (usersCodeNameEntry.getValue().equals(nameByEmployeeCodeEntry.getValue())) {
						continue;
					}
					throw new IllegalStateException("[社員番号:" + usersCodeNameEntry.getKey() + ",名前:"
								+ usersCodeNameEntry.getValue() + "]" + "データベースと一致しない登録済みデータがあります");

				}
			}
		}

	}

	/**
	 * 新規登録処理と更新登録処理に分ける
	 * @param usersList
	 * @return insert,update振り分け済みのMap
	 */
	public Map<String, List<Users>> classifyUsers(List<Users> usersList, List<Users> findIdByEmployeeCodeList) {
		Map<String, List<Users>> classifiedLists = new HashMap<>();
		// トランザクションの対象とするデータリスト
		List<Users> insertList = new ArrayList<>();
		List<Users> updateList = new ArrayList<>();

		Map<Integer, Users> employeeCodeMap = findIdByEmployeeCodeList.stream()
				.collect(Collectors.toMap(Users::getEmployeeCode, user -> user));

		usersList.forEach(user -> {
			Users existingUser = employeeCodeMap.get(user.getEmployeeCode());
			if (existingUser != null) {
				user.setId(existingUser.getId());
				updateList.add(user);
			} else {
				user.setPassword(passwordUtil.getSaltedAndStrechedPassword(Constants.FIRST_PASS, String.valueOf(user.getEmployeeCode())));
				insertList.add(user);
			}
		});
		classifiedLists.put("insert", insertList);
		classifiedLists.put("update", updateList);

		return classifiedLists;

	}

	/**
	 * データベースで登録・更新処理
	 * @param insertList
	 * @param updateList
	 * @return 反映結果
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean importUsers(List<Users> insertList, List<Users> updateList) {
		System.out.println("importUsers入り:" + insertList);
		if (!insertList.isEmpty()) {
			System.out.println("Insert入り");
			return userMapper.batchInsertUsers(insertList);
		}
		if (!updateList.isEmpty()) {
			System.out.println("Update入り:" + updateList);
			return userMapper.batchUpdateUsers(updateList);
		}
		return false;
	}

}
