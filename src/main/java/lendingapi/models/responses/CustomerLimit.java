package lendingapi.models.responses;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Itotia Kibanyu on 27 Jun, 2023
 */
@Data
public class CustomerLimit {
    private String msisdn;
    private BigDecimal loanLimit;
    private BigDecimal availableLimit;
}
