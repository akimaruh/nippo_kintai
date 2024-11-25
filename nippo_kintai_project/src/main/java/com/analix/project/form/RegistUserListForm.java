package com.analix.project.form;

import java.util.List;

import org.springframework.validation.annotation.Validated;

import com.analix.project.entity.Users;

import lombok.Data;

@Data
@Validated
public class RegistUserListForm {
	private List<Users> registUserFormList;

}
