package com.example.application.dao;

import com.example.application.models.Products;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO extends BaseDAO {
    private Products product;

    public List<Products> getListProduct() {
        List<Products> productList = new ArrayList<>();
        try {
            ensureConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM Products");
            resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                product = new Products();
                product.setId(resultSet.getString("id"));
                product.setName(resultSet.getString("name"));
                product.setDescription(resultSet.getString("description"));
                product.setPrice(resultSet.getDouble("price"));
                product.setStock(resultSet.getInt("stock"));
                product.setPromo_id(resultSet.getString("promo_id"));
                product.setImage_url(resultSet.getString("image_url"));
                product.setCategory_id(resultSet.getString("category_id"));
                product.setIs_active(resultSet.getInt("is_active"));
                productList.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return productList;
    }

    public Products getProduct(String id) {
        Products product = null;
        try {
            ensureConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM Products WHERE id = ?");
            preparedStatement.setString(1, id);
            resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                product = new Products();
                product.setId(resultSet.getString("id"));
                product.setName(resultSet.getString("name"));
                product.setDescription(resultSet.getString("description"));
                product.setPrice(resultSet.getDouble("price"));
                product.setStock(resultSet.getInt("stock"));
                product.setPromo_id(resultSet.getString("promo_id"));
                product.setImage_url(resultSet.getString("image_url"));
                product.setCategory_id(resultSet.getString("category_id"));
                product.setIs_active(resultSet.getInt("is_active"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return product;
    }

    public boolean createProduct(Products product) {
        try {
            ensureConnection();
            String query = "INSERT INTO Products (id, name, description, price, promo_id, image_url, category_id, is_active) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, product.getId());
            preparedStatement.setString(2, product.getName());
            preparedStatement.setString(3, product.getDescription());
            preparedStatement.setDouble(4, product.getPrice());
            preparedStatement.setString(5, product.getPromo_id());
            preparedStatement.setString(6, product.getImage_url());
            preparedStatement.setInt(7, Integer.parseInt(product.getCategory_id()));
            preparedStatement.setInt(8, product.getIs_active());
            
            int result = preparedStatement.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
    }

    public boolean updateProduct(Products product) {
        try {
            ensureConnection();
            String query = "UPDATE Products SET name = ?, description = ?, price = ?, " +
                         "promo_id = ?, image_url = ?, category_id = ?, is_active = ? " +
                         "WHERE id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getDescription());
            preparedStatement.setDouble(3, product.getPrice());
            preparedStatement.setString(4, product.getPromo_id());
            preparedStatement.setString(5, product.getImage_url());
            preparedStatement.setInt(6, Integer.parseInt(product.getCategory_id()));
            preparedStatement.setInt(7, product.getIs_active());
            preparedStatement.setString(8, product.getId());
            
            int result = preparedStatement.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
    }

    public boolean deleteProduct(String id) {
        try {
            ensureConnection();
            String query = "UPDATE Products SET is_active = 0 WHERE id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, id);
            
            int result = preparedStatement.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
    }

    public boolean hardDeleteProduct(String id) {
        try {
            ensureConnection();
            String query = "DELETE FROM Products WHERE id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, id);
            
            int result = preparedStatement.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
    }

    public List<Products> getAllProducts() {
        try {
            return executeQuery(() -> {
                List<Products> products = new ArrayList<>();
                String query = "SELECT p.*, c.name as category_name FROM Products p " +
                             "LEFT JOIN Categories c ON p.category_id = c.id " +
                             "WHERE p.is_active = 1";
                preparedStatement = connection.prepareStatement(query);
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    Products product = new Products();
                    product.setId(resultSet.getString("id"));
                    product.setName(resultSet.getString("name"));
                    product.setDescription(resultSet.getString("description"));
                    product.setPrice(resultSet.getDouble("price"));
                    product.setPromo_id(resultSet.getString("promo_id"));
                    product.setImage_url(resultSet.getString("image_url"));
                    product.setCategory_id(String.valueOf(resultSet.getInt("category_id")));
                    product.setCategory_name(resultSet.getString("category_name"));
                    product.setIs_active(resultSet.getBoolean("is_active") ? 1 : 0);
                    product.setCreated_at(resultSet.getTimestamp("created_at"));
                    product.setUpdated_at(resultSet.getTimestamp("updated_at"));
                    products.add(product);
                }
                return products;
            });
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Products> getProductsByCategory(String categoryId) {
        try {
            return executeQuery(() -> {
                List<Products> products = new ArrayList<>();
                String query = "SELECT p.*, c.name as category_name FROM Products p " +
                             "LEFT JOIN Categories c ON p.category_id = c.id " +
                             "WHERE p.category_id = ? AND p.is_active = 1";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, Integer.parseInt(categoryId));
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    Products product = new Products();
                    product.setId(resultSet.getString("id"));
                    product.setName(resultSet.getString("name"));
                    product.setDescription(resultSet.getString("description"));
                    product.setPrice(resultSet.getDouble("price"));
                    product.setPromo_id(resultSet.getString("promo_id"));
                    product.setImage_url(resultSet.getString("image_url"));
                    product.setCategory_id(String.valueOf(resultSet.getInt("category_id")));
                    product.setCategory_name(resultSet.getString("category_name"));
                    product.setIs_active(resultSet.getBoolean("is_active") ? 1 : 0);
                    product.setCreated_at(resultSet.getTimestamp("created_at"));
                    product.setUpdated_at(resultSet.getTimestamp("updated_at"));
                    products.add(product);
                }
                return products;
            });
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Products getProductById(String id) {
        try {
            return executeQuery(() -> {
                Products product = null;
                String query = "SELECT p.*, c.name as category_name FROM Products p " +
                             "LEFT JOIN Categories c ON p.category_id = c.id " +
                             "WHERE p.id = ? AND p.is_active = 1";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, id);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    product = new Products();
                    product.setId(resultSet.getString("id"));
                    product.setName(resultSet.getString("name"));
                    product.setDescription(resultSet.getString("description"));
                    product.setPrice(resultSet.getDouble("price"));
                    product.setPromo_id(resultSet.getString("promo_id"));
                    product.setImage_url(resultSet.getString("image_url"));
                    product.setCategory_id(String.valueOf(resultSet.getInt("category_id")));
                    product.setCategory_name(resultSet.getString("category_name"));
                    product.setIs_active(resultSet.getBoolean("is_active") ? 1 : 0);
                    product.setCreated_at(resultSet.getTimestamp("created_at"));
                    product.setUpdated_at(resultSet.getTimestamp("updated_at"));
                }
                return product;
            });
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
