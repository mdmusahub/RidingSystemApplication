package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.Wallet;
import com.mecaps.ridingBookingSystem.entity.WalletTransaction;
import com.mecaps.ridingBookingSystem.repository.WalletRepository;
import com.mecaps.ridingBookingSystem.repository.WalletTransactionRepository;
import com.mecaps.ridingBookingSystem.service.WalletTransactionService;
import jakarta.transaction.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public class WalletTransactionServiceImpl implements WalletTransactionService {

    private final WalletTransactionRepository transactionRepo;
    private final WalletRepository walletRepo;

    public WalletTransactionServiceImpl(WalletTransactionRepository transactionRepo, WalletRepository walletRepo) {
        this.transactionRepo = transactionRepo;
        this.walletRepo = walletRepo;
    }

    public List<WalletTransaction> getAllTransactions() {
        return transactionRepo.findAll();
    }

    public Optional<WalletTransaction> getTransactionById(Long id) {
        return transactionRepo.findById(id);
    }

    @Transactional
    public WalletTransaction createTransaction(WalletTransaction tx) {
        // fetch wallet
        Long walletId = tx.getWallet().getId();
        Wallet wallet = walletRepo.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found: " + walletId));

        // business rule: debit must have enough balance
        if ("DEBIT".equalsIgnoreCase(tx.getType())) {
            if (wallet.getBalance() == null) wallet.setBalance(0.0);
            if (wallet.getBalance() < tx.getAmount()) {
                throw new RuntimeException("Insufficient balance");
            }
            wallet.setBalance(wallet.getBalance() - tx.getAmount());
        } else {
            // treat any non-DEBIT as CREDIT
            if (wallet.getBalance() == null) wallet.setBalance(0.0);
            wallet.setBalance(wallet.getBalance() + tx.getAmount());
        }

        wallet.setUpdatedAt(OffsetDateTime.now());
        walletRepo.save(wallet);            // persist new balance
        tx.setTransactionTime(OffsetDateTime.now());
        return transactionRepo.save(tx);
    }

    public void deleteTransaction(Long id) {
        transactionRepo.deleteById(id);
    }
}


