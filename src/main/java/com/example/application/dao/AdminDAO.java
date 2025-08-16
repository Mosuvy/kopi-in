package com.example.application.dao;

import com.example.application.models.*;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static com.example.application.Koneksi.koneksi.getConnection;

@Repository
public class AdminDAO {

    Connection connection;
    PreparedStatement statement;
    ResultSet resultSet;

    public AdminDAO() {
        connection = getConnection();
    }

    // ==================== DASHBOARD STATS ====================

    /**
     * Get today's total sales amount
     */
    public double getTodayTotalSales() {
        double total = 0.0;
        try {
            String sql = "SELECT COALESCE(SUM(final_price), 0) as total " +
                    "FROM orders " +
                    "WHERE DATE(created_at) = CURDATE() AND status = 'accepted'";

            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                total = resultSet.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error getting today's total sales: " + e.getMessage());
            e.printStackTrace();
        }
        return total;
    }

    /**
     * Get count of active orders today
     */
    public int getActiveOrdersCount() {
        int count = 0;
        try {
            String sql = "SELECT COUNT(*) as count " +
                    "FROM orders " +
                    "WHERE DATE(created_at) = CURDATE() AND status = 'processing'";

            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting active orders count: " + e.getMessage());
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Get count of available menu items
     */
    public int getAvailableMenuCount() {
        int count = 0;
        try {
            String sql = "SELECT COUNT(*) as count FROM products WHERE is_active = 1";

            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting available menu count: " + e.getMessage());
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Get count of online customers (users with recent activity)
     */
    public int getOnlineCustomersCount() {
        int count = 0;
        try {
            String sql = "SELECT COUNT(DISTINCT user_id) as count " +
                    "FROM orders " +
                    "WHERE created_at >= DATE_SUB(NOW(), INTERVAL 1 HOUR)";

            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting online customers count: " + e.getMessage());
            e.printStackTrace();
        }
        return count;
    }

    // ==================== SALES CHART DATA ====================

    /**
     * Get sales data for the last 7 days
     */
    public List<DailySales> getWeeklySalesData() {
        List<DailySales> salesData = new ArrayList<>();
        try {
            String sql = "SELECT DATE(created_at) as sale_date, " +
                    "COALESCE(SUM(final_price), 0) as daily_total " +
                    "FROM orders " +
                    "WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
                    "AND status = 'accepted' " +
                    "GROUP BY DATE(created_at) " +
                    "ORDER BY sale_date";

            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                DailySales sales = new DailySales();
                sales.setDate(resultSet.getDate("sale_date").toLocalDate());
                sales.setTotal(resultSet.getDouble("daily_total"));
                salesData.add(sales);
            }
        } catch (SQLException e) {
            System.err.println("Error getting weekly sales data: " + e.getMessage());
            e.printStackTrace();
        }
        return salesData;
    }

    /**
     * Get sales trend percentage compared to previous week
     */
    public double getSalesTrendPercentage() {
        double percentage = 0.0;
        try {
            String sql = "SELECT " +
                    "(SELECT COALESCE(SUM(final_price), 0) FROM orders " +
                    " WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
                    " AND status = 'accepted') as current_week, " +
                    "(SELECT COALESCE(SUM(final_price), 0) FROM orders " +
                    " WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 13 DAY) " +
                    " AND created_at < DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
                    " AND status = 'accepted') as previous_week";

            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                double currentWeek = resultSet.getDouble("current_week");
                double previousWeek = resultSet.getDouble("previous_week");
                if (previousWeek > 0) {
                    percentage = ((currentWeek - previousWeek) / previousWeek) * 100;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting sales trend: " + e.getMessage());
            e.printStackTrace();
        }
        return percentage;
    }

    // ==================== POPULAR MENU DATA ====================

    /**
     * Get top 5 most popular menu items today from orderitems table
     */
    public List<PopularMenuItem> getTodayPopularMenu() {
        List<PopularMenuItem> popularItems = new ArrayList<>();
        try {
            String sql = "SELECT p.name, " +
                    "COUNT(oi.product_id) as order_count, " +
                    "ROUND((COUNT(oi.product_id) * 100.0 / (SELECT COUNT(*) FROM orderitems oi2 " +
                    "INNER JOIN orders o2 ON oi2.order_id = o2.id " +
                    "WHERE DATE(o2.created_at) = CURDATE())), 1) as percentage " +
                    "FROM orderitems oi " +
                    "INNER JOIN orders o ON oi.order_id = o.id " +
                    "INNER JOIN products p ON oi.product_id = p.id " +
                    "WHERE DATE(o.created_at) = CURDATE() " +
                    "AND o.status IN ('processing', 'accepted', 'completed') " +
                    "GROUP BY oi.product_id, p.name " +
                    "ORDER BY order_count DESC " +
                    "LIMIT 5";

            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                PopularMenuItem item = new PopularMenuItem();
                item.setName(resultSet.getString("name"));
                item.setOrderCount(resultSet.getInt("order_count"));
                item.setPercentage(resultSet.getInt("percentage"));
                popularItems.add(item);
            }

            // If no data found for today, get sample data or last week's data
            if (popularItems.isEmpty()) {
                popularItems = getLastWeekPopularMenu();
            }

        } catch (SQLException e) {
            System.err.println("Error getting today's popular menu: " + e.getMessage());
            e.printStackTrace();

            // Fallback: try to get last week's data
            try {
                popularItems = getLastWeekPopularMenu();
            } catch (Exception fallbackEx) {
                System.err.println("Fallback also failed: " + fallbackEx.getMessage());
                // Return empty list if all fails
                popularItems = new ArrayList<>();
            }
        }
        return popularItems;
    }

    /**
     * Fallback method to get popular menu from last week if today has no data
     */
    private List<PopularMenuItem> getLastWeekPopularMenu() {
        List<PopularMenuItem> popularItems = new ArrayList<>();
        try {
            String sql = "SELECT p.name, " +
                    "COUNT(oi.product_id) as order_count, " +
                    "ROUND((COUNT(oi.product_id) * 100.0 / (SELECT COUNT(*) FROM orderitems oi2 " +
                    "INNER JOIN orders o2 ON oi2.order_id = o2.id " +
                    "WHERE o2.created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY))), 1) as percentage " +
                    "FROM orderitems oi " +
                    "INNER JOIN orders o ON oi.order_id = o.id " +
                    "INNER JOIN products p ON oi.product_id = p.id " +
                    "WHERE o.created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                    "AND o.status IN ('processing', 'accepted', 'completed') " +
                    "GROUP BY oi.product_id, p.name " +
                    "ORDER BY order_count DESC " +
                    "LIMIT 5";

            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                PopularMenuItem item = new PopularMenuItem();
                item.setName(resultSet.getString("name"));
                item.setOrderCount(resultSet.getInt("order_count"));
                item.setPercentage(resultSet.getInt("percentage"));
                popularItems.add(item);
            }

        } catch (SQLException e) {
            System.err.println("Error getting last week's popular menu: " + e.getMessage());
        }
        return popularItems;
    }

    /**
     * Get popular menu items with detailed statistics
     */
    public List<PopularMenuItemDetailed> getTodayPopularMenuDetailed() {
        List<PopularMenuItemDetailed> popularItems = new ArrayList<>();
        try {
            String sql = "SELECT p.id, p.name, p.price, " +
                    "COUNT(oi.product_id) as order_count, " +
                    "SUM(oi.quantity) as total_quantity, " +
                    "SUM(oi.price * oi.quantity) as total_revenue, " +
                    "ROUND((COUNT(oi.product_id) * 100.0 / (SELECT COUNT(*) FROM orderitems oi2 " +
                    "INNER JOIN orders o2 ON oi2.order_id = o2.id " +
                    "WHERE DATE(o2.created_at) = CURDATE())), 1) as percentage " +
                    "FROM orderitems oi " +
                    "INNER JOIN orders o ON oi.order_id = o.id " +
                    "INNER JOIN products p ON oi.product_id = p.id " +
                    "WHERE DATE(o.created_at) = CURDATE() " +
                    "AND o.status IN ('processing', 'accepted', 'completed') " +
                    "GROUP BY oi.product_id, p.id, p.name, p.price " +
                    "ORDER BY order_count DESC " +
                    "LIMIT 10";

            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                PopularMenuItemDetailed item = new PopularMenuItemDetailed();
                item.setProductId(resultSet.getString("id"));
                item.setName(resultSet.getString("name"));
                item.setPrice(resultSet.getDouble("price"));
                item.setOrderCount(resultSet.getInt("order_count"));
                item.setTotalQuantity(resultSet.getInt("total_quantity"));
                item.setTotalRevenue(resultSet.getDouble("total_revenue"));
                item.setPercentage(resultSet.getDouble("percentage"));
                popularItems.add(item);
            }

        } catch (SQLException e) {
            System.err.println("Error getting detailed popular menu: " + e.getMessage());
        }
        return popularItems;
    }

    /**
     * Get menu performance comparison (today vs yesterday)
     */
    public List<MenuPerformanceComparison> getMenuPerformanceComparison() {
        List<MenuPerformanceComparison> comparisons = new ArrayList<>();
        try {
            String sql = "SELECT p.name, " +
                    "COALESCE(today.order_count, 0) as today_orders, " +
                    "COALESCE(yesterday.order_count, 0) as yesterday_orders, " +
                    "CASE " +
                    "  WHEN yesterday.order_count > 0 THEN " +
                    "    ROUND(((today.order_count - yesterday.order_count) * 100.0 / yesterday.order_count), 1) " +
                    "  ELSE 100.0 " +
                    "END as growth_percentage " +
                    "FROM products p " +
                    "LEFT JOIN ( " +
                    "  SELECT oi.product_id, COUNT(*) as order_count " +
                    "  FROM orderitems oi " +
                    "  INNER JOIN orders o ON oi.order_id = o.id " +
                    "  WHERE DATE(o.created_at) = CURDATE() " +
                    "  AND o.status IN ('processing', 'accepted', 'completed') " +
                    "  GROUP BY oi.product_id " +
                    ") today ON p.id = today.product_id " +
                    "LEFT JOIN ( " +
                    "  SELECT oi.product_id, COUNT(*) as order_count " +
                    "  FROM orderitems oi " +
                    "  INNER JOIN orders o ON oi.order_id = o.id " +
                    "  WHERE DATE(o.created_at) = DATE_SUB(CURDATE(), INTERVAL 1 DAY) " +
                    "  AND o.status IN ('processing', 'accepted', 'completed') " +
                    "  GROUP BY oi.product_id " +
                    ") yesterday ON p.id = yesterday.product_id " +
                    "WHERE p.is_active = 1 " +
                    "AND (today.order_count > 0 OR yesterday.order_count > 0) " +
                    "ORDER BY today_orders DESC, growth_percentage DESC " +
                    "LIMIT 10";

            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                MenuPerformanceComparison comparison = new MenuPerformanceComparison();
                comparison.setMenuName(resultSet.getString("name"));
                comparison.setTodayOrders(resultSet.getInt("today_orders"));
                comparison.setYesterdayOrders(resultSet.getInt("yesterday_orders"));
                comparison.setGrowthPercentage(resultSet.getDouble("growth_percentage"));
                comparisons.add(comparison);
            }

        } catch (SQLException e) {
            System.err.println("Error getting menu performance comparison: " + e.getMessage());
        }
        return comparisons;
    }

// ==================== ADDITIONAL HELPER CLASSES ====================

