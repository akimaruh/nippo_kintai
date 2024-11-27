package com.analix.project.form;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Validated
public class RegistUserForm {

	private Integer id;
//	@NotBlank(message = "パスワードを入力してください", groups = { RegistUserGroup.class })
//	@Size(max = 16, message = "16文字以内で入力してください", groups = { RegistUserGroup.class })
//	@Pattern(regexp = "^[0-9a-zA-Z\\-\\s]*$", message = "半角で入力してください")
//	private String password;
	@NotBlank(message = "名前を入力してください", groups = { RegistUserGroup.class })
	@Size(max = 20, message = "20文字以内で入力してください", groups = { RegistUserGroup.class })
	@Pattern(regexp = "^[\\u3040-\\u309F\\u30A0-\\u30FF\\u4E00-\\u9FFF\\u3400-\\u4DBF\\u20000-\\u2FA1F\\u3005\\u309D-\\u309E\\u30FD-\\u30FEa-zA-Z0-9０-９・ー'’\\-]+$", message = "全角または半角英字で入力してください", groups = {
			RegistUserGroup.class })
	private String name;
	@NotBlank(message = "権限を選択してください", groups = { RegistUserGroup.class })
	private String role;
	@NotNull(message = "所属部署を選択してください", groups = { RegistUserGroup.class })
	private Integer departmentId;
	private String departmentName;
	@NotNull(message = "利用開始日を入力して下さい", groups = { RegistUserGroup.class })
	@Pattern(regexp = "^\\d{4}/\\d{2}/\\d{2}$", message = "日付のフォーマットが不正です", groups = { RegistUserGroup.class })
	private String startDate;
	@Email(message = "メールアドレスのフォーマットが不正です", groups = { RegistUserGroup.class })
	private String email;
	@NotNull(message = "社員コードを入力してください", groups = { RegistUserGroup.class })
	@Max(value = 999999999, message = "社員コードは9桁以内で入力して下さい。", groups = { SearchUserGroup.class, RegistUserGroup.class })
	private Integer employeeCode;
	//新規登録フラグ
	private short insertFlg;
	//更新前社員番号
	private Integer beforeEmployeeCode;

}
