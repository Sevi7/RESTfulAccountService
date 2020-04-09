package com.account.service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
		if(newAccount.getTreasury()==false && newAccount.getBalance()<0) {
			throw new AccountException("Balance cannot be negative because it is not a treasury account. Account id: ",null);
		}
		EntityModel<Account> resource = assembler.toModel(repository.save(newAccount));
		return ResponseEntity.created(new URI(resource.getLink("self").orElse(new Link("self")).getHref())).body(resource);
	}
	
	@GetMapping("/accounts/{id}")EntityModel<Account> one(@PathVariable Long id){
		Account account = repository.findById(id).orElseThrow(()-> new AccountException("Could not find account ",id));
		return assembler.toModel(account);
	}
	
	@PutMapping("/accounts/{id}")ResponseEntity<?> replaceAccount(@RequestBody Account newAccount, @PathVariable Long id) throws URISyntaxException{
		Optional<Account> updatedAccount = repository.findById(id);
		if(updatedAccount.isPresent()) {
			if(newAccount.getTreasury()!=updatedAccount.get().getTreasury()) {
				throw new AccountException("Treasury property is not modifiable, you tried to modify it in account ",id);
			}else if(updatedAccount.get().getTreasury()==false && newAccount.getBalance()<0) {
				throw new AccountException("Balance cannot be negative because it is not a treasury account. Account id: ",id);
			}
			else {
				updatedAccount.map(account -> {
					account.setName(newAccount.getName());
					account.setCurrency(newAccount.getCurrency());
					account.setBalance(newAccount.getBalance());
					return repository.save(account);
				});
			}
		}else {
			newAccount.setId(id);
			repository.save(newAccount);
			updatedAccount=Optional.of(newAccount);
		}
		EntityModel<Account> resource = assembler.toModel(updatedAccount.get());
		return ResponseEntity.created(new URI(resource.getLink("self").orElse(new Link("self")).getHref())).body(resource);
	}
	
	@DeleteMapping("/accounts/{id}") ResponseEntity<?> deleteAccount(@PathVariable Long id) {
		if(!repository.existsById(id)) {
			throw new AccountException("Could not find account ",id);
		}
		repository.deleteById(id);	
		return ResponseEntity.noContent().build();
	}
	
}
