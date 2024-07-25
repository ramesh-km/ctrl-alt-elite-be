package com.ctrl_alt_elite.proxy_user_bank_application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ctrl_alt_elite.proxy_user_bank_application.entity.BankAccount;
import com.ctrl_alt_elite.proxy_user_bank_application.entity.Transaction;
import com.ctrl_alt_elite.proxy_user_bank_application.repository.BankAccountRepository;
import com.ctrl_alt_elite.proxy_user_bank_application.repository.TransactionRepository;
import com.ctrl_alt_elite.proxy_user_bank_application.security.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class BankAccountServiceImpl {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
	private JwtUtils jwtUtils;

    public List<BankAccount> getAllAccounts() {
        return bankAccountRepository.findAll();
    }

    public Optional<BankAccount> getAccountById(Long id) {
        return bankAccountRepository.findById(id);
    }

    public BankAccount createAccount(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount);
    }

    public BankAccount updateAccount(Long id, BankAccount bankAccountDetails) {
        return bankAccountRepository.findById(id)
                .map(existingAccount -> {
                    existingAccount.setAccountHolderName(bankAccountDetails.getAccountHolderName());
                    existingAccount.setBalance(bankAccountDetails.getBalance());
                    return bankAccountRepository.save(existingAccount);
                })
                .orElse(null);
    }

    public void deleteAccount(Long id) {
        bankAccountRepository.deleteById(id);
    }

    @Transactional
    public void deposit(String accountNumber, BigDecimal amount) {
        BankAccount bankAccount = bankAccountRepository.findByAccountNumber(accountNumber);
        
        if (bankAccount != null) {
            bankAccount.setBalance(bankAccount.getBalance().add(amount));
            bankAccountRepository.save(bankAccount);
            transactionRepository.save(new Transaction(accountNumber, amount, "DEPOSIT", getUsername()));
        }
    }

    @Transactional
    public void withdraw(String accountNumber, BigDecimal amount) {
        BankAccount bankAccount = bankAccountRepository.findByAccountNumber(accountNumber);
        if (bankAccount != null && bankAccount.getBalance().compareTo(amount) >= 0) {
            bankAccount.setBalance(bankAccount.getBalance().subtract(amount));
            bankAccountRepository.save(bankAccount);
            transactionRepository.save(new Transaction(accountNumber, amount, "WITHDRAW", getUsername()));
        }
    }

    @Transactional
    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        BankAccount fromAccount = bankAccountRepository.findByAccountNumber(fromAccountNumber);
        BankAccount toAccount = bankAccountRepository.findByAccountNumber(toAccountNumber);

        if (fromAccount != null && toAccount != null && fromAccount.getBalance().compareTo(amount) >= 0) {
            fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
            toAccount.setBalance(toAccount.getBalance().add(amount));

            bankAccountRepository.save(fromAccount);
            bankAccountRepository.save(toAccount);

            transactionRepository.save(new Transaction(fromAccountNumber, amount, "TRANSFER_OUT", getUsername()));
            transactionRepository.save(new Transaction(toAccountNumber, amount, "TRANSFER_IN", getUsername()));
        }
    }
    
    private String getUsername() {
    	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (jwtUtils.isProxyEnabled(jwtUtils.getJwtFromCookies(request))) {
			var proxyUser = jwtUtils.getImpersonatedEmailFromJwtToken(jwtUtils.getJwtFromCookies(request));
			var user = jwtUtils.getActualEmailFromJwtToken(jwtUtils.getJwtFromCookies(request));
			return proxyUser+"("+user+")";
		}else {
			return jwtUtils.getUserNameFromJwtToken(jwtUtils.getJwtFromCookies(request));
		}
    }
    
    public List<BankAccount> findBankAccountsByAccountHolderName() {
    	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return bankAccountRepository.findByAccountHolderName(jwtUtils.getUserNameFromJwtToken(jwtUtils.getJwtFromCookies(request)));
    }
}
