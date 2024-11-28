package com.analix.project.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component

/**
 * 権限の日本語変換用Util
 */
public class DisplayUtil {
	private static final Map<String, String> roleToJapanese = new HashMap<>();

	static {
		roleToJapanese.put("Admin", "管理者");
		roleToJapanese.put("UnitManager", "ユニットマネージャー");
		roleToJapanese.put("Manager", "マネージャー");
		roleToJapanese.put("Regular", "社員");
	}

	public static String getRoleToJapanese(String role) {
		return roleToJapanese.get(role);
	}

}
