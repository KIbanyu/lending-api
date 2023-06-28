package lendingapi.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;

import java.util.Date;

/**
 * Created by Itotia Kibanyu on 27 Jun, 2023
 */
@Entity
@Data
@Table(name = "CUSTOMERS", uniqueConstraints = {@UniqueConstraint(columnNames = "MSISDN")})
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    private String id;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;


    @Column(name = "MSISDN")
    @NonNull
    private String phoneNumber;

    @Column(name = "STATUS")
    private String status;


    @Column(name = "CREATED_ON")
    private Date createdOn;

    @Column(name = "UPDATED_ON")
    private Date updatedOn = new Date();


    public CustomerEntity() {

    }
}
