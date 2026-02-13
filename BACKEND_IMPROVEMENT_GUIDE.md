# 🚀 Trading Platform Backend Improvement Guide

## Complete Backend Development Roadmap for Real-Time Stock Trading (India & USA Markets)

---

## 📋 Table of Contents

1. [Current System Analysis](#current-system-analysis)
2. [Architecture Overview](#architecture-overview)
3. [Authentication & Authorization](#authentication--authorization)
4. [Real-Time Stock Market Data Integration](#real-time-stock-market-data-integration)
5. [Database Schema Improvements](#database-schema-improvements)
6. [API Enhancements](#api-enhancements)
7. [Real-Time Features with WebSocket](#real-time-features-with-websocket)
8. [Order Management System](#order-management-system)
9. [Security Implementations](#security-implementations)
10. [Testing Strategy](#testing-strategy)
11. [Deployment & DevOps](#deployment--devops)
12. [Implementation Phases](#implementation-phases)

---

## 📊 Current System Analysis

### Existing Features
- ✅ Basic user registration (no authentication)
- ✅ Simple buy/sell stock functionality
- ✅ Portfolio management
- ✅ Transaction history
- ✅ Wallet (deposit/withdraw)
- ✅ Mock stock prices for limited stocks

### Gaps to Address
- ❌ No JWT-based authentication/login system
- ❌ Mock stock prices instead of real market data
- ❌ No real-time price updates
- ❌ Limited stock coverage (only ~10 stocks)
- ❌ No market hours validation
- ❌ No order types (limit, stop-loss, etc.)
- ❌ No watchlist feature
- ❌ No price alerts
- ❌ Missing security (password hashing, rate limiting)
- ❌ No comprehensive error handling

---

## 🏗️ Architecture Overview

### Proposed High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                          Client Applications                         │
│                    (Web Frontend / Mobile App)                       │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                         API Gateway / Load Balancer                  │
│                        (Rate Limiting, SSL/TLS)                      │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                    ┌───────────────┼───────────────┐
                    ▼               ▼               ▼
            ┌───────────┐   ┌───────────┐   ┌───────────┐
            │   Auth    │   │  Trading  │   │  Market   │
            │  Service  │   │  Service  │   │   Data    │
            └───────────┘   └───────────┘   └───────────┘
                    │               │               │
                    └───────────────┼───────────────┘
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                         Message Queue (Redis/Kafka)                  │
│                    (Real-time Events, Order Processing)              │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                    ┌───────────────┼───────────────┐
                    ▼               ▼               ▼
            ┌───────────┐   ┌───────────┐   ┌───────────┐
            │  MySQL    │   │   Redis   │   │WebSocket  │
            │ Database  │   │   Cache   │   │  Server   │
            └───────────┘   └───────────┘   └───────────┘
```

### Technology Stack Enhancement

| Component | Current | Proposed |
|-----------|---------|----------|
| Framework | Spring Boot 4.0.2 | Spring Boot 4.0.2 (keep) |
| Security | None | Spring Security + JWT |
| Database | MySQL | MySQL + Redis (caching) |
| Real-time | None | WebSocket (STOMP) |
| Stock Data | Mock | Yahoo Finance / Alpha Vantage / Twelve Data |
| Queue | None | Redis Pub/Sub or Kafka |
| Caching | None | Redis |
| API Docs | OpenAPI 3 | OpenAPI 3 (enhanced) |

---

## 🔐 Authentication & Authorization

### 1. Add Required Dependencies

Add to `pom.xml`:

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT Support -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>

<!-- Password Hashing -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
</dependency>
```

### 2. Create JWT Utility Class

**File:** `src/main/java/com/example/tradingbackend/security/JwtTokenProvider.java`

```java
package com.example.tradingbackend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateAccessToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(Long.toString(userPrincipal.getId()))
                .claim("username", userPrincipal.getUsername())
                .claim("email", userPrincipal.getEmail())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration);

        return Jwts.builder()
                .subject(Long.toString(userId))
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

### 3. Create Authentication DTOs

**File:** `src/main/java/com/example/tradingbackend/dto/auth/LoginRequest.java`

```java
package com.example.tradingbackend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    
    @NotBlank(message = "Username or email is required")
    private String usernameOrEmail;

    @NotBlank(message = "Password is required")
    private String password;
}
```

**File:** `src/main/java/com/example/tradingbackend/dto/auth/AuthResponse.java`

```java
package com.example.tradingbackend.dto.auth;

import com.example.tradingbackend.dto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserResponse user;

    public AuthResponse(String accessToken, String refreshToken, Long expiresIn, UserResponse user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.user = user;
    }
}
```

### 4. Create Authentication Controller

**File:** `src/main/java/com/example/tradingbackend/controller/AuthController.java`

```java
package com.example.tradingbackend.controller;

import com.example.tradingbackend.dto.UserRegistrationRequest;
import com.example.tradingbackend.dto.auth.AuthResponse;
import com.example.tradingbackend.dto.auth.LoginRequest;
import com.example.tradingbackend.dto.auth.RefreshTokenRequest;
import com.example.tradingbackend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and authorization APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user and get JWT tokens")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token using refresh token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user and invalidate tokens")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.noContent().build();
    }
}
```

### 5. Security Configuration

**File:** `src/main/java/com/example/tradingbackend/config/SecurityConfig.java`

```java
package com.example.tradingbackend.config;

import com.example.tradingbackend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/stocks/quote/**").permitAll()
                .requestMatchers("/api/stocks/search/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/ws/**").permitAll()
                // Protected endpoints
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, 
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
            "http://localhost:3000",    // React dev server
            "http://localhost:5173",    // Vite dev server
            "http://localhost:4200"     // Angular dev server
        ));
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

### 6. Update application.properties

```properties
# JWT Configuration
jwt.secret=YourVerySecureSecretKeyThatShouldBeAtLeast256BitsLongForHS256Algorithm
jwt.expiration=3600000
jwt.refresh-expiration=604800000

# Token blacklist (using Redis)
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

---

## 📈 Real-Time Stock Market Data Integration

### Free & Paid API Options

| Provider | Free Tier | Rate Limit | Markets | Best For |
|----------|-----------|------------|---------|----------|
| **Yahoo Finance** | Yes | 100/day | Global | Historical data |
| **Alpha Vantage** | Yes | 5/min, 500/day | Global | Good documentation |
| **Twelve Data** | Yes | 800/day | Global | Real-time data |
| **Finnhub** | Yes | 60/min | US focus | WebSocket support |
| **Polygon.io** | Yes | Limited | US | Comprehensive |
| **NSE India** | Unofficial | Varies | India | NSE stocks |
| **BSE India** | Unofficial | Varies | India | BSE stocks |

### 1. Stock Data Service Implementation

**File:** `src/main/java/com/example/tradingbackend/service/market/MarketDataService.java`

```java
package com.example.tradingbackend.service.market;

import com.example.tradingbackend.dto.market.*;
import java.util.List;

public interface MarketDataService {
    
    // Real-time quote
    StockQuote getQuote(String symbol, Market market);
    List<StockQuote> getQuotes(List<String> symbols, Market market);
    
    // Historical data
    List<HistoricalData> getHistoricalData(String symbol, Market market, 
                                            TimeFrame timeFrame, int limit);
    
    // Search
    List<StockSearchResult> searchStocks(String query, Market market);
    
    // Market info
    List<MarketIndex> getMarketIndices(Market market);
    boolean isMarketOpen(Market market);
    MarketHours getMarketHours(Market market);
    
    // Top movers
    List<StockQuote> getTopGainers(Market market, int limit);
    List<StockQuote> getTopLosers(Market market, int limit);
    List<StockQuote> getMostActive(Market market, int limit);
}

public enum Market {
    NSE,    // National Stock Exchange (India)
    BSE,    // Bombay Stock Exchange (India)
    NYSE,   // New York Stock Exchange (USA)
    NASDAQ  // NASDAQ (USA)
}

public enum TimeFrame {
    ONE_MIN, FIVE_MIN, FIFTEEN_MIN, THIRTY_MIN,
    ONE_HOUR, FOUR_HOUR, ONE_DAY, ONE_WEEK, ONE_MONTH
}
```

### 2. Yahoo Finance Integration

**File:** `src/main/java/com/example/tradingbackend/service/market/YahooFinanceService.java`

```java
package com.example.tradingbackend.service.market;

import com.example.tradingbackend.dto.market.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class YahooFinanceService implements MarketDataService {

    private final RestTemplate restTemplate;
    private static final String BASE_URL = "https://query1.finance.yahoo.com/v8/finance";

    @Override
    @Cacheable(value = "stockQuotes", key = "#symbol + '-' + #market", 
               unless = "#result == null")
    public StockQuote getQuote(String symbol, Market market) {
        String yahooSymbol = convertToYahooSymbol(symbol, market);
        String url = UriComponentsBuilder
            .fromUriString(BASE_URL + "/quote")
            .queryParam("symbols", yahooSymbol)
            .build()
            .toUriString();

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return parseQuoteResponse(response, symbol, market);
        } catch (Exception e) {
            log.error("Error fetching quote for {}: {}", symbol, e.getMessage());
            throw new MarketDataException("Failed to fetch quote for " + symbol);
        }
    }

    @Override
    public List<StockQuote> getQuotes(List<String> symbols, Market market) {
        String symbolsStr = symbols.stream()
            .map(s -> convertToYahooSymbol(s, market))
            .collect(Collectors.joining(","));

        String url = BASE_URL + "/quote?symbols=" + symbolsStr;
        
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return parseMultipleQuotes(response, market);
        } catch (Exception e) {
            log.error("Error fetching quotes: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private String convertToYahooSymbol(String symbol, Market market) {
        return switch (market) {
            case NSE -> symbol + ".NS";
            case BSE -> symbol + ".BO";
            case NYSE, NASDAQ -> symbol;
        };
    }

    @Override
    public List<HistoricalData> getHistoricalData(String symbol, Market market,
                                                   TimeFrame timeFrame, int limit) {
        String yahooSymbol = convertToYahooSymbol(symbol, market);
        String interval = mapTimeFrameToInterval(timeFrame);
        String range = mapTimeFrameToRange(timeFrame, limit);

        String url = UriComponentsBuilder
            .fromUriString(BASE_URL + "/chart/" + yahooSymbol)
            .queryParam("interval", interval)
            .queryParam("range", range)
            .build()
            .toUriString();

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return parseHistoricalData(response);
        } catch (Exception e) {
            log.error("Error fetching historical data for {}: {}", symbol, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<StockSearchResult> searchStocks(String query, Market market) {
        String url = "https://query2.finance.yahoo.com/v1/finance/search?q=" + query;
        
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return parseSearchResults(response, market);
        } catch (Exception e) {
            log.error("Error searching stocks: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public boolean isMarketOpen(Market market) {
        MarketHours hours = getMarketHours(market);
        // Implementation to check current time against market hours
        // Consider timezone differences
        return hours.isCurrentlyOpen();
    }

    @Override
    public MarketHours getMarketHours(Market market) {
        return switch (market) {
            case NSE, BSE -> new MarketHours("09:15", "15:30", "Asia/Kolkata");
            case NYSE, NASDAQ -> new MarketHours("09:30", "16:00", "America/New_York");
        };
    }

    // Helper methods for parsing responses...
}
```

### 3. Alpha Vantage Integration (Alternative)

**File:** `src/main/java/com/example/tradingbackend/service/market/AlphaVantageService.java`

```java
package com.example.tradingbackend.service.market;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AlphaVantageService {

    private final RestTemplate restTemplate;

    @Value("${alphavantage.api-key}")
    private String apiKey;

    private static final String BASE_URL = "https://www.alphavantage.co/query";

    public StockQuote getGlobalQuote(String symbol) {
        String url = String.format(
            "%s?function=GLOBAL_QUOTE&symbol=%s&apikey=%s",
            BASE_URL, symbol, apiKey
        );
        
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        return parseGlobalQuote(response, symbol);
    }

    public List<HistoricalData> getIntradayData(String symbol, String interval) {
        String url = String.format(
            "%s?function=TIME_SERIES_INTRADAY&symbol=%s&interval=%s&apikey=%s",
            BASE_URL, symbol, interval, apiKey
        );
        
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        return parseIntradayData(response);
    }

    public List<StockSearchResult> searchSymbol(String keywords) {
        String url = String.format(
            "%s?function=SYMBOL_SEARCH&keywords=%s&apikey=%s",
            BASE_URL, keywords, apiKey
        );
        
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        return parseSearchResults(response);
    }
}
```

### 4. Stock Data DTOs

**File:** `src/main/java/com/example/tradingbackend/dto/market/StockQuote.java`

```java
package com.example.tradingbackend.dto.market;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockQuote {
    private String symbol;
    private String name;
    private Market market;
    private String exchange;
    private String currency;
    
    private BigDecimal currentPrice;
    private BigDecimal previousClose;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal change;
    private BigDecimal changePercent;
    
    private Long volume;
    private Long avgVolume;
    private BigDecimal marketCap;
    private BigDecimal fiftyTwoWeekHigh;
    private BigDecimal fiftyTwoWeekLow;
    
    private BigDecimal bid;
    private BigDecimal ask;
    private Integer bidSize;
    private Integer askSize;
    
    private LocalDateTime lastUpdated;
    private boolean marketOpen;
}
```

### 5. Caching Configuration

**File:** `src/main/java/com/example/tradingbackend/config/CacheConfig.java`

```java
package com.example.tradingbackend.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        
        // Stock quotes - cache for 15 seconds (real-time-ish)
        cacheConfigs.put("stockQuotes", RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofSeconds(15)));
        
        // Historical data - cache for 5 minutes
        cacheConfigs.put("historicalData", RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(5)));
        
        // Stock search results - cache for 1 hour
        cacheConfigs.put("stockSearch", RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(1)));
        
        // Market indices - cache for 30 seconds
        cacheConfigs.put("marketIndices", RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofSeconds(30)));

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(1)))
            .withInitialCacheConfigurations(cacheConfigs)
            .build();
    }
}
```

---

## 🗄️ Database Schema Improvements

### 1. Enhanced Entity Models

**File:** `src/main/java/com/example/tradingbackend/model/User.java` (Updated)

```java
package com.example.tradingbackend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 100)
    private String fullName;

    @Column(length = 15)
    private String phoneNumber;

    @Column(length = 12)
    private String panNumber;  // For Indian users (tax purposes)

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal blockedBalance = BigDecimal.ZERO;  // For pending orders

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean emailVerified = false;

    @Column
    private String refreshToken;

    @Column
    private LocalDateTime lastLoginAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Portfolio> portfolios = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Watchlist> watchlists = new HashSet<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

public enum UserRole {
    USER, ADMIN
}
```

**File:** `src/main/java/com/example/tradingbackend/model/Stock.java` (New)

```java
package com.example.tradingbackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String symbol;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Market market;

    @Column(length = 20)
    private String exchange;

    @Column(length = 10)
    private String currency;

    @Column(length = 50)
    private String sector;

    @Column(length = 50)
    private String industry;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
```

**File:** `src/main/java/com/example/tradingbackend/model/Order.java` (New)

```java
package com.example.tradingbackend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    private String orderId;  // UUID for external reference

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Market market;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderSide side;  // BUY or SELL

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer filledQuantity = 0;

    @Column(precision = 15, scale = 2)
    private BigDecimal limitPrice;

    @Column(precision = 15, scale = 2)
    private BigDecimal stopPrice;

    @Column(precision = 15, scale = 2)
    private BigDecimal executedPrice;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderValidity validity = OrderValidity.DAY;

    @Column
    private LocalDateTime validUntil;

    @Column
    private LocalDateTime executedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

public enum OrderType {
    MARKET,      // Execute immediately at current market price
    LIMIT,       // Execute at specified price or better
    STOP_LOSS,   // Trigger sell when price falls below stop price
    STOP_LIMIT   // Combination of stop and limit
}

public enum OrderSide {
    BUY, SELL
}

public enum OrderStatus {
    PENDING,     // Order placed but not yet executed
    PARTIAL,     // Partially filled
    FILLED,      // Completely executed
    CANCELLED,   // Cancelled by user
    REJECTED,    // Rejected by system
    EXPIRED      // Order expired (for day orders)
}

public enum OrderValidity {
    DAY,         // Valid for current trading day
    GTC,         // Good till cancelled
    IOC,         // Immediate or cancel
    GTD          // Good till date
}
```

**File:** `src/main/java/com/example/tradingbackend/model/Watchlist.java` (New)

```java
package com.example.tradingbackend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "watchlists")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String name;

    @ElementCollection
    @CollectionTable(name = "watchlist_stocks", 
                     joinColumns = @JoinColumn(name = "watchlist_id"))
    @Column(name = "symbol")
    @Builder.Default
    private List<String> symbols = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
```

**File:** `src/main/java/com/example/tradingbackend/model/PriceAlert.java` (New)

```java
package com.example.tradingbackend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Market market;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertCondition condition;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal targetPrice;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean triggered = false;

    @Column
    private LocalDateTime triggeredAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

public enum AlertCondition {
    ABOVE,   // Alert when price goes above target
    BELOW,   // Alert when price goes below target
    EQUALS   // Alert when price equals target
}
```

### 2. Update Portfolio Model

**File:** `src/main/java/com/example/tradingbackend/model/Portfolio.java` (Updated)

```java
package com.example.tradingbackend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "portfolios",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "symbol", "market"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Market market;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal averageBuyPrice;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalInvestment;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    // Calculated fields (not persisted)
    @Transient
    private BigDecimal currentPrice;

    @Transient
    private BigDecimal currentValue;

    @Transient
    private BigDecimal profitLoss;

    @Transient
    private BigDecimal profitLossPercentage;

    public void calculateMetrics(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
        this.currentValue = currentPrice.multiply(BigDecimal.valueOf(quantity));
        this.profitLoss = currentValue.subtract(totalInvestment);
        if (totalInvestment.compareTo(BigDecimal.ZERO) > 0) {
            this.profitLossPercentage = profitLoss
                .divide(totalInvestment, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        }
    }
}
```

---

## 🔄 API Enhancements

### 1. Stock Controller

**File:** `src/main/java/com/example/tradingbackend/controller/StockController.java` (Updated)

```java
package com.example.tradingbackend.controller;

import com.example.tradingbackend.dto.market.*;
import com.example.tradingbackend.service.market.MarketDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@Tag(name = "Stock Market Data", description = "APIs for fetching real-time stock market data")
public class StockController {

    private final MarketDataService marketDataService;

    @GetMapping("/quote/{symbol}")
    @Operation(summary = "Get real-time stock quote")
    public ResponseEntity<StockQuote> getQuote(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "NSE") Market market) {
        return ResponseEntity.ok(marketDataService.getQuote(symbol, market));
    }

    @GetMapping("/quotes")
    @Operation(summary = "Get multiple stock quotes")
    public ResponseEntity<List<StockQuote>> getQuotes(
            @RequestParam List<String> symbols,
            @RequestParam(defaultValue = "NSE") Market market) {
        return ResponseEntity.ok(marketDataService.getQuotes(symbols, market));
    }

    @GetMapping("/history/{symbol}")
    @Operation(summary = "Get historical data for a stock")
    public ResponseEntity<List<HistoricalData>> getHistoricalData(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "NSE") Market market,
            @RequestParam(defaultValue = "ONE_DAY") TimeFrame timeFrame,
            @RequestParam(defaultValue = "100") int limit) {
        return ResponseEntity.ok(
            marketDataService.getHistoricalData(symbol, market, timeFrame, limit));
    }

    @GetMapping("/search")
    @Operation(summary = "Search for stocks by name or symbol")
    public ResponseEntity<List<StockSearchResult>> searchStocks(
            @RequestParam String query,
            @RequestParam(required = false) Market market) {
        return ResponseEntity.ok(marketDataService.searchStocks(query, market));
    }

    @GetMapping("/indices")
    @Operation(summary = "Get market indices (NIFTY, SENSEX, S&P 500, etc.)")
    public ResponseEntity<List<MarketIndex>> getMarketIndices(
            @RequestParam(defaultValue = "NSE") Market market) {
        return ResponseEntity.ok(marketDataService.getMarketIndices(market));
    }

    @GetMapping("/market-status")
    @Operation(summary = "Check if market is open")
    public ResponseEntity<MarketStatusResponse> getMarketStatus(
            @RequestParam(defaultValue = "NSE") Market market) {
        boolean isOpen = marketDataService.isMarketOpen(market);
        MarketHours hours = marketDataService.getMarketHours(market);
        return ResponseEntity.ok(new MarketStatusResponse(market, isOpen, hours));
    }

    @GetMapping("/top-gainers")
    @Operation(summary = "Get top gaining stocks")
    public ResponseEntity<List<StockQuote>> getTopGainers(
            @RequestParam(defaultValue = "NSE") Market market,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(marketDataService.getTopGainers(market, limit));
    }

    @GetMapping("/top-losers")
    @Operation(summary = "Get top losing stocks")
    public ResponseEntity<List<StockQuote>> getTopLosers(
            @RequestParam(defaultValue = "NSE") Market market,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(marketDataService.getTopLosers(market, limit));
    }

    @GetMapping("/most-active")
    @Operation(summary = "Get most actively traded stocks")
    public ResponseEntity<List<StockQuote>> getMostActive(
            @RequestParam(defaultValue = "NSE") Market market,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(marketDataService.getMostActive(market, limit));
    }
}
```

### 2. Order Controller

**File:** `src/main/java/com/example/tradingbackend/controller/OrderController.java` (New)

```java
package com.example.tradingbackend.controller;

import com.example.tradingbackend.dto.order.*;
import com.example.tradingbackend.model.Order;
import com.example.tradingbackend.model.OrderStatus;
import com.example.tradingbackend.security.CurrentUser;
import com.example.tradingbackend.security.UserPrincipal;
import com.example.tradingbackend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "APIs for placing and managing stock orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Place a new order (buy/sell)")
    public ResponseEntity<OrderResponse> placeOrder(
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody PlaceOrderRequest request) {
        Order order = orderService.placeOrder(currentUser.getId(), request);
        return new ResponseEntity<>(OrderResponse.from(order), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all orders for current user")
    public ResponseEntity<Page<OrderResponse>> getOrders(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(required = false) OrderStatus status,
            Pageable pageable) {
        Page<Order> orders = orderService.getOrders(currentUser.getId(), status, pageable);
        return ResponseEntity.ok(orders.map(OrderResponse::from));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderResponse> getOrder(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable String orderId) {
        Order order = orderService.getOrder(currentUser.getId(), orderId);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @DeleteMapping("/{orderId}")
    @Operation(summary = "Cancel a pending order")
    public ResponseEntity<OrderResponse> cancelOrder(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable String orderId) {
        Order order = orderService.cancelOrder(currentUser.getId(), orderId);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @PutMapping("/{orderId}")
    @Operation(summary = "Modify a pending order")
    public ResponseEntity<OrderResponse> modifyOrder(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable String orderId,
            @Valid @RequestBody ModifyOrderRequest request) {
        Order order = orderService.modifyOrder(currentUser.getId(), orderId, request);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @GetMapping("/pending")
    @Operation(summary = "Get all pending orders")
    public ResponseEntity<List<OrderResponse>> getPendingOrders(
            @CurrentUser UserPrincipal currentUser) {
        List<Order> orders = orderService.getPendingOrders(currentUser.getId());
        return ResponseEntity.ok(orders.stream().map(OrderResponse::from).toList());
    }
}
```

### 3. Watchlist Controller

**File:** `src/main/java/com/example/tradingbackend/controller/WatchlistController.java` (New)

```java
package com.example.tradingbackend.controller;

import com.example.tradingbackend.dto.watchlist.*;
import com.example.tradingbackend.security.CurrentUser;
import com.example.tradingbackend.security.UserPrincipal;
import com.example.tradingbackend.service.WatchlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlists")
@RequiredArgsConstructor
@Tag(name = "Watchlist", description = "APIs for managing stock watchlists")
public class WatchlistController {

    private final WatchlistService watchlistService;

    @PostMapping
    @Operation(summary = "Create a new watchlist")
    public ResponseEntity<WatchlistResponse> createWatchlist(
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody CreateWatchlistRequest request) {
        return new ResponseEntity<>(
            watchlistService.createWatchlist(currentUser.getId(), request),
            HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all watchlists for current user")
    public ResponseEntity<List<WatchlistResponse>> getWatchlists(
            @CurrentUser UserPrincipal currentUser) {
        return ResponseEntity.ok(watchlistService.getWatchlists(currentUser.getId()));
    }

    @GetMapping("/{watchlistId}")
    @Operation(summary = "Get watchlist with real-time quotes")
    public ResponseEntity<WatchlistWithQuotesResponse> getWatchlistWithQuotes(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable Long watchlistId) {
        return ResponseEntity.ok(
            watchlistService.getWatchlistWithQuotes(currentUser.getId(), watchlistId));
    }

    @PostMapping("/{watchlistId}/stocks")
    @Operation(summary = "Add stock to watchlist")
    public ResponseEntity<WatchlistResponse> addStock(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable Long watchlistId,
            @RequestBody AddStockRequest request) {
        return ResponseEntity.ok(
            watchlistService.addStock(currentUser.getId(), watchlistId, request));
    }

    @DeleteMapping("/{watchlistId}/stocks/{symbol}")
    @Operation(summary = "Remove stock from watchlist")
    public ResponseEntity<WatchlistResponse> removeStock(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable Long watchlistId,
            @PathVariable String symbol) {
        return ResponseEntity.ok(
            watchlistService.removeStock(currentUser.getId(), watchlistId, symbol));
    }

    @DeleteMapping("/{watchlistId}")
    @Operation(summary = "Delete watchlist")
    public ResponseEntity<Void> deleteWatchlist(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable Long watchlistId) {
        watchlistService.deleteWatchlist(currentUser.getId(), watchlistId);
        return ResponseEntity.noContent().build();
    }
}
```

---

## 🔌 Real-Time Features with WebSocket

### 1. WebSocket Configuration

**File:** `src/main/java/com/example/tradingbackend/config/WebSocketConfig.java`

```java
package com.example.tradingbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable simple broker for real-time updates
        registry.enableSimpleBroker("/topic", "/queue");
        // Prefix for client-to-server messages
        registry.setApplicationDestinationPrefixes("/app");
        // Prefix for user-specific messages
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000", "http://localhost:5173")
                .withSockJS();
    }
}
```

### 2. Real-Time Price Updates Service

**File:** `src/main/java/com/example/tradingbackend/service/realtime/RealTimePriceService.java`

```java
package com.example.tradingbackend.service.realtime;

import com.example.tradingbackend.dto.market.StockQuote;
import com.example.tradingbackend.service.market.MarketDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class RealTimePriceService {

    private final SimpMessagingTemplate messagingTemplate;
    private final MarketDataService marketDataService;

    // Track subscribed symbols per user session
    private final Map<String, Set<String>> userSubscriptions = new ConcurrentHashMap<>();
    // Track all subscribed symbols globally
    private final Set<String> subscribedSymbols = ConcurrentHashMap.newKeySet();

    public void subscribeToSymbol(String sessionId, String symbol) {
        userSubscriptions.computeIfAbsent(sessionId, k -> ConcurrentHashMap.newKeySet())
                         .add(symbol);
        subscribedSymbols.add(symbol);
        log.info("User {} subscribed to {}", sessionId, symbol);
    }

    public void unsubscribeFromSymbol(String sessionId, String symbol) {
        Set<String> userSymbols = userSubscriptions.get(sessionId);
        if (userSymbols != null) {
            userSymbols.remove(symbol);
            if (userSymbols.isEmpty()) {
                userSubscriptions.remove(sessionId);
            }
        }
        // Check if any user still subscribes to this symbol
        boolean stillSubscribed = userSubscriptions.values().stream()
            .anyMatch(symbols -> symbols.contains(symbol));
        if (!stillSubscribed) {
            subscribedSymbols.remove(symbol);
        }
    }

    public void removeSession(String sessionId) {
        Set<String> symbols = userSubscriptions.remove(sessionId);
        if (symbols != null) {
            symbols.forEach(symbol -> {
                boolean stillSubscribed = userSubscriptions.values().stream()
                    .anyMatch(s -> s.contains(symbol));
                if (!stillSubscribed) {
                    subscribedSymbols.remove(symbol);
                }
            });
        }
    }

    // Broadcast price updates every 5 seconds for subscribed symbols
    @Scheduled(fixedRate = 5000)
    public void broadcastPriceUpdates() {
        if (subscribedSymbols.isEmpty()) {
            return;
        }

        for (String symbol : subscribedSymbols) {
            try {
                StockQuote quote = marketDataService.getQuote(symbol, null);
                messagingTemplate.convertAndSend(
                    "/topic/prices/" + symbol,
                    quote
                );
            } catch (Exception e) {
                log.error("Error broadcasting price for {}: {}", symbol, e.getMessage());
            }
        }
    }
}
```

### 3. WebSocket Controller

**File:** `src/main/java/com/example/tradingbackend/controller/WebSocketController.java`

```java
package com.example.tradingbackend.controller;

import com.example.tradingbackend.dto.websocket.*;
import com.example.tradingbackend.service.realtime.RealTimePriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final RealTimePriceService realTimePriceService;

    @MessageMapping("/subscribe")
    @SendToUser("/queue/subscribed")
    public SubscriptionResponse subscribe(
            @Payload SubscriptionRequest request,
            @Header("simpSessionId") String sessionId,
            Principal principal) {
        request.getSymbols().forEach(symbol ->
            realTimePriceService.subscribeToSymbol(sessionId, symbol)
        );
        return new SubscriptionResponse(true, 
            "Subscribed to " + request.getSymbols().size() + " symbols");
    }

    @MessageMapping("/unsubscribe")
    @SendToUser("/queue/unsubscribed")
    public SubscriptionResponse unsubscribe(
            @Payload SubscriptionRequest request,
            @Header("simpSessionId") String sessionId) {
        request.getSymbols().forEach(symbol ->
            realTimePriceService.unsubscribeFromSymbol(sessionId, symbol)
        );
        return new SubscriptionResponse(true, 
            "Unsubscribed from " + request.getSymbols().size() + " symbols");
    }
}
```

---

## 📦 Order Management System

### 1. Order Service Implementation

**File:** `src/main/java/com/example/tradingbackend/service/OrderService.java`

```java
package com.example.tradingbackend.service;

import com.example.tradingbackend.dto.order.*;
import com.example.tradingbackend.exception.ResourceNotFoundException;
import com.example.tradingbackend.model.*;
import com.example.tradingbackend.repository.*;
import com.example.tradingbackend.service.market.MarketDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final TransactionRepository transactionRepository;
    private final MarketDataService marketDataService;

    @Transactional
    public Order placeOrder(Long userId, PlaceOrderRequest request) {
        User user = getUser(userId);
        
        // Validate market hours for market orders
        if (request.getOrderType() == OrderType.MARKET && 
            !marketDataService.isMarketOpen(request.getMarket())) {
            throw new IllegalArgumentException(
                "Market is closed. Place a limit order for after-hours trading.");
        }

        // Validate stock exists and get current price
        StockQuote quote = marketDataService.getQuote(
            request.getSymbol(), request.getMarket());

        // Calculate order value
        BigDecimal orderValue = calculateOrderValue(request, quote.getCurrentPrice());

        // Validate funds/holdings
        if (request.getSide() == OrderSide.BUY) {
            validateBuyOrder(user, orderValue);
        } else {
            validateSellOrder(user, request);
        }

        // Create order
        Order order = Order.builder()
            .orderId(UUID.randomUUID().toString())
            .user(user)
            .symbol(request.getSymbol().toUpperCase())
            .market(request.getMarket())
            .orderType(request.getOrderType())
            .side(request.getSide())
            .quantity(request.getQuantity())
            .limitPrice(request.getLimitPrice())
            .stopPrice(request.getStopPrice())
            .validity(request.getValidity())
            .validUntil(calculateValidUntil(request))
            .status(OrderStatus.PENDING)
            .build();

        // Block funds for buy orders
        if (request.getSide() == OrderSide.BUY) {
            user.setBlockedBalance(user.getBlockedBalance().add(orderValue));
            userRepository.save(user);
        }

        Order savedOrder = orderRepository.save(order);

        // Execute immediately if market order
        if (request.getOrderType() == OrderType.MARKET) {
            executeOrder(savedOrder, quote.getCurrentPrice());
        }

        return savedOrder;
    }

    @Transactional
    public void executeOrder(Order order, BigDecimal executionPrice) {
        User user = order.getUser();
        BigDecimal totalValue = executionPrice.multiply(
            BigDecimal.valueOf(order.getQuantity()));

        if (order.getSide() == OrderSide.BUY) {
            // Deduct blocked balance
            user.setBlockedBalance(user.getBlockedBalance().subtract(totalValue));
            user.setBalance(user.getBalance().subtract(totalValue));

            // Update portfolio
            updatePortfolioOnBuy(user, order, executionPrice);

        } else {
            // Add to balance
            user.setBalance(user.getBalance().add(totalValue));

            // Update portfolio
            updatePortfolioOnSell(user, order);
        }

        // Update order status
        order.setStatus(OrderStatus.FILLED);
        order.setFilledQuantity(order.getQuantity());
        order.setExecutedPrice(executionPrice);
        order.setTotalValue(totalValue);
        order.setExecutedAt(LocalDateTime.now());

        // Create transaction record
        Transaction transaction = Transaction.builder()
            .user(user)
            .symbol(order.getSymbol())
            .type(order.getSide() == OrderSide.BUY ? 
                  TransactionType.BUY : TransactionType.SELL)
            .quantity(order.getQuantity())
            .price(executionPrice)
            .build();

        userRepository.save(user);
        orderRepository.save(order);
        transactionRepository.save(transaction);

        log.info("Order {} executed at price {}", order.getOrderId(), executionPrice);
    }

    @Transactional
    public Order cancelOrder(Long userId, String orderId) {
        Order order = getOrder(userId, orderId);

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("Only pending orders can be cancelled");
        }

        // Release blocked funds for buy orders
        if (order.getSide() == OrderSide.BUY) {
            User user = order.getUser();
            BigDecimal blockedAmount = calculateOrderValue(order);
            user.setBlockedBalance(user.getBlockedBalance().subtract(blockedAmount));
            userRepository.save(user);
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    // Scheduled job to process pending limit/stop orders
    @Scheduled(fixedRate = 10000) // Every 10 seconds
    @Transactional
    public void processPendingOrders() {
        List<Order> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING);

        for (Order order : pendingOrders) {
            try {
                // Check if order expired
                if (order.getValidUntil() != null && 
                    LocalDateTime.now().isAfter(order.getValidUntil())) {
                    expireOrder(order);
                    continue;
                }

                StockQuote quote = marketDataService.getQuote(
                    order.getSymbol(), order.getMarket());
                BigDecimal currentPrice = quote.getCurrentPrice();

                boolean shouldExecute = shouldExecuteOrder(order, currentPrice);

                if (shouldExecute) {
                    executeOrder(order, currentPrice);
                }

            } catch (Exception e) {
                log.error("Error processing order {}: {}", 
                    order.getOrderId(), e.getMessage());
            }
        }
    }

    private boolean shouldExecuteOrder(Order order, BigDecimal currentPrice) {
        return switch (order.getOrderType()) {
            case LIMIT -> {
                if (order.getSide() == OrderSide.BUY) {
                    yield currentPrice.compareTo(order.getLimitPrice()) <= 0;
                } else {
                    yield currentPrice.compareTo(order.getLimitPrice()) >= 0;
                }
            }
            case STOP_LOSS -> {
                if (order.getSide() == OrderSide.SELL) {
                    yield currentPrice.compareTo(order.getStopPrice()) <= 0;
                }
                yield false;
            }
            case STOP_LIMIT -> {
                // First check if stop price is triggered
                if (currentPrice.compareTo(order.getStopPrice()) <= 0) {
                    // Then check limit price
                    yield currentPrice.compareTo(order.getLimitPrice()) >= 0;
                }
                yield false;
            }
            default -> false;
        };
    }

    // Helper methods...
    private User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public Order getOrder(Long userId, String orderId) {
        return orderRepository.findByUserIdAndOrderId(userId, orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    public Page<Order> getOrders(Long userId, OrderStatus status, Pageable pageable) {
        if (status != null) {
            return orderRepository.findByUserIdAndStatus(userId, status, pageable);
        }
        return orderRepository.findByUserId(userId, pageable);
    }

    public List<Order> getPendingOrders(Long userId) {
        return orderRepository.findByUserIdAndStatus(userId, OrderStatus.PENDING);
    }
}
```

---

## 🔒 Security Implementations

### 1. Rate Limiting

Add dependency:
```xml
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.1.1</version>
</dependency>
```

**File:** `src/main/java/com/example/tradingbackend/config/RateLimitConfig.java`

```java
package com.example.tradingbackend.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitConfig {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String key) {
        return buckets.computeIfAbsent(key, this::createNewBucket);
    }

    private Bucket createNewBucket(String key) {
        // 100 requests per minute
        Bandwidth limit = Bandwidth.classic(100, 
            Refill.intervally(100, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }
}
```

### 2. Input Validation

**File:** `src/main/java/com/example/tradingbackend/dto/order/PlaceOrderRequest.java`

```java
package com.example.tradingbackend.dto.order;

import com.example.tradingbackend.model.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlaceOrderRequest {

    @NotBlank(message = "Symbol is required")
    @Size(min = 1, max = 20, message = "Symbol must be 1-20 characters")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Symbol must be alphanumeric")
    private String symbol;

    @NotNull(message = "Market is required")
    private Market market;

    @NotNull(message = "Order type is required")
    private OrderType orderType;

    @NotNull(message = "Order side is required")
    private OrderSide side;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100000, message = "Quantity cannot exceed 100,000")
    private Integer quantity;

    @DecimalMin(value = "0.01", message = "Limit price must be positive")
    @DecimalMax(value = "10000000", message = "Limit price too high")
    private BigDecimal limitPrice;

    @DecimalMin(value = "0.01", message = "Stop price must be positive")
    private BigDecimal stopPrice;

    private OrderValidity validity = OrderValidity.DAY;
}
```

### 3. Audit Logging

**File:** `src/main/java/com/example/tradingbackend/aspect/AuditLogAspect.java`

```java
package com.example.tradingbackend.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AuditLogAspect {

    @Around("@annotation(com.example.tradingbackend.annotation.Audited)")
    public Object auditMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "anonymous";
        String methodName = joinPoint.getSignature().getName();

        log.info("AUDIT: User '{}' called method '{}' with args: {}", 
                 username, methodName, joinPoint.getArgs());

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            log.info("AUDIT: Method '{}' completed successfully in {}ms", 
                     methodName, duration);
            return result;
        } catch (Exception e) {
            log.error("AUDIT: Method '{}' failed with error: {}", 
                      methodName, e.getMessage());
            throw e;
        }
    }
}
```

---

## 🧪 Testing Strategy

### 1. Unit Tests

**File:** `src/test/java/com/example/tradingbackend/service/OrderServiceTest.java`

```java
package com.example.tradingbackend.service;

import com.example.tradingbackend.dto.order.PlaceOrderRequest;
import com.example.tradingbackend.model.*;
import com.example.tradingbackend.repository.OrderRepository;
import com.example.tradingbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MarketDataService marketDataService;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private PlaceOrderRequest buyRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1L)
            .username("testuser")
            .balance(BigDecimal.valueOf(100000))
            .blockedBalance(BigDecimal.ZERO)
            .build();

        buyRequest = new PlaceOrderRequest();
        buyRequest.setSymbol("RELIANCE");
        buyRequest.setMarket(Market.NSE);
        buyRequest.setOrderType(OrderType.MARKET);
        buyRequest.setSide(OrderSide.BUY);
        buyRequest.setQuantity(10);
    }

    @Test
    void placeOrder_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(marketDataService.isMarketOpen(Market.NSE)).thenReturn(true);
        when(marketDataService.getQuote("RELIANCE", Market.NSE))
            .thenReturn(StockQuote.builder()
                .currentPrice(BigDecimal.valueOf(2400))
                .build());
        when(orderRepository.save(any(Order.class)))
            .thenAnswer(i -> i.getArgument(0));

        Order result = orderService.placeOrder(1L, buyRequest);

        assertNotNull(result);
        assertEquals("RELIANCE", result.getSymbol());
        assertEquals(OrderStatus.FILLED, result.getStatus());
        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    void placeOrder_InsufficientFunds_ThrowsException() {
        testUser.setBalance(BigDecimal.valueOf(1000)); // Not enough

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(marketDataService.isMarketOpen(Market.NSE)).thenReturn(true);
        when(marketDataService.getQuote("RELIANCE", Market.NSE))
            .thenReturn(StockQuote.builder()
                .currentPrice(BigDecimal.valueOf(2400))
                .build());

        assertThrows(IllegalArgumentException.class, 
            () -> orderService.placeOrder(1L, buyRequest));
    }
}
```

### 2. Integration Tests

**File:** `src/test/java/com/example/tradingbackend/integration/TradingIntegrationTest.java`

```java
package com.example.tradingbackend.integration;

import com.example.tradingbackend.dto.auth.LoginRequest;
import com.example.tradingbackend.dto.order.PlaceOrderRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TradingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullTradingFlow() throws Exception {
        // 1. Register user
        // 2. Login and get token
        // 3. Deposit funds
        // 4. Place buy order
        // 5. Check portfolio
        // 6. Place sell order
        // 7. Verify transaction history
    }
}
```

---

## 🚀 Deployment & DevOps

### 1. Docker Configuration

**File:** `Dockerfile`

```dockerfile
FROM eclipse-temurin:17-jdk-alpine as builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SERVER_PORT=8080

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

**File:** `docker-compose.yml`

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/trading_db
      - SPRING_DATASOURCE_USERNAME=trading_user
      - SPRING_DATASOURCE_PASSWORD=secure_password
      - SPRING_DATA_REDIS_HOST=redis
      - JWT_SECRET=${JWT_SECRET}
    depends_on:
      - mysql
      - redis
    networks:
      - trading-network

  mysql:
    image: mysql:8.0
    environment:
      - MYSQL_DATABASE=trading_db
      - MYSQL_USER=trading_user
      - MYSQL_PASSWORD=secure_password
      - MYSQL_ROOT_PASSWORD=root_password
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
    networks:
      - trading-network

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    networks:
      - trading-network

volumes:
  mysql-data:
  redis-data:

networks:
  trading-network:
    driver: bridge
```

### 2. Application Profiles

**File:** `src/main/resources/application-prod.properties`

```properties
# Production Configuration
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Redis
spring.data.redis.host=${REDIS_HOST}
spring.data.redis.port=${REDIS_PORT}
spring.data.redis.password=${REDIS_PASSWORD}

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=3600000
jwt.refresh-expiration=604800000

# Logging
logging.level.root=WARN
logging.level.com.example.tradingbackend=INFO

# API Keys
alphavantage.api-key=${ALPHAVANTAGE_API_KEY}
```

---

## 📅 Implementation Phases

### Phase 1: Foundation (Week 1-2)
- [ ] Set up Spring Security with JWT authentication
- [ ] Implement login/register endpoints
- [ ] Add password hashing with BCrypt
- [ ] Configure CORS properly
- [ ] Add Redis for caching

### Phase 2: Market Data (Week 3-4)
- [ ] Integrate Yahoo Finance API
- [ ] Implement stock search functionality
- [ ] Add historical data endpoints
- [ ] Set up caching for stock quotes
- [ ] Add market status checking

### Phase 3: Trading Features (Week 5-6)
- [ ] Implement order management system
- [ ] Add limit orders, stop-loss orders
- [ ] Update portfolio calculations
- [ ] Add order execution engine
- [ ] Implement transaction history

### Phase 4: Real-Time Features (Week 7-8)
- [ ] Set up WebSocket for live prices
- [ ] Implement watchlist functionality
- [ ] Add price alerts system
- [ ] Real-time portfolio updates
- [ ] Notification system

### Phase 5: Polish & Security (Week 9-10)
- [ ] Add rate limiting
- [ ] Implement audit logging
- [ ] Comprehensive error handling
- [ ] Unit and integration tests
- [ ] API documentation updates

### Phase 6: Deployment (Week 11-12)
- [ ] Docker containerization
- [ ] CI/CD pipeline setup
- [ ] Production environment setup
- [ ] Performance optimization
- [ ] Security audit

---

## 📚 API Documentation Summary

### Authentication Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get tokens |
| POST | `/api/auth/refresh` | Refresh access token |
| POST | `/api/auth/logout` | Logout user |

### Stock Data Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/stocks/quote/{symbol}` | Get real-time quote |
| GET | `/api/stocks/quotes` | Get multiple quotes |
| GET | `/api/stocks/history/{symbol}` | Get historical data |
| GET | `/api/stocks/search` | Search stocks |
| GET | `/api/stocks/indices` | Get market indices |
| GET | `/api/stocks/top-gainers` | Get top gainers |
| GET | `/api/stocks/top-losers` | Get top losers |

### Order Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders` | Place new order |
| GET | `/api/orders` | Get all orders |
| GET | `/api/orders/{id}` | Get order by ID |
| PUT | `/api/orders/{id}` | Modify order |
| DELETE | `/api/orders/{id}` | Cancel order |

### Watchlist Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/watchlists` | Create watchlist |
| GET | `/api/watchlists` | Get all watchlists |
| POST | `/api/watchlists/{id}/stocks` | Add stock |
| DELETE | `/api/watchlists/{id}/stocks/{symbol}` | Remove stock |

### WebSocket Endpoints
| Destination | Description |
|-------------|-------------|
| `/app/subscribe` | Subscribe to price updates |
| `/topic/prices/{symbol}` | Receive price updates |
| `/user/queue/orders` | Receive order updates |

---

## 🎯 Key Success Metrics

1. **Performance**
   - API response time < 200ms for quotes
   - WebSocket latency < 100ms
   - Order execution < 1 second

2. **Reliability**
   - 99.9% uptime during market hours
   - Zero data loss for transactions
   - Automatic failover for external APIs

3. **Security**
   - All endpoints properly authenticated
   - No SQL injection vulnerabilities
   - Rate limiting prevents abuse

4. **Scalability**
   - Handle 1000+ concurrent users
   - Process 100+ orders per second
   - Efficient use of API rate limits

---

*This guide provides a comprehensive roadmap for transforming the basic trading backend into a production-ready system with real-time stock market data from India and USA markets.*
