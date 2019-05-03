package com.demo.mtba.domain;

import java.math.BigDecimal;

public class Transaction {

    private long id;
    private String user;
    private Account accountFrom;
    private Account accountTo;
    private BigDecimal amount;
    private TransactionStatus status;

    public Transaction(long id, String user, Account accountFrom, Account accountTo,
                       BigDecimal amount, TransactionStatus status) {
        this.id = id;
        this.user = user;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public Account getAccountFrom() {
        return accountFrom;
    }

    public Account getAccountTo() {
        return accountTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public static class Builder{
        private long id;
        private String user;
        private Account accountFrom;
        private Account accountTo;
        private BigDecimal amount;
        private TransactionStatus status;

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder user(String user) {
            this.user = user;
            return this;
        }

        public Builder accountFrom(Account accountFrom) {
            this.accountFrom = accountFrom;
            return this;
        }

        public Builder accountTo(Account accountTo) {
            this.accountTo = accountTo;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }
        public Builder status(TransactionStatus status) {
            this.status = status;
            return this;
        }

        public Transaction build() {
            return new Transaction(id, user, accountFrom, accountTo, amount, status);
        }
    }

}
