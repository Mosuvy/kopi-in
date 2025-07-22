package com.example.application.dao;

import com.example.application.Koneksi.koneksi;
import com.example.application.models.Feedback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class FeedbackDAO {
    Connection connection;
    PreparedStatement statement;
    ResultSet resultSet;
    ArrayList<Feedback> feedbackList;
    Feedback feedback;

    public FeedbackDAO() {
        connection = koneksi.getConnection();
    }

    public ArrayList<Feedback> getListFeedbacks() {
        feedbackList = new ArrayList<>();
        try {
            statement = connection.prepareStatement("SELECT * FROM Feedbacks",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                feedback = new Feedback();
                feedback.setId(resultSet.getString("id"));
                feedback.setUser_id(resultSet.getString("user_id"));
                feedback.setMessage(resultSet.getString("message"));
                feedback.setCreated_at(resultSet.getTimestamp("created_at"));
                feedback.setStatus(resultSet.getString("status"));
                feedbackList.add(feedback);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return feedbackList;
    }

    public Feedback getFeedback(String id) {
        try {
            statement = connection.prepareStatement("SELECT * FROM Feedbacks WHERE id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, id);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                feedback = new Feedback();
                feedback.setId(resultSet.getString("id"));
                feedback.setUser_id(resultSet.getString("user_id"));
                feedback.setMessage(resultSet.getString("message"));
                feedback.setCreated_at(resultSet.getTimestamp("created_at"));
                feedback.setStatus(resultSet.getString("status"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return feedback;
    }

    public boolean createFeedback(Feedback feedback) {
        try {
            statement = connection.prepareStatement("INSERT INTO Feedbacks (id, user_id, message, status) VALUES (?, ?, ?, ?)",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, feedback.getId());
            statement.setString(2, feedback.getUser_id());
            statement.setString(3, feedback.getMessage());
            statement.setString(4, feedback.getStatus());
            statement.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateFeedback(Feedback feedback) {
        try {
            statement = connection.prepareStatement("UPDATE Feedbacks SET user_id = ?, message = ? status = ? WHERE id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, feedback.getUser_id());
            statement.setString(2, feedback.getMessage());
            statement.setString(3, feedback.getStatus());
            statement.setString(4, feedback.getId());
            statement.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean readFeedback(String id) {
        try {
            statement = connection.prepareStatement("UPDATE Feedbacks SET status = 'read' WHERE id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, id);
            statement.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean unreadFeedback(String id) {
        try {
            statement = connection.prepareStatement("UPDATE Feedbacks SET status = 'unread' WHERE id =  ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, id);
            statement.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteFeedback(String id) {
        try {
            statement = connection.prepareStatement("DELETE FROM Feedbacks WHERE id = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, id);
            statement.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
