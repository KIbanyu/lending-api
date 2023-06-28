package lendingapi.services;

import com.google.gson.Gson;
import lendingapi.configs.ApplicationProps;
import lendingapi.entities.CustomerEntity;
import lendingapi.entities.LoanLimits;
import lendingapi.models.requests.CreateCustomerRequest;
import lendingapi.models.responses.CustomerLimit;
import lendingapi.respositories.CustomerRepo;
import lendingapi.respositories.LoanLimitRepo;
import lendingapi.utils.AppUtil;
import lendingapi.utils.LendingApiEnums;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Itotia Kibanyu on 27 Jun, 2023
 */
@Service
@Slf4j
public class CustomerService {

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private ApplicationProps applicationProps;

    @Autowired
    private LoanLimitRepo loanLimitRepo;

    private HashMap<String, Object> response;


    public ResponseEntity<HashMap<String, Object>> createUser(CreateCustomerRequest request) {
        response = new HashMap<>();

        try {

            log.info(AppUtil.LINE);
            log.info("INCOMING CREATE CUSTOMER REQUEST {}", new Gson().toJson(request));
            log.info(AppUtil.LINE);

            //Validate incoming request
            if (request == null) {
                response.put(LendingApiEnums.STATUS.label, HttpStatus.BAD_REQUEST);
                response.put(LendingApiEnums.MESSAGE.label, HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            //We validate only one name for this service
            if (!AppUtil.isInputValid(request.getFirstName())) {
                response.put(LendingApiEnums.STATUS.label, HttpStatus.BAD_REQUEST);
                response.put(LendingApiEnums.MESSAGE.label, "First name is invalid");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }


            //Validate phone number
            if (!AppUtil.isInputValid(request.getMsisdn()) || !AppUtil.isPhoneNumberValid(request.getMsisdn())) {
                response.put(LendingApiEnums.STATUS.label, HttpStatus.BAD_REQUEST);
                response.put(LendingApiEnums.MESSAGE.label, "Msisdn is invalid");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }


            //Check if the MSISDN is already registered
            Optional<CustomerEntity> doesPhoneNumberExist = customerRepo.findByPhoneNumber(AppUtil.parsePhoneNumber(request.getMsisdn()));
            if (doesPhoneNumberExist.isPresent()) {
                response.put(LendingApiEnums.STATUS.label, HttpStatus.FOUND);
                response.put(LendingApiEnums.MESSAGE.label, "Phone number already registered");
                return new ResponseEntity<>(response, HttpStatus.FOUND);
            }


            //All checks passes,
            //Store user
            CustomerEntity newCustomer = new CustomerEntity();
            newCustomer.setFirstName(request.getFirstName());
            newCustomer.setLastName(request.getLastName());
            newCustomer.setPhoneNumber(AppUtil.parsePhoneNumber(request.getMsisdn()));
            newCustomer.setStatus("ACTIVE");
            newCustomer.setCreatedOn(new Date());
            CustomerEntity createdUser = customerRepo.save(newCustomer);

            //Check if the user has been created successfully
            if (AppUtil.isInputValid(createdUser.getId())) {
                response.put(LendingApiEnums.STATUS.label, HttpStatus.OK);
                response.put(LendingApiEnums.MESSAGE.label, "Customer created Successfully");

                log.info(AppUtil.LINE);
                log.info("CUSTOMER CREATED SUCCESSFULLY");
                log.info(AppUtil.LINE);

                //Get customer limit
                //This will get the set limits from the application properties
                //Get random value and assign to the newly created customer
                List<BigDecimal> loanLimits = new ArrayList<>();
                Arrays.stream(applicationProps.getLoanLimits()).forEach(node -> {
                    if (AppUtil.isNumberValid(node.trim()))
                        loanLimits.add(new BigDecimal(node));
                });

                //Get random loan limit from the limits provided
                BigDecimal customerLimit = loanLimits.get(new Random().nextInt(loanLimits.size()));
                log.info(AppUtil.LINE);
                log.info("CUSTOMER LOAN LIMIT IS " + customerLimit);
                log.info(AppUtil.LINE);

                //Set customer loan limit, for the new created customer
                LoanLimits limit = new LoanLimits();
                limit.setLoanLimit(customerLimit);
                limit.setAvailableLimit(customerLimit);
                limit.setCreatedOn(new Date());
                limit.setPhoneNumber(createdUser.getPhoneNumber());
                loanLimitRepo.save(limit);

                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            response.put(LendingApiEnums.STATUS.label, HttpStatus.EXPECTATION_FAILED);
            response.put(LendingApiEnums.MESSAGE.label, "Failed to create the customer");
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);


        } catch (Exception e) {
            log.error("An exception occurred while creating a customer {} \n", e.getLocalizedMessage());
            response.put(LendingApiEnums.STATUS.label, HttpStatus.INTERNAL_SERVER_ERROR);
            response.put(LendingApiEnums.MESSAGE.label, "Exception occurred while creating the customer");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    public ResponseEntity<HashMap<String, Object>> getCustomerLimit(String phoneNumber) {
        response = new HashMap<>();
        try {

            log.info(AppUtil.LINE);
            log.info("CUSTOMER TO GET LIMIT FOR " + phoneNumber);
            log.info(AppUtil.LINE);


            //Validate phone number
            //Validate phone number
            if (!AppUtil.isInputValid(phoneNumber) || !AppUtil.isPhoneNumberValid(phoneNumber)) {
                response.put(LendingApiEnums.STATUS.label, HttpStatus.BAD_REQUEST);
                response.put(LendingApiEnums.MESSAGE.label, "Msisdn is invalid");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }


            //Parse phone number
            phoneNumber = AppUtil.parsePhoneNumber(phoneNumber);



            //Check if the customer exist
            Optional<CustomerEntity> doesPhoneNumberExist = customerRepo.findByPhoneNumber(phoneNumber);
            if (doesPhoneNumberExist.isEmpty()) {
                response.put(LendingApiEnums.STATUS.label, HttpStatus.NOT_FOUND);
                response.put(LendingApiEnums.MESSAGE.label, "Customer does not exit");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            CustomerLimit customerLimit = new CustomerLimit();
            customerLimit.setMsisdn(phoneNumber);

            //Check if the customer has a limit
            Optional<LoanLimits> doesLimitExist = loanLimitRepo.findByPhoneNumber(phoneNumber);
            if (doesLimitExist.isEmpty()) {
                customerLimit.setAvailableLimit(new BigDecimal(0));
                customerLimit.setLoanLimit(new BigDecimal(0));
            } else {
                customerLimit.setAvailableLimit(doesLimitExist.get().getAvailableLimit());
                customerLimit.setLoanLimit(doesLimitExist.get().getLoanLimit());
            }

            response.put(LendingApiEnums.STATUS.label, HttpStatus.OK);
            response.put(LendingApiEnums.MESSAGE.label, "Success");
            response.put(LendingApiEnums.LIMIT.label, customerLimit);
            return new ResponseEntity<>(response, HttpStatus.OK);


        } catch (Exception e) {

            log.error("An exception occurred while getting customer limit {}", e.getMessage());
            response.put(LendingApiEnums.STATUS.label, HttpStatus.INTERNAL_SERVER_ERROR);
            response.put(LendingApiEnums.MESSAGE.label, "An exception occurred while getting customer limit");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }
}
