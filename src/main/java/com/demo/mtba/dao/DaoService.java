package com.demo.mtba.dao;

import com.demo.mtba.domain.Account;
import com.demo.mtba.domain.Transaction;

import java.math.BigDecimal;

public interface DaoService {

    long createNewTransaction(Transaction transaction);

    boolean updateAccount(String accountId, BigDecimal newAmount);

    boolean updateTransactionStatus(Transaction transaction);

    Account getAccountById(String accountId);

    void initializeDB(boolean withServerAccess);
}
