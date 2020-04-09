package com.account.service;

public class AccountException extends RuntimeException {
	public AccountException(String message, Long id) {
		super(message+id);
	}
}
