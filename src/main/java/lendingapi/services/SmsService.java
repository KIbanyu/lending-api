package lendingapi.services;

import lendingapi.entities.SmsEntity;
import lendingapi.respositories.SmsRepo;
import lendingapi.utils.AppUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.CompletableFuture;


/**
 * Created by Itotia Kibanyu on 28 Jun, 2023
 */

@Service
@Slf4j
public class SmsService {

    @Autowired
    private SmsRepo smsRepo;

    @Async
    public void saveSms(String message, String recipient){
        try {
            SmsEntity smsEntity = new SmsEntity();
            smsEntity.setMessage(message);
            smsEntity.setRecipient(recipient);
            smsEntity.setCreatedOn(new Date());
            smsEntity.setStatus("NEW");
            smsRepo.save(smsEntity);

            log.info(AppUtil.LINE);
            log.info("SMS SUCCESSFULLY SAVED");
            log.info(AppUtil.LINE);

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void sendSms(){

        try {
            for (SmsEntity smsEntity : smsRepo.findByStatus("NEW")){
                smsEntity.setStatus("SENT");
                smsRepo.save(smsEntity);
                log.info(AppUtil.LINE);
                log.info("SMS SENT TO " + smsEntity.getRecipient());
                log.info(AppUtil.LINE);
            }


        }catch (Exception e){
            e.printStackTrace();
        }




    }
}
