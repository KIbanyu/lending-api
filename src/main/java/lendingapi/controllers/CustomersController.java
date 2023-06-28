package lendingapi.controllers;

import lendingapi.models.requests.CreateCustomerRequest;
import lendingapi.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * Created by Itotia Kibanyu on 27 Jun, 2023
 */
@RestController
@RequestMapping("/customers/api/v1")
public class CustomersController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/register-customer")
    public ResponseEntity<HashMap<String, Object>> createUser(@RequestBody CreateCustomerRequest request){
        return  customerService.createUser(request);
    }


    @GetMapping("/get-customer-limit")
    public ResponseEntity<HashMap<String, Object>> getCustomerLimit(@RequestParam String msisdn){
        return  customerService.getCustomerLimit(msisdn);
    }

}
