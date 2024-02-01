package org.example.DAO;

import org.example.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/library";
    private static final String JDBC_USER = "n_khoza21";
    private static final String JDBC_PASSWORD = "securedPassword";
    private TransactionDAO transactionDAO;
    public BookDAO() {
        this.transactionDAO = new TransactionDAO();
    }
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "SELECT * FROM books";
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                try(ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Book book = new Book();
                        book.setId(resultSet.getInt("book_id"));
                        book.setTitle(resultSet.getString("title"));
                        book.setAuthor(resultSet.getString("author"));
                        book.setQuantity(resultSet.getInt("quantity"));
                        books.add(book);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
    public void addBook(Book book) {
        try(Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "INSERT INTO books (title, author, quantity) VALUES (?, ?, ?)";
            try(PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//                statement.setInt(1, 0);
                statement.setString(1, book.getTitle());
                statement.setString(2, book.getAuthor());
                statement.setInt(3, book.getQuantity());
                statement.executeUpdate();

                try(ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        book.setId(generatedId);
                    } else {

                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateBook(Book book) {
        try(Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "UPDATE books SET title=?, author=?, quantity=? WHERE book_id=?";
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, book.getTitle());
                statement.setString(2, book.getAuthor());
                statement.setInt(3, book.getQuantity());
                statement.setInt(4, book.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteBook(int book_id) {
        transactionDAO.deleteTransactionsByBookId(book_id);
        try(Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
           String sql = "DELETE FROM books WHERE book_id=?";
           try(PreparedStatement statement = connection.prepareStatement(sql)) {
               statement.setInt(1, book_id);
               int rowsAffected = statement.executeUpdate();

               if (rowsAffected > 0) {
                   System.out.println("Book deleted successfully!");
               } else {
                   System.out.println("No book found with ID: " + book_id);
               }
           }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Book getBookById(int bookId) {
        Book book = null;
        try(Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "SELECT * FROM books WHERE book_id=?";
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, bookId);
                try(ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        book = new Book();
                        book.setId(resultSet.getInt("book_id"));
                        book.setTitle(resultSet.getString("title"));
                        book.setAuthor(resultSet.getString("author"));
                        book.setQuantity(resultSet.getInt("quantity"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return book;
    }
}
