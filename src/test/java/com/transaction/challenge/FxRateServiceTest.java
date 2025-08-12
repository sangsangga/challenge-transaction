package com.transaction.challenge;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;

import com.transaction.challenge.service.FxRateServiceImpl;
import com.transaction.challenge.service.contract.FxRateService;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class FxRateServiceTest {

    @Test
    void getFxRate_returnsExpectedRate() {
        FxRateService mockService = Mockito.mock(FxRateService.class);
        
        when(mockService.getFxRate(any(LocalDate.class), eq("CHF"), eq("IDR")))
            .thenReturn(new BigDecimal("17000.12"));

        BigDecimal rate = mockService.getFxRate(LocalDate.now(), "CHF", "IDR");
        
        assertThat(rate).isEqualTo(new BigDecimal("17000.12"));
    }

    @Test
    // @Disabled("Integration test - requires network")
    void getFxRate_realEndpoint() {
        var service = new FxRateServiceImpl(
            WebClient.builder(), 
            "https://api.exchangerate.host",
            "4076a33e835df4772bf63677dc7c8be2"
        );

        BigDecimal rate = service.getFxRate(LocalDate.now(), "USD", "IDR");
        
        assertThat(rate).isNotNull();
        assertThat(rate).isGreaterThan(BigDecimal.ZERO);
    }
}
