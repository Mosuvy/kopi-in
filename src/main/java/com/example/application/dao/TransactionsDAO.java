package com.example.application.dao;

import com.example.application.models.TransactionHistory;
import com.example.application.models.Transactions;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionsDAO extends BaseDAO {

    public boolean createTransaction(Transactions transaction) {
        try {
            ensureConnection();
            String query = "INSERT INTO Transactions (order_id, payment_method, paid_amount, change_returned) " +
                    "VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(transaction.getOrder_id()));
            preparedStatement.setString(2, transaction.getPayment_method());
            preparedStatement.setDouble(3, transaction.getPaid_amount());
            preparedStatement.setDouble(4, transaction.getChange_returned());

            int result = preparedStatement.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
    }

    public Transactions getTransactionByOrderId(String orderId) {
        try {
            return executeQuery(() -> {
                String query = "SELECT * FROM Transactions WHERE order_id = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, orderId);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    Transactions transaction = new Transactions();
                    transaction.setId(resultSet.getString("id"));
                    transaction.setOrder_id(resultSet.getString("order_id"));
                    transaction.setPayment_method(resultSet.getString("payment_method"));
                    transaction.setPaid_amount(resultSet.getDouble("paid_amount"));
                    transaction.setChange_returned(resultSet.getDouble("change_returned"));
                    transaction.setPaid_at(resultSet.getTimestamp("paid_at"));
                    return transaction;
                }
                return null;
            });
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<TransactionHistory> getTransactionHistoryByUserId(int userId) {
        try {
            return executeQuery(() -> {
                List<TransactionHistory> historyList = new ArrayList<>();

                String query = "SELECT o.id AS order_id, t.id AS transaction_id, o.user_id, " +
                        "o.status, t.payment_method, o.final_price AS total_price, " +
                        "t.paid_amount, t.change_returned, o.created_at, t.paid_at " +
                        "FROM Orders o " +
                        "JOIN Transactions t ON o.id = t.order_id " +
                        "WHERE o.user_id = ? " +
                        "ORDER BY o.created_at DESC";

                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, userId);
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    TransactionHistory history = new TransactionHistory();
                    history.setOrderId(resultSet.getString("order_id"));
                    history.setTransactionId(resultSet.getString("transaction_id"));
                    history.setUserId(resultSet.getInt("user_id"));
                    history.setStatus(resultSet.getString("status"));
                    history.setPaymentMethod(resultSet.getString("payment_method"));
                    history.setTotalPrice(resultSet.getDouble("total_price"));
                    history.setPaidAmount(resultSet.getDouble("paid_amount"));
                    history.setChangeReturned(resultSet.getDouble("change_returned"));
                    history.setCreatedAt(resultSet.getTimestamp("created_at"));
                    history.setPaidAt(resultSet.getTimestamp("paid_at"));

                    historyList.add(history);
                }
                return historyList;
            });
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public TransactionHistory getTransactionHistoryByOrderId(String orderId) {
        try {
            return executeQuery(() -> {
                String query = "SELECT o.id AS order_id, t.id AS transaction_id, o.user_id, " +
                        "o.status, t.payment_method, o.final_price AS total_price, " +
                        "t.paid_amount, t.change_returned, o.created_at, t.paid_at " +
                        "FROM Orders o " +
                        "JOIN Transactions t ON o.id = t.order_id " +
                        "WHERE o.id = ?";

                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, orderId);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    TransactionHistory history = new TransactionHistory();
                    history.setOrderId(resultSet.getString("order_id"));
                    history.setTransactionId(resultSet.getString("transaction_id"));
                    history.setUserId(resultSet.getInt("user_id"));
                    history.setStatus(resultSet.getString("status"));
                    history.setPaymentMethod(resultSet.getString("payment_method"));
                    history.setTotalPrice(resultSet.getDouble("total_price"));
                    history.setPaidAmount(resultSet.getDouble("paid_amount"));
                    history.setChangeReturned(resultSet.getDouble("change_returned"));
                    history.setCreatedAt(resultSet.getTimestamp("created_at"));
                    history.setPaidAt(resultSet.getTimestamp("paid_at"));
                    return history;
                }
                return null;
            });
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}