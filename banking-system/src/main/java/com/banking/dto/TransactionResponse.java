package com.banking.dto;

import com.banking.entity.Transaction;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransactionResponse {
    private Long id;
    private String transactionReference;
    private Transaction.TransactionType type;
    private BigDecimal amount;
    private BigDecimal balanceAfterTransaction;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private Transaction.TransactionStatus status;
    private String description;
    private LocalDateTime createdAt;

    public static TransactionResponse from(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .transactionReference(t.getTransactionReference())
                .type(t.getType())
                .amount(t.getAmount())
                .balanceAfterTransaction(t.getBalanceAfterTransaction())
                .sourceAccountNumber(t.getSourceAccount() != null ? t.getSourceAccount().getAccountNumber() : null)
                .destinationAccountNumber(t.getDestinationAccount() != null ? t.getDestinationAccount().getAccountNumber() : null)
                .status(t.getStatus())
                .description(t.getDescription())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
