package com.analix.project.service;

import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.analix.project.entity.WorkSchedule;
import com.analix.project.form.WorkScheduleForm;
import com.analix.project.mapper.WorkScheduleMapper;
import com.analix.project.util.SessionHelper;

@Service
public class WorkScheduleService {

	@Autowired
	SessionHelper sessionHelper;
	@Autowired
	private WorkScheduleMapper workScheduleMapper;
	@Autowired
	private WorkScheduleCacheService workScheduleCacheService;

	/**
	 * データが存在するかどうか確認
	 * @param userId
	 * @return ture存在する false存在しない
	 */
	public boolean existsByUserId(Integer userId) {
		return workScheduleMapper.exisistsByUserId(userId);
	}

	/**
	 * 新しいデータを登録
	 * @param workSchedule
	 * @return ture成功 false失敗
	 */
	public boolean insertWorkSchedule(WorkSchedule workSchedule) {
		return workScheduleMapper.insertWorkSchedule(workSchedule);
	}

	/**
	 * 既存データを更新
	 * @param workSchedule
	 * @return ture成功 false失敗
	 */
	public boolean updateWorkSchedule(WorkSchedule workSchedule) {
		return workScheduleMapper.updateWorkSchedule(workSchedule);
	}

	/**
	 * 設定したデータ取得(form)
	 * @param userId
	 * @return 存在する場合formを返す
	 * 存在しない場合nullを返す
	 */
	public WorkScheduleForm getWorkSchedule(Integer userId) {
		WorkSchedule workSchedules = workScheduleMapper.findWorkSchedule(userId);

		if (workSchedules != null) {
			// entityからformに入れなおし
			WorkScheduleForm form = new WorkScheduleForm();
			form.setUserId(workSchedules.getUserId());
			form.setStartTimeHour(workSchedules.getStartTime().getHour());
			form.setStartTimeMinute(workSchedules.getStartTime().getMinute());
			form.setEndTimeHour(workSchedules.getEndTime().getHour());
			form.setEndTimeMinute(workSchedules.getEndTime().getMinute());
			form.setBreakTime(workSchedules.getBreakTime());

			return form;
		}
		return null;
	}

	/**
	 * 設定したデータ取得(entity)
	 * @param userId
	 * @return 存在する場合entityを返す
	 */
	public WorkSchedule getWorkScheduleEntity(Integer userId) {
		return workScheduleMapper.findWorkSchedule(userId);
	}
	
	/**
	 * 保存ボタン押下
	 * @param workScheduleForm
	 * @return ture成功 false失敗
	 */
	public boolean saveWorkSchedule(WorkScheduleForm workScheduleForm) {

		// Integer型のHourとMinuteをLocalTime型に変換
		LocalTime formattedStartTime = LocalTime.of(workScheduleForm.getStartTimeHour(),
				workScheduleForm.getStartTimeMinute());
		LocalTime formattedEndTime = LocalTime.of(workScheduleForm.getEndTimeHour(),
				workScheduleForm.getEndTimeMinute());

		// formからentityに入れなおし
		WorkSchedule schedule = new WorkSchedule();
		schedule.setUserId(workScheduleForm.getUserId());
		schedule.setStartTime(formattedStartTime);
		schedule.setEndTime(formattedEndTime);
		schedule.setBreakTime(workScheduleForm.getBreakTime());
		
		boolean isSuccsess = saveOrUpdateSchedule(schedule);

		return isSuccsess;

	}
	
	//既存データがあれば更新、なければ登録
	@Transactional
	public boolean saveOrUpdateSchedule(WorkSchedule schedule) {
		boolean isSuccess;
		if (existsByUserId(schedule.getUserId())) {
			isSuccess = updateWorkSchedule(schedule);
		} else {
			isSuccess = insertWorkSchedule(schedule);
		}
		
		// 成功していたらキャッシュの更新もする
		if (isSuccess) {
			workScheduleCacheService.put(schedule.getUserId(), schedule);
		}
		
		return isSuccess;
	}
	

	/**
	 * 保存時入力チェック
	 * @param workScheduleForm
	 * @param result
	 */
	public void validationForm(WorkScheduleForm workScheduleForm, BindingResult result) {

		if (!result.hasErrors()) {
			// 出勤時間
			LocalTime startTime = LocalTime.of(workScheduleForm.getStartTimeHour(),
					workScheduleForm.getStartTimeMinute());
			// 退勤時間
			LocalTime endTime = LocalTime.of(workScheduleForm.getEndTimeHour(), workScheduleForm.getEndTimeMinute());
			// 休憩時間
			String breakTimeStr = workScheduleForm.getBreakTime();
			Integer breakTime = Integer.parseInt(breakTimeStr);
			// 出勤時間+休憩時間の最終退勤可能時間
			LocalTime adjustedEndTime = startTime.plusMinutes(breakTime);

			// 出勤時間が退勤時間より後または同じになっている場合のエラーチェック
			if (!startTime.isBefore(endTime)) {
				result.addError(
						new FieldError("workScheduleForm", "startTimeHour", "出勤時間は退勤時間より先になるように入力してください。"));

				result.addError(
						new FieldError("workScheduleForm", "endTimeHour", "退勤時間は出勤時間より後になるように入力してください。"));
			} else {
				// 出勤時間が退勤時間より早い場合、退勤可能時間のエラーチェック
				if (endTime.isBefore(adjustedEndTime)) {
					result.addError(
							new FieldError("workScheduleForm", "endTimeHour", "退勤時間は出勤時間と休憩時間を加えた時間より後に設定してください。"));
				}
			}
		}
	}

}
