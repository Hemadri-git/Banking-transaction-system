package com.banking.service;

import com.banking.dto.AccountResponse;
import com.banking.dto.CreateAccountRequest;
import com.banking.entity.Account;
import com.banking.entity.User;
import com.banking.exception.AccountNotFoundException;
import com.banking.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountResponse createAccount(CreateAccountRequest request, User user) {
        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .accountType(request.getAccountType())
                .balance(request.getInitialDeposit() != null ? request.getInitialDeposit() : BigDecimal.ZERO)
                .status(Account.AccountStatus.ACTIVE)
                .user(user)
                .build();

        return AccountResponse.from(accountRepository.save(account));
    }

    public AccountResponse getAccountByNumber(String accountNumber) {
        Account account = findByNumber(accountNumber);
        return AccountResponse.from(account);
    }

    public List<AccountResponse> getAccountsByUser(Long userId) {
        return accountRepository.findByUserId(userId)
                .stream()
                .map(AccountResponse::from)
                .collect(Collectors.toList());
    }

    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(AccountResponse::from)
                .collect(Collectors.toList());
    }

    public AccountResponse closeAccount(String accountNumber) {
        Account account = findByNumber(accountNumber);
        account.setStatus(Account.AccountStatus.INACTIVE);
        return AccountResponse.from(accountRepository.save(account));
    }

    public Account findByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
    }

    private String generateAccountNumber() {
        String number;
        do {
            number = "ACC" + String.format("%010d", new Random().nextLong(9_999_999_999L));
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }
}
