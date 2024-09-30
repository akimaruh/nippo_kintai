package com.analix.project.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analix.project.dto.MonthlyAttendanceReqDto;
import com.analix.project.entity.Attendance;
import com.analix.project.entity.Notifications;
import com.analix.project.entity.Users;
import com.analix.project.form.AttendanceFormList;
import com.analix.project.service.AttendanceService;
import com.analix.project.service.EmailService;
import com.analix.project.service.InformationService;
import com.analix.project.service.WebPushService;

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

		//バラバラに取得した年と月を合体(yyyy-MM)
		YearMonth targetYearMonth = YearMonth.of(year, month);
		session.setAttribute("yearMonth", targetYearMonth);
		//月次勤怠テーブルは対象年月をyyyy-MM-ddで格納しているためyyyy-MM-01の形になるように整形
		LocalDate targetYearMonthAtDay = targetYearMonth.atDay(1);

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
			emailService.sendRequestEmail(request);
		}
		informationService.approveRequestInsertNotifications(user.getName(), approveYearMonth);
		redirectAttributes.addFlashAttribute("message", message);
// プッシュ通知
		try {
			String payload = "{\"title\":\"【日報勤怠アプリ】\",\"body\":\"申請があります\"}";
			webPushService.sendRequestPush(payload);
		} catch (GeneralSecurityException | IOException | JoseException e) {
			e.printStackTrace();
		}

		return "redirect:/attendance/regist/display?year=" + year + "&month=" + month;
	}

	/**
	 * 『承認申請者』リンク押下後
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
			Model model, HttpSession session,
			AttendanceFormList attendanceFormList) {
		System.out.println(targetYearMonthAtDay);
		YearMonth targetYearMonth = YearMonth.of(targetYearMonthAtDay.getYear(), targetYearMonthAtDay.getMonthValue());
		session.setAttribute("targetYearMonth", targetYearMonth);

		//		String yearMonth = targetYearMonth.substring(0, 7); // String型に変換　/2024-01/-01
		//		LocalDate ldYearMonth = LocalDate.parse(yearMonth);
		//		System.out.println(ldYearMonth);

		List<Attendance> attendanceList = attendanceService.findByUserIdAndYearMonth(userId, targetYearMonth);

		model.addAttribute("attendanceList", attendanceList);
		model.addAttribute("targetYearMonth", targetYearMonth);

		model.addAttribute("name", name);
		model.addAttribute("yearMonth", targetYearMonth);
		model.addAttribute("date", date);

		return "/attendance/regist";
	}

	/**
	 * 『承認』『却下』ボタン押下後
	 * @param userId
	 * @param targetYearMonth
	 * @param action
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/attendance/update")
	public String updateStatus(@RequestParam("userId") Integer userId,
			@RequestParam("targetYearMonth") YearMonth targetYearMonth, @RequestParam("action") String action,
			RedirectAttributes redirectAttributes) {
		LocalDate targetYearMonthAtDay = targetYearMonth.atDay(1);
		
		
		
//		List<MonthlyAttendanceReqDto> requests = attendanceService.getMonthlyAttendanceReqByUserId(userId,
//				targetYearMonthAtDay);

//		for (MonthlyAttendanceReqDto request : requests) {

			if ("approve".equals(action)) {
				//				attendanceService.updateStatusApprove(userId, targetYearMonth);
				String message = attendanceService.updateStatusApprove(userId, targetYearMonthAtDay);
				emailService.sendApproveEmail(userId,targetYearMonth);
				redirectAttributes.addFlashAttribute("message", message);
				informationService.approveInsertNotifications(userId, targetYearMonth);
//				System.out.println("Sending approve request: " + request);
				try {
					webPushService.sendApprovePush(userId);
					System.out.println("承認:通知が正常に送信されました！");
				} catch (GeneralSecurityException | IOException | JoseException e) {
					System.out.println("承認:通知送信中にエラーが発生しました: " + e.getMessage());
					e.printStackTrace();
				}
				

			} else if ("reject".equals(action)) {
				//				attendanceService.updateStatusReject(userId, targetYearMonth);
				String message = attendanceService.updateStatusReject(userId, targetYearMonthAtDay);
				emailService.sendRejectEmail(userId,targetYearMonth);
				redirectAttributes.addFlashAttribute("message", message);
				informationService.rejectInsertNotifications(userId, targetYearMonth);
//				System.out.println("Sending reject request: " + request);
				try {
					webPushService.sendRejectPush(userId);
				} catch (GeneralSecurityException | IOException | JoseException e) {
					e.printStackTrace();
				}
				
			}
//		}

		return "redirect:/attendance/regist";
	}
//	テストボタン押下
	@PostMapping("/attendance/test")
	public ResponseEntity<Notifications> testBtn(HttpSession session) {
	    Users user = (Users) session.getAttribute("loginUser");
	    Integer userId = (Integer) user.getId();
	    Notifications data = new Notifications();
 
	    try {
	        webPushService.sendTestPush(userId);
	        data.setTitle("テストテストコントローラ");
	        data.setMessage("テスト通知です");
	    } catch (GeneralSecurityException | IOException | JoseException e) {
	        e.printStackTrace();
	        data.setTitle("エラー");
	        data.setMessage("通知の送信中にエラーが発生しました");
	    }
 
	    return ResponseEntity.ok(data);
	}

}
