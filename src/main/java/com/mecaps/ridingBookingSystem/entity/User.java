package com.mecaps.ridingBookingSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Role role;

    @DateTimeFormat
    @CreationTimestamp
    private Date createdAt;


    @OneToOne(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Rider rider;

    @OneToOne(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Driver driver;
}
