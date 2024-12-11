package com.analix.project.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.analix.project.entity.WorkSchedule;

@Service
public class WorkScheduleCacheService {
	
	private final Map<Integer, WorkSchedule> cache = new ConcurrentHashMap<>();

	// キャッシュにデータを保存
	public void put(Integer userId, WorkSchedule schedule) {
		cache.put(userId, schedule);
	}
	
	// キャッシュからデータを取得
	public WorkSchedule get(Integer userId) {
		return cache.get(userId);
	}

	// キャッシュから削除
	public void remove(Integer userId) {
		cache.remove(userId);
	}
}
