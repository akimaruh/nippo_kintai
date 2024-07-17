package com.analix.project.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.analix.project.entity.Users;

@Mapper
public interface UserMapper {
	
	Users findUserDataByUserName (@Param("name") String name);
	
	Boolean updateUserData (@Param("users") Users users);
	
	Boolean insertUserData (@Param("users") Users users);
}