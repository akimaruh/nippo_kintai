package com.analix.project.util;

/*
 * 定数クラス
 */
public interface Constants {
	/** 改行指定 **/
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	/** ユーザー権限:社員 **/
	public static final String CODE_VAL_ADMIN = "Admin";
	/** ユーザー権限:マネージャ **/
	public static final String CODE_VAL_MANAGER = "Manager";
	/** ユーザー権限:ユニットマネージャ **/
	public static final String CODE_VAL_UNITMANAGER = "UnitManager";
	/** ユーザー権限:社員 **/
	public static final String CODE_VAL_REGULAR = "Regular";

	/** 日報提出ステータス：未提出 */
	public static final Integer CODE_VAL_UNSUBMITTED = 0;
	/** 日報提出ステータス：提出済承認前 */
	public static final Integer CODE_VAL_BEFORE_SUBMITTED_APPROVAL = 1;
	/** 日報提出ステータス：承認済 */
	public static final Integer CODE_VAL_APPROVED = 2;

	/** 帳票名 : 勤怠帳票**/
	public static final String CODE_VAL_ATTENDANCE_OUTPUT = "勤怠帳票";
	/** 帳票名 : 日報帳票**/
	public static final String CODE_VAL_DAILY_REPORT_OUTPUT = "日報帳票";
	/** 帳票名 : 日報勤怠帳票**/
	public static final String CODE_VAL_ATTENDANCE_DAILY_REPORT_OUTPUT = "日報勤怠帳票";
	/** 帳票ヘッダーカラム名 ： 日報帳票 日毎集計部分 **/
	public static final String[] DAILY_REPORT_HEADERNAME_ARRAY = { "日付", "曜日", "作業①", "時間①", "作業②", "時間②", "作業③", "時間③",
			"作業④", "時間④",
			"作業⑤", "時間⑤", "総作業時間" };
	/** 帳票に表示する１日の作業の最大種類数 **/
	public static final int DISPLAY_WORK_QTY = 5;

}
