package com.demo.mtba.dao;

import com.demo.mtba.domain.Account;
import com.demo.mtba.domain.Transaction;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.h2.tools.Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.*;

public class DaoServiceH2Impl implements DaoService {

    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:mem:mtba;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    private Connection connection;

    @Override
    public long createNewTransaction(Transaction transaction) {
        transaction.setId(getNextTransactionId());
        String insertQuery =
                "INSERT INTO TRANSACTIONS (ID, USER, ACCOUNT_FROM, ACCOUNT_TO, AMOUNT, STATUS) values" + "(?,?,?,?,?,?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setLong(1, transaction.getId());
            preparedStatement.setString(2, transaction.getUser());
            preparedStatement.setString(3, transaction.getAccountFrom().getAccountId());
            preparedStatement.setString(4, transaction.getAccountTo().getAccountId());
            preparedStatement.setBigDecimal(5, transaction.getAmount());
            preparedStatement.setString(6, transaction.getStatus().toString());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transaction.getId();
    }

    @Override
    public boolean updateAccount(String accountId, BigDecimal newAmount) {
        String updateQuery =
                "UPDATE ACCOUNTS SET AMOUNT = (?) WHERE ACCOUNTS.ACCOUNT_ID = '" + accountId + "'";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setBigDecimal(1, newAmount);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.commit();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean updateTransactionStatus(Transaction transaction) {
        String updateQuery =
                "UPDATE TRANSACTIONS SET STATUS = (?) WHERE TRANSACTIONS.ID = " + transaction.getId();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, transaction.getStatus().toString());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.commit();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public Account getAccountById(String accountId) {
        Account account = new Account(accountId);
        String query =
                "SELECT * FROM ACCOUNTS WHERE ACCOUNTS.ACCOUNT_ID = (?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, accountId);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                account.setUser(rs.getString(2));
                account.setAmount(rs.getBigDecimal(3));
            }
            preparedStatement.close();
            connection.commit();
        } catch (SQLException e) {
            return null;
        }
        return account;
    }

    @Override
    public void initializeDB(boolean withServerAccess) {
        try {
            connection = getDBConnection();
            connection.setAutoCommit(true);
            buildDBStructure();
            if (withServerAccess) {
                openServerMode();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private long getNextTransactionId() {
        long result = 0L;
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT TRANSACTION_ID.NEXTVAL;");
            while (rs.next()) {
                result = rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void buildDBStructure() {
        try {
            connection.setAutoCommit(true);
            ScriptRunner runner = new ScriptRunner(connection);
            runner.runScript(new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("createDB.sql"))));
            runner.runScript(new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("insertDB.sql"))));

        } catch (SQLException e) {
            System.out.println("Exception Message " + e.getLocalizedMessage());
        }
    }

    private Connection getDBConnection() {
        Connection dbConnection = null;
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            return dbConnection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return dbConnection;
    }

    private void openServerMode() throws SQLException {
        Server server = Server.createTcpServer().start();
        System.out.println("DB Server started and connection is open at URL: jdbc:h2:" + server.getURL() + "/mem:mtba");
    }

}
