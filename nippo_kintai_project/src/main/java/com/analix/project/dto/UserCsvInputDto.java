package com.analix.project.dto;

import org.springframework.validation.annotation.Validated;

import com.analix.project.validation.ValidDepartment;
import com.analix.project.validation.ValidRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Validated
public class UserCsvInputDto {
	@NotBlank(message = "パスワードを入力してください。")
	@Pattern(regexp = "^[0-9a-zA-Z\\-\\s]*$", message = "パスワードは半角16文字以内で入力してください。")
	@Size(max = 16, message = "パスワードは半角16文字以内で入力してください。")
	private String password;
	@NotBlank(message = "名前を入力してください。")
	@Size(max = 20, message = "名前は全角20文字以内で入力してください。文字数ダメ")
	@Pattern(regexp = "^[\\u3040-\\u309F\\u30A0-\\u30FF\\u4E00-\\u9FFF\\u3400-\\u4DBF\\u20000-\\u2FA1F\\u3005\\u309D-\\u309E\\u30FD-\\u30FEa-zA-Z0-9０-９・ー'’\\-]+$", message = "名前は全角20文字以内で入力してください。正規表現ダメ")
	private String name;
	@NotBlank(message = "権限を入力してください。")
	@ValidRole
	private String role;
	@NotNull(message = "所属部署IDを入力してください。")
	@Pattern(regexp = "^[0-9]*$", message = "所属部署IDは数字で入力して下さい。")
	@ValidDepartment
	private String departmentId;
	@Email(message = "メールアドレスのフォーマットが正しくありません。")
	private String email;
	@NotNull(message = "利用開始日を入力して下さい。")
	@Pattern(regexp = "^\\d{4}/\\d{1,2}/\\d{1,2}$", message = "日付のフォーマットが正しくありません。")
	private String startDate;
	@NotNull(message = "社員コードを入力してください。")
	@Size(max = 9, message = "社員コードは9桁以内で入力して下さい。")
	@Pattern(regexp = "^[0-9]*$", message = "社員コードは数字で入力して下さい。")
	private String employeeCode;

}
