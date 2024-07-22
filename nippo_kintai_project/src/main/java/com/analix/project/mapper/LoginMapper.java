package com.analix.project.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.analix.project.entity.Users;

@Mapper
public interface LoginMapper {

	Users findByIdAndPassword(@Param("id") Integer id, @Param("password") String password);

}
