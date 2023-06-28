package lendingapi.jobs;

import lendingapi.services.LoanService;
import lendingapi.services.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Itotia Kibanyu on 28 Jun, 2023
 */
@Component
public class LendingApiCronJobs {

    @Autowired
    private SmsService smsService;

    @Autowired
    private LoanService loanService;

    @Scheduled(cron = "*/30 * * * * *")
    public void sendSmsJob() {
        smsService.sendSms();
    }

    @Scheduled(cron = "* */45 12 * * *")
    public void backUpDb() {
        loanService.backupDb();
    }


    @Scheduled(cron = "* */10 12 * * *")
    public void updateLoansStatus() {
        loanService.updateLoansStatus();
    }


    @Scheduled(cron = "* */30 12 * * *")
    public void sweepDefaultedLoans() {
        loanService.sweepDefaultedLoans();
    }


}
