package com.analix.project.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analix.project.dto.MonthlyAttendanceReqDto;
import com.analix.project.entity.Attendance;
import com.analix.project.entity.AttendanceCorrection;
import com.analix.project.entity.Users;
import com.analix.project.form.AttendanceCorrectionForm;
import com.analix.project.form.AttendanceFormList;
import com.analix.project.service.AttendanceService;
import com.analix.project.service.EmailService;
import com.analix.project.service.InformationService;
import com.analix.project.service.WebPushService;
import com.analix.project.util.MessageUtil;

import jakarta.servlet.http.HttpSession;

@Controller
public class AttendanceController {

	@Autowired
	public AttendanceService attendanceService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private InformationService informationService;
	@Autowired
	private WebPushService webPushService;

	/**
	 * 初期表示
	 * @param userId
	 * @param year
	 * @param month
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "/attendance/regist")
	public String attendanceRegist(Model model, HttpSession session, AttendanceFormList attendanceFormList) {
		
		model.addAttribute("currentPath", "/attendance/regist");
		
		//【社員権限】
		//対象年月(デフォルトは遷移当日の年月)	
		LocalDate now = LocalDate.now();
		int year = now.getYear();
		int month = now.getMonthValue();
		//		YearMonth ymYearMonth = YearMonth.of(year, month);
		//		System.out.println(ymYearMonth);
		//		System.out.println(ymYearMonth.atDay(1));
		//		System.out.println(ymYearMonth.atEndOfMonth());

		//年月の表示
		model.addAttribute("year", year);
		model.addAttribute("month", month);

		//【マネージャ権限】申請提出者一覧表示
		List<MonthlyAttendanceReqDto> monthlyAttendanceReqList = attendanceService.getMonthlyAttendanceReq();
		System.out.println(monthlyAttendanceReqList);
		model.addAttribute("monthlyAttendanceReqList", monthlyAttendanceReqList);
		
		//【マネージャ権限】訂正リスト
		List<AttendanceCorrection> AttendanceCorrectionList = attendanceService.getAttendanceCorrection();
		model.addAttribute("attendanceCorrectionList",AttendanceCorrectionList);

		// 初期表示で「登録ボタン」を非活性にさせるためのフラグ
		boolean displayRegistButtonFlg = true;
		model.addAttribute("displayFlg", displayRegistButtonFlg);

		return "/attendance/regist";
	}

	/**
	 * 『表示』ボタン押下後
	 * @param yearMonth
	 * @param model
	 * @param session
	 * @param attendanceFormList
	 * @return
	 */
	@RequestMapping(path = "/attendance/regist/display", method = RequestMethod.GET)
	public String attendanceDisplay(@RequestParam int year, @RequestParam int month,
			Model model, HttpSession session, AttendanceFormList attendanceFormList) {
		
		model.addAttribute("currentPath", "/attendance/regist/display");

		//バラバラに取得した年と月を合体(yyyy-MM)
		YearMonth targetYearMonth = YearMonth.of(year, month);
		session.setAttribute("yearMonth", targetYearMonth);
		// yyyy-MMをyyyy/MMに変換(String型にしてから)
		String yearMonthStr = targetYearMonth.toString();
		String formattedYearMonth = yearMonthStr.replace("-", "/"); // yyyy/MM
		model.addAttribute("formattedYearMonth", formattedYearMonth);
		//月次勤怠テーブルは対象年月をyyyy-MM-ddで格納しているためyyyy-MM-01の形になるように整形
		LocalDate targetYearMonthAtDay = targetYearMonth.atDay(1);
		session.setAttribute("targetYearMonthAtDay", targetYearMonthAtDay);

		// ヘッダー:ステータス部分
		//ログイン時にセットしたセッションからユーザー情報を取り出す
		Users user = (Users) session.getAttribute("loginUser");
		Integer userId = user.getId();
		//		session.setAttribute("attendanceDate", targetYearMonth);
		Integer status = attendanceService.findStatusByUserId(userId, targetYearMonthAtDay);
		//statusの値に紐づくステータス名を設定する
		String statusFlg;
		if (status == null) {
			statusFlg = "未申請";
		} else {
			switch (status) {
			case 1:
				statusFlg = "申請中";
				break;
			case 2:
				statusFlg = "承認済";
				break;
			case 3:
				statusFlg = "却下";
				break;
			default:
				statusFlg = "未申請";
				break;
			}
		}
		//ステータス名とDB格納のステータス値どちらもフロントに渡す
		model.addAttribute("statusFlg", statusFlg);
		model.addAttribute("status", status);
		//		session.setAttribute("yearMonth", targetYearMonth);

		//ユーザーIDと年月の値を渡して取得した勤怠日付と勤怠情報を勤怠フォームリストへセット
		attendanceFormList.setAttendanceFormList(attendanceService.getFindAllDailyAttendance(userId, targetYearMonth));
		//勤怠フォームリストの中が空だった場合年月が入っていないためエラー
		if (attendanceFormList.getAttendanceFormList().contains(null)) {
			model.addAttribute("error", "年月を入力してください");

			return "/attendance/regist";
		}

		//１か月分登録されていたら活性化
		boolean monthlyRegistCheck = attendanceService.applicableCheck(attendanceFormList.getAttendanceFormList());

		//表示ボタン押下後も入力した年月の値を残す
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		//登録ボタンの活性可否チェックの結果を渡す
		model.addAttribute("registCheck", monthlyRegistCheck);
		
		// 却下理由【月次申請】表示
		List<MonthlyAttendanceReqDto> monthlyList = attendanceService.getMonthlyAttendanceReqByUserId(userId, targetYearMonthAtDay);
		if (!monthlyList.isEmpty()) {
		    MonthlyAttendanceReqDto firstRequest = monthlyList.get(0);
		    model.addAttribute("monthlyRejectComment", firstRequest.getComment());
		}
		
		// 訂正申請中アコーディオン表示
		List<AttendanceCorrection> requestedCorrectionList = attendanceService.findRequestedByUserIdAndYearMonth(userId, targetYearMonth);
		model.addAttribute("requestedCorrectionList", requestedCorrectionList);

		// 却下【訂正理由】表示（勤怠リスト）
		List<Attendance> attendanceList = attendanceService.findByUserIdAndYearMonth(userId, targetYearMonth);
		Map<LocalDate, Attendance> attendanceMap = new HashMap<>();
		for (Attendance attendance : attendanceList) {
			attendanceMap.put(attendance.getDate(), attendance);
		}
		model.addAttribute("attendanceMap", attendanceMap);

		// 却下【訂正理由】表示（訂正リスト）
		List<AttendanceCorrection> rejectedCorrectionList = attendanceService.findRejectedByUserIdAndYearMonth(userId, targetYearMonth);
		model.addAttribute("rejectedCorrectionList", rejectedCorrectionList);
		
		return "/attendance/regist";
	}


