package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.Wallet;
import com.mecaps.ridingBookingSystem.repository.WalletRepository;
import com.mecaps.ridingBookingSystem.service.WalletService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet createWallet(Wallet wallet) {
        if (wallet.getBalance() == null) wallet.setBalance(0.0);
        wallet.setUpdatedAt(OffsetDateTime.now());
        return walletRepository.save(wallet);
    }

    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }

    public Optional<Wallet> getWalletById(Long id) {
        return walletRepository.findById(id);
    }

    public Wallet updateWallet(Long id, Wallet walletDetails) {
        Wallet w = walletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wallet not found: " + id));
        // update fields you allow
        w.setBalance (walletDetails.getBalance());
        w.setUser(walletDetails.getUser());
        w.setUpdatedAt(OffsetDateTime.now());
        return walletRepository.save(w);
    }

    public void deleteWallet(Long id) {
        walletRepository.deleteById(id);
    }
}


