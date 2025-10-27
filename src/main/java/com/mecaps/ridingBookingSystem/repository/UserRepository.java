package com.mecaps.ridingBookingSystem.repository;

import com.mecaps.ridingBookingSystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    @Query("SELECT u FROM User WHERE u.email = ?1")
    Optional<User> findByEmail(String email);
}
