package com.example.puzzel_game.models;
import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:puzzle_scores.db";

    public static void initialize() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            // Crée une table
            String sql = "CREATE TABLE IF NOT EXISTS scores (nom TEXT, best_score INTEGER);";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // insert the best score
    public static void saveBestScore(String playerName, int score) {
        String sql = "INSERT INTO scores(nom, best_score) VALUES(?, ?);";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, playerName);
            pstmt.setInt(2, score);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Récupère le meilleur score
    public static int getBestScore() {
        String sql = "SELECT best_score FROM scores ORDER BY best_score DESC LIMIT 1;";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("best_score");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}