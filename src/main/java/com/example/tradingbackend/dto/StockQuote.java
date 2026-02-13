package com.example.tradingbackend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StockQuote {
    private String symbol;
    private BigDecimal price;
    private LocalDateTime timestamp;

    public StockQuote(String symbol, BigDecimal price) {
        this.symbol = symbol;
        this.price = price;
        this.timestamp = LocalDateTime.now();
    }
}
