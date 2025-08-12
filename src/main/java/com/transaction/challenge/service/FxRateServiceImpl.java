package com.transaction.challenge.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.transaction.challenge.dto.RateResponse;
import com.transaction.challenge.service.contract.FxRateService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FxRateServiceImpl implements FxRateService {
    private final WebClient webClient;
    private final String apiKey;

    public FxRateServiceImpl(WebClient.Builder builder, @Value("${app.fx.rate.base-url}") String baseUrl, @Value("${app.fx.rate.api-key}") String apiKey) {
        this.webClient = builder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
    }
    
    @Override
    public BigDecimal getFxRate(LocalDate date, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            log.debug("Same currency conversion: {} -> {}, returning 1.0", fromCurrency, toCurrency);
            return BigDecimal.ONE;
        }

        log.debug("Fetching FX rate: {} -> {} for date {}", fromCurrency, toCurrency, date);
        
        try {
            RateResponse response = webClient.get()
                .uri("/live?source=" + fromCurrency + "&currencies=" + toCurrency + "&access_key=" + apiKey)
                .retrieve()
                .bodyToMono(RateResponse.class)
                .block();

            BigDecimal rate = response.getQuotes().getOrDefault(fromCurrency + toCurrency, BigDecimal.ONE);
            log.info("FX rate retrieved: {} -> {} = {}", fromCurrency, toCurrency, rate);
            return rate;
            
        } catch (Exception e) {
            log.error("Failed to fetch FX rate: {} -> {}", fromCurrency, toCurrency, e);
            return BigDecimal.ONE;
        }
    }
}
