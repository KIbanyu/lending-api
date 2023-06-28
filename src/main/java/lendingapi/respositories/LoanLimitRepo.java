package lendingapi.respositories;

import lendingapi.entities.LoanLimits;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by Itotia Kibanyu on 27 Jun, 2023
 */
public interface LoanLimitRepo extends JpaRepository<LoanLimits, String> {
    Optional<LoanLimits> findByPhoneNumber(String phoneNumber);
    LoanLimits findFirstByPhoneNumber(String phoneNumber);
}
