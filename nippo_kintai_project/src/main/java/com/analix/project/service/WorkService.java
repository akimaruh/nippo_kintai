package com.analix.project.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.analix.project.dto.UserWorkVisibilityDto;
import com.analix.project.entity.UserWorkVisibility;
import com.analix.project.form.UserWorkVisibilityListForm;
import com.analix.project.mapper.WorkMapper;

@Service
public class WorkService {

	@Autowired
	private WorkMapper workMapper;
	
	/**
	 * 表示設定込みの作業リスト
	 * @param userId
	 * @return
	 */
	public List<UserWorkVisibilityDto> getVisibilityWorkList(Integer userId) {
		return workMapper.findAllWorkByUserId(userId);
	}
	
	/**
	 * 新しい作業名追加
	 * @param workName
	 * @return ture成功 false失敗
	 */
	public boolean insertWork(String workName) {
		return workMapper.registWork(workName);
	}
	
	/**
	 * 同じ作業名があるかどうか確認
	 * @param workName
	 * @return
	 */
	public boolean exisistsByWorkName(String workName) {
		return workMapper.exisistsByWorkName(workName);
	}
	
	/**
	 * 表示設定したデータが存在するかどうか確認
	 * @param userId
	 * @param workId
	 * @return ture存在する false存在しない
	 */
	public boolean exisistsByUserIdAndWorkId(Integer userId, Integer workId) {
		return workMapper.exisistsByUserIdAndWorkId(userId, workId);
	}
	
	/**
	 * 【日報】保存ボタン押下（社員権限）
	 * @param workListForm
	 * @param userId
	 * @return ture成功 false失敗
	 */
	public boolean saveWorkList(UserWorkVisibilityListForm workListForm, Integer userId) {

        if (workListForm.getWorkVisibilityList() == null) {
            workListForm.setWorkVisibilityList(new ArrayList<>());
        }

        for (UserWorkVisibilityDto dto : workListForm.getWorkVisibilityList()) {
            UserWorkVisibility work = new UserWorkVisibility();
            work.setUserId(userId);
            work.setWorkId(dto.getWorkId());
            work.setIsVisible(dto.getIsVisible() == null ? 0 : dto.getIsVisible());
            work.setCreatedAt(LocalDate.now());
            work.setUpdatedAt(LocalDate.now());

            // 既存データがあれば更新、なければ登録
            if (!saveOrUpdateWork(work)) {
                return false;
            }
        }
        return true;
    }
	
	/**
	 * 保存時に既存データがあれば更新、なければ登録
	 * @param work
	 * @return true成功 false失敗
	 */
	@Transactional
	public boolean saveOrUpdateWork (UserWorkVisibility work) {
		boolean isSuccess;
		if (workMapper.exisistsByUserIdAndWorkId(work.getUserId(), work.getWorkId())) {
			isSuccess = workMapper.updateWorkVisibility(work);
		} else {
			isSuccess = workMapper.insertWorkVisibility(work);
		}
		return isSuccess;
	}
	
	/**
	 * 登録時入力チェック(1件登録)
	 * @param workName
	 * @return エラーメッセージ
	 */
	public String validateWorkName(String workName) {
		if(workName == null || workName.isEmpty()) {
			return "作業名を入力してください。";
		}
		
		if (workName.length() > 10) {
			return "作業名は10文字以内で入力してください。";
		}
		
		if (exisistsByWorkName(workName)) {
			return "作業名が既に存在します。";
		}
		return null;
	}
}
