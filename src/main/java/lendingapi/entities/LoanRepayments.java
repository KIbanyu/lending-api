package lendingapi.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Itotia Kibanyu on 27 Jun, 2023
 */
@Entity
@Data
@Table(name = "LOAN_REPAYMENTS")
public class LoanRepayments {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    private String id;

    @Column(name = "LOAN_ID")
    private String loanId;

    @Column(name = "AMOUNT_PAID")
    private BigDecimal amountPaid;

    @Column(name = "CREATED_ON")
    private Date createdOn;

    @Column(name = "UPDATED_ON")
    private Date updatedOn = new Date();

}
