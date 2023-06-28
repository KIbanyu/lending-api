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
@Table(name = "LOANS")
public class LoansEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "MSISDN")
    private String phoneNumber;

    @Column(name = "AMOUNT_REQUESTED")
    private BigDecimal amountRequested;

    @Column(name = "AMOUNT_LENT")
    private BigDecimal amountLent;

    @Column(name = "AMOUNT_PAID")
    private BigDecimal amountPaid;

    @Column(name = "LOAN_BALANCE")
    private BigDecimal loanBalance;

    @Column(name = "LOAN_STATUS")
    private String loanStatus;

    @Column(name = "REPAYMENT_PERIOD")
    private String repaymentPeriod;

    @Column(name = "DUE_ON")
    private String dueOn;

    @Column(name = "DUE_DAYS")
    private Integer dueDays;

    @Column(name = "CREATED_ON")
    private Date createdOn;

    @Column(name = "UPDATED_ON")
    private Date updatedOn = new Date();


}
