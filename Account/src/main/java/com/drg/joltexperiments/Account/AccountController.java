package com.drg.joltexperiments.Account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @GetMapping("/{id}")
    public Account getAccountById(@PathVariable Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id " + id));
    }

    @PostMapping
    public Account createAccount(@RequestBody Account account) {
        return accountRepository.save(account);
    }

    @PutMapping("/{id}")
    public Account updateAccount(@PathVariable Long id, @RequestBody Account updatedAccount) {
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id " + id));
        existingAccount.setAccountNumber(updatedAccount.getAccountNumber());
        existingAccount.setAccountType(updatedAccount.getAccountType());
        existingAccount.setBalance(updatedAccount.getBalance());
        existingAccount.setCustomerId(updatedAccount.getCustomerId()); // Update customer ID reference
        return accountRepository.save(existingAccount);
    }

    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable Long id) {
        accountRepository.deleteById(id);
    }
}
