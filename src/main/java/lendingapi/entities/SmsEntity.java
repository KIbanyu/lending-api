package lendingapi.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

/**
 * Created by Itotia Kibanyu on 28 Jun, 2023
 */
@Entity
@Data
@Table(name = "MESSAGES")
public class SmsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    private String id;

    @Column(name = "MESSAGE")
    private String message;

    @Column(name = "RECIPIENT")
    private String recipient;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "CREATED_ON")
    private Date createdOn;

    @Column(name = "UPDATED_ON")
    private Date updatedOn = new Date();
}
