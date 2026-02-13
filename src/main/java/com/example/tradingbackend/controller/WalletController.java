package com.example.tradingbackend.controller;

import com.example.tradingbackend.dto.UserResponse;
import com.example.tradingbackend.dto.WalletTransactionRequest;
import com.example.tradingbackend.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/deposit")
    public ResponseEntity<UserResponse> depositFunds(@Valid @RequestBody WalletTransactionRequest request) {
        return ResponseEntity.ok(walletService.depositFunds(request));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<UserResponse> withdrawFunds(@Valid @RequestBody WalletTransactionRequest request) {
        return ResponseEntity.ok(walletService.withdrawFunds(request));
    }

    @GetMapping("/balance/{userId}")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getBalance(userId));
    }
}
