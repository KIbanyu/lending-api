package lendingapi.configs;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by Itotia Kibanyu on 27 Jun, 2023
 */
@Data
@Component
public class ApplicationProps {
    @Value("${lending-config.loan-limits}")
    private String[] loanLimits;

    @Value("${lending-config.repayment-periods}")
    private String[] repaymentPeriods;

    @Value("${lending-config.sweep-periods}")
    private int sweepPeriod;

    @Value("${lending-config.test-phone-number}")
    private String testPhoneNumber;

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String databaseUserName;


    @Value("${spring.datasource.password}")
    private String databasePassword;


}
