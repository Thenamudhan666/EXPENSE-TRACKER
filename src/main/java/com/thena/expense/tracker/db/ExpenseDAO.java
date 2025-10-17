package com.thena.expense.tracker.db;

import com.thena.expense.tracker.model.Expense;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {

    /** Insert a new expense. Sets the generated id back on the given object and returns it. */
    public Expense insert(Expense e) throws SQLException {
        final String sql = """
            INSERT INTO expenses (date, amount, currency, category, payee, notes)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(e.date));
            ps.setBigDecimal(2, e.amount);
            ps.setString(3, e.currency);
            ps.setString(4, e.category);
            ps.setString(5, e.payee);
            ps.setString(6, e.notes);

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) e.id = keys.getLong(1);
            }
            return e;
        }
    }

    /** Update an existing expense by id. */
    public void update(Expense e) throws SQLException {
        final String sql = """
            UPDATE expenses
            SET date = ?, amount = ?, currency = ?, category = ?, payee = ?, notes = ?
            WHERE id = ?
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(e.date));
            ps.setBigDecimal(2, e.amount);
            ps.setString(3, e.currency);
            ps.setString(4, e.category);
            ps.setString(5, e.payee);
            ps.setString(6, e.notes);
            ps.setLong(7, e.id);

            ps.executeUpdate();
        }
    }

    /** Delete by id. */
    public void delete(long id) throws SQLException {
        final String sql = "DELETE FROM expenses WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    /** Fetch all expenses ordered by date desc, id desc. */
    public List<Expense> findAll() throws SQLException {
        final String sql = """
            SELECT id, date, amount, currency, category, payee, notes
            FROM expenses
            ORDER BY date DESC, id DESC
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Expense> out = new ArrayList<>();
            while (rs.next()) out.add(mapRow(rs));
            return out;
        }
    }

    /**
     * Free-text search across category, payee, and notes fields.
     * If keyword is null/blank, behaves like findAll().
     */
    public List<Expense> search(String keyword) throws SQLException {
        String like = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        final String sql = """
            SELECT id, date, amount, currency, category, payee, notes
            FROM expenses
            WHERE category LIKE ? OR payee LIKE ? OR notes LIKE ?
            ORDER BY date DESC, id DESC
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);

            try (ResultSet rs = ps.executeQuery()) {
                List<Expense> out = new ArrayList<>();
                while (rs.next()) out.add(mapRow(rs));
                return out;
            }
        }
    }

    /** Optional helper: fetch a single expense by id (not required by the UI but handy). */
    public Expense findById(long id) throws SQLException {
        final String sql = """
            SELECT id, date, amount, currency, category, payee, notes
            FROM expenses
            WHERE id = ?
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    // ===== internal mapper =====
    private Expense mapRow(ResultSet rs) throws SQLException {
        Expense e = new Expense();
        e.id       = rs.getLong("id");
        e.date     = rs.getDate("date").toLocalDate();
        e.amount   = rs.getBigDecimal("amount");
        e.currency = rs.getString("currency");
        e.category = rs.getString("category");
        e.payee    = rs.getString("payee");
        e.notes    = rs.getString("notes");
        return e;
    }
}
