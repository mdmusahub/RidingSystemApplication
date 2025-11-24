package com.mecaps.ridingBookingSystem.controller;

import com.mecaps.ridingBookingSystem.entity.Wallet;
import com.mecaps.ridingBookingSystem.exception.WalletNotFoundException;
import com.mecaps.ridingBookingSystem.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<Wallet> createWallet(@RequestBody Wallet wallet) {
        Wallet created = walletService.createWallet(wallet);
        return ResponseEntity.created(URI.create("/api/wallets/" + created.getId())).body(created);
    }

    @GetMapping
    public List<Wallet> getAll() {
        return walletService.getAllWallets();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Wallet> getById(@PathVariable Long id) {
         try {
            Wallet wallet = walletService.getWalletById(id);
            return ResponseEntity.ok(wallet);
        } catch (WalletNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Wallet> update(@PathVariable Long id, @RequestBody Wallet walletDetails) {
        try {
            Wallet updated = walletService.updateWallet(id, walletDetails);
            return ResponseEntity.ok(updated);
        } catch (WalletNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            walletService.deleteWallet(id);
            return ResponseEntity.noContent().build();
        } catch (WalletNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

    }
}


