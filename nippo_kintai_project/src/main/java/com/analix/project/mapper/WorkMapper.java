package com.analix.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.analix.project.dto.UserWorkVisibilityDto;
import com.analix.project.entity.UserWorkVisibility;
import com.analix.project.entity.Work;

@Mapper
public interface WorkMapper {
	/**
	 * 作業名リスト
	 * @return
	 */
	public List<Work> findAllWorkName();
	
	/**
	 * 作業内容追加
	 * @param workName
	 * @return ture成功 false失敗
	 */
	public boolean registWork(@Param("workName") String workName);
	
	/**
	 * 同じ作業名があるか存在チェック
	 * @param workName
	 * @return ture存在する false存在しない
	 */
	public boolean exisistsByWorkName(@Param("workName") String workName);
	
	/**
	 * 表示設定込みの作業リスト
	 * @param userId
	 * @return
	 */
	public List<UserWorkVisibilityDto> findAllWorkByUserId(@Param("userId") Integer userId);
	
	/**
	 * [表示]作業リスト
	 * @param userId
	 * @return
	 */
	public List<UserWorkVisibilityDto> findVisibleWorkByUserId(@Param("userId") Integer userId);
	
	/**
	 * 表示設定したデータの存在チェック
	 * @param userId
	 * @param workId
	 * @return ture存在する false存在しない
	 */
	public boolean exisistsByUserIdAndWorkId(@Param("userId") Integer userId, @Param("workId") Integer workId);
	
	/**
	 * 表示設定したデータの登録処理
	 * @param userWorkVisibility
	 * @return ture成功 false失敗
	 */
	public boolean insertWorkVisibility(@Param("userWorkVisibility") UserWorkVisibility userWorkVisibility);
	
	/**
	 * 表示設定したデータの更新処理
	 * @param userWorkVisibility
	 * @return ture成功 false失敗
	 */
	public boolean updateWorkVisibility(@Param("userWorkVisibility") UserWorkVisibility userWorkVisibility);
	
	

}

