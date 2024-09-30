package com.analix.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.analix.project.entity.UserNotificationRequest;
import com.analix.project.service.InformationService;

//@CrossOrigin(origins = "http://localhost:8080")
@CrossOrigin("*")
@RestController
public class RequestController {

	@Autowired
	private InformationService informationService;

	@PostMapping(path="/notifications/markAsRead")
    public ResponseEntity<String> markAsRead(@RequestBody UserNotificationRequest request) {
		 System.out.println("Received request: " + request.getNotificationId() + ", " + request.getUserId());
		 
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
}
