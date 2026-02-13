package com.example.tradingbackend.service;

import com.example.tradingbackend.dto.WalletTransactionRequest;
import com.example.tradingbackend.dto.UserResponse;
import com.example.tradingbackend.exception.ResourceNotFoundException;
import com.example.tradingbackend.model.User;
import com.example.tradingbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse depositFunds(WalletTransactionRequest request) {
        User user = getUser(request.getUserId());
        user.setBalance(user.getBalance().add(request.getAmount()));
        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    @Transactional
    public UserResponse withdrawFunds(WalletTransactionRequest request) {
        User user = getUser(request.getUserId());
        if (user.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        user.setBalance(user.getBalance().subtract(request.getAmount()));
        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    public BigDecimal getBalance(Long userId) {
        User user = getUser(userId);
        return user.getBalance();
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }
    
    // Helper method to map User entity to UserResponse DTO
    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setActive(user.getActive());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        response.setBalance(user.getBalance());
        return response;
    }
}
