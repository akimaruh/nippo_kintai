package com.analix.project.validation;

import java.util.List;

import com.analix.project.util.Constants;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
/**
 * 権限名の存在チェック検証クラス
 */
public class RoleValidator implements ConstraintValidator<ValidRole, String> {
	
	private List<String> validRoles;
	
	@Override
	public void initialize(ValidRole constraintAnnotation) {
		this.validRoles = Constants.CODE_VAL_ROLE_ARRAY;
	}
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return validRoles.contains(value);
	}

	
}
