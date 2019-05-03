package com.demo.mtba.service;

import com.demo.mtba.domain.Transaction;
import com.demo.mtba.domain.exceptions.TransferException;

public interface TransferService {
    String doTransfer(Transaction transaction) throws TransferException;
}
