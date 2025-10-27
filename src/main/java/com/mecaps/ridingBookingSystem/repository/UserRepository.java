package com.mecaps.ridingBookingSystem.repository;

import com.mecaps.ridingBookingSystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Override
    Optional<User> findById(Long id);

    Optional<User> finddByEmail(String email);

    Optional<User> findByPhone(String phone);
}
