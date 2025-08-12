package com.example.application.dao;

import com.example.application.Koneksi.koneksi;
import com.example.application.models.Products;
import com.vaadin.flow.component.notification.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private Connection connection;

    public ProductDAO() {
        connection = koneksi.getConnection();
    }

    public List<Products> getListProduct() {
        List<Products> productList = new ArrayList<>();
        String sql = "SELECT * FROM Products";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Products product = mapResultSetToProduct(resultSet);
                productList.add(product);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting product list", e);
        }
        return productList;
    }

    public Products getProductById(String id) {
        String sql = "SELECT * FROM Products WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToProduct(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting product by id: " + id, e);
        }
        return null;
    }

    public boolean createProduct(Products product) {
        String sql = "INSERT INTO Products (name, description, price, promo_id, image_url, category_id, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setDouble(3, product.getPrice());
            statement.setString(4, product.getPromo_id());
            statement.setString(5, product.getImage_url());
            statement.setString(6, product.getCategory_id());
            statement.setInt(7, product.getIs_active() != null ? product.getIs_active() : 1);

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        product.setId(generatedKeys.getString(1));
                    }
                }
                // Tetap return true meskipun tidak dapat ID
                return true;
            }

            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating product: " + e.getMessage(), e);
        }
    }


    public boolean updateProduct(Products product) {
        String sql = "UPDATE Products SET name = ?, description = ?, price = ?, promo_id = ?, image_url = ?, " +
                "category_id = ?, is_active = ?, updated_at = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setDouble(3, product.getPrice());
            statement.setString(4, product.getPromo_id());
            statement.setString(5, product.getImage_url());
            statement.setString(6, product.getCategory_id());
            statement.setInt(7, product.getIs_active());
            statement.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
            statement.setString(9, product.getId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating product: " + product.getId(), e);
        }
    }

    public boolean deleteProduct(String id) {
        String sql = "DELETE FROM Products WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting product: " + id, e);
        }
    }

    private Products mapResultSetToProduct(ResultSet resultSet) throws SQLException {
        Products product = new Products();
        product.setId(resultSet.getString("id"));
        product.setName(resultSet.getString("name"));
        product.setDescription(resultSet.getString("description"));
        product.setPrice(resultSet.getDouble("price"));
        product.setPromo_id(resultSet.getString("promo_id"));
        product.setImage_url(resultSet.getString("image_url"));
        product.setCategory_id(resultSet.getString("category_id"));
        product.setIs_active(resultSet.getInt("is_active"));
        product.setCreated_at(resultSet.getTimestamp("created_at"));
        product.setUpdated_at(resultSet.getTimestamp("updated_at"));
        return product;
    }
}