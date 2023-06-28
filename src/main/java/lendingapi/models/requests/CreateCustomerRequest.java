package lendingapi.models.requests;

import lombok.Data;

/**
 * Created by Itotia Kibanyu on 27 Jun, 2023
 */
@Data
public class CreateCustomerRequest {
    private String firstName;
    private String lastName;
    private String msisdn;

}
