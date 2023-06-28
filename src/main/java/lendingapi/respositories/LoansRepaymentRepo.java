package lendingapi.respositories;

import lendingapi.entities.LoanRepayments;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Itotia Kibanyu on 27 Jun, 2023
 */
public interface LoansRepaymentRepo extends JpaRepository<LoanRepayments, String> {
}
