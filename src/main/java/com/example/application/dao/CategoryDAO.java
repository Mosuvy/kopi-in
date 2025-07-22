package com.example.application.dao;

import com.example.application.Koneksi.koneksi;
import com.example.application.models.Categories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class CategoryDAO {
    Connection connection;
    PreparedStatement statement;
    ResultSet resultSet;
    ArrayList<Categories> categoryList;
    Categories category;

    public CategoryDAO() {
        connection = koneksi.getConnection();
    }

    public ArrayList<Categories> getListCategories() {
        categoryList = new ArrayList<>();
        try {
            statement = connection.prepareStatement("SELECT * FROM Categories",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                category = new Categories();
                category.setId(resultSet.getString("id"));
                category.setName(resultSet.getString("name"));
                category.setCode(resultSet.getString("code"));
                categoryList.add(category);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return categoryList;
    }

    public Categories getCategory(String id) {
        try {
            statement = connection.prepareStatement("SELECT * FROM Categories WHERE id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, id);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                category = new Categories();
                category.setId(resultSet.getString("id"));
                category.setName(resultSet.getString("name"));
                category.setCode(resultSet.getString("code"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return category;
    }

    public boolean createCategory(Categories category) {
        try {
            statement = connection.prepareStatement("INSERT INTO Categories (id, name, code) VALUES (?, ?, ?)",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, category.getId());
            statement.setString(2, category.getName());
            statement.setString(3, category.getCode());
            statement.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateCategory(Categories category) {
        try {
            statement = connection.prepareStatement("UPDATE Categories SET name = ?, code = ? WHERE id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, category.getName());
            statement.setString(2, category.getCode());
            statement.setString(3, category.getId());
            statement.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteCategory(String id) {
        try {
            statement = connection.prepareStatement("DELETE FROM Categories WHERE id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, id);
            statement.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
