package ua.ardas.udemy_ai.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private BigDecimal amount;
    private String currency; // ISO 4217 code
    private String description;
    private String status; // PENDING | COMPLETED | FAILED | CANCELED
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