	/**
	 * 『登録』ボタン押下後
	 * @param userId
	 * @param attendanceFormList
	 * @param result
	 * @param model
	 * @param session
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(path = "/attendance/regist/complete", method = RequestMethod.POST)
	public String attendanceComplete(@RequestParam("sessionUserId") Integer userId,
			@Validated @ModelAttribute AttendanceFormList attendanceFormList, BindingResult result, Model model,
			HttpSession session, RedirectAttributes redirectAttributes) {

		//『表示』ボタン押下時にセッションにセットした年月の値を取得する
		YearMonth yearMonth = (YearMonth) session.getAttribute("yearMonth");
		int year = yearMonth.getYear();
		int month = yearMonth.getMonthValue();

		//入力チェックへ
		attendanceService.validationForm(attendanceFormList, result);

		//入力チェックエラ－時
		if (result.hasErrors()) {
			model.addAttribute("attendanceFormList", attendanceFormList);
			model.addAttribute("year", year);
			model.addAttribute("month", month);
			model.addAttribute("error", "エラー内容に従って修正してください");

			return "/attendance/regist";
		}
		//勤怠表を登録更新
		String message = attendanceService.getRegistDailyAttendance(userId, attendanceFormList);

		redirectAttributes.addFlashAttribute("message", message);

		//表示ボタン押下後の画面へリダイレクト
		return "redirect:/attendance/regist/display?year=" + year + "&month=" + month;

	}

	/**
	 * 『承認申請』ボタン押下後
	 * @param session
	 * @param model
	 * @param redirectAttributes
	 * @param targetYearMonth 
	 * @param request 
	 * @return
	 */
	@RequestMapping(path = "/attendance/approveRequestComplete", method = RequestMethod.POST)
	public String approveRequest(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
		
		//セッションからユーザー情報の受け取り
		Users user = (Users) session.getAttribute("loginUser");
		Integer userId = user.getId();
		//『表示』ボタン押下時にセッションにセットした年月の値を取得する
		YearMonth approveYearMonth = (YearMonth) session.getAttribute("yearMonth");
		//月次勤怠テーブルの対象年月はyyyy-MM-01で格納されているので形を合わせる
		LocalDate approveYearMonthAtDay = approveYearMonth.atDay(1);

		int year = approveYearMonth.getYear();
		int month = approveYearMonth.getMonthValue();

		//承認申請の登録更新
		String message = attendanceService.insertMonthlyAttendanceReq(userId, approveYearMonthAtDay);

		List<MonthlyAttendanceReqDto> requests = attendanceService.getMonthlyAttendanceReqByUserId(userId,
				approveYearMonthAtDay);
		for (MonthlyAttendanceReqDto request : requests) {
			System.out.println("Sending request: " + request);
			String mailMessage = MessageUtil.mailCommonMessage();
			emailService.sendRequestEmail(request, mailMessage);
		}
		informationService.approveRequestInsertNotifications(user.getName(), approveYearMonth);
		redirectAttributes.addFlashAttribute("message", message);
		// プッシュ通知
		try {
			String payload = "{\"title\":\"【日報勤怠アプリ】勤怠承認申請\",\"body\":\"承認申請があります。\"}";
			webPushService.sendRequestPush(payload);
		} catch (GeneralSecurityException | IOException | JoseException e) {
			System.out.println("承認申請:通知送信中にエラーが発生しました: " + e.getMessage());
			e.printStackTrace();
		}

		return "redirect:/attendance/regist/display?year=" + year + "&month=" + month;
	}

