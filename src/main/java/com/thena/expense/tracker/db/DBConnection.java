package com.thena.expense.tracker.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // ✅ Update these according to your MySQL setup
    private static final String URL  = "jdbc:mysql://localhost:3306/expensetracker?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";           // your MySQL username
    private static final String PASS = "12345";  // your MySQL password

    // Method to obtain connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // Optional test main
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("✅ Connected to database successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
