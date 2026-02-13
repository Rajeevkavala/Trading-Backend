package com.example.tradingbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TradingBackendApplication {

	public static void main(String[] args) {

		SpringApplication.run(TradingBackendApplication.class, args);
		System.out.println("Trading Backend Application started successfully!");
		System.out.println("\n========================================");
		System.out.println("E-Wallet Backend Started!");
		System.out.println("API Base URL: http://localhost:8080/api");
		System.out.println("Swagger UI: http://localhost:8080/swagger-ui.html");
		System.out.println("API Docs: http://localhost:8080/v3/api-docs");
		System.out.println("Database: MySQL (trading_db)");
		System.out.println("========================================\n");

	}

}
