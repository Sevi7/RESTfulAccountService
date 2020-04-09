package com.account.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class AccountExceptionAdvice {
	@ResponseBody
	@ExceptionHandler(AccountException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String accountExceptionHandler (AccountException ex) {
		return ex.getMessage();
	}
}
