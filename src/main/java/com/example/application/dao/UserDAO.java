package com.example.application.dao;

import com.example.application.models.Customer;
import com.example.application.models.Users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    // -------------------- Users
    public ArrayList<Users> getListUsers() {
        listUsers = new ArrayList<>();
        try {
            statement = connection.prepareStatement("SELECT * FROM Users",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Users = new Users();
                Users.setId(resultSet.getString("id"));
                Users.setUsername(resultSet.getString("username"));
                Users.setEmail(resultSet.getString("email"));
                Users.setPassword(resultSet.getString("password"));
                Users.setRole(resultSet.getString("role"));
                Users.setIs_active(resultSet.getInt("is_active"));
                Users.setCreated_at(resultSet.getTimestamp("created_at"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return listUsers;
    }

    public Users getUsers(String id) {
        listUsers = new ArrayList<>();
        try {
            statement = connection.prepareStatement("SELECT * FROM Users WHERE id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, id);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Users = new Users();
                Users.setId(resultSet.getString("id"));
                Users.setUsername(resultSet.getString("username"));
                Users.setEmail(resultSet.getString("email"));
                Users.setPassword(resultSet.getString("password"));
                Users.setRole(resultSet.getString("role"));
                Users.setIs_active(resultSet.getInt("is_active"));
                Users.setCreated_at(resultSet.getTimestamp("created_at"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Users;
    }

    public boolean createUsers(Users Users) {
        try {
            statement = connection.prepareStatement("INSERT INTO Users (id, username, email, password, role, is_active" +
                            " VALUES (?, ?, ?, ?, ?, ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, Users.getId());
            statement.setString(2, Users.getUsername());
            statement.setString(3, Users.getEmail());
            statement.setString(4, Users.getPassword());
            statement.setString(5, Users.getRole());
            statement.setInt(6, Users.getIs_active());
            statement.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateUsers(Users Users) {
        try {
            statement = connection.prepareStatement("UPDATE Users SET username = ?, email = ?, password = ?, role = ?, is_active" +
                            "WHERE id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, Users.getUsername());
            statement.setString(2, Users.getEmail());
            statement.setString(3, Users.getPassword());
            statement.setString(4, Users.getRole());
            statement.setInt(5, Users.getIs_active());
            statement.setString(6, Users.getId());
            statement.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteUsers(String id) {
        try {
            statement = connection.prepareStatement("DELETE FROM Users WHERE id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, id);
            statement.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // -------------------- Customers
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