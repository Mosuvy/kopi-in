package com.example.application.dao;

import com.example.application.dao.TransactionsDAO;
import com.example.application.models.Orders;
import com.example.application.models.OrderItems;
import com.example.application.models.Transactions;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO extends BaseDAO {
    // Statistik: jumlah order hari ini
    public int getTodayOrderCount() {
        try {
            ensureConnection();
            String query = "SELECT COUNT(*) FROM Orders WHERE DATE(created_at) = CURDATE()";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return 0;
    }

    // Statistik: total penjualan (final_price semua order)
    public double getTotalSales() {
        try {
            ensureConnection();
            String query = "SELECT SUM(final_price) FROM Orders";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return 0.0;
    }

    // Statistik: menu populer hari ini (top N produk berdasarkan jumlah order)
    public List<String[]> getPopularMenus(int limit) {
        List<String[]> popularMenus = new ArrayList<>();
        try {
            ensureConnection();
            String query = "SELECT oi.product_id, p.name, SUM(oi.quantity) as total_ordered " +
                    "FROM OrderItems oi JOIN Products p ON oi.product_id = p.id " +
                    "JOIN Orders o ON oi.order_id = o.id " +
                    "WHERE DATE(o.created_at) = CURDATE() " +
                    "GROUP BY oi.product_id, p.name " +
                    "ORDER BY total_ordered DESC LIMIT ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, limit);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String[] row = new String[3];
                row[0] = resultSet.getString("product_id");
                row[1] = resultSet.getString("name");
                row[2] = String.valueOf(resultSet.getInt("total_ordered"));
                popularMenus.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return popularMenus;
    }

    private TransactionsDAO transactionDAO = new TransactionsDAO();
    
    public String createOrder(Orders order) {
        try {
            ensureConnection();
            String query = "INSERT INTO Orders (user_id, created_by, status, order_type, total_price, promo_id, final_price) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, order.getUser_id());
            preparedStatement.setInt(2, order.getCreated_by());
            preparedStatement.setString(3, order.getStatus());
            preparedStatement.setString(4, order.getOrder_type());
            preparedStatement.setDouble(5, order.getTotal_price());
            preparedStatement.setString(6, order.getPromo_id());
            preparedStatement.setDouble(7, order.getFinal_price());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    return String.valueOf(resultSet.getInt(1));
                }
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeResources();
        }
    }

    public boolean addOrderItem(OrderItems item) {
        try {
            ensureConnection();
            String query = "INSERT INTO OrderItems (order_id, product_id, quantity, price, subtotal) " +
                         "VALUES (?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, item.getOrder_id());
            preparedStatement.setString(2, item.getProduct_id());
            preparedStatement.setInt(3, item.getQuantity());
            preparedStatement.setDouble(4, item.getPrice());
            preparedStatement.setDouble(5, item.getPrice() * item.getQuantity());

            int result = preparedStatement.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
    }

    public String createOrderWithItems(Orders order, List<OrderItems> items) throws SQLException {
        String orderId = null;
        try {
            ensureConnection();

            // 1. Create Order
            orderId = createOrderInternal(order);
            if (orderId == null) {
                throw new SQLException("Failed to create order");
            }

            // 2. Add Order Items
            for (OrderItems item : items) {
                item.setOrder_id(orderId);
                if (!addOrderItemInternal(item)) {
                    throw new SQLException("Failed to add order items");
                }
            }

            return orderId;
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        }
    }

    // Method internal untuk createOrder (modified)
    private String createOrderInternal(Orders order) throws SQLException {
        try {
            String query = "INSERT INTO Orders (user_id, created_by, status, order_type, total_price, promo_id, final_price) VALUES (?, ?, ?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, order.getUser_id());
            preparedStatement.setInt(2, order.getCreated_by());
            preparedStatement.setString(3, order.getStatus());
            String type = order.getOrder_type();
            if (type == null) {
                throw new SQLException("order_type is null");
            }
            type = type.trim().toLowerCase();
            if (!type.equals("online") && !type.equals("pos")) {
                throw new SQLException("Invalid order_type: " + type);
            }
            preparedStatement.setString(4, type);
            preparedStatement.setDouble(5, order.getTotal_price());
            preparedStatement.setString(6, order.getPromo_id());
            preparedStatement.setDouble(7, order.getFinal_price());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    return String.valueOf(resultSet.getInt(1));
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Method internal untuk addOrderItem (modified)
    private boolean addOrderItemInternal(OrderItems item) throws SQLException {
        try {
            String query = "INSERT INTO OrderItems (order_id, product_id, quantity, price, subtotal) VALUES (?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(item.getOrder_id()));
            preparedStatement.setString(2, item.getProduct_id());
            preparedStatement.setInt(3, item.getQuantity());
            preparedStatement.setDouble(4, item.getPrice());
            preparedStatement.setDouble(5, item.getPrice() * item.getQuantity());

            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Orders> getOrdersByStatus(String status) {
        try {
            return executeQuery(() -> {
                List<Orders> orders = new ArrayList<>();
                String query = "SELECT * FROM Orders WHERE status = ? ORDER BY created_at DESC";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, status);
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    Orders order = new Orders();
                    order.setId(resultSet.getString("id"));
                    order.setUser_id(resultSet.getInt("user_id"));
                    order.setCreated_by(resultSet.getInt("created_by"));
                    order.setStatus(resultSet.getString("status"));
                    order.setOrder_type(resultSet.getString("order_type"));
                    order.setTotal_price(resultSet.getDouble("total_price"));
                    order.setPromo_id(resultSet.getString("promo_id"));
                    order.setFinal_price(resultSet.getDouble("final_price"));
                    order.setCreated_at(resultSet.getTimestamp("created_at"));
                    orders.add(order);
                }
                return orders;
            });
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<OrderItems> getOrderItems(String orderId) {
        try {
            return executeQuery(() -> {
                List<OrderItems> items = new ArrayList<>();
                String query = "SELECT * FROM OrderItems WHERE order_id = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, orderId);
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    OrderItems item = new OrderItems();
                    item.setId(resultSet.getString("id"));
                    item.setOrder_id(resultSet.getString("order_id"));
                    item.setProduct_id(resultSet.getString("product_id"));
                    item.setQuantity(resultSet.getInt("quantity"));
                    item.setPrice(resultSet.getDouble("price"));
                    items.add(item);
                }
                return items;
            });
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean updateOrderStatus(String orderId, String status) {
        try {
            ensureConnection();
            String query = "UPDATE Orders SET status = ? WHERE id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, status);
            preparedStatement.setString(2, orderId);

            int result = preparedStatement.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
    }
} 