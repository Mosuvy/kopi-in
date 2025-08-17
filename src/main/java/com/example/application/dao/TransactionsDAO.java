package com.example.application.dao;

import com.example.application.models.Transactions;
import java.sql.*;

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
}