package com.account.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
public class RestfulAccountServiceComponentTests {

	@LocalServerPort
	private int port;
	
	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private AccountRepository repository;
	
	@Autowired AccountModelAssembler assembler;
	
	private MockMvc mockMvc;
	
	@BeforeAll
	public void setup() {

		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

	}
	
	@BeforeEach
	public void setupEach() {

		this.repository.deleteAll();
		
	}
	
	@Test
	void testGetAccount() throws Exception {
		//given
		Account account1 = new Account("Fernando Rios","euros",200,false);
		account1 = repository.save(account1);
		
		//when + then
		mockMvc.perform(get("/accounts/"+account1.getId()))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.name",is(account1.getName())))
		.andExpect(jsonPath("$.currency",is(account1.getCurrency())))
		.andExpect(jsonPath("$.balance",is(account1.getBalance())))
		.andExpect(jsonPath("$.treasury",is(account1.getTreasury())))
		.andExpect(jsonPath("$._links.self.href",is("http://localhost/accounts/"+account1.getId())))
		.andExpect(jsonPath("$._links.accounts.href",is("http://localhost/accounts")));
	}
	
	@Test
	void testGetAccounts() throws Exception {
		//given
		Account account1 = new Account("Fernando Rios","euros",200,false);
		account1 = repository.save(account1);
		Account account2 = new Account("Lorenzo Padilla","euros",1232.54,false);
		account2 = repository.save(account2);
		
		//when + then
		mockMvc.perform(get("/accounts"))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$._embedded.accountList[0].name",is(account1.getName())))
		.andExpect(jsonPath("$._embedded.accountList[0].currency",is(account1.getCurrency())))
		.andExpect(jsonPath("$._embedded.accountList[0].balance",is(account1.getBalance())))
		.andExpect(jsonPath("$._embedded.accountList[0].treasury",is(account1.getTreasury())))
		.andExpect(jsonPath("$._embedded.accountList[0]._links.self.href",is("http://localhost/accounts/"+account1.getId())))
		.andExpect(jsonPath("$._embedded.accountList[0]._links.accounts.href",is("http://localhost/accounts")))
		.andExpect(jsonPath("$._embedded.accountList[1].name",is(account2.getName())))
		.andExpect(jsonPath("$._embedded.accountList[1].currency",is(account2.getCurrency())))
		.andExpect(jsonPath("$._embedded.accountList[1].balance",is(account2.getBalance())))
		.andExpect(jsonPath("$._embedded.accountList[1].treasury",is(account2.getTreasury())))
		.andExpect(jsonPath("$._embedded.accountList[1]._links.self.href",is("http://localhost/accounts/"+account2.getId())))
		.andExpect(jsonPath("$._embedded.accountList[1]._links.accounts.href",is("http://localhost/accounts")));
	}
	
	@Test
	void testPostAccount() throws Exception {
		//given
		Account account1 = new Account("Fernando Rios","euros",200,false);		
	     		
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
		.andExpect(jsonPath("$._links.accounts.href",is("http://localhost/accounts")));		
	}
	
	@Test
	void testPutAccount() throws Exception {
		//given
		Account account = new Account("Fernando Rios","euros",200,false);
		account = repository.save(account);
		Account accountUpdated = new Account("Fernando Cuevas","dollars", 101, false);
	     		
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
		.andExpect(jsonPath("$._links.self.href",is("http://localhost/accounts/"+account.getId())))
		.andExpect(jsonPath("$._links.accounts.href",is("http://localhost/accounts")));		
	}
	
	@Test
	void testDeleteAccount() throws Exception {
		//given
		Account account = new Account("Fernando Rios","euros",200,false);
		account = repository.save(account);
		
		//when + then
		mockMvc.perform(delete("/accounts/"+account.getId()))
				.andExpect(status().isNoContent());				
	}
	
	@Test
	void testTransferMoney() throws Exception {
		//given
		Account accountDebited = new Account("Fernando Rios","euros",200,false);
		accountDebited = repository.save(accountDebited);
		Account accountCredited = new Account("Lorenzo Padilla","euros",1232.54,false);
		accountCredited = repository.save(accountCredited);
		double money = 85;
		
		accountDebited.transferMoney(accountCredited, money);
		
		//when + then
		mockMvc.perform(post("/transfer/"+accountDebited.getId()+"/"+accountCredited.getId()+"/"+money))
		.andDo(print())
		.andExpect(jsonPath("$._embedded.accountList[0].balance",is(accountDebited.getBalance())))
		.andExpect(jsonPath("$._embedded.accountList[1].balance",is(accountCredited.getBalance())));
	}
	
