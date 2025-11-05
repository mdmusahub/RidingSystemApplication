package com.mecaps.ridingBookingSystem.repository;

import com.mecaps.ridingBookingSystem.entity.TokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenBlackListRepository extends JpaRepository<TokenBlackList,Long> {

    Optional<TokenBlackList> findByBlackListedToken(String token);

    boolean existsByBlackListedToken(String token);
}
