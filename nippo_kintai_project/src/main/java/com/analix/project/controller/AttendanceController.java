package com.analix.project.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analix.project.dto.AttendanceCorrectionDto;
import com.analix.project.dto.AttendanceInfoDto;
import com.analix.project.dto.MonthlyAttendanceReqDto;
import com.analix.project.entity.Attendance;
import com.analix.project.entity.AttendanceCorrection;
import com.analix.project.form.AttendanceCorrectionForm;
import com.analix.project.form.AttendanceFormList;
import com.analix.project.form.MonthlyAttendanceReqForm;
import com.analix.project.form.RejectAttendanceCorrectionGroup;
import com.analix.project.service.AttendanceService;
import com.analix.project.service.EmailService;
import com.analix.project.service.InformationService;
import com.analix.project.util.AttendanceUtil;
import com.analix.project.util.JapaneseHoliday;
import com.analix.project.util.MessageUtil;
import com.analix.project.util.SessionHelper;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor //lombok使ってコンストラクタ作成省略
@Controller
public class AttendanceController {
	
	private final AttendanceService attendanceService;
	private final EmailService emailService;
	private final InformationService informationService;
//	private final WebPushService webPushService;
	private final SessionHelper sessionHelper;

	/**
	 * 初期表示【社員権限】
	 * @param model
	 * @param session
	 * @param attendanceFormList
	 * @return
	 */
	@RequestMapping(path = "/attendance/regist")
	public String attendanceRegist(Model model, HttpSession session, AttendanceFormList attendanceFormList) {
		model.addAttribute("action", "initial");
		
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

		// 初期表示で「登録ボタン」を非活性にさせるためのフラグ
		boolean displayRegistButtonFlg = true;
		model.addAttribute("displayFlg", displayRegistButtonFlg);

		return "/attendance/regist";
	}
	
	/**
	 * 初期表示【マネージャ権限】
	 * @param model
	 * @param session
	 * @param attendanceFormList
	 * @return
	 */
	@RequestMapping(path = "/attendance/approve")
	public String attendanceApprove(Model model, HttpSession session, AttendanceFormList attendanceFormList) {
		model.addAttribute("action", "initial");

		//【マネージャ権限】月次申請提出者一覧リスト
		List<MonthlyAttendanceReqDto> monthlyAttendanceReqList = attendanceService.getMonthlyAttendanceReq();
		model.addAttribute("monthlyAttendanceReqList", monthlyAttendanceReqList);
		
		//【マネージャ権限】訂正申請提出者一覧リスト
		List<AttendanceCorrection> AttendanceCorrectionList = attendanceService.getAttendanceCorrection();
		model.addAttribute("attendanceCorrectionList",AttendanceCorrectionList);
		
//		List<AttendanceInfoDto> attendanceAndCorrectionList = attendanceService.findAttendanceAndCorrectionsByUserAndMonth(userId, targetYearMonth);
//		model.addAttribute("attendanceAndCorrectionList", attendanceAndCorrectionList);
//		
		return "/attendance/approve";
	}

