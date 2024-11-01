package com.analix.project.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analix.project.dto.DailyReportDetailDto;
import com.analix.project.dto.DailyReportDto;
import com.analix.project.dto.DailyReportSummaryDto;
import com.analix.project.entity.DailyReport;
import com.analix.project.entity.DailyReportDetail;
import com.analix.project.entity.Users;
import com.analix.project.entity.Work;
import com.analix.project.form.DailyReportDetailForm;
import com.analix.project.form.DailyReportForm;
import com.analix.project.mapper.DailyReportMapper;
import com.analix.project.mapper.WorkMapper;
import com.analix.project.util.Constants;
import com.analix.project.util.DailyReportUtil;

@Service
public class DailyReportService {

	@Autowired
	private DailyReportMapper dailyReportMapper;

	@Autowired
	private WorkMapper workMapper;

	/**
	 * ステータス名取得
	 * @param userId
	 * @param targetDate
	 * @return ステータス名
	 */
	public String findStatusByUserId(Integer userId, LocalDate targetDate) {
		//		Map<Integer, String> dailyReportStatusMap = new LinkedHashMap<>();
		//		dailyReportStatusMap.put(0, "未提出");
		//		dailyReportStatusMap.put(1, "提出済承認前");
		//		dailyReportStatusMap.put(2, "承認済");
		System.out.println(targetDate);

		Integer dailyReportStatus = dailyReportMapper.findStatusByUserIdAndTargetDate(userId, targetDate);
		System.out.println(dailyReportStatus);
		int result = (dailyReportStatus == null) ? 0 : dailyReportStatus;
		final String statusName = DailyReportUtil.getSubmitStatus(result);
		return statusName;
	}

	/**
	 * 日報取得
	 * @param userId
	 * @param targetDate
	 * @return
	 */

	public DailyReportForm getDailyReport(Integer userId, LocalDate targetDate) {
		//Dtoを取得
		DailyReportDto dailyReportDto = dailyReportMapper.findAllDailyReportByUserIdAndTargetDate(userId, targetDate);
		//該当日で最初に日報を登録する場合
		if (dailyReportDto == null) {
			DailyReportForm dailyReportForm = new DailyReportForm();
			dailyReportForm.setDailyReportFormDetailList(new ArrayList<>());
			return dailyReportForm;
		}
		//該当日が既に登録済みの場合
		List<DailyReportDetailDto> dailyReportDetailDtoList = dailyReportDto.getDailyReportDetailDtoList();

		//Formを作成してDtoの中身を詰め替える
		DailyReportForm dailyReportForm = new DailyReportForm();
		//日報テーブル部分
		dailyReportForm.setDate(dailyReportDto.getDate());
		dailyReportForm.setId(dailyReportDto.getReportId());
		dailyReportForm.setUserId(dailyReportDto.getUserId());
		//日報詳細テーブル部分
		//ここから
		List<DailyReportDetailForm> dailyReportDetailFormList = new ArrayList<>();
		System.out.println(dailyReportDto);
		System.out.println(dailyReportForm);
		if (dailyReportDetailDtoList != null) {

			for (DailyReportDetailDto dailyReportDetailDto : dailyReportDetailDtoList) {
				DailyReportDetailForm dailyReportDetailForm = new DailyReportDetailForm();
				dailyReportDetailForm.setId(dailyReportDetailDto.getReportDetailId());
				dailyReportDetailForm.setUserId(dailyReportDetailDto.getUserId());
				dailyReportDetailForm.setDate(dailyReportDetailDto.getDate());
				dailyReportDetailForm.setWorkId(dailyReportDetailDto.getWorkId());
				//timeはフォームクラスに格納するときにString型にする
				dailyReportDetailForm.setTime(dailyReportDetailDto.getTime().toString());
				dailyReportDetailForm.setContent(dailyReportDetailDto.getContent());
				dailyReportDetailFormList.add(dailyReportDetailForm);
				dailyReportForm.setDailyReportFormDetailList(dailyReportDetailFormList);
				System.out.println(dailyReportForm);
			}

		}
		System.out.println(dailyReportForm);
		//ここまで
		return dailyReportForm;

	}

	/**
	 * 処理メニューから日報登録
	 * @param dailyReportDetailForm
	 */
	public boolean registDailyReportAtStartMenu(DailyReportDetailForm dailyReportDetailForm) {
		DailyReportForm dailyReportForm = new DailyReportForm();
		List<DailyReportDetailForm> dailyReportDetailFormList = new ArrayList<>();
		dailyReportDetailFormList.add(dailyReportDetailForm);
		dailyReportForm.setDailyReportFormDetailList(dailyReportDetailFormList);
		dailyReportForm.setDate(dailyReportDetailForm.getDate());
		dailyReportForm.setUserId(dailyReportDetailForm.getUserId());
		//日報登録メソッドへ移動 登録成否を返す
		boolean isRegistCheck = registDailyReportService(dailyReportForm);
		if (isRegistCheck) {
			//登録が成功したら日報テーブルのステータスを提出済承認前に変更
			return updateDailyReportStatus(dailyReportForm);
		} else {
			//登録失敗時はそのままfalseを返す
			System.out.println("日報登録時に登録失敗");
			return false;
		}

	}

