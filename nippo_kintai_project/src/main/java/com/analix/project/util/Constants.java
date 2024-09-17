package com.analix.project.util;
/*
 * 定数クラス
 */
public interface Constants {
	/** 改行指定 **/
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	/** ユーザー権限:社員 **/
	public static final String CODE_VAL_ADMIN ="Admin";
	/** ユーザー権限:マネージャ **/
	public static final String CODE_VAL_MANAGER ="Manager";
	/** ユーザー権限:ユニットマネージャ **/
	public static final String CODE_VAL_UNITMANAGER ="UnitManager";
	/** ユーザー権限:社員 **/
	public static final String CODE_VAL_REGULAR ="Regular";
	
	/** 日報提出ステータス：未提出 */
	public static final Integer CODE_VAL_UNSUBMITTED = 0;
	/** 日報提出ステータス：提出済承認前 */
	public static final Integer CODE_VAL_BEFORE_SUBMITTED_APPROVAL = 1;
	/** 日報提出ステータス：承認済 */
	public static final Integer CODE_VAL_APPROVED = 2;
}
