package com.account.service;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Account {
	
	private @Id @GeneratedValue Long id;
	private String name;
	private String currency;
	private double balance;
	private Boolean treasury;
	
	public Account() {};
	
	public Account (String name, String currency, double balance, Boolean treasury) {
		this.name=name;
		this.currency=currency;
		this.balance=balance;
		this.treasury=treasury;
	}
}