	@Test
	void testGetAccountWrongId() throws Exception {
		//given
		
		//when + then
		mockMvc.perform(get("/accounts/5"))
		.andDo(print())
		.andExpect(status().isNotFound())
		.andExpect(content().string(containsString("Could not find account 5")));
	}
	
	@Test
	void testPostTreasuryAccountNegativeBalance() throws Exception {
		//given
		Account account1 = new Account("Fernando Rios","euros",-5,false);
		account1 = repository.save(account1);
		
		//when + then
	    mockMvc.perform(MockMvcRequestBuilders
	    .post("/accounts")
	    .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\":\"Fernando Rios\",\"currency\":\"euros\",\"balance\":-5,\"treasury\":false}"))
		.andDo(print())
		.andExpect(status().isNotFound())
		.andExpect(content().string(containsString("Balance cannot be negative because it is not a treasury account. Account id: "+null)));		
	}
	
	@Test
	void testPutAccountModifyTreasury() throws Exception {
		//given
		Account account = new Account("Fernando Rios","euros",200,false);
		account = repository.save(account);
		Account accountUpdated = new Account("Fernando Rios","euros", 200, true);
		accountUpdated.setId(1L);
						
		//when + then
	    mockMvc.perform(MockMvcRequestBuilders
	    .put("/accounts/"+account.getId())
	    .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\":\"Fernando Rios\",\"currency\":\"euros\",\"balance\":200,\"treasury\":true}"))
		.andDo(print())
		.andExpect(status().isNotFound())
		.andExpect(content().string(containsString("Treasury property is not modifiable, you tried to modify it in account "+account.getId())));
	}
	
	@Test
	void testPutTreasuryAccountBalanceNegative() throws Exception {
		//given
		Account account = new Account("Fernando Rios","euros",200,false);
		account = repository.save(account);
		Account accountUpdated = new Account("Fernando Rios","euros", -5, false);
		accountUpdated.setId(1L);
						
		//when + then
	    mockMvc.perform(MockMvcRequestBuilders
	    .put("/accounts/"+account.getId())
	    .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\":\"Fernando Rios\",\"currency\":\"euros\",\"balance\":-5,\"treasury\":false}"))
		.andDo(print())
		.andExpect(status().isNotFound())
		.andExpect(content().string(containsString("Balance cannot be negative because it is not a treasury account. Account id: "+account.getId())));
	}
	
	@Test
	void testDeleteAccountWrongId() throws Exception {
		//given
		
		//when + then
		mockMvc.perform(delete("/accounts/5"))
			.andExpect(status().isNotFound())
			.andExpect(content().string(containsString("Could not find account 5")));				
	}
	
	@Test
	void testTransferMoneyTreasuryAccountNotEnoughBalance() throws Exception {
		//given
		Account accountDebited = new Account("Fernando Rios","euros",200,false);
		accountDebited = repository.save(accountDebited);
		Account accountCredited = new Account("Lorenzo Padilla","euros",1232.54,false);
		accountCredited = repository.save(accountCredited);
		double money = 350;
		
		//when + then
		mockMvc.perform(post("/transfer/"+accountDebited.getId()+"/"+accountCredited.getId()+"/"+money))
		.andDo(print())
		.andExpect(status().isNotFound())
		.andExpect(content().string(containsString("Could not transfer money because debited account is not treasury and there is not enough balance. Debited Account id: "+accountDebited.getId())));
	}
	
	@Test
	void testTransferMoneyTreasuryAccountNegativeBalance() throws Exception {
		//given
		Account accountDebited = new Account("Fernando Rios","euros",200,true);
		accountDebited = repository.save(accountDebited);
		Account accountCredited = new Account("Lorenzo Padilla","euros",1232.54,false);
		accountCredited = repository.save(accountCredited);
		double money = 350;
				
		accountDebited.transferMoney(accountCredited, money);
				
		//when + then
		mockMvc.perform(post("/transfer/"+accountDebited.getId()+"/"+accountCredited.getId()+"/"+money))
		.andDo(print())
		.andExpect(jsonPath("$._embedded.accountList[0].balance",is(accountDebited.getBalance())))
		.andExpect(jsonPath("$._embedded.accountList[1].balance",is(accountCredited.getBalance())));
	}
}
