package com.example.application.dao;

import com.example.application.Koneksi.koneksi;
import java.sql.*;

public class BaseDAO {
    protected Connection connection;
    protected PreparedStatement preparedStatement;
    protected ResultSet resultSet;

    public BaseDAO() {
        // Don't create connection in constructor
    }

    protected synchronized void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = koneksi.getConnection();
        }
    }

    protected void closeResources() {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            resultSet = null;
        }
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            preparedStatement = null;
        }
    }

    protected void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            connection = null;
        }
    }

    protected void closeAll() {
        closeResources();
        closeConnection();
    }

    // Helper method to execute queries with proper connection management
    protected <T> T executeQuery(SQLFunction<T> function) throws SQLException {
        try {
            ensureConnection();
            return function.apply();
        } catch (SQLException e) {
            throw e;
        } finally {
            closeResources();
        }
    }

    // Functional interface for SQL operations
    @FunctionalInterface
    protected interface SQLFunction<T> {
        T apply() throws SQLException;
    }
} 