package com.example.application.dao;

import com.example.application.Koneksi.koneksi;
import com.example.application.models.Promo;

import java.sql.*;
import java.util.ArrayList;

public class PromoDAO {
    private Connection connection;

    public PromoDAO() {
        connection = koneksi.getConnection();
    }

    public ArrayList<Promo> getListPromo() {
        ArrayList<Promo> promoList = new ArrayList<>();
        String query = "SELECT * FROM promo";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Promo promo = new Promo();
                promo.setId(rs.getString("id"));
                promo.setName(rs.getString("name"));
                promo.setCode(rs.getString("code"));
                promo.setDescription(rs.getString("description"));
                double discountValue = rs.getDouble("discount_value");
                if (rs.wasNull()) {
                    promo.setDiscount_value(null);
                } else {
                    promo.setDiscount_value(discountValue);
                }
                double minPurchase = rs.getDouble("min_purchase");
                if (rs.wasNull()) {
                    promo.setMin_purchase(null);
                } else {
                    promo.setMin_purchase(minPurchase);
                }
                promo.setStart_date(rs.getDate("start_date"));
                promo.setEnd_date(rs.getDate("end_date"));
                promoList.add(promo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return promoList;
    }

    public String generateIdPromo() {
        String prefix = "PRM";
        int nextNumber = 1;

        String query = "SELECT id FROM promo ORDER BY id DESC LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString("id"); // contoh: PRM007
                String numberPart = lastId.replace(prefix, ""); // hasil: 007
                nextNumber = Integer.parseInt(numberPart) + 1;  // hasil: 8
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return String.format("%s%03d", prefix, nextNumber); // PRM008
    }


    public Promo getPromoById(String id) {
        String query = "SELECT * FROM promo WHERE id = ?";
        Promo promo = null;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    promo = new Promo();
                    promo.setId(rs.getString("id"));
                    promo.setName(rs.getString("name"));
                    promo.setCode(rs.getString("code"));
                    promo.setDescription(rs.getString("description"));
                    double discountValue = rs.getDouble("discount_value");
                    if (rs.wasNull()) {
                        promo.setDiscount_value(null);
                    } else {
                        promo.setDiscount_value(discountValue);
                    }
                    double minPurchase = rs.getDouble("min_purchase");
                    if (rs.wasNull()) {
                        promo.setMin_purchase(null);
                    } else {
                        promo.setMin_purchase(minPurchase);
                    }
                    promo.setStart_date(rs.getDate("start_date"));
                    promo.setEnd_date(rs.getDate("end_date"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return promo;
    }

    public boolean createPromo(Promo promo) {
        String query = "INSERT INTO promo (id, name, code, description, discount_value, min_purchase, start_date, end_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        if (promo.getId() == null || promo.getId().isEmpty()) {
            promo.setId(generateIdPromo());
        }

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, promo.getId());
            stmt.setString(2, promo.getName());
            stmt.setString(3, promo.getCode());
            stmt.setString(4, promo.getDescription());
            stmt.setDouble(5, promo.getDiscount_value());
            stmt.setDouble(6, promo.getMin_purchase());
            stmt.setDate(7, promo.getStart_date());
            stmt.setDate(8, promo.getEnd_date());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePromo(Promo promo) {
        try {
            // Pastikan query UPDATE benar dan menggunakan ID yang tepat
            String query = "UPDATE promo SET name = ?, code = ?, description = ?, discount_value = ?, min_purchase = ?, start_date = ?, end_date = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, promo.getName());
            stmt.setString(2, promo.getCode());
            stmt.setString(3, promo.getDescription());
            stmt.setDouble(4, promo.getDiscount_value());
            Double min_purchase = promo.getMin_purchase();
            if (min_purchase == null) {
                stmt.setNull(5, Types.DOUBLE);
            } else {
                stmt.setDouble(5, promo.getMin_purchase());
            }
            stmt.setDate(6, promo.getStart_date());
            stmt.setDate(7, promo.getEnd_date());
            stmt.setString(8, promo.getId()); // Pastikan ID benar
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePromo(String id) {
        String query = "DELETE FROM promo WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Promo getPromoByCode(String code) {
        try {

            if (!code.equals(code.toUpperCase())) {
                return null;
            }

            Promo promo = null;
            String query = "SELECT * FROM Promo WHERE code = ? AND (end_date >= CURRENT_DATE OR end_date IS NULL)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                promo = new Promo();
                promo.setId(rs.getString("id"));
                promo.setName(rs.getString("name"));
                promo.setCode(rs.getString("code"));
                promo.setDescription(rs.getString("description"));
                double discountValue = rs.getDouble("discount_value");
                if (rs.wasNull()) {
                    promo.setDiscount_value(null);
                } else {
                    promo.setDiscount_value(discountValue);
                }
                double minPurchase = rs.getDouble("min_purchase");
                if (rs.wasNull()) {
                    promo.setMin_purchase(null);
                } else {
                    promo.setMin_purchase(minPurchase);
                }
                promo.setStart_date(rs.getDate("start_date"));
                promo.setEnd_date(rs.getDate("end_date"));
            }
            rs.close();
            stmt.close();
            return promo;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Statistik: jumlah promo aktif
    public int getActivePromoCount() {
        String query = "SELECT COUNT(*) FROM promo WHERE (start_date <= CURRENT_DATE OR start_date IS NULL) AND (end_date >= CURRENT_DATE OR end_date IS NULL)";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
