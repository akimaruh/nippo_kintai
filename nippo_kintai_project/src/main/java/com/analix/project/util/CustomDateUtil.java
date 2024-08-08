package com.analix.project.util;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

@Component
public class CustomDateUtil {
	
	//java.sql.Date→LocalDateへ変換
	public LocalDate convertToLocalDate(java.sql.Date sqlDate){
	    return sqlDate.toLocalDate();
	}
	
	//LocalDate→java.sql.Dateへ変換
	public java.sql.Date convertToSqlDate(LocalDate localDate){
	    return java.sql.Date.valueOf(localDate);
	}
	
	
    
    

}
