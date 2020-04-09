package com.account.service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {
	
	private final AccountRepository repository;
	private final AccountModelAssembler assembler;
	
	public AccountController (AccountRepository repository, AccountModelAssembler assembler){
		this.repository = repository;
		this.assembler = assembler;
	}
	
	@GetMapping("/accounts")CollectionModel<EntityModel<Account>> all(){ 
		List<EntityModel<Account>> accounts = repository.findAll().stream()
				.map(assembler::toModel)
				.collect(Collectors.toList());
		return new CollectionModel<>(accounts,
				linkTo(methodOn(AccountController.class).all()).withSelfRel());
	}
	
	@PostMapping("/accounts")ResponseEntity<?> newAccount(@RequestBody Account newAccount) throws URISyntaxException{
		EntityModel<Account> resource = assembler.toModel(repository.save(newAccount));
		return ResponseEntity.created(new URI(resource.getLink("self").orElse(new Link("self")).getHref())).body(resource);
	}
	
	@GetMapping("/accounts/{id}")EntityModel<Account> one(@PathVariable Long id){
		Account account = repository.findById(id).orElseThrow(()-> new AccountException("Could not find account ",id));
		return assembler.toModel(account);
	}
	
	
	

}
