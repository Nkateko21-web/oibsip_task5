package org.example.DAO;

import org.example.Book;
import org.example.Member;
import org.example.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TransactionDAO {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/library";
    private static final String JDBC_USER = "n_khoza21";
    private static final String JDBC_PASSWORD = "securedPassword";
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "SELECT * FROM transactions";
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                try(ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Transaction transaction = new Transaction();
                        transaction.setId(resultSet.getInt("transaction_id"));
                        transaction.setBook(getBookById(resultSet.getInt("book_id")));
                        transaction.setMember(getMemberById(resultSet.getInt("member_id")));
                        transaction.setIssueDate(resultSet.getDate("issue_date"));
                        transaction.setReturnDate(resultSet.getDate("return_date"));
                        transaction.setFineAmount(resultSet.getDouble("fine_amount"));
                        transactions.add(transaction);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
    public void addTransaction(Transaction transaction, double fineRatePerDay) {
        try(Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "INSERT INTO transactions (book_id, member_id, issue_date, return_date, fine_amount) VALUES (?, ?, ?, ?, ?)";
            try(PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, transaction.getId());
                statement.setInt(1, transaction.getBook().getId());
                statement.setInt(2, transaction.getMember().getId());
                statement.setDate(3, new java.sql.Date(transaction.getIssueDate().getTime()));

                if (transaction.getReturnDate() != null) {
                    statement.setDate(4, new java.sql.Date(transaction.getReturnDate().getTime()));
                } else  {
                    statement.setNull(4, Types.DATE);
                }
                statement.setDouble(5, calculateFine(transaction.getIssueDate(), transaction.getReturnDate(), fineRatePerDay));
//                statement.executeUpdate();

                int affectedRows = statement.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating transaction failed, no rows affected.");
                }
                // Retrieve the generated key (transaction_id)
                try(ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        transaction.setId(generatedId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private Book getBookById(int book_id) {
        Book book = null;
        try(Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "SELECT * FROM books WHERE book_id=?";
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, book_id);
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
        return  book;
    }
    private Member getMemberById(int member_id) {
        Member member = null;
        try(Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "SELECT * FROM members WHERE member_id=?";
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, member_id);
                try(ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        member = new Member();
                        member.setId(resultSet.getInt("member_id"));
                        member.setName(resultSet.getString("name"));
                        member.setEmail(resultSet.getString("email"));

                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return member;
    }
    public void updateTransactionReturnDateAndFine(Transaction transaction, double fineRatePerDay) {
        try(Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "UPDATE transactions SET return_date=? WHERE transaction_id=?";
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setDate(1, new java.sql.Date(transaction.getReturnDate().getTime()));
                statement.setInt(2, transaction.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        updateFineAmount(transaction.getId(), calculateFine(transaction.getIssueDate(), transaction.getReturnDate(), fineRatePerDay));
    }
    public Transaction getTransactionById(int transactionId) {
        Transaction transaction = null;
        try(Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "SELECT * FROM transactions WHERE transaction_id=?";
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, transactionId);
                try(ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        transaction = new Transaction();
                        transaction.setId(resultSet.getInt("transaction_id"));
                        transaction.setBook(getBookById(resultSet.getInt("book_id")));
                        transaction.setMember(getMemberById(resultSet.getInt("member_id")));
                        transaction.setIssueDate(resultSet.getDate("issue_date"));
                        transaction.setReturnDate(resultSet.getDate("return_date"));
                        transaction.setFineAmount(resultSet.getDouble("fine_amount"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transaction;
    }

    public void updateFineAmount(int transactionId, double fineAmount) {
        try(Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "UPDATE transactions SET fine_amount=? WHERE transaction_id=?";
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setDouble(1, fineAmount);
                statement.setInt(2, transactionId);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteTransactionsByBookId(int bookId) {
        try(Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "DELETE FROM transactions WHERE book_id=?";
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, bookId);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public double calculateFine(java.util.Date issueDate, java.util.Date returnDate, double fineRatePerDay) {
        if(returnDate != null && returnDate.after(issueDate)) {
            long timeDifference = returnDate.getTime() - issueDate.getTime();
            long daysOverdue = TimeUnit.DAYS.convert(timeDifference, TimeUnit.MILLISECONDS);

            return daysOverdue * fineRatePerDay;
        } else {
            return 0.0;
        }
    }
    public void deleteTransactionsByMemberId(int memberId) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "DELETE FROM transactions WHERE member_id=?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, memberId);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