	/**
	 * 日報登録
	 * @param dailyReportForm
	 * @return
	 */
	public boolean registDailyReportService(DailyReportForm dailyReportForm) {
		boolean isRegistCheck = false;
		//詰め替えと追加
		Integer userId = dailyReportForm.getUserId();
		LocalDate targetDate = dailyReportForm.getDate();

		System.out.println(dailyReportForm.getDailyReportFormDetailList());

		//日報マスタが既に存在するか確認
		boolean dailyReportExistsFlg = dailyReportMapper.registedDailyReportByTargetDateExistCheck(userId, targetDate);
		//日報マスタが存在しなければ作成する
		if (dailyReportExistsFlg == false) {
			System.out.println("日報テーブル登録開始");
			DailyReport dailyReport = new DailyReport();
			dailyReport.setUserId(userId);
			dailyReport.setDate(targetDate);
			dailyReport.setStatus(Constants.CODE_VAL_UNSUBMITTED);//未提出
			System.out.println("登録処理寸前：" + dailyReport);
			dailyReportMapper.registDailyReport(dailyReport);
			System.out.println("日報テーブル登録完了");
		}
		//日報詳細を取り出して処理方法の判別
		for (DailyReportDetailForm dailyReportDetailForm : dailyReportForm.getDailyReportFormDetailList()) {
			System.out.println("日報詳細テーブル準備開始" + dailyReportDetailForm);
			Integer workId = dailyReportDetailForm.getWorkId();
			String time = dailyReportDetailForm.getTime();
			String content = dailyReportDetailForm.getContent();
			Integer dailyReportDetailId = dailyReportDetailForm.getId();

			//日報詳細idがあり、作業時間がnullかつ作業内容が空欄の場合削除処理を行う
			if (dailyReportDetailId != null && (time == null && content == "")) {
				System.out.println("削除処理開始");
				isRegistCheck = dailyReportMapper.deleteDailyReportDetail(dailyReportDetailId);
				continue;
			}
			//時間と作業内容がnullまたは空欄でない行のみ登録または更新
			if (time != null && (content != null || content != "")) {
				DailyReportDetail dailyDetailReport = new DailyReportDetail();
				dailyDetailReport.setWorkId(workId);
				dailyDetailReport.setContent(content);
				//timeはエンティティに格納するときにInteger型にする
				dailyDetailReport.setTime(Integer.parseInt(time));
				dailyDetailReport.setUserId(dailyReportForm.getUserId());
				dailyDetailReport.setDate(dailyReportForm.getDate());
				System.out.println("登録処理寸前：" + dailyDetailReport);

				//登録処理
				if (dailyReportDetailId == null || dailyReportDetailId == 0) {
					System.out.println("登録処理開始");
					isRegistCheck = dailyReportMapper.registDailyReportDetail(dailyDetailReport);

					//更新処理
				} else {
					System.out.println("更新処理開始");
					//日報修正機能が今後追加されるなら別メソッドにした方がいいのか…？
					dailyDetailReport.setId(dailyReportDetailId);
					System.out.println("更新処理寸前：" + dailyDetailReport);
					isRegistCheck = dailyReportMapper.updateDailyReportDetail(dailyDetailReport);

				}
				continue;
			}

		}
		if (isRegistCheck == true) {
			System.out.println("登録完了");
			return true;
		} else {
			System.out.println("登録失敗");
			return false;
		}
	}

	/**
	 * 日報ステータスを提出済承認前に変更
	 * @param dailyReportForm
	 */
	public boolean updateDailyReportStatus(DailyReportForm dailyReportForm) {
		DailyReport dailyReport = new DailyReport();
		dailyReport.setUserId(dailyReportForm.getUserId());
		dailyReport.setDate(dailyReportForm.getDate());
		dailyReport.setStatus(Constants.CODE_VAL_BEFORE_SUBMITTED_APPROVAL);
		return dailyReportMapper.updateDailyReportStatus(dailyReport);
	}

	//ステータスを取得する
	public List<Users> registCheck() {
		LocalDate today = LocalDate.now();
		List<Users> unSubmitterList = dailyReportMapper.dailyReportUnsubmittedPersonList(today);
		return unSubmitterList;

	}

	/**
	 * 作業入力プルダウン用リスト
	 * @return 作業リスト
	 */
	public Map<String, Integer> pulldownWork() {
		List<Work> workList = workMapper.findAllWorkName();
		Map<String, Integer> workMap = new LinkedHashMap<>();

		for (Work row : workList) {
			String workName = row.getWorkName();
			Integer workId = row.getWorkId();
			workMap.put(workName, workId);
		}

		return workMap;

	}

