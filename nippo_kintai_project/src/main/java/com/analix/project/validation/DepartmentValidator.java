package com.analix.project.validation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.analix.project.service.DepartmentService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
/**
 * 部署IDの存在チェック検証クラス
 * @author akimaru
 */
public class DepartmentValidator implements ConstraintValidator<ValidDepartment, String> {

	@Autowired
	private DepartmentService departmentService; // DBからリスト取得のため

	private List<String> validDepartments;
	
	//検証クラスが呼び出されたら最初に実行するメソッド
	//ここでは部署IDの全件リストを取得する
	@Override
	public void initialize(ValidDepartment constraintAnnotation) {
		this.validDepartments = departmentService.getAllDepartmentId();
	}
	//入力チェックを実行するメソッド
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return validDepartments.contains(value);
	}
}
