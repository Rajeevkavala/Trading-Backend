package com.example.tradingbackend.controller;

import com.example.tradingbackend.dto.TradeRequest;
import com.example.tradingbackend.model.Portfolio;
import com.example.tradingbackend.model.Transaction;
import com.example.tradingbackend.service.TradingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trading")
@RequiredArgsConstructor
public class TradingController {

    private final TradingService tradingService;

    @PostMapping("/buy")
    public ResponseEntity<Transaction> buyStock(@Valid @RequestBody TradeRequest request) {
        return ResponseEntity.ok(tradingService.buyStock(request));
    }

    @PostMapping("/sell")
    public ResponseEntity<Transaction> sellStock(@Valid @RequestBody TradeRequest request) {
        return ResponseEntity.ok(tradingService.sellStock(request));
    }

    @GetMapping("/transactions/{userId}")
    public ResponseEntity<List<Transaction>> getTransactionHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(tradingService.getTransactionHistory(userId));
    }

    @GetMapping("/portfolio/{userId}")
    public ResponseEntity<List<Portfolio>> getUserPortfolio(@PathVariable Long userId) {
        return ResponseEntity.ok(tradingService.getUserPortfolio(userId));
    }
}
