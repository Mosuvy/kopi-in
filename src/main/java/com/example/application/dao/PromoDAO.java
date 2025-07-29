package com.example.application.dao;

import com.example.application.models.Promo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PromoDAO extends BaseDAO {
    public List<Promo> getAllPromos() {
        try {
            return executeQuery(() -> {
                List<Promo> promos = new ArrayList<>();
                String query = "SELECT * FROM Promo WHERE (end_date >= CURRENT_DATE OR end_date IS NULL)";
                preparedStatement = connection.prepareStatement(query);
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    Promo promo = new Promo();
                    promo.setId(resultSet.getString("id"));
                    promo.setName(resultSet.getString("name"));
                    promo.setCode(resultSet.getString("code"));
                    promo.setDescription(resultSet.getString("description"));
                    promo.setDiscount_value(resultSet.getDouble("discount_value"));
                    promo.setMin_purchase(resultSet.getDouble("min_purchase"));
                    promo.setStart_date(resultSet.getDate("start_date"));
                    promo.setEnd_date(resultSet.getDate("end_date"));
                    promos.add(promo);
                }
                return promos;
            });
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Promo getPromoById(String id) {
        try {
            return executeQuery(() -> {
                Promo promo = null;
                String query = "SELECT * FROM Promo WHERE id = ? AND (end_date >= CURRENT_DATE OR end_date IS NULL)";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, id);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    promo = new Promo();
                    promo.setId(resultSet.getString("id"));
                    promo.setName(resultSet.getString("name"));
                    promo.setCode(resultSet.getString("code"));
                    promo.setDescription(resultSet.getString("description"));
                    promo.setDiscount_value(resultSet.getDouble("discount_value"));
                    promo.setMin_purchase(resultSet.getDouble("min_purchase"));
                    promo.setStart_date(resultSet.getDate("start_date"));
                    promo.setEnd_date(resultSet.getDate("end_date"));
                }
                return promo;
            });
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