	/**
	 * 『月次：承認申請者』リンク押下後
	 * @param userId
	 * @param targetYearMonth
	 * @param model
	 * @param session
	 * @param attendanceFormList
	 * @return
	 */
	@PostMapping("/attendance/approveRequests")
	public String showApproveRequests(@RequestParam("userId") Integer userId,
			@RequestParam("targetYearMonth") LocalDate targetYearMonthAtDay,
			@RequestParam("name") String name, @RequestParam("date") String date,
			@RequestParam("formattedYearMonth") String formattedYearMonth,
			@RequestParam("formattedDate") String formattedDate,
			Model model, HttpSession session,
			AttendanceFormList attendanceFormList) {
		
		session.setAttribute("targetType", "monthly");
		model.addAttribute("currentPath", "/attendance/approveRequests");
		
		System.out.println(targetYearMonthAtDay); // yyyy-MM-01
		YearMonth targetYearMonth = YearMonth.of(targetYearMonthAtDay.getYear(), targetYearMonthAtDay.getMonthValue());
		session.setAttribute("targetYearMonth", targetYearMonth); // yyyy-MM

		//		String yearMonth = targetYearMonth.substring(0, 7); // String型に変換　/2024-01/-01
		//		LocalDate ldYearMonth = LocalDate.parse(yearMonth);
		//		System.out.println(ldYearMonth);

		List<Attendance> attendanceList = attendanceService.findByUserIdAndYearMonth(userId, targetYearMonth);

		model.addAttribute("attendanceList", attendanceList);
		model.addAttribute("formattedYearMonth", formattedYearMonth); //yyyy/MM
		model.addAttribute("formattedDate", formattedDate); // yyyy/MM/dd
		model.addAttribute("name", name);
		
		// モーダルのメッセージ
		String modalMessage = name + "さんの" + formattedYearMonth + "の月次申請を却下しますか？";
		model.addAttribute("modalMessage", modalMessage);

		return "/attendance/regist";
	}
	
