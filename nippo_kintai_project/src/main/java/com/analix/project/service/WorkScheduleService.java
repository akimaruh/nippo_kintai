package com.analix.project.service;

import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
	public boolean saveWorkSchedule(WorkSchedule workSchedule) {
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
	 * 入力チェック
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
			}
			
			// 退勤可能時間が退勤時間より早い場合のエラーチェック
			if (endTime.isBefore(adjustedEndTime)) {
				result.addError(
						new FieldError("workScheduleForm", "endTimeHour", "退勤時間は出勤時間と休憩時間を加えた時間より後に設定してください。"));
			}
		}

	}

}
