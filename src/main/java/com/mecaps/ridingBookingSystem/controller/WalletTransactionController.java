package com.mecaps.ridingBookingSystem.controller;

import com.mecaps.ridingBookingSystem.entity.WalletTransaction;
import com.mecaps.ridingBookingSystem.service.WalletTransactionService;
import com.mecaps.ridingBookingSystem.serviceImpl.WalletTransactionServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class WalletTransactionController {

    private final WalletTransactionServiceImpl txService;

    public WalletTransactionController(WalletTransactionServiceImpl txService) {
        this.txService = txService;
    }

    @PostMapping
    public ResponseEntity<WalletTransaction> create(@RequestBody WalletTransaction tx) {
        WalletTransaction created = txService.createTransaction(tx);
        return ResponseEntity.created(URI.create("/api/transactions/" + created.getId())).body(created);
    }

    @GetMapping
    public List<WalletTransaction> getAll() {
        return txService.getAllTransactions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WalletTransaction> getById(@PathVariable Long id) {
        return txService.getTransactionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        txService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}


