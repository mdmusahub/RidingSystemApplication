package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.Wallet;
import com.mecaps.ridingBookingSystem.exception.WalletNotFoundException;
import com.mecaps.ridingBookingSystem.repository.WalletRepository;
import com.mecaps.ridingBookingSystem.service.WalletService;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Wallet createWallet(Wallet wallet) {
        if (wallet.getBalance() == null) wallet.setBalance(0.0);
        wallet.setUpdatedAt(OffsetDateTime.now());
        return walletRepository.save(wallet);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }

    @Override
    @PreAuthorize("#id == authentication.principal.id or hasRole('ROLE_ADMIN')")
    public Wallet getWalletById(Long id) {
        return walletRepository.findById(id)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found: " + id));
    }

    @Override
    @Transactional
    @PreAuthorize("#id == authentication.principal.id or hasRole('ROLE_ADMIN')")
    public Wallet updateWallet(Long id, Wallet walletDetails) {
        Wallet w = walletRepository.findById(id)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found: " + id));
        // update fields you allow
        w.setBalance (walletDetails.getBalance());
        w.setUpdatedAt(OffsetDateTime.now());
        return walletRepository.save(w);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteWallet(Long id) {
        Wallet wallet = walletRepository.findById(id)
                        .orElseThrow(() -> new WalletNotFoundException("Wallet not found: " + id));

        walletRepository.deleteById(id);
    }
}


