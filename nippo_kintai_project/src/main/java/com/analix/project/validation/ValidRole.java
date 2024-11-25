package com.analix.project.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
@Constraint(validatedBy = RoleValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)

/**
 * 権限名存在チェックアノテーション
 * @author akimaru
 */
public @interface ValidRole {
	String message() default "権限名が存在しません。";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
