package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.Wallet;
import com.mecaps.ridingBookingSystem.entity.WalletTransaction;
import com.mecaps.ridingBookingSystem.exception.WalletNotFoundException;
import com.mecaps.ridingBookingSystem.repository.WalletRepository;
import com.mecaps.ridingBookingSystem.repository.WalletTransactionRepository;
import com.mecaps.ridingBookingSystem.service.WalletTransactionService;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WalletTransactionServiceImpl implements WalletTransactionService {

    private final WalletTransactionRepository transactionRepo;
    private final WalletRepository walletRepo;

    public WalletTransactionServiceImpl(WalletTransactionRepository transactionRepo, WalletRepository walletRepo) {
        this.transactionRepo = transactionRepo;
        this.walletRepo = walletRepo;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<WalletTransaction> getAllTransactions() {
        return transactionRepo.findAll();
    }


    // Id ke through Transaction Dekhna ---
    @Override
    // Check karna ki yeh transaction, logged-in user ke wallet ka hai ya nahi. Ya phir Admin ho is method ko accesss krne wala.
    @PreAuthorize("@transactionSecurity.isTransactionOwner(#id, authentication.principal.id) or hasRole('ROLE_ADMIN')")
    public Optional<WalletTransaction> getTransactionById(Long id) {
        return transactionRepo.findById(id);
    }

    @Transactional
    // Transaction sirf wohi user kr sakta hai, jiske wallet ID se transaction link hai. Ya phir Admin hona chaiye.
    @PreAuthorize("#tx.wallet.id == authentication.principal.id or hasRole('ROLE_ADMIN')")
    public WalletTransaction createTransaction(WalletTransaction tx) {
        // fetch wallet
        Long walletId = tx.getWallet().getId();
        Wallet wallet = walletRepo.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found: " + walletId));

        // debit must have enough balance
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

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteTransaction(Long id) {
        WalletTransaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found: " + id));
        transactionRepo.deleteById(id);
    }
}


