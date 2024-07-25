package com.ctrl_alt_elite.proxy_user_bank_application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ctrl_alt_elite.proxy_user_bank_application.aspect.LogExecutionTime;
import com.ctrl_alt_elite.proxy_user_bank_application.aspect.ProxyRoleAccessCheck;
import com.ctrl_alt_elite.proxy_user_bank_application.entity.BankAccount;
import com.ctrl_alt_elite.proxy_user_bank_application.entity.Transaction;
import com.ctrl_alt_elite.proxy_user_bank_application.repository.TransactionRepository;
import com.ctrl_alt_elite.proxy_user_bank_application.service.BankAccountServiceImpl;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {

    @Autowired
    private BankAccountServiceImpl bankAccountService;

    @Autowired
    private TransactionRepository transactionRepository;

    @ProxyRoleAccessCheck
    @LogExecutionTime
    @GetMapping
    public ResponseEntity<List<BankAccount>> getAllAccounts() {
        return ResponseEntity.ok(bankAccountService.getAllAccounts());
    }

    @ProxyRoleAccessCheck
    @LogExecutionTime
    @GetMapping("/{id}")
    public ResponseEntity<BankAccount> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(bankAccountService.getAccountById(id).orElse(null));
    }

    @ProxyRoleAccessCheck
    @LogExecutionTime
    @PostMapping
    public ResponseEntity<BankAccount> createAccount(@RequestBody BankAccount bankAccount) {
        return ResponseEntity.ok(bankAccountService.createAccount(bankAccount));
    }

    @ProxyRoleAccessCheck
    @LogExecutionTime
    @PutMapping("/{id}")
    public ResponseEntity<BankAccount> updateAccount(@PathVariable Long id, @RequestBody BankAccount bankAccountDetails) {
        return ResponseEntity.ok(bankAccountService.updateAccount(id, bankAccountDetails));
    }

    @ProxyRoleAccessCheck
    @LogExecutionTime
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long id) {
        bankAccountService.deleteAccount(id);
        return ResponseEntity.ok("Bank account deletion successful");
    }

    @ProxyRoleAccessCheck
    @LogExecutionTime
    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestParam String accountNumber, @RequestParam BigDecimal amount) {
        bankAccountService.deposit(accountNumber, amount);
        return ResponseEntity.ok("Money deposit successful");
    }

    @ProxyRoleAccessCheck
    @LogExecutionTime
    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestParam String accountNumber, @RequestParam BigDecimal amount) {
        bankAccountService.withdraw(accountNumber, amount);
        return ResponseEntity.ok("Money withdraw successful");
    }

    @ProxyRoleAccessCheck
    @LogExecutionTime
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestParam String fromAccountNumber, @RequestParam String toAccountNumber, @RequestParam BigDecimal amount) {
        bankAccountService.transfer(fromAccountNumber, toAccountNumber, amount);
        return ResponseEntity.ok("Money transfer successful");
    }

    @ProxyRoleAccessCheck
    @LogExecutionTime
    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(@RequestParam String accountNumber) {
        return ResponseEntity.ok(transactionRepository.findByAccountNumber(accountNumber));
    }
    
    @ProxyRoleAccessCheck
    @LogExecutionTime
    @GetMapping("/my-accounts")
    public ResponseEntity<List<BankAccount>> getMyBankAccount() {
        return ResponseEntity.ok(bankAccountService.findBankAccountsByAccountHolderName());
    }
}
