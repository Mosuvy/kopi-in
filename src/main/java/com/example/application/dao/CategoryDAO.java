package com.example.application.dao;

import com.example.application.models.Categories;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO extends BaseDAO {

    public List<Categories> getAllCategories() {
        List<Categories> categories = new ArrayList<>();
        try {
            ensureConnection();
            String query = "SELECT * FROM Categories ORDER BY name";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Categories category = new Categories();
                category.setId(resultSet.getString("id"));
                category.setName(resultSet.getString("name"));
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return categories;
    }

    public Categories getCategoryById(String id) {
        Categories category = null;
        try {
            ensureConnection();
            String query = "SELECT * FROM Categories WHERE id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                category = new Categories();
                category.setId(resultSet.getString("id"));
                category.setName(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return category;
    }
}
