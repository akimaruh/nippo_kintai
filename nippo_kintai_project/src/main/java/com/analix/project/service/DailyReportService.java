package com.analix.project.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	 * 日報提出ステータス名取得
	 * @param userId
	 * @param targetDate
	 * @return 日報提出ステータス名
	 */
	public String findStatusByUserId(Integer userId, LocalDate targetDate) {

		Integer dailyReportStatus = dailyReportMapper.findStatusByUserIdAndTargetDate(userId, targetDate);
		int result = (dailyReportStatus == null) ? 0 : dailyReportStatus;
		final String statusName = DailyReportUtil.getSubmitStatus(result);
		return statusName;
	}

	/**
	 * 日報取得
	 * @param userId
	 * @param targetDate
	 * @return 日報フォーム(フォーム内が空白でも可)
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
		//日報フォームの作成
		DailyReportForm dailyReportForm = new DailyReportForm();
		dailyReportForm.setDate(dailyReportDto.getDate());
		dailyReportForm.setId(dailyReportDto.getReportId());
		dailyReportForm.setUserId(dailyReportDto.getUserId());
		// 日報詳細フォームリストの初期化
		List<DailyReportDetailForm> dailyReportDetailFormList = new ArrayList<>();
		// 日報詳細DTOリストを取得
		List<DailyReportDetailDto> dailyReportDetailDtoList = dailyReportDto.getDailyReportDetailDtoList();
		// 日報詳細DTOからフォームリストを作成
		if (dailyReportDetailDtoList != null) {
			for (DailyReportDetailDto dailyReportDetailDto : dailyReportDetailDtoList) {
				DailyReportDetailForm dailyReportDetailForm = new DailyReportDetailForm();
				dailyReportDetailForm.setId(dailyReportDetailDto.getReportDetailId());
				dailyReportDetailForm.setUserId(dailyReportDetailDto.getUserId());
				dailyReportDetailForm.setDate(dailyReportDetailDto.getDate());
				dailyReportDetailForm.setWorkId(dailyReportDetailDto.getWorkId());
				dailyReportDetailForm.setTime(dailyReportDetailDto.getTime());
				dailyReportDetailForm.setContent(dailyReportDetailDto.getContent());
				dailyReportDetailFormList.add(dailyReportDetailForm);
			}
		}
		// 日報詳細フォームリストを設定
		dailyReportForm.setDailyReportFormDetailList(dailyReportDetailFormList);
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
			return true;
		} else {
			//登録失敗時はそのままfalseを返す
			System.out.println("日報登録時に登録失敗");
			return false;
		}

	}

	/**
	 * 日報登録
	 * @param dailyReportForm
	 * @return 登録成功ならtrue、失敗ならfalse
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean registDailyReportService(DailyReportForm dailyReportForm) {
		Integer userId = dailyReportForm.getUserId();
		LocalDate targetDate = dailyReportForm.getDate();

		// トランザクションの対象とするデータリスト
		List<DailyReportDetail> insertList = new ArrayList<>();
		List<DailyReportDetail> updateList = new ArrayList<>();
		List<Integer> deleteList = new ArrayList<>();
		DailyReport dailyReport = new DailyReport();

		// 日報マスタの存在チェックと作成・登録
		dailyReport.setUserId(userId);
		dailyReport.setDate(targetDate);
		dailyReport.setStatus(Constants.CODE_VAL_UNSUBMITTED);
		if (!dailyReportMapper.registedDailyReportByTargetDateExistCheck(userId, targetDate)) {
			dailyReportMapper.registDailyReport(dailyReport);
		}
		//処理の最後にステータスを更新するため日報マスタのステータスをセット
		dailyReport.setStatus(Constants.CODE_VAL_BEFORE_SUBMITTED_APPROVAL);

		// 日報詳細データの準備
		for (DailyReportDetailForm dailyReportDetailForm : dailyReportForm.getDailyReportFormDetailList()) {
			Integer workId = dailyReportDetailForm.getWorkId();
			Integer time = dailyReportDetailForm.getTime();
			String content = dailyReportDetailForm.getContent();
			Integer dailyReportDetailId = dailyReportDetailForm.getId();

			// 削除対象チェック
			if (dailyReportDetailId != null && (time == null || content.isEmpty())) {
				deleteList.add(dailyReportDetailId);
				continue;
			}

			// 新規登録または更新対象チェック
			if (time != null && !content.isEmpty()) {
				DailyReportDetail dailyDetailReport = new DailyReportDetail();
				dailyDetailReport.setWorkId(workId);
				dailyDetailReport.setContent(content);
				dailyDetailReport.setTime(time);
				dailyDetailReport.setUserId(userId);
				dailyDetailReport.setDate(targetDate);

				if (dailyReportDetailId == null || dailyReportDetailId == 0) {
					insertList.add(dailyDetailReport);
				} else {
					dailyDetailReport.setId(dailyReportDetailId);
					updateList.add(dailyDetailReport);
				}
			}
		}
		// トランザクションを開始し、一括登録・更新・削除を実行
		if (!deleteList.isEmpty()) {
			dailyReportMapper.deleteDailyReportDetails(deleteList);
		}
		if (!insertList.isEmpty()) {
			dailyReportMapper.batchInsertDailyReportDetails(insertList);
		}
		if (!updateList.isEmpty()) {
			for (DailyReportDetail update : updateList) {
				dailyReportMapper.updateDailyReportDetail(update);
			}
		}
		dailyReportMapper.updateDailyReportStatus(dailyReport);
		return true;
	}

	/***
	 * 日報未提出者リスト作成(バッチ処理用)
	 * @return
	 */
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
	 * 指定日の日報を全件取得してフォームに詰め替え
	 * @param targetDate
	 * @return 指定日の全ユーザ日報フォームリスト
	 */
	public List<DailyReportForm> getDailyReportList(LocalDate targetDate) {
		
		List<DailyReportDto> dailyReportDtoList = dailyReportMapper.getAllDatilReportListByTargetDate(targetDate);
		if (dailyReportDtoList == null || dailyReportDtoList.isEmpty()) {
			return Collections.emptyList();
		}

		//該当日の全ユーザ日報リストフォームを新規作成してDTOから詰め替え
		List<DailyReportForm> dailyReportFormList = new ArrayList<>();

		for (DailyReportDto dailyReportDto : dailyReportDtoList) {
			DailyReportForm dailyReportForm = convertToDailyReportForm(dailyReportDto);
			
			List<DailyReportDetailForm> dailyReportDetailFormList = new ArrayList<>();
			for (DailyReportDetailDto dailyReportDetailDto : dailyReportDto.getDailyReportDetailDtoList()) {
				DailyReportDetailForm dailyReportDetailForm = convertToDailyReportDetailForm(dailyReportDetailDto);
				//日報内容1行文を追加
				dailyReportDetailFormList.add(dailyReportDetailForm);
			}
			//1日分の日報詳細を格納したリストを日報マスタにセット
			dailyReportForm.setDailyReportFormDetailList(dailyReportDetailFormList);
			//ビューへ渡す用のリストに格納
			dailyReportFormList.add(dailyReportForm);
		}
		return dailyReportFormList;
	}

	/**
	 * 日報マスタをDTOからフォームクラスへ詰め替え
	 * @param dailyReportDto
	 * @return DTOを詰め替えた日報フォーム
	 */
	private DailyReportForm convertToDailyReportForm(DailyReportDto dailyReportDto) {
		DailyReportForm dailyReportForm = new DailyReportForm();
		dailyReportForm.setUserId(dailyReportDto.getUserId());
		dailyReportForm.setDate(dailyReportDto.getDate());
		dailyReportForm.setName(dailyReportDto.getUserName());
		dailyReportForm.setStatus(dailyReportDto.getStatus());
		return dailyReportForm;
	}

	/**
	 * 日報詳細テーブルをDTOからフォームクラスへ詰め替え
	 * @param dailyReportDetailDto
	 * @return DTOを詰め替えた日報詳細フォーム
	 */
	private DailyReportDetailForm convertToDailyReportDetailForm(DailyReportDetailDto dailyReportDetailDto) {
		DailyReportDetailForm dailyReportDetailForm = new DailyReportDetailForm();
		dailyReportDetailForm.setWorkId(dailyReportDetailDto.getWorkId());
		dailyReportDetailForm.setWorkName(dailyReportDetailDto.getWorkName());
		dailyReportDetailForm.setTime(dailyReportDetailDto.getTime());
		dailyReportDetailForm.setContent(dailyReportDetailDto.getContent());

		return dailyReportDetailForm;
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
		return dailyReportSummaryDto;

	}

}
