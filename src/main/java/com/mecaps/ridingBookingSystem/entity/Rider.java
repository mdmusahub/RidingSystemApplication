package com.mecaps.ridingBookingSystem.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Rider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User userId;

    private Float rating;
}
