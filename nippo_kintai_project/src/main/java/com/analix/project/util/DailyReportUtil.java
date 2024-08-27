package com.analix.project.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class DailyReportUtil {
	private static final Map<Integer, String> submitStatus = new HashMap<>();
	static {
		submitStatus.put(0, "未提出");
		submitStatus.put(1, "提出済承認前");
		submitStatus.put(2, "承認済");

	}

	public static String getSubmitStatus(Integer status) {
		return submitStatus.get(status);
	}
	

}
