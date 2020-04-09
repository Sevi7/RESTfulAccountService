package com.account.service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;


@Component
public class AccountModelAssembler implements RepresentationModelAssembler<Account,EntityModel<Account>> {
	@Override
	public EntityModel<Account> toModel(Account account){
		
		return new EntityModel<> (account, 
				linkTo(methodOn(AccountController.class).one(account.getId())).withSelfRel(),
				linkTo(methodOn(AccountController.class).all()).withRel("accounts"));
	}
}
