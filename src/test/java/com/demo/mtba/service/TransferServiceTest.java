package com.demo.mtba.service;

import com.demo.mtba.dao.DaoService;
import com.demo.mtba.domain.Account;
import com.demo.mtba.domain.Transaction;
import com.demo.mtba.domain.TransactionStatus;
import com.demo.mtba.domain.exceptions.TransferException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TransferServiceTest {

    @Mock
    private DaoService daoService;
    @InjectMocks
    private TransferServiceImpl transferService;

    private Transaction transaction;
    private Account accountFrom;
    private Account accountTo;

    @Before
    public void setup() {
        transaction = new Transaction.Builder()
                .id(0L)
                .user("USER_FROM")
                .accountFrom(new Account("FROM"))
                .accountTo(new Account("TO"))
                .amount(new BigDecimal(100))
                .status(TransactionStatus.NEW)
                .build();
        accountFrom = new Account("FROM");
        accountFrom.setUser("USER_FROM");
        accountFrom.setAmount(new BigDecimal(1000));

        accountTo = new Account("TO");
        accountTo.setUser("USER_TO");
        accountTo.setAmount(new BigDecimal(1000));

        when(daoService.getAccountById(transaction.getAccountFrom().getAccountId()))
                .thenReturn(accountFrom);
        when(daoService.getAccountById(transaction.getAccountTo().getAccountId()))
                .thenReturn(accountTo);
        when(daoService.createNewTransaction(transaction)).thenReturn(1L);
        when(daoService.updateAccount(anyString(), any())).thenReturn(true);
    }

    @Test
    public void shouldReturnSuccessfulMessage() throws TransferException {

        String actualResponse = transferService.doTransfer(transaction);

        verify(daoService, atLeastOnce()).createNewTransaction(transaction);
        verify(daoService, times(2)).getAccountById(anyString());
        verify(daoService, times(2)).updateAccount(anyString(), any());

        assertEquals("Transfer completed successfully.", actualResponse);
        assertEquals(TransactionStatus.COMPLETED_SUCCESS, transaction.getStatus());
    }

    @Test
    public void shouldThrowExceptionDueToNoEnoughMoney() {
        accountFrom.setAmount(BigDecimal.TEN);

        try {
            transferService.doTransfer(transaction);
            fail();
        } catch (TransferException expected) {
            assertEquals("Transfer is not allowed.", expected.getMessage());
        }

        verify(daoService, never()).createNewTransaction(transaction);
        verify(daoService, times(2)).getAccountById(anyString());
        verify(daoService, never()).updateAccount(anyString(), any());
    }

    @Test
    public void shouldThrowExceptionDueToWrongUser() {
        accountFrom.setUser("WRONG_USER");

        try {
            transferService.doTransfer(transaction);
            fail();
        } catch (TransferException expected) {
            assertEquals("Transfer is not allowed.", expected.getMessage());
        }

        verify(daoService, never()).createNewTransaction(transaction);
        verify(daoService, times(2)).getAccountById(anyString());
        verify(daoService, never()).updateAccount(anyString(), any());
    }

    @Test
    public void shouldThrowExceptionDueToAccountNotFound() {
        accountTo.setUser(null);

        try {
            transferService.doTransfer(transaction);
            fail();
        } catch (TransferException expected) {
            assertEquals("Transfer is not allowed.", expected.getMessage());
        }

        verify(daoService, never()).createNewTransaction(transaction);
        verify(daoService, times(2)).getAccountById(anyString());
        verify(daoService, never()).updateAccount(anyString(), any());
    }

    @Test
    public void shouldThrowExceptionDueToNewTransactionCreationFail() {
        when(daoService.createNewTransaction(transaction)).thenReturn(0L);

        try {
            transferService.doTransfer(transaction);
            fail();
        } catch (TransferException expected) {
            assertEquals("Transaction creation failed.", expected.getMessage());
        }

        verify(daoService, times(1)).createNewTransaction(transaction);
        verify(daoService, times(2)).getAccountById(anyString());
        verify(daoService, never()).updateAccount(anyString(), any());
    }

    @Test
    public void shouldThrowExceptionDueToAccountUpdateFailed() {
        when(daoService.updateAccount(anyString(), any())).thenReturn(false);

        try {
            transferService.doTransfer(transaction);
            fail();
        } catch (TransferException expected) {
            assertEquals("Transfer failed during processing.", expected.getMessage());
        }

        verify(daoService, atLeastOnce()).createNewTransaction(transaction);
        verify(daoService, times(2)).getAccountById(anyString());
        verify(daoService, atLeastOnce()).updateAccount(anyString(), any());

        assertEquals(TransactionStatus.COMPLETED_FAILED, transaction.getStatus());
    }

}
