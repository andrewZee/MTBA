package com.demo.mtba.service;

import com.demo.mtba.dao.DaoService;
import com.demo.mtba.domain.Account;
import com.demo.mtba.domain.Transaction;
import com.demo.mtba.domain.TransactionStatus;
import com.demo.mtba.domain.exceptions.TransferException;

public class TransferServiceImpl implements TransferService {

    private static final String SUCCESSFUL_TRANSFER_COMPLETE = "Transfer completed successfully.";

    private DaoService daoService;

    public TransferServiceImpl(DaoService daoService) {
        this.daoService = daoService;
    }

    @Override
    public String doTransfer(Transaction transaction) throws TransferException {
        if (!validateOperation(transaction)) {
            throw new TransferException("Transfer is not allowed.");
        }

        if (daoService.createNewTransaction(transaction) == 0L) {
            throw new TransferException("Transaction creation failed.");
        }

        if (daoService.updateAccount(transaction.getAccountFrom().getAccountId()
                , transaction.getAccountFrom().getAmount().subtract(transaction.getAmount()))
                && daoService.updateAccount(transaction.getAccountTo().getAccountId()
                , transaction.getAccountTo().getAmount().add(transaction.getAmount()))) {

            transaction.setStatus(TransactionStatus.COMPLETED_SUCCESS);
            daoService.updateTransactionStatus(transaction);
        } else {
            transaction.setStatus(TransactionStatus.COMPLETED_FAILED);
            daoService.updateTransactionStatus(transaction);
            throw new TransferException("Transfer failed during processing.");
        }

        return SUCCESSFUL_TRANSFER_COMPLETE;
    }

    private boolean validateOperation(Transaction transaction) {
        Account accountFrom = daoService.getAccountById(transaction.getAccountFrom().getAccountId());
        Account accountTo = daoService.getAccountById(transaction.getAccountTo().getAccountId());

        transaction.getAccountFrom().setUser(accountFrom.getUser());
        transaction.getAccountFrom().setAmount(accountFrom.getAmount());
        transaction.getAccountTo().setUser(accountTo.getUser());
        transaction.getAccountTo().setAmount(accountTo.getAmount());

        return (accountFrom.getUser() != null
                && accountTo.getUser() != null
                && accountFrom.getUser().equals(transaction.getUser())
                && (accountFrom.getAmount().compareTo(transaction.getAmount()) >= 0)) ? true : false;
    }
}
