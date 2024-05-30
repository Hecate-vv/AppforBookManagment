package app.database;
import app.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseController {
    Connection conn;
    public DatabaseController() {
        this.conn = null;
        try {
            String url = "src/main/resources/app/books.db";
            conn = DriverManager.getConnection("jdbc:sqlite:" + url);
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void addBook(String title, String author, int year, String genre) {
        String insertSQL = "INSERT INTO books(title, author, year, genre) VALUES(?,?,?,?)";
        try {
            PreparedStatement ps = this.conn.prepareStatement(insertSQL);
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setInt(3, year);
            ps.setString(4, genre);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void deleteBook(String title) {
        String deleteSQL = "DELETE FROM books WHERE title = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(deleteSQL);
            ps.setString(1, title);
            ps.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public List<Book> bookList() {
        List<Book> books = new ArrayList<>();
        String selectSQL = "SELECT * FROM books";
        try {
            PreparedStatement ps = conn.prepareStatement(selectSQL);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String title = rs.getString("title");
                String author = rs.getString("author");
                int year = rs.getInt("year");
                String genre = rs.getString("genre");
                books.add(new Book(title, author, year, genre));
            }
        }
            catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        return books;
        }
    }
