package com.mecaps.ridingBookingSystem.service;

import com.mecaps.ridingBookingSystem.entity.WalletTransaction;

import java.util.List;
import java.util.Optional;

public interface WalletTransactionService {
    WalletTransaction createTransaction(WalletTransaction tx);

    List<WalletTransaction> getAllTransactions();

    Optional<WalletTransaction> getTransactionById(Long id);

    void deleteTransaction(Long id);
}