	/**
	 * 『訂正：承認申請者』リンク押下後
	 * @param userId
	 * @param date
	 * @param userName
	 * @param formattedDate yyyy/MM/dd
	 * @param model
	 * @param session
	 * @return
	 */
	@PostMapping("/attendance/correctionRequests")
	public String showCorrectionRequests(@RequestParam("userId") Integer userId, @RequestParam("date") String dateStr, 
			@RequestParam("userName") String userName, @RequestParam("formattedDate") String formattedDate, 
			@RequestParam("formattedApplicationDate") String formattedApplicationDate, Model model, HttpSession session) {
		AttendanceCorrection correction = attendanceService.findCorrectionByUserIdAndDate(userId, dateStr);
		
		session.setAttribute("targetType", "correction");
		session.setAttribute("userName", userName);
		session.setAttribute("date", dateStr);
		session.setAttribute("formattedDate", formattedDate);
		
		model.addAttribute("currentPath", "/attendance/correctionRequests");
		model.addAttribute("correction", correction);
		model.addAttribute("userName", userName);
//		String formattedDate = date.replace("-", "/");
		model.addAttribute("formattedDate", formattedDate); // yyyy/MM/dd
		model.addAttribute("formattedApplicationDate", formattedApplicationDate); // yyyy/MM/dd
		
		// 訂正前の情報を表示するための処理
		YearMonth targetYearMonth = YearMonth.parse(dateStr.substring(0, 7)); // dateをyyyy-MMにしてYearMonth型に変換
		List<Attendance> attendanceList = attendanceService.findByUserIdAndYearMonth(userId, targetYearMonth);
		model.addAttribute("attendanceList", attendanceList);
		
		// モーダルのメッセージ
		String modalMessage = userName + "さんの" + formattedDate + "の訂正申請を却下しますか？";
		model.addAttribute("modalMessage", modalMessage);
		
		return "/attendance/regist";
	}
	
	/**
	 * 月次申請『承認』ボタン押下後
	 * @param userId
	 * @param targetYearMonth
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/attendance/approveMonthly")
	public String approveStatus(@RequestParam("userId") Integer userId, @RequestParam("targetYearMonth") YearMonth targetYearMonth, RedirectAttributes redirectAttributes) {
		
		LocalDate targetYearMonthAtDay = targetYearMonth.atDay(1);
		String message = attendanceService.updateStatusApprove(userId, targetYearMonthAtDay);
		String mailMessage = MessageUtil.mailCommonMessage();
		emailService.sendApproveEmail(userId, targetYearMonth, mailMessage);
		redirectAttributes.addFlashAttribute("message", message);
		informationService.approveInsertNotifications(userId, targetYearMonth);
		
		// プッシュ通知送信
		try {
			String payload = "{\"title\":\"【日報勤怠アプリ】\",\"body\":\"申請が承認されました。\"}";
			webPushService.sendApprovePush(userId, payload);
		} catch (GeneralSecurityException | IOException | JoseException e) {
			System.out.println("承認:通知送信中にエラーが発生しました: " + e.getMessage());
			e.printStackTrace();
		}
		
		return "redirect:/attendance/regist";
	}
		
	/**
	 * 月次申請『却下』ボタン押下後
	 * @param userId
	 * @param targetYearMonth
	 * @param comment
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/attendance/rejectMonthly")
	public String rejectStatus(@RequestParam("userId") Integer userId, @RequestParam("targetYearMonth") YearMonth targetYearMonth, @RequestParam("comment") String comment, RedirectAttributes redirectAttributes) {
		
		LocalDate targetYearMonthAtDay = targetYearMonth.atDay(1);
		String message = attendanceService.updateStatusReject(userId, targetYearMonthAtDay, comment);
		String mailMessage = MessageUtil.mailCommonMessage();
		emailService.sendRejectEmail(userId, targetYearMonth, mailMessage);
		redirectAttributes.addFlashAttribute("message", message);
		informationService.rejectInsertNotifications(userId, targetYearMonth);
		
		// プッシュ通知送信
		try {
			String payload = "{\"title\":\"【日報勤怠アプリ】\",\"body\":\"申請が却下されました。再度申請を行ってください。\"}";
			webPushService.sendRejectPush(userId, payload);
		} catch (GeneralSecurityException | IOException | JoseException e) {
			System.out.println("却下:通知送信中にエラーが発生しました: " + e.getMessage());
			e.printStackTrace();
		}
		
		return "redirect:/attendance/regist";
	}

	/**
	 * 訂正申請『承認』ボタン押下後
	 * @param id
	 * @param userId
	 * @param confirmer
	 * @param redirectAttributes
	 * @param session
	 * @return
	 */
	@PostMapping("/attendance/approveCorrection")
	public String approveCorrection(@RequestParam("id") Integer id, @RequestParam("userId") Integer userId,
			@RequestParam("confirmer") String confirmer, RedirectAttributes redirectAttributes, HttpSession session) {
		String userName = (String) session.getAttribute("userName");
		String formattedDate = (String) session.getAttribute("formattedDate");
		String dateStr = (String) session.getAttribute("date");
		// dateStrをLocalDateに変換
		LocalDate localDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd")); // フォーマットを調整
		YearMonth targetYearMonth = YearMonth.from(localDate); // YearMonthを生成

		// 処理
		boolean isApprove = attendanceService.updateApproveCorrection(id, confirmer);
		String message;

		if (isApprove) {
			message = userName + "の" + formattedDate + "に訂正申請が承認されました。";
			// メール
			String mailMessage = MessageUtil.mailCommonMessage();
			emailService.sendCorrectionApproveEmail(userId, formattedDate, mailMessage);
			// お知らせ
			informationService.correctionApproveInsertNotifications(userId, formattedDate, targetYearMonth);

		} else {
			message = "承認に失敗しました。";

			redirectAttributes.addFlashAttribute("message", message);

		}
		return "redirect:/attendance/regist";
	}
	
