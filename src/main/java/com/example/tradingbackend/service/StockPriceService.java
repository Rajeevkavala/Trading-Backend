package com.example.tradingbackend.service;

import com.example.tradingbackend.dto.StockQuote;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

@Service
public class StockPriceService {

    private final Random random = new Random();

    public StockQuote getPrice(String symbol) {
        // Mock implementation
        // In a real app, this would call an external API (e.g., Alpha Vantage, Yahoo Finance)
        BigDecimal price;
        String upperSymbol = symbol.toUpperCase();

        switch (upperSymbol) {
            // US Stocks
            case "AAPL":
                price = generateRandomPrice(150.0, 160.0);
                break;
            case "GOOGL":
                price = generateRandomPrice(2800.0, 2900.0);
                break;
            case "MSFT":
                price = generateRandomPrice(300.0, 310.0);
                break;

            // Indian Stocks (NSE/BSE Mock Data)
            case "RELIANCE":
                price = generateRandomPrice(2300.0, 2450.0);
                break;
            case "TCS":
                price = generateRandomPrice(3400.0, 3600.0);
                break;
            case "INFY":
                price = generateRandomPrice(1400.0, 1500.0);
                break;
            case "HDFCBANK":
                price = generateRandomPrice(1500.0, 1650.0);
                break;
            case "ICICIBANK":
                price = generateRandomPrice(900.0, 1000.0);
                break;
            case "SBIN":
                price = generateRandomPrice(550.0, 650.0);
                break;
            case "TATAMOTORS":
                price = generateRandomPrice(600.0, 700.0);
                break;
            case "BAJFINANCE":
                price = generateRandomPrice(7000.0, 7500.0);
                break;
            case "WIPRO":
                price = generateRandomPrice(400.0, 450.0);
                break;
            case "ITC":
                price = generateRandomPrice(400.0, 480.0);
                break;

            default:
                price = generateRandomPrice(100.0, 500.0);
                break;
        }
        return new StockQuote(upperSymbol, price);
    }

    private BigDecimal generateRandomPrice(double min, double max) {
        double randomValue = min + (max - min) * random.nextDouble();
        return BigDecimal.valueOf(randomValue).setScale(2, RoundingMode.HALF_UP);
    }
}
