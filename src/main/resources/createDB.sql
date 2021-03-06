-- CREATE DATABASE STRUCTURE --

CREATE SEQUENCE TRANSACTION_ID;

CREATE TABLE TRANSACTIONS
(
    ID           LONG PRIMARY KEY,
    USER         VARCHAR2(255),
    ACCOUNT_FROM VARCHAR2(255),
    ACCOUNT_TO   VARCHAR2(255),
    AMOUNT       DECIMAL,
    STATUS       VARCHAR2(32)
);

CREATE TABLE ACCOUNTS
(
    ACCOUNT_ID   VARCHAR2(255) PRIMARY KEY,
    USER         VARCHAR2(255),
    AMOUNT       DECIMAL
);

COMMIT;
