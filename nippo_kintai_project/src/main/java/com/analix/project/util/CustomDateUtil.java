package com.analix.project.util;

import java.time.LocalDate;
import java.time.YearMonth;

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
	
	
	//LocalDate→String型のyyyy/MM/ddへ変換
	public String dateHyphenSlashConverter(LocalDate localDate) {
		return localDate.toString().replace("-", "/");
	}
    //YearMonth→String型のyyyy/MMへ変換
	public String yearMonthHyphenSlashConverter(YearMonth yearMonth) {
		return yearMonth.toString().replace("-", "/");
	}

}