    public static class PopularMenuItemDetailed {
        private String productId;
        private String name;
        private double price;
        private int orderCount;
        private int totalQuantity;
        private double totalRevenue;
        private double percentage;

        // Getters and Setters
        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        public int getOrderCount() { return orderCount; }
        public void setOrderCount(int orderCount) { this.orderCount = orderCount; }
        public int getTotalQuantity() { return totalQuantity; }
        public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }
        public double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
        public double getPercentage() { return percentage; }
        public void setPercentage(double percentage) { this.percentage = percentage; }
    }

    public static class MenuPerformanceComparison {
        private String menuName;
        private int todayOrders;
        private int yesterdayOrders;
        private double growthPercentage;

        // Getters and Setters
        public String getMenuName() { return menuName; }
        public void setMenuName(String menuName) { this.menuName = menuName; }
        public int getTodayOrders() { return todayOrders; }
        public void setTodayOrders(int todayOrders) { this.todayOrders = todayOrders; }
        public int getYesterdayOrders() { return yesterdayOrders; }
        public void setYesterdayOrders(int yesterdayOrders) { this.yesterdayOrders = yesterdayOrders; }
        public double getGrowthPercentage() { return growthPercentage; }
        public void setGrowthPercentage(double growthPercentage) { this.growthPercentage = growthPercentage; }
    }

    // ==================== FEEDBACK MANAGEMENT ====================

    /**
     * Get all feedback with user information
     */
    public List<Feedback> getAllFeedback() {
        List<Feedback> feedbackList = new ArrayList<>();
        try {
            // Check if feedbacks table exists
            String checkTableSql = "SHOW TABLES LIKE 'feedbacks'";
            statement = connection.prepareStatement(checkTableSql);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String sql = "SELECT f.*, u.username " +
                        "FROM feedbacks f " +
                        "LEFT JOIN users u ON f.user_id = u.id " +
                        "ORDER BY f.submitted_at DESC";

                statement = connection.prepareStatement(sql);
                resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Feedback feedback = new Feedback();
                    feedback.setId(resultSet.getString("id"));
                    feedback.setUser_id(resultSet.getString("user_id"));
                    feedback.setMessage(resultSet.getString("message"));
                    feedback.setCreated_at(resultSet.getTimestamp("submitted_at"));
                    feedback.setStatus(resultSet.getString("status"));
                    feedbackList.add(feedback);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting feedback: " + e.getMessage());
        }
        return feedbackList;
    }

    /**
     * Get feedback count by status
     */
    public int getFeedbackCountByStatus(String status) {
        int count = 0;
        try {
            String sql = "SELECT COUNT(*) as count FROM feedbacks WHERE status = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, status);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting feedback count: " + e.getMessage());
        }
        return count;
    }

    /**
     * Update feedback status
     */
    public boolean updateFeedbackStatus(String feedbackId, String status) {
        try {
            String sql = "UPDATE feedbacks SET status = ? WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, status);
            statement.setString(2, feedbackId);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating feedback status: " + e.getMessage());
            return false;
        }
    }

    /**
     * Search feedback by keyword
     */
    public List<Feedback> searchFeedback(String keyword) {
        List<Feedback> feedbackList = new ArrayList<>();
        try {
            String sql = "SELECT f.*, u.username " +
                    "FROM feedbacks f " +
                    "LEFT JOIN users u ON f.user_id = u.id " +
                    "WHERE f.message LIKE ? OR u.username LIKE ? " +
                    "ORDER BY f.submitted_at DESC";

            statement = connection.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Feedback feedback = new Feedback();
                feedback.setId(resultSet.getString("id"));
                feedback.setUser_id(resultSet.getString("user_id"));
                feedback.setMessage(resultSet.getString("message"));
                feedback.setCreated_at(resultSet.getTimestamp("submitted_at"));
                feedback.setStatus(resultSet.getString("status"));
                feedbackList.add(feedback);
            }
        } catch (SQLException e) {
            System.err.println("Error searching feedback: " + e.getMessage());
        }
        return feedbackList;
    }

    // ==================== USER MANAGEMENT ====================

    /**
     * Get total users count
     */
    public int getTotalUsersCount() {
        int count = 0;
        try {
            String sql = "SELECT COUNT(*) as count FROM users WHERE role = 'customer'";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting total users count: " + e.getMessage());
        }
        return count;
    }

    /**
     * Get new users count today
     */
    public int getNewUsersToday() {
        int count = 0;
        try {
            String sql = "SELECT COUNT(*) as count " +
                    "FROM users " +
                    "WHERE DATE(created_at) = CURDATE() AND role = 'customer'";

            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting new users today: " + e.getMessage());
        }
        return count;
    }

    // ==================== ORDER MANAGEMENT ====================

    /**
     * Get orders by status
     */
    public List<Orders> getOrdersByStatus(String status) {
        List<Orders> orders = new ArrayList<>();
        try {
            // Check if orders table exists
            String checkTableSql = "SHOW TABLES LIKE 'orders'";
            statement = connection.prepareStatement(checkTableSql);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String sql = "SELECT o.*, u.username " +
                        "FROM orders o " +
                        "LEFT JOIN users u ON o.user_id = u.id " +
                        "WHERE o.status = ? " +
                        "ORDER BY o.created_at DESC";

                statement = connection.prepareStatement(sql);
                statement.setString(1, status);
                resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Orders order = new Orders();
                    order.setId(resultSet.getString("id"));
                    order.setUser_id(resultSet.getString("user_id"));
                    order.setCreated_by(resultSet.getString("created_by"));
                    order.setStatus(resultSet.getString("status"));
                    order.setOrder_type(resultSet.getString("order_type"));
                    order.setTotal_price(resultSet.getDouble("total_price"));
                    order.setPromo_id(resultSet.getString("promo_id"));
                    order.setFinal_price(resultSet.getDouble("final_price"));
                    order.setCreated_at(resultSet.getTimestamp("created_at"));
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting orders by status: " + e.getMessage());
        }
        return orders;
    }

    /**
     * Get total orders count by date range
     */
    public int getOrdersCountByDateRange(LocalDate startDate, LocalDate endDate) {
        int count = 0;
        try {
            String sql = "SELECT COUNT(*) as count " +
                    "FROM orders " +
                    "WHERE DATE(created_at) BETWEEN ? AND ?";

            statement = connection.prepareStatement(sql);
            statement.setDate(1, Date.valueOf(startDate));
            statement.setDate(2, Date.valueOf(endDate));
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting orders count by date range: " + e.getMessage());
        }
        return count;
    }

    // ==================== ADDITIONAL ANALYTICS ====================

    /**
     * Get revenue by time period
     */
    public double getRevenueByPeriod(int days) {
        double total = 0.0;
        try {
            String sql = "SELECT COALESCE(SUM(final_price), 0) as total " +
                    "FROM orders " +
                    "WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL ? DAY) " +
                    "AND status = 'accepted'";

            statement = connection.prepareStatement(sql);
            statement.setInt(1, days);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                total = resultSet.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error getting revenue by period: " + e.getMessage());
        }
        return total;
    }

    /**
     * Get average order value
     */
    public double getAverageOrderValue() {
        double avgValue = 0.0;
        try {
            String sql = "SELECT COALESCE(AVG(final_price), 0) as avg_value " +
                    "FROM orders " +
                    "WHERE status = 'accepted' " +
                    "AND DATE(created_at) = CURDATE()";

            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                avgValue = resultSet.getDouble("avg_value");
            }
        } catch (SQLException e) {
            System.err.println("Error getting average order value: " + e.getMessage());
        }
        return avgValue;
    }

    /**
     * Get busiest hours of the day
     */
    public List<HourlyOrderCount> getBusiestHours() {
        List<HourlyOrderCount> hourlyData = new ArrayList<>();
        try {
            String sql = "SELECT HOUR(created_at) as hour, COUNT(*) as order_count " +
                    "FROM orders " +
                    "WHERE DATE(created_at) = CURDATE() " +
                    "GROUP BY HOUR(created_at) " +
                    "ORDER BY order_count DESC " +
                    "LIMIT 5";

            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                HourlyOrderCount hourlyCount = new HourlyOrderCount();
                hourlyCount.setHour(resultSet.getInt("hour"));
                hourlyCount.setOrderCount(resultSet.getInt("order_count"));
                hourlyData.add(hourlyCount);
            }
        } catch (SQLException e) {
            System.err.println("Error getting busiest hours: " + e.getMessage());
        }
        return hourlyData;
    }

    // ==================== HELPER CLASSES ====================

    public static class DailySales {
        private LocalDate date;
        private double total;

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public double getTotal() { return total; }
        public void setTotal(double total) { this.total = total; }
    }

    public static class PopularMenuItem {
        private String name;
        private int orderCount;
        private int percentage;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getOrderCount() { return orderCount; }
        public void setOrderCount(int orderCount) { this.orderCount = orderCount; }
        public int getPercentage() { return percentage; }
        public void setPercentage(int percentage) { this.percentage = percentage; }
    }

    public static class HourlyOrderCount {
        private int hour;
        private int orderCount;

        public int getHour() { return hour; }
        public void setHour(int hour) { this.hour = hour; }
        public int getOrderCount() { return orderCount; }
        public void setOrderCount(int orderCount) { this.orderCount = orderCount; }
    }
}
