package lendingapi.models.requests;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Itotia Kibanyu on 27 Jun, 2023
 */
@Data
public class LoanRequest {
    private BigDecimal amount;
    private Integer repaymentPeriod;
    private String phoneNumber;
}
