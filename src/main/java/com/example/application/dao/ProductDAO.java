package com.example.application.dao;

import com.example.application.Koneksi.koneksi;
import com.example.application.models.Products;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    Connection connection;
    PreparedStatement statement;
    ResultSet resultSet;
    ArrayList<Products> productList;
    Products product;

    public ProductDAO() {
        connection = koneksi.getConnection();
    }

    public ArrayList<Products> getListProduct() {
        productList = new ArrayList<>();
        try {
            statement = connection.prepareStatement("SELECT * FROM Products",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            resultSet = statement.executeQuery();
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return productList;
    }

    public Products getProduct(String id) {
        try {
            statement = connection.prepareStatement("SELECT * FROM Products WHERE id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, id);
            resultSet = statement.executeQuery();
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return product;
    }

    public boolean createProduct(Products product) {
        try {
            statement = connection.prepareStatement("INSERT INTO Products (id, name, description, price, stock, promo_id, image_url, category_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, product.getId());
            statement.setString(2, product.getName());
            statement.setString(3, product.getDescription());
            statement.setDouble(4, product.getPrice());
            statement.setInt(5, product.getStock());
            statement.setString(6, product.getPromo_id());
            statement.setString(7, product.getImage_url());
            statement.setString(8, product.getCategory_id());
            statement.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateProduct(Products product) {
        try {
            statement = connection.prepareStatement("UPDATE Products SET name = ?, description = ?, price = ?, stock = ?, promo_id = ?, image_url = ?, category_id = ?, is_active = ? WHERE id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setDouble(3, product.getPrice());
            statement.setInt(4, product.getStock());
            statement.setString(5, product.getPromo_id());
            statement.setString(6, product.getImage_url());
            statement.setString(7, product.getCategory_id());
            statement.setInt(8, product.getIs_active());
            statement.setString(9, product.getId());
            statement.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteProduct(String id) {
        try {
            statement = connection.prepareStatement("DELETE FROM Products WHERE id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, id);
            statement.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Products> getAllProducts() {
        List<Products> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE is_active = true ORDER BY name";
        
        try {
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                product = new Products();
                product.setId(resultSet.getString("id"));
                product.setName(resultSet.getString("name"));
                product.setDescription(resultSet.getString("description"));
                product.setPrice(resultSet.getDouble("price"));
                product.setCategory_id(resultSet.getString("category_id"));
                product.setImage_url(resultSet.getString("image_url"));
                product.setIs_active(resultSet.getInt("is_active"));
                products.add(product);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return products;
    }
    
    public Products getProductById(String id) {
        String sql = "SELECT * FROM products WHERE id = ? AND is_active = true";
        
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, id);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                product = new Products();
                product.setId(resultSet.getString("id"));
                product.setName(resultSet.getString("name"));
                product.setDescription(resultSet.getString("description"));
                product.setPrice(resultSet.getDouble("price"));
                product.setCategory_id(resultSet.getString("category_id"));
                product.setImage_url(resultSet.getString("image_url"));
                product.setIs_active(resultSet.getInt("is_active"));
                return product;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
