package org.example.DAO;

import org.example.Member;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/library";
    private static final String JDBC_USER = "n_khoza21";
    private static final String JDBC_PASSWORD = "securedPassword";
    private TransactionDAO transactionDAO;  // Declare the variable

    // Constructor to initialize the TransactionDAO
    public MemberDAO(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }
    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "SELECT * FROM members";
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                try(ResultSet resultSet = statement.executeQuery()) {
                   while (resultSet.next()) {
                       Member member = new Member();
                       member.setId(resultSet.getInt("member_id"));
                       member.setName(resultSet.getString("name"));
                       member.setEmail(resultSet.getString("email"));
                       members.add(member);
                   }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }
    public void addMember(Member member) {
        try(Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "INSERT INTO members (name, email) VALUES (?, ?)";
            try(PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, member.getName());
                statement.setString(2, member.getEmail());
                statement.executeUpdate();

                try(ResultSet generatedKey = statement.getGeneratedKeys()) {
                    if (generatedKey.next()) {
                        int generatedId = generatedKey.getInt(1);
                        member.setId(generatedId);
                    } else {

                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateMember(Member member) {
        try(Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "UPDATE members SET name=?, email=? WHERE member_id=?";
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, member.getName());
                statement.setString(2, member.getEmail());
                statement.setInt(3, member.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteMember(int member_id) {
        transactionDAO.deleteTransactionsByMemberId(member_id);
        try(Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "DELETE FROM members WHERE member_id=?";
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, member_id);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Member getMemberById(int memberId) {
        Member member = null;
        try(Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "SELECT * FROM members WHERE member_id =?";
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, memberId);
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
}
