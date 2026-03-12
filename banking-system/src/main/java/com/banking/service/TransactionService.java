package com.banking.service;

import com.banking.dto.*;
import com.banking.entity.Account;
import com.banking.entity.Transaction;
import com.banking.exception.InsufficientFundsException;
import com.banking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    @Transactional
    public TransactionResponse deposit(DepositRequest request) {
        Account account = accountService.findByNumber(request.getAccountNumber());
        validateAccountActive(account);

        account.setBalance(account.getBalance().add(request.getAmount()));

        Transaction transaction = Transaction.builder()
                .transactionReference(generateReference())
                .type(Transaction.TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .balanceAfterTransaction(account.getBalance())
                .destinationAccount(account)
                .status(Transaction.TransactionStatus.SUCCESS)
                .description(request.getDescription())
                .build();

        return TransactionResponse.from(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse withdraw(WithdrawalRequest request) {
        Account account = accountService.findByNumber(request.getAccountNumber());
        validateAccountActive(account);
        validateSufficientFunds(account, request.getAmount());

        account.setBalance(account.getBalance().subtract(request.getAmount()));

        Transaction transaction = Transaction.builder()
                .transactionReference(generateReference())
                .type(Transaction.TransactionType.WITHDRAWAL)
                .amount(request.getAmount())
                .balanceAfterTransaction(account.getBalance())
                .sourceAccount(account)
                .status(Transaction.TransactionStatus.SUCCESS)
                .description(request.getDescription())
                .build();

        return TransactionResponse.from(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse transfer(TransferRequest request) {
        if (request.getSourceAccountNumber().equals(request.getDestinationAccountNumber())) {
            throw new IllegalArgumentException("Source and destination accounts cannot be the same");
        }

        Account source = accountService.findByNumber(request.getSourceAccountNumber());
        Account destination = accountService.findByNumber(request.getDestinationAccountNumber());

        validateAccountActive(source);
        validateAccountActive(destination);
        validateSufficientFunds(source, request.getAmount());

        source.setBalance(source.getBalance().subtract(request.getAmount()));
        destination.setBalance(destination.getBalance().add(request.getAmount()));

        Transaction transaction = Transaction.builder()
                .transactionReference(generateReference())
                .type(Transaction.TransactionType.TRANSFER)
                .amount(request.getAmount())
                .balanceAfterTransaction(source.getBalance())
                .sourceAccount(source)
                .destinationAccount(destination)
                .status(Transaction.TransactionStatus.SUCCESS)
                .description(request.getDescription())
                .build();

        return TransactionResponse.from(transactionRepository.save(transaction));
    }

    public Page<TransactionResponse> getTransactionHistory(Long accountId, Pageable pageable) {
        return transactionRepository.findByAccountId(accountId, pageable)
                .map(TransactionResponse::from);
    }

    public Page<TransactionResponse> getTransactionHistoryByDateRange(
            Long accountId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return transactionRepository.findByAccountIdAndDateRange(accountId, startDate, endDate, pageable)
                .map(TransactionResponse::from);
    }

    private void validateAccountActive(Account account) {
        if (account.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new IllegalArgumentException("Account " + account.getAccountNumber() + " is not active");
        }
    }

    private void validateSufficientFunds(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                    String.format("Insufficient funds. Available: %.2f, Required: %.2f",
                            account.getBalance(), amount));
        }
    }

    private String generateReference() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }
}
