package com.mecaps.ridingBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
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
    private Long phone;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @DateTimeFormat
    @CreationTimestamp
    private Date createdAt;


    @JsonIgnore
    @OneToOne(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Rider rider;

    @JsonIgnore
    @OneToOne(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Driver driver;

    @JsonIgnore
    @OneToOne(mappedBy = "reviewerId")
    private Review reviewerId;

    @JsonIgnore
    @OneToOne(mappedBy = "revieweeId")
    private Review revieweeId;

}
