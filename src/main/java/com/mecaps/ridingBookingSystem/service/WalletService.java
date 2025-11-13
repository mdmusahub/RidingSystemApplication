package com.mecaps.ridingBookingSystem.service;

import com.mecaps.ridingBookingSystem.entity.Wallet;

import java.util.List;
import java.util.Optional;

public interface WalletService {
    Wallet createWallet(Wallet wallet);

    List<Wallet> getAllWallets();

    Optional<Wallet> getWalletById(Long id);

    Wallet updateWallet(Long id, Wallet walletDetails);

    void deleteWallet(Long id);
}
