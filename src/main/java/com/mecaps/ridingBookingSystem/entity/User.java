package com.mecaps.ridingBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
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
    @OneToMany(mappedBy = "reviewer")
    private List<Review> reviewerReviews;

    @JsonIgnore
    @OneToMany(mappedBy = "reviewee")
    private List<Review> revieweeReviews;
}
