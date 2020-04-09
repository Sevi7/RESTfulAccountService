package com.account.service;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@AutoConfigureMockMvc
public class RestfulAccountServiceUnitTests {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private AccountModelAssembler assembler;
	@MockBean
	private AccountRepository repository;

	@Test
	void testGetAccount() throws Exception {
		//given
		Account account1 = new Account("Fernando Rios","euros",200,false);
		account1.setId(1L);
		Optional<Account> optionalAccount = Optional.of(account1);
		
		given(repository.findById(account1.getId())).willReturn(optionalAccount);
		
		EntityModel<Account> resource = new EntityModel<> (account1, new Link("http://localhost:8080/accounts/"+ account1.getId()),new Link("http://localhost:8080/accounts", "accounts"));
		given(assembler.toModel(account1)).willReturn(resource);
		
		//when + then
		mockMvc.perform(get("/accounts/"+account1.getId()))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.name",is(account1.getName())))
		.andExpect(jsonPath("$.currency",is(account1.getCurrency())))
		.andExpect(jsonPath("$.balance",is(account1.getBalance())))
		.andExpect(jsonPath("$.treasury",is(account1.getTreasury())))
		.andExpect(jsonPath("$._links.self.href",is("http://localhost:8080/accounts/"+account1.getId())))
		.andExpect(jsonPath("$._links.accounts.href",is("http://localhost:8080/accounts")));
	}
	
	@Test
	void testGetAccounts() throws Exception {
		//given
		Account account1 = new Account("Fernando Rios","euros",200,false);
		account1.setId(1L);
		Account account2 = new Account("Lorenzo Padilla","euros",1232.54,false);
		account2.setId(2L);
		List<Account> accountList = new ArrayList<>();
		accountList.add(account1);
		accountList.add(account2);
		given(repository.findAll()).willReturn(accountList);		
		
		EntityModel<Account> resource1 = new EntityModel<> (account1, new Link("http://localhost:8080/accounts/"+ account1.getId()),new Link("http://localhost:8080/accounts", "accounts"));
		given(assembler.toModel(account1)).willReturn(resource1);
		EntityModel<Account> resource2 = new EntityModel<> (account2, new Link("http://localhost:8080/accounts/"+ account2.getId()),new Link("http://localhost:8080/accounts", "accounts"));
		given(assembler.toModel(account2)).willReturn(resource2);
		
		//when + then
		mockMvc.perform(get("/accounts"))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$._embedded.accountList[0].name",is(account1.getName())))
		.andExpect(jsonPath("$._embedded.accountList[0].currency",is(account1.getCurrency())))
		.andExpect(jsonPath("$._embedded.accountList[0].balance",is(account1.getBalance())))
		.andExpect(jsonPath("$._embedded.accountList[0].treasury",is(account1.getTreasury())))
		.andExpect(jsonPath("$._embedded.accountList[0]._links.self.href",is("http://localhost:8080/accounts/"+account1.getId())))
		.andExpect(jsonPath("$._embedded.accountList[0]._links.accounts.href",is("http://localhost:8080/accounts")))
		.andExpect(jsonPath("$._embedded.accountList[1].name",is(account2.getName())))
		.andExpect(jsonPath("$._embedded.accountList[1].currency",is(account2.getCurrency())))
		.andExpect(jsonPath("$._embedded.accountList[1].balance",is(account2.getBalance())))
		.andExpect(jsonPath("$._embedded.accountList[1].treasury",is(account2.getTreasury())))
		.andExpect(jsonPath("$._embedded.accountList[1]._links.self.href",is("http://localhost:8080/accounts/"+account2.getId())))
		.andExpect(jsonPath("$._embedded.accountList[1]._links.accounts.href",is("http://localhost:8080/accounts")));
	}
	
