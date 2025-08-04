package com.example.application.dao;

import com.example.application.models.Customer;
import com.example.application.models.Users;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import java.sql.*;
import java.util.ArrayList;

import static com.example.application.Koneksi.koneksi.getConnection;

public class UserDAO {
    Connection connection;
    PreparedStatement statement;
    ResultSet resultSet;
    ArrayList<Users> listUsers;
    ArrayList<Customer> listCustomer;
    Users Users;
    Customer Customer;

    public UserDAO() {
        connection = getConnection();
    }

    public ArrayList<Users> getListUsers() {
        listUsers = new ArrayList<>();
        try {
            statement = connection.prepareStatement("SELECT * FROM users",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Users = new Users();
                Users.setId(String.valueOf(resultSet.getInt("id")));
                Users.setUsername(resultSet.getString("username"));
                Users.setEmail(resultSet.getString("email"));
                Users.setPassword(resultSet.getString("password"));
                Users.setRole(convertRoleToDisplayFormat(resultSet.getString("role")));
                Users.setIs_active(resultSet.getInt("is_active"));
                Users.setCreated_at(resultSet.getTimestamp("created_at"));
                listUsers.add(Users);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return listUsers;
    }

    private String convertRoleToDatabaseFormat(String role) {
        if (role == null) return null;

        switch (role.toLowerCase()) {
            case "admin": return "admin";
            case "kasir": return "cashier";
            case "customer": return "customer";
            default: return role.toLowerCase();
        }
    }

    private String convertRoleToDisplayFormat(String role) {
        if (role == null) return null;

        switch (role.toLowerCase()) {
            case "admin": return "Admin";
            case "cashier": return "Kasir";
            case "customer": return "Customer";
            default: return role;
        }
    }

    public Users getUsers(String id) {
        try {
            statement = connection.prepareStatement("SELECT * FROM users WHERE id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setInt(1, Integer.parseInt(id));
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Users = new Users();
                Users.setId(String.valueOf(resultSet.getInt("id")));
                Users.setUsername(resultSet.getString("username"));
                Users.setEmail(resultSet.getString("email"));
                Users.setPassword(resultSet.getString("password"));
                Users.setRole(convertRoleToDisplayFormat(resultSet.getString("role")));
                Users.setIs_active(resultSet.getInt("is_active"));
                Users.setCreated_at(resultSet.getTimestamp("created_at"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Users;
    }

    public boolean createUsers(Users user) {
        try {
            String dbRole = convertRoleToDatabaseFormat(user.getRole());

            statement = connection.prepareStatement(
                    "INSERT INTO users (username, email, password, role, is_active, created_at) " +
                            "VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setString(4, dbRole);
            statement.setInt(5, user.getIs_active());
            statement.setTimestamp(6, user.getCreated_at());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(String.valueOf(generatedKeys.getInt(1)));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            Notification.show("Gagal menyimpan user: " + e.getMessage(), 5000,
                            Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }
        return false;
    }

    public boolean updateUsers(Users user) {
        try {
            String dbRole = convertRoleToDatabaseFormat(user.getRole());

            statement = connection.prepareStatement(
                    "UPDATE users SET username = ?, email = ?, password = ?, role = ?, is_active = ? " +
                            "WHERE id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setString(4, dbRole);
            statement.setInt(5, user.getIs_active());
            statement.setInt(6, Integer.parseInt(user.getId()));

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            Notification.show("Gagal mengupdate user: " + e.getMessage(), 5000,
                            Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }
    }

    public boolean deleteUsers(String id) {
        try {
            statement = connection.prepareStatement("DELETE FROM users WHERE id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setInt(1, Integer.parseInt(id));
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Customer> getListCustomer() {
        listCustomer = new ArrayList<>();
        try {
            statement = connection.prepareStatement("SELECT * FROM Customer",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Customer = new Customer();
                Customer.setId(resultSet.getString("id"));
                Customer.setUser_id(resultSet.getString("user_id"));
                Customer.setFull_name(resultSet.getString("full_name"));
                Customer.setAddress(resultSet.getString("address"));
                Customer.setPhone_number(resultSet.getString("phone_number"));
                Customer.setBirth_date(resultSet.getDate("birth_date"));
                listCustomer.add(Customer);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return listCustomer;
    }

    public Customer getCustomer(String id) {
        Customer customer = null;
        String sql = "SELECT * FROM Customer WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    customer = new Customer();
                    customer.setId(resultSet.getString("id"));
                    customer.setUser_id(resultSet.getString("user_id"));
                    customer.setFull_name(resultSet.getString("full_name"));
                    customer.setAddress(resultSet.getString("address"));
                    customer.setPhone_number(resultSet.getString("phone_number"));
                    customer.setBirth_date(resultSet.getDate("birth_date"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return customer;
    }

    public boolean createCustomer(Customer customer) {
        String sql = "INSERT INTO Customer (id, user_id, full_name, address, phone_number, birth_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customer.getId());
            statement.setString(2, customer.getUser_id());
            statement.setString(3, customer.getFull_name());
            statement.setString(4, customer.getAddress());
            statement.setString(5, customer.getPhone_number());
            statement.setDate(6, new java.sql.Date(customer.getBirth_date().getTime()));

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE Customer SET user_id = ?, full_name = ?, address = ?, phone_number = ?, birth_date = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customer.getUser_id());
            statement.setString(2, customer.getFull_name());
            statement.setString(3, customer.getAddress());
            statement.setString(4, customer.getPhone_number());
            statement.setDate(5, new java.sql.Date(customer.getBirth_date().getTime()));
            statement.setString(6, customer.getId());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean deleteCustomer(String id) {
        String sql = "DELETE FROM Customer WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}