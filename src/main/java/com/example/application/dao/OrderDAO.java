package com.example.application.dao;

import com.example.application.models.Orders;
import com.example.application.models.OrderItems;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO extends BaseDAO {
    
    public String createOrder(Orders order) {
        String sql = "INSERT INTO Orders (user_id, created_by, status, order_type, total_price, promo_id, final_price, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, order.getUser_id() != null ? order.getUser_id() : 1); // Default guest user
            preparedStatement.setInt(2, order.getCreated_by() != null ? order.getCreated_by() : 1);
            preparedStatement.setString(3, "processing"); // Default status for new orders
            preparedStatement.setString(4, "pos"); // Default type for cashier orders
            preparedStatement.setDouble(5, order.getTotal_price());
            preparedStatement.setString(6, order.getPromo_id());
            preparedStatement.setDouble(7, order.getFinal_price());
            preparedStatement.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
            
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return String.valueOf(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeResources();
        }
    }

    public boolean addOrderItem(OrderItems item) {
        String sql = "INSERT INTO OrderItems (order_id, product_id, quantity, price, subtotal) VALUES (?, ?, ?, ?, ?)";
        
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, Integer.parseInt(item.getOrder_id()));
            preparedStatement.setString(2, item.getProduct_id());
            preparedStatement.setInt(3, item.getQuantity());
            preparedStatement.setDouble(4, item.getPrice());
            preparedStatement.setDouble(5, item.getPrice() * item.getQuantity());
            
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
    }

    public List<Orders> getOrdersByStatus(String status) {
        List<Orders> ordersList = new ArrayList<>();
        String sql = "SELECT * FROM Orders WHERE status = ? ORDER BY created_at DESC";
        
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, status);
            resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                Orders order = new Orders();
                order.setId(String.valueOf(resultSet.getInt("id")));
                order.setUser_id(resultSet.getInt("user_id"));
                order.setCreated_by(resultSet.getInt("created_by"));
                order.setStatus(resultSet.getString("status"));
                order.setOrder_type(resultSet.getString("order_type"));
                order.setTotal_price(resultSet.getDouble("total_price"));
                order.setPromo_id(resultSet.getString("promo_id"));
                order.setFinal_price(resultSet.getDouble("final_price"));
                order.setCreated_at(resultSet.getTimestamp("created_at"));
                ordersList.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return ordersList;
    }

    public List<OrderItems> getOrderItems(String orderId) {
        List<OrderItems> items = new ArrayList<>();
        String sql = "SELECT * FROM OrderItems WHERE order_id = ?";
        
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, Integer.parseInt(orderId));
            resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                OrderItems item = new OrderItems();
                item.setId(String.valueOf(resultSet.getInt("id")));
                item.setOrder_id(orderId);
                item.setProduct_id(resultSet.getString("product_id"));
                item.setQuantity(resultSet.getInt("quantity"));
                item.setPrice(resultSet.getDouble("price"));
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return items;
    }

    public boolean updateOrderStatus(String orderId, String status) {
        String sql = "UPDATE Orders SET status = ? WHERE id = ?";
        
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, Integer.parseInt(orderId));
            
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
    }
} 