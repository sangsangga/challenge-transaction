package com.transaction.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "RateResponse", description = "Rate Response")
public class RateResponse {
    @Schema(description = "Success status", example = "true")
    private boolean success;
    
    @Schema(description = "Terms URL", example = "https://exchangerate.host/terms")
    private String terms;
    
    @Schema(description = "Privacy URL", example = "https://exchangerate.host/privacy")
    private String privacy;
    
    @Schema(description = "Timestamp", example = "1432400348")
    private long timestamp;
    
    @Schema(description = "Source Currency", example = "USD")
    private String source;
    
    @Schema(description = "Exchange Rates", example = "{\"USDAUD\": 1.278342, \"USDEUR\": 1.278342}")
    private Map<String, BigDecimal> quotes;
}
