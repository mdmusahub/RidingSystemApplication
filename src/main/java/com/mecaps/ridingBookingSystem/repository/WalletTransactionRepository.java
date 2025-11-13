package com.mecaps.ridingBookingSystem.repository;

import com.mecaps.ridingBookingSystem.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction,Long> {
}
