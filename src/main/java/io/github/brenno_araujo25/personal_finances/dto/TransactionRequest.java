package io.github.brenno_araujo25.personal_finances.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TransactionRequest {

    private BigDecimal amount;
    private String category;
    private String description;
    private LocalDateTime dateTime;
    private String type;

}
