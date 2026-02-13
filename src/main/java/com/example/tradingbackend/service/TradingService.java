package com.example.tradingbackend.service;

import com.example.tradingbackend.dto.StockQuote;
import com.example.tradingbackend.dto.TradeRequest;
import com.example.tradingbackend.exception.ResourceNotFoundException;
import com.example.tradingbackend.model.Portfolio;
import com.example.tradingbackend.model.Transaction;
import com.example.tradingbackend.model.TransactionType;
import com.example.tradingbackend.model.User;
import com.example.tradingbackend.repository.PortfolioRepository;
import com.example.tradingbackend.repository.TransactionRepository;
import com.example.tradingbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradingService {

    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final TransactionRepository transactionRepository;
    private final StockPriceService stockPriceService;

    @Transactional
    public Transaction buyStock(TradeRequest request) {
        User user = getUser(request.getUserId());
        StockQuote quote = stockPriceService.getPrice(request.getSymbol());
        BigDecimal totalCost = quote.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        if (user.getBalance().compareTo(totalCost) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        user.setBalance(user.getBalance().subtract(totalCost));
        userRepository.save(user);

        Portfolio portfolio = portfolioRepository.findByUserAndSymbol(user, quote.getSymbol())
                .orElse(new Portfolio(null, user, quote.getSymbol(), 0));
        portfolio.setQuantity(portfolio.getQuantity() + request.getQuantity());
        portfolioRepository.save(portfolio);

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setSymbol(quote.getSymbol());
        transaction.setType(TransactionType.BUY);
        transaction.setQuantity(request.getQuantity());
        transaction.setPrice(quote.getPrice());
        
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction sellStock(TradeRequest request) {
        User user = getUser(request.getUserId());
        StockQuote quote = stockPriceService.getPrice(request.getSymbol());

        Portfolio portfolio = portfolioRepository.findByUserAndSymbol(user, quote.getSymbol())
                .orElseThrow(() -> new IllegalArgumentException("Stock not found in portfolio"));

        if (portfolio.getQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock quantity");
        }

        BigDecimal totalRevenue = quote.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        user.setBalance(user.getBalance().add(totalRevenue));
        userRepository.save(user);

        portfolio.setQuantity(portfolio.getQuantity() - request.getQuantity());
        if (portfolio.getQuantity() == 0) {
            portfolioRepository.delete(portfolio);
        } else {
            portfolioRepository.save(portfolio);
        }

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setSymbol(quote.getSymbol());
        transaction.setType(TransactionType.SELL);
        transaction.setQuantity(request.getQuantity());
        transaction.setPrice(quote.getPrice());

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionHistory(Long userId) {
        User user = getUser(userId);
        return transactionRepository.findByUser(user);
    }

    public List<Portfolio> getUserPortfolio(Long userId) {
        User user = getUser(userId);
        return portfolioRepository.findByUser(user);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }
}
