package lendingapi.respositories;

import lendingapi.entities.SmsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Itotia Kibanyu on 28 Jun, 2023
 */
public interface SmsRepo extends JpaRepository<SmsEntity, String> {
    List<SmsEntity> findByStatus(String status);

}
