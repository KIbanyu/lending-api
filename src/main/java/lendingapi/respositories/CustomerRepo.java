package lendingapi.respositories;

import lendingapi.entities.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Itotia Kibanyu on 27 Jun, 2023
 */
@Repository
public interface CustomerRepo extends JpaRepository<CustomerEntity, String> {
    Optional<CustomerEntity> findByPhoneNumber(String phoneNumber);
    CustomerEntity findFirstByPhoneNumber(String  phoneNumber);


}
