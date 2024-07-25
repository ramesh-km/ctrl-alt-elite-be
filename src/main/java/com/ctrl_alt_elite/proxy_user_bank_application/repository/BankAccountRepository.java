package com.ctrl_alt_elite.proxy_user_bank_application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ctrl_alt_elite.proxy_user_bank_application.entity.BankAccount;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    BankAccount findByAccountNumber(String accountNumber);
    List<BankAccount> findByAccountHolderName(String accountHolderName);
}