	/**
	 * 該当日日報全件取得してフォームに詰め替え
	 * @param targetDate
	 * @return 該当日の全ユーザ日報リスト
	 */
	public List<DailyReportForm> getDailyReportList(LocalDate targetDate) {
		//データベースから該当日のユーザの日報内容を取得
		List<DailyReportDto> dailyReportDtoList = dailyReportMapper.getAllDatilReportListByTargetDate(targetDate);
		//該当日の全ユーザ日報リストフォームを新規作成してDTOから詰め替え
		List<DailyReportForm> dailyReportFormList = new ArrayList<>();

		for (DailyReportDto dailyReportDto : dailyReportDtoList) {
			DailyReportForm dailyReportForm = new DailyReportForm();
			dailyReportForm.setUserId(dailyReportDto.getUserId());
			dailyReportForm.setDate(targetDate);
			dailyReportForm.setName(dailyReportDto.getUserName());
			dailyReportForm.setStatus(dailyReportDto.getStatus());
			List<DailyReportDetailForm> dailyReportDetailFormList = new ArrayList<>();
			for (DailyReportDetailDto dailyReportDetailDto : dailyReportDto.getDailyReportDetailDtoList()) {
				DailyReportDetailForm dailyReportDetailForm = new DailyReportDetailForm();
				dailyReportDetailForm.setWorkId(dailyReportDetailDto.getWorkId());
				dailyReportDetailForm.setWorkName(dailyReportDetailDto.getWorkName());
				//timeはフォームクラスに格納するときにString型にする
				dailyReportDetailForm.setTime(dailyReportDetailDto.getTime().toString());
				dailyReportDetailForm.setContent(dailyReportDetailDto.getContent());
				//日報内容一行目を追加
				dailyReportDetailFormList.add(dailyReportDetailForm);
			}
			//ユーザの一日分の日報内容をセット
			dailyReportForm.setDailyReportFormDetailList(dailyReportDetailFormList);
			//n人目のユーザの日報を全ユーザ日報リストに追加
			dailyReportFormList.add(dailyReportForm);
		}
		return dailyReportFormList;
	}

	/**
	 * 日報承認ボタン押下後
	 * @param userId
	 * @param targetDate
	 * @return
	 */
	public boolean approveDailyReport(Integer userId, LocalDate targetDate) {
		DailyReport dailyReport = new DailyReport();
		dailyReport.setUserId(userId);
		dailyReport.setDate(targetDate);
		dailyReport.setStatus(Constants.CODE_VAL_APPROVED);
		System.out.println(dailyReport);
		return dailyReportMapper.updateDailyReportStatus(dailyReport);
	}

	/**
	 * 帳票出力用１か月の日報取得
	 * @param userId
	 * @param targetYearMonth
	 * @return
	 */
	public List<DailyReportDto> getDailyReportListForOutput(Integer userId, YearMonth targetYearMonth) {
		List<DailyReportDto> dailyReportDtoList = dailyReportMapper.dailyReportListForAMonth(userId, targetYearMonth);
		List<Map<String, Object>> timePerDayMapList = dailyReportMapper.getTimePerDate(userId, targetYearMonth);

		System.out.println("dailyReportDtoList:" + dailyReportDtoList);
		System.out.println("timePerDayMapList:" + timePerDayMapList);
		Map<LocalDate, Integer> timeMap = timePerDayMapList.stream()
				.collect(Collectors.toMap(
						map -> LocalDate.parse(map.get("date").toString()), // dateをLocalDateに変換
						map -> ((BigDecimal) map.get("total_time")).intValue(), // total_timeをIntegerとして取得
						Integer::sum // 重複キーの合計を取る
				));
		// dailyReportDtoListをStreamを使って処理
		return dailyReportDtoList.stream()
				.peek(report -> {
					// 日付に基づいて作業時間を設定
					Integer workTime = timeMap.get(report.getDate());
					if (workTime != null) {
						report.setTimePerDay(workTime);
					}
				})
				.collect(Collectors.toList()); // 統合リストを作成
	}

	/**
	 * 帳票出力用１か月の総作業時間取得
	 * @param userId
	 * @param targetYearMonth
	 * @return
	 */
	public DailyReportSummaryDto getMonthlyDailyReportSummary(Integer userId, YearMonth targetYearMonth) {
		DailyReportSummaryDto dailyReportSummaryDto = new DailyReportSummaryDto();
		dailyReportSummaryDto.setTimePerMonth(dailyReportMapper.getTimePerMonth(userId, targetYearMonth));
		List<Map<String, Object>> workTimeByProcessMapList = dailyReportMapper.getWorkTimeByProcess(userId,
				targetYearMonth);
		Map<String, Integer> workTimeByProcessMap = workTimeByProcessMapList.stream()
				.collect(Collectors.toMap(
						map -> map.get("workName").toString(),
						map -> ((BigDecimal) map.get("time")).intValue(),
						Integer::sum));
		dailyReportSummaryDto.setWorkTimeByProcessMapList(workTimeByProcessMap);
		System.out.println("dailyReportSummaryDto:" + dailyReportSummaryDto);
		return dailyReportSummaryDto;

	}

}
