package lendingapi;
import lendingapi.configs.ApplicationProps;
import lendingapi.models.requests.CreateCustomerRequest;

import lendingapi.models.requests.LoanRepayment;
import lendingapi.models.requests.LoanRequest;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.*;


class LendingApiApplicationTests extends AbstractTest {

	@Autowired
	private ApplicationProps applicationProps;

	@Override
	@Before
	public void setUp() {
		super.setUp();
	}
	String customerUri = "/customers/api/v1";
	String loansUri = "/loan/api/v1";


	@Test
	void createCustomerWithInvalidRequest() throws Exception {

		if (mvc == null) {
			setUp();
		}

		String inputJson = super.mapToJson(null);
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(customerUri + "/register-customer")
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);

	}


	@Test
	void createCustomerMissingFirstName() throws Exception {

		if (mvc == null) {
			setUp();
		}
		CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
		createCustomerRequest.setLastName("Doe");
		createCustomerRequest.setMsisdn(applicationProps.getTestPhoneNumber());
		String inputJson = super.mapToJson(createCustomerRequest);
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(customerUri + "/register-customer")
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);

	}



	@Test
	void createCustomerWithNoPhoneNumber() throws Exception {

		if (mvc == null) {
			setUp();
		}

		CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
		createCustomerRequest.setFirstName("John");
		createCustomerRequest.setLastName("Doe");
		String inputJson = super.mapToJson(createCustomerRequest);
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(customerUri + "/register-customer")
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);

	}



	@Test
	void createCustomerWitInvalidPhoneNumber() throws Exception {

		if (mvc == null) {
			setUp();
		}

		CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
		createCustomerRequest.setFirstName("John");
		createCustomerRequest.setLastName("Doe");
		createCustomerRequest.setMsisdn("07erefdfdf");

		String inputJson = super.mapToJson(createCustomerRequest);
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(customerUri + "/register-customer")
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);

	}


	@Test
	void createCustomer() throws Exception {

		if (mvc == null) {
			setUp();
		}

		CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
		createCustomerRequest.setFirstName("John");
		createCustomerRequest.setLastName("Doe");
		createCustomerRequest.setMsisdn(applicationProps.getTestPhoneNumber());

		String inputJson = super.mapToJson(createCustomerRequest);
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(customerUri + "/register-customer")
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertThat(status).isIn(200, 302);


	}


	@Test
	void getLimitWithoutRequestParam() throws Exception {

		if (mvc == null) {
			setUp();
		}
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(customerUri + "/get-customer-limit")
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertThat(status).isIn(400);


	}


	@Test
	void getLimitWithInvalidPhoneNumber() throws Exception {

		if (mvc == null) {
			setUp();
		}
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(customerUri + "/get-customer-limit?msisdn=07erefdfdf")
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertThat(status).isIn( 400);

	}

	@Test
	void getLimitWithValidPhoneNumber() throws Exception {

		if (mvc == null) {
			setUp();
		}
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(customerUri + "/get-customer-limit?msisdn="+applicationProps.getTestPhoneNumber())
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertThat(status).isIn(200, 404);

	}


	@Test
	void getLoansWithoutPassingMsisdn() throws Exception {

		if (mvc == null) {
			setUp();
		}
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(loansUri + "/get-loans")
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertThat(status).isIn( 400);

	}

	@Test
	void getLoansWithInvalidPhoneNumber() throws Exception {

		if (mvc == null) {
			setUp();
		}
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(loansUri + "/get-loans?msisdn=07erefdfdf")
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertThat(status).isIn( 400);

	}


	@Test
	void getLoansWithValidPhoneNumber() throws Exception {

		if (mvc == null) {
			setUp();
		}
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(customerUri + "/get-loans?msisdn="+applicationProps.getTestPhoneNumber())
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertThat(status).isIn(200, 404);

	}



	@Test
	void loanRequestWithNullBody() throws Exception {

		if (mvc == null) {
			setUp();
		}

		String inputJson = super.mapToJson(null);
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(loansUri + "/loan-request")
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);

	}

	@Test
	void createLoanWithInvalidFields() throws Exception {

		if (mvc == null) {
			setUp();
		}

		LoanRequest loanRequest = new LoanRequest();
		loanRequest.setAmount(BigDecimal.valueOf(0));
		loanRequest.setRepaymentPeriod(0);
		String inputJson = super.mapToJson(loanRequest);
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(loansUri + "/loan-request")
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);

	}

	@Test
	void createLoanWithValidFields() throws Exception {

		if (mvc == null) {
			setUp();
		}

		LoanRequest loanRequest = new LoanRequest();
		loanRequest.setAmount(BigDecimal.valueOf(200));
		loanRequest.setRepaymentPeriod(30);
		loanRequest.setPhoneNumber(applicationProps.getTestPhoneNumber());
		String inputJson = super.mapToJson(loanRequest);
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(loansUri + "/loan-request")
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertThat(status).isIn(200, 404, 406);

	}




	@Test
	void loanRepaymentWithEmptyBody() throws Exception {

		if (mvc == null) {
			setUp();
		}

		String inputJson = super.mapToJson(null);
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(loansUri + "/loan-repayment")
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);

	}

	@Test
	void loanRepaymentWithInvalidFields() throws Exception {

		if (mvc == null) {
			setUp();
		}

		LoanRepayment loanRequest = new LoanRepayment();
		loanRequest.setLoanId("");
		loanRequest.setAmountToRepay(BigDecimal.valueOf(2000000));
		String inputJson = super.mapToJson(loanRequest);
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(loansUri + "/loan-repayment")
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);

	}

	@Test
	void loanRepaymentWithValidFields() throws Exception {

		if (mvc == null) {
			setUp();
		}

		LoanRepayment loanRequest = new LoanRepayment();
		loanRequest.setLoanId("testloanreapyment");
		loanRequest.setAmountToRepay(BigDecimal.valueOf(20000));
		String inputJson = super.mapToJson(loanRequest);
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(loansUri + "/loan-repayment")
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertThat(status).isIn(200, 404);

	}


	@Test
	void performMysqlBackup() throws Exception {

		if (mvc == null) {
			setUp();
		}

		LoanRepayment loanRequest = new LoanRepayment();
		loanRequest.setLoanId("testloanreapyment");
		loanRequest.setAmountToRepay(BigDecimal.valueOf(20000));
		String inputJson = super.mapToJson(loanRequest);
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(loansUri + "/loan-repayment")
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertThat(status).isIn(200, 404);

	}



}
