package com.demo.mtba.domain;

import java.math.BigDecimal;

public class Account {

    private String accountId;
    private String user;
    private BigDecimal amount;

    public Account(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Account)) {
            return false;
        }

        Account a = (Account) o;

        return accountId.equals(a.accountId)
                && ((user == null && a.user == null) || user.equals(a.user))
                && ((amount == null && a.amount == null) || amount.equals(a.amount));
    }

}