	/**
	 * 『表示』ボタン押下後
	 * @param year
	 * @param month
	 * @param model
	 * @param session
	 * @param attendanceFormList
	 * @return
	 */
	@RequestMapping(path = "/attendance/regist/display", method = RequestMethod.GET)
	public String attendanceDisplay(@RequestParam int year, @RequestParam int month,
			Model model, HttpSession session, AttendanceFormList attendanceFormList) {
		model.addAttribute("action", "display");

		//バラバラに取得した年と月を合体(yyyy-MM)
		YearMonth targetYearMonth = YearMonth.of(year, month);
		session.setAttribute("yearMonth", targetYearMonth);
		// yyyy-MMをString型のyyyy/MMに変換
		String formattedYearMonth = targetYearMonth.toString().replace("-", "/");
		model.addAttribute("formattedYearMonth", formattedYearMonth);
		//月次勤怠テーブルは対象年月をyyyy-MM-ddで格納しているためyyyy-MM-01の形になるように整形
		LocalDate targetYearMonthAtDay = targetYearMonth.atDay(1);
		session.setAttribute("targetYearMonthAtDay", targetYearMonthAtDay);
		//勤怠状況プルダウンの内容を取得
		model.addAttribute("statusMap",AttendanceUtil.attendanceStatus);
		
		// 対象年月の祝日を取得
		List<String> holidays = JapaneseHoliday.getHoliday(targetYearMonth);
		model.addAttribute("holidays", holidays);
		
		// セッションヘルパークラスからuserIdを取得
		Integer userId = sessionHelper.getUser().getId();
		Integer status = attendanceService.findStatusByUserId(userId, targetYearMonthAtDay);
		model.addAttribute("status", status);

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
		MonthlyAttendanceReqDto monthlydto = attendanceService.getMonthlyAttendanceReqByUserIdAndYearMonth(userId, targetYearMonthAtDay);
		model.addAttribute("monthlydto", monthlydto);
		
		// 訂正申請中アコーディオン表示
		LinkedHashMap<LocalDate, String> correctionMessageMap = attendanceService.getCorrectionMessageMap(userId, targetYearMonth);
		model.addAttribute("correctionMessageMap", correctionMessageMap);

		// 却下【訂正理由】表示（勤怠リスト）
//		List<Attendance> attendanceList = attendanceService.findByUserIdAndYearMonth(userId, targetYearMonth);
//		Map<LocalDate, Attendance> attendanceMap = new HashMap<>();
//		for (Attendance attendance : attendanceList) {
//			attendanceMap.put(attendance.getDate(), attendance);
//		}
//		model.addAttribute("attendanceMap", attendanceMap);

		// 却下【訂正理由】表示（訂正リスト）
//		List<AttendanceCorrection> rejectedCorrectionList = attendanceService.findRejectedByUserIdAndYearMonth2(userId, targetYearMonth);
//		model.addAttribute("rejectedCorrectionList", rejectedCorrectionList);
		
		// 却下理由コンテナ表示
		List<AttendanceInfoDto> attendanceAndCorrectionList = attendanceService.getAttendanceInfoList(userId, targetYearMonth, null);
		model.addAttribute("attendanceAndCorrectionList", attendanceAndCorrectionList);

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
			model.addAttribute("statusMap",AttendanceUtil.attendanceStatus);
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
	 * @return
	 */
	@RequestMapping(path = "/attendance/approveRequestComplete", method = RequestMethod.POST)
	public String approveRequest(HttpSession session, RedirectAttributes redirectAttributes) {

		//『表示』ボタン押下時にセッションにセットした対象年月取得
		YearMonth approveYearMonth = (YearMonth) session.getAttribute("yearMonth");
		
		String message = attendanceService.insertMonthlyAttendanceReq(approveYearMonth);

		// リダイレクト先のURL用に取得
		int year = approveYearMonth.getYear();
		int month = approveYearMonth.getMonthValue();
		
		redirectAttributes.addFlashAttribute("message", message);

		return "redirect:/attendance/regist/display?year=" + year + "&month=" + month;
	}
	
	/**
	 * 『月次：承認申請者』リンク押下後
	 * @param id
	 * @param model
	 * @param session
	 * @param attendanceFormList
	 * @return
	 */
	@GetMapping("/attendance/approveRequests/monthlyId={id}")
	public String showApproveRequests(@PathVariable("id") Integer id, Model model, HttpSession session) {
		
		// action == 'monthly'
		model.addAttribute("action", "monthly");
		
		// 月次idからdto取得してsessionにセット（userId, name, date, targetYearMonth, formattedしたやつ）
		MonthlyAttendanceReqDto monthlyDto = attendanceService.getMonthlyDataById(id);
		session.setAttribute("monthlyDto", monthlyDto);
		model.addAttribute("monthlyDto", monthlyDto);
		Integer userId = monthlyDto.getUserId();
		String name = monthlyDto.getName();
		YearMonth formattedYearMonth = monthlyDto.getFormattedYearMonth(); 
		String yearMonthStr = monthlyDto.getYearMonthStr();

		// 月次申請に対応する勤怠データを取得
		List<Attendance> attendanceList = attendanceService.findByUserIdAndYearMonth(userId, formattedYearMonth);
		model.addAttribute("attendanceList", attendanceList);
		
		// 対象年月の祝日を取得
		List<String> holidays = JapaneseHoliday.getHoliday(YearMonth.from(formattedYearMonth));
		System.out.println(YearMonth.from(formattedYearMonth));
		model.addAttribute("holidays", holidays);
		
		// モーダルのメッセージ
		String modalMessage = name + "さんの" + yearMonthStr + "の月次申請を却下しますか？";
		model.addAttribute("modalMessage", modalMessage);
		
		// エラーがあった場合の情報を渡す
		model.addAttribute("openModal", session.getAttribute("openModal"));
		model.addAttribute("modalError", session.getAttribute("modalError"));
		model.addAttribute("errorMessages", session.getAttribute("errorMessages"));
		
	    // セッションにモーダル表示のフラグを設定しない
	    session.removeAttribute("openModal"); // 初期表示でモーダルは開かない
	    session.removeAttribute("modalError");

		return "/attendance/approve";
	}
	
	/**
	 * 『訂正：承認申請者』リンク押下後
	 * @param id
	 * @param correctionForm
	 * @param model
	 * @param session
	 * @return
	 */
	@GetMapping("/attendance/correctionRequests/correctionId={id}")
	public String showCorrectionRequests(@PathVariable("id") Integer id, @ModelAttribute("correctionForm") AttendanceCorrectionForm correctionForm,  
			Model model, HttpSession session) {
		
		// action == 'correction'
		model.addAttribute("action", "correction");
		
		// 訂正idからdto取得してsessionにセット（userId, userName, date, applicationDate, formattedしたやつ）
		AttendanceCorrectionDto correctionDto = attendanceService.getCorrectionDataById(id);
		model.addAttribute("correctionDto", correctionDto);
		session.setAttribute("correctionDto", correctionDto);
		Integer userId = correctionDto.getUserId();
		String userName = correctionDto.getUserName();
		LocalDate date = correctionDto.getDate(); 
		String formattedDate = correctionDto.getFormattedDate();
		
		// 対象日付の勤怠情報と訂正情報を取得(1件)
		AttendanceInfoDto attendanceInfo = attendanceService.getAttendanceWithCorrecton(userId, null, date);
		model.addAttribute("attendanceInfo", attendanceInfo);
		
		// 対象年月の祝日を取得(dateのyyyy-MM-ddをyearMonthのyyyy-MMに変換)
		YearMonth targetYearMonth = YearMonth.of(date.getYear(), date.getMonth());
		List<String> holidays = JapaneseHoliday.getHoliday(targetYearMonth);
		model.addAttribute("holidays", holidays);
		
		// モーダルのメッセージ
		String modalMessage = userName + "さんの" + formattedDate + "の訂正申請を却下しますか？";
		model.addAttribute("modalMessage", modalMessage);
		
		// エラーがあった場合の情報を渡す
		model.addAttribute("openModal", session.getAttribute("openModal"));
		model.addAttribute("modalError", session.getAttribute("modalError"));
		model.addAttribute("errorMessages", session.getAttribute("errorMessages"));
		
	    // セッションにモーダル表示のフラグを設定しない
	    session.removeAttribute("openModal"); // 初期表示でモーダルは開かない
	    session.removeAttribute("modalError");
	    
//		List<AttendanceInfoDto> attendanceAndCorrectionList = attendanceService.getAttendanceInfoList(userId, targetYearMonth, null);
//		model.addAttribute("attendanceAndCorrectionList", attendanceAndCorrectionList);
		// 訂正した勤怠表表示
//		AttendanceCorrection correction = attendanceService.findCorrectionByUserIdAndDate(userId, date);
//		model.addAttribute("correction", correction);
		
		// 訂正前の情報を表示するための処理
//		YearMonth targetYearMonth = YearMonth.of(date.getYear(), date.getMonth()); // yyyy-MMに変換
//		List<Attendance> attendanceList = attendanceService.findByUserIdAndYearMonth(userId, targetYearMonth);
//		model.addAttribute("attendanceList", attendanceList);
		
		return "/attendance/approve";
	}
	
	/**
	 * 月次申請『承認』ボタン押下後
	 * @param redirectAttributes
	 * @param session
	 * @return
	 */
	@PostMapping("/attendance/approveMonthly")
	public String approveMonthly(RedirectAttributes redirectAttributes, HttpSession session) {

		// sessionからmonthlyDtoを取得
		MonthlyAttendanceReqDto monthlyDto = (MonthlyAttendanceReqDto) session.getAttribute("monthlyDto");
		String message = attendanceService.approveMonthly(monthlyDto);

		redirectAttributes.addFlashAttribute("message", message);

		return "redirect:/attendance/approve";
	}

	/**
	 * 月次申請『却下』ボタン押下後
	 * @param monthlyForm
	 * @param result
	 * @param id
	 * @param redirectAttributes
	 * @param session
	 * @return
	 */
	@PostMapping("/attendance/rejectMonthly")
	public String rejectMonthly(@Validated @ModelAttribute("monthlyForm") MonthlyAttendanceReqForm monthlyForm,
			BindingResult result, @RequestParam("id") Integer id, RedirectAttributes redirectAttributes,
			HttpSession session) {
		// sessionからmonthlyDtoを取得
		MonthlyAttendanceReqDto monthlyDto = (MonthlyAttendanceReqDto) session.getAttribute("monthlyDto");

		// エラーメッセージを格納するMap
		Map<String, String> errorMessages = new HashMap<>();

		// フィールドごとのエラーメッセージを収集
		result.getFieldErrors().forEach(error -> {
			errorMessages.put(error.getField(), error.getDefaultMessage());
		});

		// 入力チェック
		if (result.hasErrors()) {
			// セッションにエラー情報を保存
			session.setAttribute("openModal", true); // モーダルを開くフラグ
			session.setAttribute("modalError", "エラーがあります。"); // エラーメッセージ
			session.setAttribute("errorMessages", errorMessages); // フィールドエラーメッセージ

			// リダイレクト時にモーダルを再表示
			redirectAttributes.addFlashAttribute("openModal", true);
			redirectAttributes.addFlashAttribute("modalError", "エラーがあります。");
			redirectAttributes.addFlashAttribute("errorMessages", errorMessages);

			redirectAttributes.addFlashAttribute("action", "monthly");
			redirectAttributes.addFlashAttribute("id", id);

			return "redirect:/attendance/approveRequests/monthlyId=" + id;
		}

		// 却下処理
		String message = attendanceService.rejectMonthly(monthlyDto, monthlyForm.getComment());
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:/attendance/approve";
	}

	/**
	 * 訂正申請『承認』ボタン押下後
	 * @param correctionForm
	 * @param id
	 * @param redirectAttributes
	 * @param session
	 * @return
	 */
	@PostMapping("/attendance/approveCorrection")
	public String approveCorrection(@ModelAttribute("correctionForm") AttendanceCorrectionForm correctionForm,
			@RequestParam("id") Integer id,
			RedirectAttributes redirectAttributes, HttpSession session) {

		AttendanceCorrectionDto correctionDto = (AttendanceCorrectionDto) session.getAttribute("correctionDto");
		String confirmer = sessionHelper.getUser().getName();
		String message = attendanceService.approveCorrection(correctionDto, id, confirmer);
		
		redirectAttributes.addFlashAttribute("message", message);
		
		return "redirect:/attendance/approve";		
	}

	/**
	 * 訂正申請『却下』ボタン押下後
	 * @param correctionForm
	 * @param result
	 * @param action
	 * @param id
	 * @param redirectAttributes
	 * @param session
	 * @return
	 */
	@PostMapping("/attendance/rejectCorrection")
	public String rejectCorrection(
			@Validated(RejectAttendanceCorrectionGroup.class) @ModelAttribute("correctionForm") AttendanceCorrectionForm correctionForm,
			BindingResult result,
			@RequestParam("action") String action, @RequestParam("id") Integer id,
			RedirectAttributes redirectAttributes, HttpSession session) {

		// エラーメッセージを格納するMap
		Map<String, String> errorMessages = new HashMap<>();

		// フィールドごとのエラーメッセージを収集
		result.getFieldErrors().forEach(error -> {
			errorMessages.put(error.getField(), error.getDefaultMessage());
		});

		// 入力チェック
		if (result.hasErrors()) {
			// セッションにエラー情報を保存
			session.setAttribute("openModal", true); // モーダルを開くフラグ
			session.setAttribute("modalError", "エラーがあります。"); // エラーメッセージ
			session.setAttribute("errorMessages", errorMessages); // フィールドエラーメッセージ

			redirectAttributes.addFlashAttribute("correctionForm", correctionForm);
			redirectAttributes.addFlashAttribute("modalError", "エラーがあります。");
			redirectAttributes.addFlashAttribute("openModal", true);
			redirectAttributes.addFlashAttribute("errorMessages", errorMessages);

			redirectAttributes.addFlashAttribute("action", "correction");
			redirectAttributes.addFlashAttribute("id", id);

			return "redirect:/attendance/correctionRequests/correctionId=" + id;
		}

		AttendanceCorrectionDto correctionDto = (AttendanceCorrectionDto) session.getAttribute("correctionDto");
		String confirmer = sessionHelper.getUser().getName();
		String message = attendanceService.rejectCorrection(correctionDto, correctionForm, confirmer);

		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:/attendance/approve";
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
			BindingResult result, HttpSession session, RedirectAttributes redirectAttributes) {

		Map<String, Object> response = new HashMap<>();

		if (result.hasErrors()) {
			response.put("success", false);
			response.put("message", result.getFieldError().getDefaultMessage());
			String errorMessage = "エラーがあります。";
			response.put(errorMessage, errorMessage);
			return ResponseEntity.badRequest().body(response); // エラー時のレスポンス
		}

		// エラーがなければ
		String message = attendanceService.registCorrection(correctionForm);
		response.put("message", message);
		response.put("success", true);
		// リダイレクト先の情報をセッションに格納
		YearMonth targetYearMonth = (YearMonth) session.getAttribute("yearMonth");
		response.put("year", targetYearMonth.getYear());
		response.put("month", targetYearMonth.getMonthValue());
		
		// お知らせ表示
		String userName = sessionHelper.getUser().getName();
	    String correctionDate = correctionForm.getDate();
		informationService.correctionRequestInsertNotifications(userName, correctionDate);
		// メール
		String mailMessage = MessageUtil.mailCommonMessage();
		emailService.sendCorrectionRequestEmail(userName, correctionDate, mailMessage);

		return ResponseEntity.ok(response); // 成功時のレスポンス
	}

	/**
	 * 却下理由【訂正申請】×ボタン押下(却下理由をnullに更新)
	 * @param correctionId
	 * @param session
	 * @return
	 */
	@PostMapping("/attendance/removeRejectedCorrection")
	public String removeRejectedCorrection(@RequestParam("correctionId") Integer correctionId, HttpSession session) {
//		attendanceService.updateRejectFlg(correctionId);
		attendanceService.updateRejectionReason(correctionId);
		
		YearMonth targetYearMonth = (YearMonth) session.getAttribute("yearMonth");
		int year = targetYearMonth.getYear();
		int month = targetYearMonth.getMonthValue();
		
		return "redirect:/attendance/regist/display?year=" + year + "&month=" + month;
	}
	

}
