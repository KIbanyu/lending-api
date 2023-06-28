package lendingapi.respositories;

import lendingapi.entities.LoansEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * Created by Itotia Kibanyu on 27 Jun, 2023
 */
public interface LoansRepo extends JpaRepository<LoansEntity, String> {

    List<LoansEntity> findAllByPhoneNumberAndLoanStatus(String customerId, String loanStatus);
    List<LoansEntity> findAllByLoanStatus(String  loanStatus);

}
