package com.shinhan.stock;

import java.sql.*;

public class UserDAO {

    // 새로운 사용자 추가 (UserDTO 객체를 받아서 처리)
    public boolean insertUser(UserDTO user) {
        String sql = "INSERT INTO users (user_id, username, password, balance) VALUES (user_seq.NEXTVAL, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setDouble(3, user.getBalance());

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // username으로 사용자 찾기
    public UserDTO getUserByUsername(String username) {
        String sql = "SELECT user_id, username, password, balance FROM users WHERE username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int user_id = rs.getInt("user_id");
                String userName = rs.getString("username");
                String password = rs.getString("password");
                double balance = rs.getDouble("balance");

                return new UserDTO(userName, password, balance, user_id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // user_id로 사용자 찾기
    public UserDTO getUserById(int user_id) {
        String sql = "SELECT user_id, username, password, balance FROM users WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, user_id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                double balance = rs.getDouble("balance");

                return new UserDTO(username, password, balance, user_id); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 특정 사용자의 잔액 조회
    public double getBalance(int user_id) {
        String sql = "SELECT balance FROM users WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, user_id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // 특정 사용자의 잔액 업데이트
    public boolean updateBalance(int user_id, double newBalance) {
        String sql = "UPDATE users SET balance = ? WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newBalance);
            pstmt.setInt(2, user_id);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 입금하기
    public boolean deposit(int user_id, double amount) {
        if (amount <= 0) {
            System.out.println("입금 금액은 0보다 커야 합니다.");
            return false;
        }

        // 현재 잔액 조회
        double currentBalance = getBalance(user_id);
        double newBalance = currentBalance + amount;

        // 잔액 업데이트
        return updateBalance(user_id, newBalance);
    }

    // 출금하기
    public boolean withdraw(int user_id, double amount) {
        if (amount <= 0) {
            System.out.println("출금 금액은 0보다 커야 합니다.");
            return false;
        }

        // 현재 잔액 조회
        double currentBalance = getBalance(user_id);

        // 잔액이 부족하면 출금 불가
        if (currentBalance < amount) {
            System.out.println("잔액이 부족합니다.");
            return false;
        }

        // 출금 후 새로운 잔액 계산
        double newBalance = currentBalance - amount;

        // 잔액 업데이트
        return updateBalance(user_id, newBalance);
    }
}
