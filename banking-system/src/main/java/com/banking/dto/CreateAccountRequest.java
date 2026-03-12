package com.banking.dto;

import com.banking.entity.Account;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateAccountRequest {
    @NotNull(message = "Account type is required")
    private Account.AccountType accountType;
    private BigDecimal initialDeposit;
}
