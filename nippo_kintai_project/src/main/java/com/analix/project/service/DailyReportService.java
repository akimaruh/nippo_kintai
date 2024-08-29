package com.analix.project.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analix.project.dto.DailyReportDetailDto;
import com.analix.project.dto.DailyReportDto;
import com.analix.project.entity.DailyReport;
import com.analix.project.entity.DailyReportDetail;
import com.analix.project.form.DailyReportDetailForm;
import com.analix.project.form.DailyReportForm;
import com.analix.project.mapper.DailyReportMapper;
import com.analix.project.util.Constants;
import com.analix.project.util.DailyReportUtil;

@Service
public class DailyReportService {

	@Autowired
	private DailyReportMapper dailyReportMapper;

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
		dailyReportForm.setId(dailyReportDto.getId());
		dailyReportForm.setUserId(dailyReportDto.getUserId());
		//日報詳細テーブル部分
		//ここから
		List<DailyReportDetailForm> dailyReportDetailFormList = new ArrayList<>();
		System.out.println(dailyReportDto);
		System.out.println(dailyReportForm);
		if (dailyReportDetailDtoList != null) {

			for (DailyReportDetailDto dailyReportDetailDto : dailyReportDetailDtoList) {
				DailyReportDetailForm dailyReportDetailForm = new DailyReportDetailForm();
				dailyReportDetailForm.setId(dailyReportDetailDto.getId());
				dailyReportDetailForm.setUserId(dailyReportDetailDto.getUserId());
				dailyReportDetailForm.setDate(dailyReportDetailDto.getDate());
				dailyReportDetailForm.setTime(dailyReportDetailDto.getTime());
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
	 * 入力チェック
	 * @param submittedDailyReportForm
	 */
//	public void validationForm(DailyReportForm dailyReportForm) {
//		//いらない？？
//	}
	
	/**
	 * 日報登録
	 * @param dailyReportForm
	 * @return
	 */
	public String registDailyReportService(DailyReportForm dailyReportForm) {
		boolean isRegistCheck = false;
		//詰め替えと追加
		Integer userId = dailyReportForm.getUserId();
		LocalDate targetDate = dailyReportForm.getDate();

		System.out.println(dailyReportForm.getDailyReportFormDetailList());
		
		//日報マスタが既に存在するか確認
		int count = dailyReportMapper.countRegistedDailyReportByTargetDate(userId, targetDate);
		boolean dailyReportExistsFlg = (count > 0);
		//日報マスタが存在しなければ作成する
		if (dailyReportExistsFlg==false) {
			System.out.println("日報テーブル登録開始");
			DailyReport dailyReport = new DailyReport();
			dailyReport.setUserId(userId);
			dailyReport.setDate(targetDate);
			dailyReport.setStatus(Constants.CODE_VAL_UNSUBMITTED);//未提出
			System.out.println("登録処理寸前：" + dailyReport);
			dailyReportMapper.registDailyReport(dailyReport);
			System.out.println("日報テーブル登録完了");
		}

		for (DailyReportDetailForm dailyReportDetailForm : dailyReportForm.getDailyReportFormDetailList()) {
			System.out.println("日報詳細テーブル準備開始" + dailyReportDetailForm.getTime());
			Integer time = dailyReportDetailForm.getTime();
			String content = dailyReportDetailForm.getContent();
			Integer dailyReportDetailId = dailyReportDetailForm.getId();

			if (time != null && (content != null || content != "")) {
				DailyReportDetail dailyDetailReport = new DailyReportDetail();
				dailyDetailReport.setContent(content);
				dailyDetailReport.setTime(time);
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
			return "登録が完了しました";
		} else {
			System.out.println("登録失敗");
			return "登録失敗";
		}
	}

	/**
	 * 日報ステータスを提出済承認前に変更
	 * @param dailyReportForm
	 */
	public void updateDailyReportStatus(DailyReportForm dailyReportForm) {
		DailyReport dailyReport = new DailyReport();
		dailyReport.setUserId(dailyReportForm.getUserId());
		dailyReport.setDate(dailyReportForm.getDate());
		dailyReport.setStatus(Constants.CODE_VAL_BEFORE_SUBMITTED_APPROVAL);
		dailyReportMapper.updateDailyReportStatus(dailyReport);
	}

}
