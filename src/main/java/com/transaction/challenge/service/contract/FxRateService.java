package com.transaction.challenge.service.contract;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface FxRateService {
    BigDecimal getFxRate(LocalDate date, String fromCurrency, String toCurrency);
}
