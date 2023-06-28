package lendingapi.models.requests;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Itotia Kibanyu on 27 Jun, 2023
 */

@Data
public class LoanRepayment {
    private String loanId;
    private BigDecimal amountToRepay;
}
