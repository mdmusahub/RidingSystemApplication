package com.mecaps.ridingBookingSystem.repository;

import com.mecaps.ridingBookingSystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Query("SELECT u FROM User WHERE u.id = ?1")
    Optional<User> findById(Long id);

    @Query("SELECT u FROM User WHERE u.email = ?1")
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User WHERE u.phone = ?1")
    Optional<User> findByPhone(String phone);
}