	/**
	 * 訂正申請『却下』ボタン押下後
	 * @param id
	 * @param correctionReason
	 * @param redirectAttributes
	 * @param session
	 * @return
	 */
	@PostMapping("/attendance/rejectCorrection")
	public String rejectCorrection(@RequestParam("id") Integer id, @RequestParam("userId") Integer userId,
			@RequestParam("rejectionReason") String rejectionReason, RedirectAttributes redirectAttributes,
			HttpSession session, Model model) {
		
		Users user = (Users) session.getAttribute("loginUser");
		String confirmer = user.getName();
		String userName = (String)session.getAttribute("userName");
		String formattedDate = (String)session.getAttribute("formattedDate");
		String dateStr = (String)session.getAttribute("date");
		// dateStrをLocalDateに変換
	    LocalDate localDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd")); // フォーマットを調整
	    YearMonth targetYearMonth = YearMonth.from(localDate); // YearMonthを生成
		
	    // 処理
		boolean isReject = attendanceService.updateRejectCorrection(confirmer, rejectionReason, id);
		String message;
		
		if (isReject) {
			message = userName + "の" + formattedDate + "における訂正申請が却下されました。";
			// メール
			String mailMessage = MessageUtil.mailCommonMessage();
			emailService.sendCorrectionRejectEmail(userId, formattedDate, mailMessage);
			// お知らせ
			informationService.correctionRejectInsertNotifications(userId, formattedDate, targetYearMonth);
			
		} else {
			message = "却下に失敗しました。";
		}
		
		redirectAttributes.addFlashAttribute("message", message);
		
		return "redirect:/attendance/regist";
	}

