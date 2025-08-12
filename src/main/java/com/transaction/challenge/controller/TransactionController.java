package com.transaction.challenge.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.transaction.challenge.dto.TransactionPageResponse;
import com.transaction.challenge.service.contract.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Parameter;


@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Transaction", description = "Transaction API")
@RestController
@RequestMapping("/api/v1/transactions")
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    @Operation(summary = "List transactions for a month",
    description = "Returns a paginated list plus page-level credit/debit totals converted to base currency.")

    @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = TransactionPageResponse.class)))
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public TransactionPageResponse getTransactions(
      @Parameter(description = "First day of month", example = "2020-10-01")
      @RequestParam LocalDate monthKey,
      @Parameter(description = "Page (0-based)", example = "0") @RequestParam int page,
      @Parameter(description = "Page size", example = "20") @RequestParam int size,
      JwtAuthenticationToken jwt
      ) {
        
        String customerId = (String) jwt.getToken().getClaims().getOrDefault("user_id", jwt.getName());
        log.info("API request: GET /api/v1/transactions customer={} month={} page={} size={}", 
             customerId, monthKey, page, size);
        
        try {
            TransactionPageResponse response = transactionService.getTransactions(customerId, monthKey, PageRequest.of(page, size));
            log.info("API response: {} transactions returned", response.getTransactions().size());
            return response;
        } catch (Exception e) {
            log.error("API error: customer={} month={}", customerId, monthKey, e);
            throw e;
        }
    }
    
    
}