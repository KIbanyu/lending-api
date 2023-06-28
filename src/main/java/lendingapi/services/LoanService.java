package lendingapi.services;

import com.google.gson.Gson;
import lendingapi.configs.ApplicationProps;
import lendingapi.entities.CustomerEntity;
import lendingapi.entities.LoanLimits;
import lendingapi.entities.LoanRepayments;
import lendingapi.entities.LoansEntity;
import lendingapi.models.requests.LoanRepayment;
import lendingapi.models.requests.LoanRequest;
import lendingapi.respositories.*;
import lendingapi.utils.AppUtil;
import lendingapi.utils.LendingApiEnums;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Itotia Kibanyu on 27 Jun, 2023
 */
@Service
@Slf4j
public class LoanService {

    @Autowired
    private LoansRepo loansRepo;

    @Autowired
    private LoanLimitRepo loanLimitRepo;

    @Autowired
    private ApplicationProps applicationProps;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private LoansRepaymentRepo loansRepaymentRepo;

    @Autowired
    private SmsService smsService;

    private HashMap<String, Object> response;


    public ResponseEntity<HashMap<String, Object>> loanRequest(LoanRequest request) {
        response = new HashMap<>();
        try {

            //Validate loan request
            //Get repayment periods
            List<Integer> repaymentPeriods = new ArrayList<>();
            Arrays.stream(applicationProps.getRepaymentPeriods()).forEach(node -> {
                if (AppUtil.isNumberValid(node))
                    repaymentPeriods.add(Integer.valueOf(node));
            });

            log.info(AppUtil.LINE);
            log.info("INCOMING LOAN REQUEST {}", new Gson().toJson(request));
            log.info(AppUtil.LINE);


            //Validate phone number
            if (!AppUtil.isInputValid(request.getPhoneNumber()) || !AppUtil.isPhoneNumberValid(request.getPhoneNumber())) {
                response.put(LendingApiEnums.STATUS.label, HttpStatus.BAD_REQUEST);
                response.put(LendingApiEnums.MESSAGE.label, "Msisdn is invalid");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }


            //Check if the request repayment period is in the repayment periods
            if (!repaymentPeriods.contains(request.getRepaymentPeriod())) {
                response.put(LendingApiEnums.STATUS.label, HttpStatus.NOT_FOUND);
                response.put(LendingApiEnums.MESSAGE.label, "Repayment period is invalid, use any of this " + repaymentPeriods);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }


            //Check if amount is valid
            if (request.getAmount().doubleValue() < 100) {
                response.put(LendingApiEnums.STATUS.label, HttpStatus.BAD_REQUEST);
                response.put(LendingApiEnums.MESSAGE.label, "You cannot borrow less than KSH 100");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }




            String phoneNumber = request.getPhoneNumber();
            phoneNumber = AppUtil.parsePhoneNumber(phoneNumber);

            //Check if the customer exists
            Optional<CustomerEntity> doesCustomerExist = customerRepo.findByPhoneNumber(phoneNumber);
            if (doesCustomerExist.isEmpty()) {
                response.put(LendingApiEnums.STATUS.label, HttpStatus.NOT_FOUND);
                response.put(LendingApiEnums.MESSAGE.label, "Customer with supplied phone number " + request.getPhoneNumber() + " does not exist");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            CustomerEntity customer = doesCustomerExist.get();

            //Check if the user has a limit
            Optional<LoanLimits> doesUserHasLimit = loanLimitRepo.findByPhoneNumber(phoneNumber);
            if (doesUserHasLimit.isEmpty()) {
                response.put(LendingApiEnums.STATUS.label, HttpStatus.NOT_FOUND);
                response.put(LendingApiEnums.MESSAGE.label, "You are don't have loan limit, try next time");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            LoanLimits loanLimit = doesUserHasLimit.get();


            //Check if the amount requested is higher than limit
            if (request.getAmount().doubleValue() > doesUserHasLimit.get().getAvailableLimit().doubleValue()) {
                response.put(LendingApiEnums.STATUS.label, HttpStatus.NOT_ACCEPTABLE);
                response.put(LendingApiEnums.MESSAGE.label, "Your requested amount is above your available limit of " + doesUserHasLimit.get().getAvailableLimit().doubleValue());
                return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
            }

            //Store the loan request

            Date currentDate = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            log.info(AppUtil.LINE);
            log.info("CURRENT DATE IS {} ", currentDate);
            log.info(AppUtil.LINE);


            Calendar c = Calendar.getInstance();
            c.setTime(currentDate);
            c.add(Calendar.DATE, request.getRepaymentPeriod());
            Date dueDate = c.getTime();

            log.info(AppUtil.LINE);
            log.info("DUE DATE {} ", dateFormat.format(dueDate));
            log.info(AppUtil.LINE);


            LoansEntity loansEntity = new LoansEntity();
            loansEntity.setPhoneNumber(customer.getPhoneNumber());
            loansEntity.setLoanBalance(request.getAmount());
            loansEntity.setAmountRequested(request.getAmount());
            loansEntity.setAmountLent(request.getAmount());
            loansEntity.setAmountPaid(new BigDecimal(0));
            loansEntity.setCreatedOn(new Date());
            loansEntity.setDueOn(dateFormat.format(dueDate));
            loansEntity.setDueDays(request.getRepaymentPeriod());
            loansEntity.setRepaymentPeriod(String.valueOf(request.getRepaymentPeriod()));
            loansEntity.setLoanStatus("ACTIVE");

            //Check if the loan was created successfully
            LoansEntity createdLoan = loansRepo.save(loansEntity);
            if (AppUtil.isInputValid(createdLoan.getId())) {
                log.info(AppUtil.LINE);
                log.info("LOAN CREATED SUCCESSFULLY");
                log.info(AppUtil.LINE);

                //Update loan limit table
                loanLimit.setPhoneNumber(phoneNumber);
                loanLimit.setAvailableLimit(BigDecimal.valueOf(loanLimit.getAvailableLimit().doubleValue() - request.getAmount().doubleValue()));
                loanLimitRepo.save(loanLimit);

                log.info(AppUtil.LINE);
                log.info("LOAN LIMIT UPDATED");
                log.info(AppUtil.LINE);
                String message = "Dear customer, your loan request of KSH " + request.getAmount() + " was successful. Loan is due on " + createdLoan.getDueOn() + ". Thank you";
                smsService.saveSms(message, phoneNumber);
                response.put(LendingApiEnums.STATUS.label, HttpStatus.OK);
                response.put(LendingApiEnums.MESSAGE.label, "Loan request was Successful");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            response.put(LendingApiEnums.STATUS.label, HttpStatus.EXPECTATION_FAILED);
            response.put(LendingApiEnums.MESSAGE.label, "Error occurred while creating request");
            return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);

        } catch (Exception e) {
            log.error("An exception occurred while creating a loan request {}", e.getMessage());
            response.put(LendingApiEnums.STATUS.label, HttpStatus.INTERNAL_SERVER_ERROR);
            response.put(LendingApiEnums.MESSAGE.label, "An exception occurred while creating a loan request");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }


    public ResponseEntity<HashMap<String, Object>> repayLoan(LoanRepayment request) {
        response = new HashMap<>();
        try {


            //Validate loan request
            if(!AppUtil.isInputValid(request.getLoanId())){
                response.put(LendingApiEnums.STATUS.label, HttpStatus.BAD_REQUEST);
                response.put(LendingApiEnums.MESSAGE.label, "Invalid loan id");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            if (request.getAmountToRepay().intValue() <= 0){
                response.put(LendingApiEnums.STATUS.label, HttpStatus.BAD_REQUEST);
                response.put(LendingApiEnums.MESSAGE.label, "Invalid amount");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }


            //Check if loan exists
            Optional<LoansEntity> doesLoanExist = loansRepo.findById(request.getLoanId());
            if (doesLoanExist.isEmpty()) {
                response.put(LendingApiEnums.STATUS.label, HttpStatus.NOT_FOUND);
                response.put(LendingApiEnums.MESSAGE.label, "Loan not found with the supplied loan ID");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            LoansEntity loansEntity = doesLoanExist.get();

            log.info(AppUtil.LINE);
            log.info("LOAN REPAYMENT REQUEST {}", new Gson().toJson(request));
            log.info(AppUtil.LINE);


            //Check the amount to be paid is more than loan balance
            if (request.getAmountToRepay().doubleValue() > loansEntity.getLoanBalance().doubleValue()) {
                response.put(LendingApiEnums.STATUS.label, HttpStatus.NOT_ACCEPTABLE);
                response.put(LendingApiEnums.MESSAGE.label, "You cannot pay more than the loan balance of " + loansEntity.getLoanBalance().doubleValue());
                return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
            }

            //Add a record in loan repayment table
            LoanRepayments loanRepayments = new LoanRepayments();
            loanRepayments.setLoanId(request.getLoanId());
            loanRepayments.setAmountPaid(request.getAmountToRepay());
            loanRepayments.setCreatedOn(new Date());
            loansRepaymentRepo.save(loanRepayments);


            // This should execute a database query, but for test I will use this computation
            //Update the loans table
            double loanBalance = loansEntity.getLoanBalance().doubleValue() - request.getAmountToRepay().doubleValue();
            loansEntity.setAmountPaid(request.getAmountToRepay());
            loansEntity.setLoanBalance(BigDecimal.valueOf(loanBalance));
            String message;
            if (loanBalance == 0) {
                loansEntity.setLoanStatus("PAID");
                message = "Dear customer, we have received your loan payment of KSH " + request.getAmountToRepay().doubleValue() + " your loan is fully paid. Thank you";
            } else {
                message = "Dear customer, we have received your loan payment of KSH " + request.getAmountToRepay().doubleValue() + " your loan balance is KSH " + loanBalance + ". Thank you";

            }
            loansRepo.save(loansEntity);


            LoanLimits loanLimits = loanLimitRepo.findFirstByPhoneNumber(loansEntity.getPhoneNumber());


            //Update the loan limit table
            loanLimits.setAvailableLimit(loanLimits.getAvailableLimit().add(request.getAmountToRepay()));
            loanLimitRepo.save(loanLimits);

            smsService.saveSms(message, loansEntity.getPhoneNumber());

            log.info(AppUtil.LINE);
            log.info("LOAN REPAYMENT SUCCESSFUL");
            log.info(AppUtil.LINE);

            response.put(LendingApiEnums.STATUS.label, HttpStatus.OK);
            response.put(LendingApiEnums.MESSAGE.label, "Loan repaid successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);


        } catch (Exception e) {
            log.error("An exception occurred while performing loan repayment {}", e.getMessage());
            response.put(LendingApiEnums.STATUS.label, HttpStatus.INTERNAL_SERVER_ERROR);
            response.put(LendingApiEnums.MESSAGE.label, "An exception occurred while performing loan repayment");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }


    public ResponseEntity<HashMap<String, Object>> getLoans(String phoneNumber) {
        response = new HashMap<>();
        try {


            if (!AppUtil.isInputValid(phoneNumber) || !AppUtil.isPhoneNumberValid(phoneNumber)) {
                response.put(LendingApiEnums.STATUS.label, HttpStatus.BAD_REQUEST);
                response.put(LendingApiEnums.MESSAGE.label, "Msisdn is invalid");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }



            phoneNumber = AppUtil.parsePhoneNumber(phoneNumber);

            Optional<CustomerEntity> doesCustomerExist = customerRepo.findByPhoneNumber(phoneNumber);

            if (doesCustomerExist.isEmpty()) {
                response.put(LendingApiEnums.STATUS.label, HttpStatus.NOT_FOUND);
                response.put(LendingApiEnums.MESSAGE.label, "Customer with phone number " + phoneNumber + " not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            response.put(LendingApiEnums.STATUS.label, HttpStatus.OK);
            response.put(LendingApiEnums.MESSAGE.label, "Success");
            response.put(LendingApiEnums.DATA.label, loansRepo.findAllByPhoneNumberAndLoanStatus(doesCustomerExist.get().getPhoneNumber(), LendingApiEnums.ACTIVE.label));
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("An exception occurred while performing get loans {}", e.getMessage());
            response.put(LendingApiEnums.STATUS.label, HttpStatus.INTERNAL_SERVER_ERROR);
            response.put(LendingApiEnums.MESSAGE.label, "An exception occurred while performing get loans");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    public void updateLoansStatus() {
        log.info(AppUtil.LINE);
        log.info("UPDATING LOANS STATUS");
        log.info(AppUtil.LINE);

        //Update loan status, and number of remaining days
        List<LoansEntity> loansEntities = loansRepo.findAllByLoanStatus("ACTIVE");
        for (LoansEntity loan : loansEntities) {
            int daysDue = (int) AppUtil.returnDays(loan.getDueOn());
            log.info(AppUtil.LINE);
            log.info(" LOAN " + loan.getId() + " DUE DATE " + daysDue);
            log.info(AppUtil.LINE);

            if (daysDue < 0) {
                loan.setLoanStatus("OVERDUE");
            } else {
                loan.setDueDays(daysDue);
            }
            loan.setUpdatedOn(new Date());
            loansRepo.save(loan);

        }

    }

    public void sweepDefaultedLoans() {

        log.info(AppUtil.LINE);
        log.info("SWEEPING DEFAULTED LOANS");
        log.info(AppUtil.LINE);

        try {
            List<LoansEntity> loansOverDue = loansRepo.findAllByLoanStatus("OVERDUE");
            for (LoansEntity loan : loansOverDue) {
                if (Math.abs(loan.getDueDays()) >= applicationProps.getSweepPeriod() * 30) {
                    log.info(AppUtil.LINE);
                    log.info("LOAN " + loan.getId() + " cleared due to default");
                    log.info(AppUtil.LINE);
                    loan.setLoanStatus("DEFAULTED");
                    loan.setLoanBalance(BigDecimal.valueOf(0));
                    loansRepo.save(loan);
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
