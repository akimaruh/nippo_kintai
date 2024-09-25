package com.analix.project.Exception;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.analix.project.entity.Users;
import com.analix.project.mapper.UserMapper;
import com.analix.project.service.EmailService;
import com.analix.project.service.InformationService;
import com.analix.project.util.Constants;

@ControllerAdvice
public class GlobalExceptionHandler {
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private InformationService informationService;
	@Autowired
	private EmailService emailService;
	
	@ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleException(Exception ex) {
		List<Users> users = userMapper.findUserListByRole(Constants.CODE_VAL_ADMIN);
		// 通知へ登録
		informationService.insertErrorNotifications(ex,users);
        // メール送信
		emailService.sendErrorNotification(ex,users);
		
    }

}