	@Test
	void testPostAccount() throws Exception {
		//given
		Account account1 = new Account("Fernando Rios","euros",200,false);
		account1.setId(1L);
		
		given(repository.save(Mockito.any(Account.class))).willReturn(account1);
		
		EntityModel<Account> resource = new EntityModel<> (account1, new Link("http://localhost:8080/accounts/"+ account1.getId()),new Link("http://localhost:8080/accounts", "accounts"));
		given(assembler.toModel(Mockito.any(Account.class))).willReturn(resource);
	     		
		//when + then
	    mockMvc.perform(MockMvcRequestBuilders
	    .post("/accounts")
	    .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\":\"Fernando Rios\",\"currency\":\"euros\",\"balance\":200,\"treasury\":false}"))
		.andDo(print())
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.name",is(account1.getName())))
		.andExpect(jsonPath("$.currency",is(account1.getCurrency())))
		.andExpect(jsonPath("$.balance",is(account1.getBalance())))
		.andExpect(jsonPath("$.treasury",is(account1.getTreasury())))
		.andExpect(jsonPath("$._links.self.href",is("http://localhost:8080/accounts/"+account1.getId())))
		.andExpect(jsonPath("$._links.accounts.href",is("http://localhost:8080/accounts")));		
	}
	
	@Test
	void testPutAccount() throws Exception {
		//given
		Account account = new Account("Fernando Rios","euros",200,false);
		account.setId(1L);
		Account accountUpdated = new Account("Fernando Cuevas","dollars", 101, false);
		accountUpdated.setId(1L);
		
		Optional<Account> optionalAccount = Optional.of(account);
		given(repository.findById(account.getId())).willReturn(optionalAccount);
		
		given(repository.save(Mockito.any(Account.class))).willReturn(accountUpdated);
		
		EntityModel<Account> resource = new EntityModel<> (accountUpdated, new Link("http://localhost:8080/accounts/"+ accountUpdated.getId()),new Link("http://localhost:8080/accounts", "accounts"));
		given(assembler.toModel(Mockito.any(Account.class))).willReturn(resource);
	     		
		//when + then
	    mockMvc.perform(MockMvcRequestBuilders
	    .put("/accounts/"+account.getId())
	    .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\":\"Fernando Cuevas\",\"currency\":\"dollars\",\"balance\":101,\"treasury\":false}"))
		.andDo(print())
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.name",is(accountUpdated.getName())))
		.andExpect(jsonPath("$.currency",is(accountUpdated.getCurrency())))
		.andExpect(jsonPath("$.balance",is(accountUpdated.getBalance())))
		.andExpect(jsonPath("$.treasury",is(accountUpdated.getTreasury())))
		.andExpect(jsonPath("$._links.self.href",is("http://localhost:8080/accounts/"+account.getId())))
		.andExpect(jsonPath("$._links.accounts.href",is("http://localhost:8080/accounts")));		
	}
	
	@Test
	void testDeleteAccount() throws Exception {
		//given
		Account account = new Account("Fernando Rios","euros",200,false);
		account.setId(1L);
		given(repository.existsById(account.getId())).willReturn(true);
		//when + then
		mockMvc.perform(delete("/accounts/"+account.getId()))
				.andExpect(status().isNoContent());
				
	}
	@Test
	void testTransferMoney() throws Exception {
		//given
		Account accountDebited = new Account("Fernando Rios","euros",200,false);
		accountDebited.setId(1L);
		Account accountCredited = new Account("Lorenzo Padilla","euros",1232.54,false);
		accountCredited.setId(2L);
		double money = 85;
		
		Optional<Account> optionalAccountDebited = Optional.of(accountDebited);
		given(repository.findById(accountDebited.getId())).willReturn(optionalAccountDebited);
		Optional<Account> optionalAccountCredited = Optional.of(accountCredited);
		given(repository.findById(accountCredited.getId())).willReturn(optionalAccountCredited);
		
		Account accountDebitedUpdated = new Account("Fernando Rios","euros",200-money,false);
		accountDebitedUpdated.setId(1L);
		Account accountCreditedUpdated = new Account("Lorenzo Padilla","euros",1232.54+money,false);
		accountCreditedUpdated.setId(2L);
		
		List<Account> accountList = new ArrayList<>();
		accountList.add(accountDebitedUpdated);
		accountList.add(accountCreditedUpdated);
		given(repository.findAll()).willReturn(accountList);
		
		EntityModel<Account> resource1 = new EntityModel<> (accountDebitedUpdated, new Link("http://localhost:8080/accounts/"+ accountDebitedUpdated.getId()),new Link("http://localhost:8080/accounts", "accounts"));
		given(assembler.toModel(accountDebitedUpdated)).willReturn(resource1);
		EntityModel<Account> resource2 = new EntityModel<> (accountCreditedUpdated, new Link("http://localhost:8080/accounts/"+ accountCreditedUpdated.getId()),new Link("http://localhost:8080/accounts", "accounts"));
		given(assembler.toModel(accountCreditedUpdated)).willReturn(resource2);
		
		//when + then
		mockMvc.perform(post("/transfer/"+accountDebited.getId()+"/"+accountCredited.getId()+"/"+money))
		.andDo(print())
		.andExpect(jsonPath("$._embedded.accountList[0].balance",is(accountDebitedUpdated.getBalance())))
		.andExpect(jsonPath("$._embedded.accountList[1].balance",is(accountCreditedUpdated.getBalance())));
	}
}
