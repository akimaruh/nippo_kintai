package com.analix.project.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = DepartmentValidator.class) //検証クラスを指定
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
/**
 * 部署の存在チェックアノテーション
 * @author akimaru
 */
public @interface ValidDepartment {
	String message() default "部署IDが存在しません。";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
