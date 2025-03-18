package io.github.brenno_araujo25.personal_finances.dto;

import java.math.BigDecimal;
import java.util.Map;

import lombok.Data;

@Data
public class SummaryResponse {

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal totalBalance;
    private Map<String, BigDecimal> incomesByCategory;
    private Map<String, BigDecimal> expensesByCategory;
    
}
