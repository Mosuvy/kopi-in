package com.example.application.dao;

import com.example.application.Koneksi.koneksi;
import com.example.application.models.Promo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PromoDAO {
    Connection connection;
    PreparedStatement statement;
    ResultSet resultSet;
    ArrayList<Promo> promoArrayList;
    Promo promo;

    public PromoDAO() {
        connection = koneksi.getConnection();
    }

    public ArrayList<Promo> getListPromo() {
        promoArrayList = new ArrayList<>();
        try {
            statement = connection.prepareStatement("SELECT * FROM Promo",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                promo = new Promo();
                promo.setId(resultSet.getString("id"));
                promo.setName(resultSet.getString("name"));
                promo.setDescription(resultSet.getString("description"));
                promo.setDiscount_value(resultSet.getDouble("discount_value"));
                promo.setMin_purchase(resultSet.getDouble("min_purchase"));
                promo.setStart_date(resultSet.getDate("start_date"));
                promo.setEnd_date(resultSet.getDate("end_date"));
                promoArrayList.add(promo);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return promoArrayList;
    }

    public Promo getPromo(String id) {
        promoArrayList = new ArrayList<>();
        try {
            statement = connection.prepareStatement("SELECT * FROM Promo WHERE id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, id);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                promo = new Promo();
                promo.setId(resultSet.getString("id"));
                promo.setName(resultSet.getString("name"));
                promo.setDescription(resultSet.getString("description"));
                promo.setDiscount_value(resultSet.getDouble("discount_value"));
                promo.setMin_purchase(resultSet.getDouble("min_purchase"));
                promo.setStart_date(resultSet.getDate("start_date"));
                promo.setEnd_date(resultSet.getDate("end_date"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return promo;
    }

    public boolean createPromo(Promo promo) {
        try {
            statement = connection.prepareStatement("INSERT INTO Promo (id, name, description, discount_value, min_purchase, start_date, end_date",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, promo.getId());
            statement.setString(2, promo.getName());
            statement.setString(3, promo.getDescription());
            statement.setDouble(4, promo.getDiscount_value());
            statement.setDouble(5, promo.getMin_purchase());
            statement.setDate(6, promo.getStart_date());
            statement.setDate(7, promo.getEnd_date());
            statement.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updatePromo(Promo promo) {
        try {
            statement = connection.prepareStatement("UPDATE Promo SET name = ?, description = ?, discount_value = ?, min_purchase = ?, start_date = ?, end_date" +
                    "WHERE id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, promo.getName());
            statement.setString(2, promo.getDescription());
            statement.setDouble(3, promo.getDiscount_value());
            statement.setDouble(4, promo.getMin_purchase());
            statement.setDate(5, promo.getStart_date());
            statement.setDate(6, promo.getEnd_date());
            statement.setString(7, promo.getId());
            statement.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deletePromo(String id) {
        try {
            statement = connection.prepareStatement("DELETE FROM Promo WHERE id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, id);
            statement.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
