package com.analix.project.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

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
	
	
	
	
	private static final String SPECIAL_DATE = "9999/99/99";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    public static Optional<LocalDate> parseDate(String dateStr) {
        if (SPECIAL_DATE.equals(dateStr)) {
            return Optional.empty(); // 特殊日付は空のOptionalで返す
        }
        try {
            return Optional.of(LocalDate.parse(dateStr, FORMATTER));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + dateStr);
        }
    }

    public static String formatDate(LocalDate date) {
        if (date == null) {
            return SPECIAL_DATE;
        }
        return date.format(FORMATTER);
    }

    public static boolean isSpecialDate(String dateStr) {
        return SPECIAL_DATE.equals(dateStr);
    }

    public static boolean isSpecialDate(LocalDate date) {
        return date == null;
    }
    
    

}
