package lendingapi;

import lendingapi.entities.CustomerEntity;
import lendingapi.models.requests.CreateCustomerRequest;
import lendingapi.respositories.CustomerRepo;
import lendingapi.services.CustomerService;
import lendingapi.utils.AppUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
class LendingApiApplicationTests {

	@Autowired
	private CustomerRepo customerRepo;

	@Test
	 void testCreateCustomer() {
		CreateCustomerRequest request = new CreateCustomerRequest();
		request.setFirstName("John");
		request.setLastName("Doe");
		request.setMsisdn("0101235679");

		//Create new customer
		CustomerEntity newCustomer = new CustomerEntity();
		newCustomer.setFirstName(request.getFirstName());
		newCustomer.setLastName(request.getLastName());
		newCustomer.setPhoneNumber(AppUtil.parsePhoneNumber(request.getMsisdn()));
		newCustomer.setStatus("ACTIVE");
		newCustomer.setCreatedOn(new Date());
		newCustomer.setUpdatedOn(new Date());
		CustomerEntity createdCustomer = customerRepo.save(newCustomer);
		assertThat(createdCustomer).isNotNull();

		 createdCustomer = customerRepo.save(newCustomer);
	    assertThat(createdCustomer.;
	}
}
