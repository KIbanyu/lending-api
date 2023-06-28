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
@Table(name = "LOAN_LIMITS")
public class LoanLimits {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    private String id;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "LOAN_LIMIT")
    private BigDecimal loanLimit;

    @Column(name = "AVAILABLE_LIMIT")
    private BigDecimal availableLimit;

    @Column(name = "CREATED_ON")
    private Date createdOn;

    @Column(name = "UPDATED_ON")
    private Date updatedOn = new Date();
}
