 package com.delulu.db;

import com.delulu.model.Ticket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {
    public static void initializeDatabase() throws SQLException {
        try (Connection conn = DBConnection.connect(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS tickets (id INTEGER PRIMARY KEY, title TEXT, description TEXT, status TEXT)");
        }
    }

    public static void addTicket(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO tickets (title, description, status) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ticket.getTitle());
            pstmt.setString(2, ticket.getDescription());
            pstmt.setString(3, ticket.getStatus());
            pstmt.executeUpdate();
        }
    }

    public static List<Ticket> getTickets() throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets";
        try (Connection conn = DBConnection.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tickets.add(new Ticket(rs.getInt("id"), rs.getString("title"), rs.getString("description"), rs.getString("status")));
            }
        }
        return tickets;
    }
}
