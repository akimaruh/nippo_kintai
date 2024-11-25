package com.analix.project.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.analix.project.entity.UserNotificationRequest;
import com.analix.project.entity.UserSearchRequest;
import com.analix.project.entity.Users;
import com.analix.project.service.DailyReportService;
import com.analix.project.service.InformationService;
import com.analix.project.service.UserService;

//@CrossOrigin(origins = "http://localhost:8080")
@CrossOrigin("*")
@RestController
public class RequestController {

	@Autowired
	private InformationService informationService;
	@Autowired
	private UserService userService;
	@Autowired
	private DailyReportService dailyReportService;

	@PostMapping(path = "/notifications/markAsRead")
	public ResponseEntity<String> markAsRead(@RequestBody UserNotificationRequest request) {
		System.out.println("Received request:" + request.getNotificationId() + ", " + request.getUserId());

		try {
			informationService.markAsRead(request.getNotificationId(), request.getUserId());
			return ResponseEntity.ok().body("既読処理が完了しました");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("既読処理に失敗しました");
		}
	}

	//		try {
	//            informationService.markAsRead(request.getNotificationId(), request.getUserId());
	//            return ResponseEntity.ok().build(); // 成功レスポンス
	//        } catch (Exception e) {
	//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("既読処理に失敗しました");
	//        }
	//    }
	/**
	 * 出力対象ユーザーの検索
	 * @param request
	 * @return 出力する元データ
	 */
	@PostMapping(path = "/output/userSearch")
	public Map<Integer, Users> searchForOutputUser(@RequestBody UserSearchRequest request) {
		Map<Integer, Users> userNameAndEmployeeCodeMap = userService.searchForUserNameAndEmployeeCode(request.getUserKeyword());
		return userNameAndEmployeeCodeMap;

	}

//	@PostMapping(path = "/startMenu/dailyReport/submitComplete")
//	public ResponseEntity<String> submitDailyReportAtStartMenu(
//			@RequestBody @Validated(StartMenuDailyReportGroup.class) @ModelAttribute("dailyReportDetailForm") DailyReportDetailForm dailyReportdetailForm,
//			BindingResult result, Model model, RedirectAttributes redirectAttributes) {
//		System.out.println("submitDailyReportAtStartMenu通過");
//		//入力チェック(お知らせ見える版)
//		if (result.hasErrors()) {
//			System.out.println(dailyReportdetailForm);
//			redirectAttributes.addFlashAttribute("dailyReportDetailForm", dailyReportdetailForm);
//			redirectAttributes.addFlashAttribute("modalError", "日報の登録が失敗しました。");
//
//			model.addAttribute("result", result);
//			model.addAttribute("modalError", "日報の登録が失敗しました。");
//			//再度モーダルを開きエラー内容表示
//			System.out.println("入力チェックエラーあり");
//			System.out.println(result.getAllErrors());
//			return  ResponseEntity.ok().body("既読処理が完了しました")
//					.created(redirect:/common/startMenu);
//		}
//		//本日の日付を取得
//		LocalDate today = LocalDate.now();
//		dailyReportdetailForm.setDate(today);
//		//通常の日報登録方法用に変換＆日報登録
//		boolean isRegistAtStartMenu = dailyReportService.registDailyReportAtStartMenu(dailyReportdetailForm);
//
//		if (isRegistAtStartMenu) {
//			redirectAttributes.addFlashAttribute("reportMessage", "日報の登録が完了しました。");
//		} else {
//			redirectAttributes.addFlashAttribute("reportError", "日報の登録が失敗しました。");
//		}
//		System.out.println("提出完了");
//		return "redirect:/common/startMenu";
//
//	}
}
