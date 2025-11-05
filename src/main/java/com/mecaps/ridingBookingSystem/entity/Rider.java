package com.mecaps.ridingBookingSystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Rider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    private Float rating;

    @OneToMany(mappedBy = "riderId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rides> rides;


    @OneToOne(mappedBy = "reviewerId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Review review;

}
