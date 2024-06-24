package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DATABASE_URL = "jdbc:sqlite:jokes.db";
    private final Connection connection;

    public DatabaseManager() throws SQLException {
        connection = DriverManager.getConnection(DATABASE_URL);
        createTable();
    }

    private void createTable() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS jokes (" +
                "id INTEGER PRIMARY KEY," +
                "joke TEXT NOT NULL," +
                "type TEXT NOT NULL," +
                "nsfw BOOLEAN NOT NULL," +
                "religious BOOLEAN NOT NULL," +
                "political BOOLEAN NOT NULL," +
                "racist BOOLEAN NOT NULL," +
                "sexist BOOLEAN NOT NULL," +
                "explicit BOOLEAN NOT NULL)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    public boolean jokeExists(int id) throws SQLException {
        String checkJokeIdSQL = "SELECT 1 FROM jokes WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(checkJokeIdSQL)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void saveJoke(
            int id,
            String joke,
            String type,
            boolean nsfw,
            boolean religious,
            boolean political,
            boolean racist,
            boolean sexist,
            boolean explicit
    ) throws SQLException {
        if (jokeExists(id)) {
            System.out.println("Joke with ID " + id + " already exists in the database. Skipping save.");
            return;
        }

        String insertJokeSQL = "INSERT INTO jokes (id, joke, type, nsfw, religious, political, racist, sexist, explicit) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertJokeSQL)) {
            stmt.setInt(1, id);
            stmt.setString(2, joke);
            stmt.setString(3, type);
            stmt.setBoolean(4, nsfw);
            stmt.setBoolean(5, religious);
            stmt.setBoolean(6, political);
            stmt.setBoolean(7, racist);
            stmt.setBoolean(8, sexist);
            stmt.setBoolean(9, explicit);
            stmt.executeUpdate();
        }
    }

    public List<Joke> getAllJokes() throws SQLException {
        List<Joke> jokes = new ArrayList<>();
        String selectAllJokesSQL = "SELECT * FROM jokes";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectAllJokesSQL)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String joke = rs.getString("joke");
                String type = rs.getString("type");
                boolean nsfw = rs.getBoolean("nsfw");
                boolean religious = rs.getBoolean("religious");
                boolean political = rs.getBoolean("political");
                boolean racist = rs.getBoolean("racist");
                boolean sexist = rs.getBoolean("sexist");
                boolean explicit = rs.getBoolean("explicit");

                jokes.add(new Joke(id, joke, type, nsfw, religious, political, racist, sexist, explicit));
            }
        }
        return jokes;
    }

    public void deleteJoke(int id) throws SQLException {
        String deleteJokeSQL = "DELETE FROM jokes WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteJokeSQL)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void clearDatabase() throws SQLException {
        String clearTableSQL = "DELETE FROM jokes;";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(clearTableSQL);
        }
    }
}
