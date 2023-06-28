package lendingapi.controllers;

import com.google.gson.Gson;
import lendingapi.models.requests.LoanRepayment;
import lendingapi.models.requests.LoanRequest;
import lendingapi.services.LoanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * Created by Itotia Kibanyu on 27 Jun, 2023
 */
@RestController
@Slf4j
@RequestMapping("/loan/api/v1")
public class LoansController {

    @Autowired
    private LoanService loanService;

    @PostMapping("/loan-request")
    public ResponseEntity<HashMap<String, Object>> loanRequest(@RequestBody LoanRequest request){
        return loanService.loanRequest(request);
    }


    @PostMapping("/loan-repayment")
    public ResponseEntity<HashMap<String, Object>> loanRepayment(@RequestBody LoanRepayment request){
        return loanService.repayLoan(request);
    }


    @GetMapping("/get-loans")
    public ResponseEntity<HashMap<String, Object>> getLoans(@RequestParam String msisdn){
        return loanService.getLoans(msisdn);
    }
}