	/**
	 * 処理メニュー画面『出勤』・『退勤』ボタン押下後 
	 * @param action
	 * @param userId
	 * @param redirectAttributes
	 * @return 処理メニュー画面
	 */
	@GetMapping("/attendance/stamping")
	public String beginRegist(@RequestParam("action") String action, @RequestParam("userId") Integer userId,
			RedirectAttributes redirectAttributes) {

		LocalDate today = LocalDate.now();
		LocalTime now = LocalTime.now();
		//メッセージに打刻時間を入れたかったら↓の２行を使えます。
		//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		//		 String nowString = now.format(formatter);

		boolean isRegist = false;
		String message = null;
		String error = null;

		//『出勤』ボタン押下時
		if ("begin".equals(action)) {
			isRegist = attendanceService.insertStartTime(userId, today, now);
			if (isRegist) {
				message = "おはようございます。出勤を打刻しました。";
			} else {
				error = "打刻に失敗しました。";
			}
		}
		//『退勤』ボタン押下時
		if ("finish".equals(action)) {
			isRegist = attendanceService.updateEndTime(userId, today, now);
			if (isRegist) {
				message = "退勤を打刻しました。本日もお疲れ様でした。";
			} else {
				error = "打刻に失敗しました。";
			}
		}
		redirectAttributes.addFlashAttribute("message", message);
		redirectAttributes.addFlashAttribute("error", error);

		return "redirect:/common/startMenu";
	}
		
//	/**
//	 * 勤怠訂正モーダルの申請ボタン押下後
//	 * @param correctionForm
//	 * @param model
//	 * @param redirectAttributes
//	 * @return
//	 */
//	@PostMapping("/attendance/correction")
//	@ResponseBody
//	public ResponseEntity<Map<String, Object>> correctionReq(
//			@Validated @RequestBody AttendanceCorrectionForm correctionForm, BindingResult result, HttpSession session) {
//
//		Map<String, Object> response = new HashMap<>();
//
//		// 入力チェック
//		attendanceService.correctionValidationForm(correctionForm, result);
//
//		// 入力チェックエラ－時
//		if (result.hasErrors()) {
//			String errorMessage = result.getFieldError().getDefaultMessage();
//			response.put("succses", false);
//			response.put("message", errorMessage);
//			System.out.println("エラー時");
//		} else {
//			//正常な場合、データベースに登録
//			String message = attendanceService.registCorrection(correctionForm);
//			response.put("succes", true);
//			response.put("message", message);
//			
//			//リダイレクト先の情報を返す
//			YearMonth targetYearMonth = (YearMonth) session.getAttribute("yearMonth");
//			int year = targetYearMonth.getYear();
//			int month = targetYearMonth.getMonthValue();
//			response.put("year", year);
//			response.put("month", month);
//		}
//
//		return ResponseEntity.ok(response);
//	}
	
	/**
	 * 勤怠訂正モーダルの申請ボタン押下後
	 * @param correctionForm
	 * @param session
	 * @param result
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/attendance/correction")
	public ResponseEntity<Map<String, Object>> correctionReq(
			@Validated @RequestBody AttendanceCorrectionForm correctionForm,
			HttpSession session, BindingResult result, RedirectAttributes redirectAttributes) {

		Map<String, Object> response = new HashMap<>();

		if (result.hasErrors()) {
			response.put("success", false);
			response.put("message", result.getFieldError().getDefaultMessage());
			String errorMessage = "エラーがあります。";
			response.put(errorMessage, errorMessage);
			return ResponseEntity.badRequest().body(response); // エラー時のレスポンス
		}


		// リダイレクト先の情報をセッションに格納
		YearMonth targetYearMonth = (YearMonth) session.getAttribute("yearMonth");
		int year = targetYearMonth.getYear();
		int month = targetYearMonth.getMonthValue();
		
		// エラーがなければ
		String message = attendanceService.registCorrection(correctionForm);
		response.put("success", true);
		response.put("message", message);
		response.put("year", year);
		response.put("month", month);
		
		// お知らせ表示
		Users user = (Users) session.getAttribute("loginUser");
		String userName = user.getName();
	    String correctionDate = correctionForm.getDate();
		informationService.correctionRequestInsertNotifications(userName, correctionDate);
		// メール
		String mailMessage = MessageUtil.mailCommonMessage();
		emailService.sendCorrectionRequestEmail(userName, correctionDate, mailMessage);

		return ResponseEntity.ok(response); // 成功時のレスポンス
	}


	
	/**
	 * 却下理由【訂正申請】×ボタン押下(却下フラグ0に更新)
	 * @param correctionId
	 * @param session
	 * @return
	 */
	@PostMapping("/attendance/removeRejectedCorrection")
	public String removeRejectedCorrection(@RequestParam("correctionId") Integer correctionId, HttpSession session) {
		attendanceService.updateRejectFlg(correctionId);
		
		YearMonth targetYearMonth = (YearMonth) session.getAttribute("yearMonth");
		int year = targetYearMonth.getYear();
		int month = targetYearMonth.getMonthValue();
		
		return "redirect:/attendance/regist/display?year=" + year + "&month=" + month;
	}
	
	

}
