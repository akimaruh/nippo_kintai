package com.analix.project.util;

import java.util.List;

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
	/** ユーザー権限一覧 **/
	public static final List<String> CODE_VAL_ROLE_ARRAY = List.of(CODE_VAL_ADMIN, CODE_VAL_MANAGER,
			CODE_VAL_UNITMANAGER, CODE_VAL_REGULAR);

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
	/** 帳票名 : 勤怠日報帳票**/
	public static final String CODE_VAL_ATTENDANCE_REPORT_OUTPUT = "日報勤怠帳票";
	/** 帳票ヘッダーカラム名 ： 日報帳票 日毎集計部分 **/
	public static final String[] DAILY_REPORT_HEADERNAME_ARRAY = { "日付", "曜日", "作業①", "時間①", "作業②", "時間②", "作業③", "時間③",
			"作業④", "時間④",
			"作業⑤", "時間⑤", "総作業時間" };
	/** 帳票ヘッダーカラム名 ： 勤怠帳票 残業時間部分 **/
	public static final String TOTAL_OVERTIME = "総残業時間";
	/** 帳票ヘッダーカラム名 ： 勤怠帳票 総労働集計部分 **/
	public static final String[] ATTENDANCE_SUMMARY_HEADERNAME_ARRAY = { "総労働日数", "総労働時間" };
	/** 帳票ヘッダーカラム名 ： 勤怠帳票 勤怠表部分 **/
	public static final String[] ATTENDANCE_HEADERNAME_ARRAY = { "日付", "曜日", "勤怠状況", "出勤時間", "退勤時間", "備考" };
	/** 帳票に表示する１日の作業の最大種類数 **/
	public static final int DISPLAY_WORK_QTY = 5;
	/** 帳票ヘッダーカラム名 ： 勤怠日報帳票 勤怠+日報表部分 **/
	public static final String[] ATTENDANCE_REPORT_HEADERNAME_ARRAY = { "日付", "曜日", "勤務状況", "出勤時間", "退勤時間", "作業", "作業時間", "作業内容" };

	/** ユーザー一括登録CSVヘッダーカラム名 **/
	public static final String[] USER_HEADER_ARRAY = {"社員番号", "ユーザー名", "権限", "所属部署ID", "利用開始日", "メールアドレス"};
	
	/** ユーザーテーブルのカラム数 **/
	public static final int USER_COLUMN_LENGTH = 7;
	/** ユーザー登録で更新登録するフラグ **/
	public static final Byte INSERT_FLG = 1;
	/** ユーザー登録で更新登録するフラグ **/
	public static final Byte UPDATE_FLG = 2;
	/** 仮パスワード利用期限範囲(登録時間から"TEMP_PASSWORD_EXPIRE_HOURS"時間) **/
	public static final long TEMP_PASSWORD_EXPIRE_HOURS = 1;
	/** 仮パスワード有効フラグ: 無効 **/
	public static final Byte INACTIVE_FLG = 0;
	/** 仮パスワード有効フラグ: 有効 **/
	public static final Byte ACTIVE_FLG = 1;
	/** ログイン試行回数上限 **/
	public static final Integer LOGIN_FAILURE_LIMIT = 3;
	/** ロック期間 */
	public static final long LOGIN_LOCK_TIMEOUT_MINUTES = 1;

}
