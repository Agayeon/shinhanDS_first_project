package com.shinhan.stock;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockService {

    private StockDAO stockDAO;
    private UserDAO userDAO;

    public StockService() {
        this.stockDAO = new StockDAO();
        this.userDAO = new UserDAO();
    }

    // 주식 구매 메서드
    public boolean buyStock(UserDTO user, StockDTO stockDTO) {
        // 1. 사용자가 가진 잔액 조회
        double userBalance = userDAO.getBalance(user.getUserId());

        // 2. 주식 가격을 계산하여 잔액과 비교
        double totalPrice = stockDTO.getPrice() * stockDTO.getQuantity();
        if (userBalance < totalPrice) {
            return false; // 잔액 부족
        }

        // 3. 주식 구매: 주식이 이미 보유된 경우 수량만 업데이트, 보유되지 않은 경우 새로 추가
        boolean purchaseSuccess = stockDAO.addStock(user.getUserId(), stockDTO.getStockTicker(), stockDTO.getPrice(), stockDTO.getQuantity());

        if (purchaseSuccess) {
            // 4. 주식 구매가 성공하면 잔액 차감
            boolean balanceUpdated = userDAO.updateBalance(user.getUserId(), userBalance - totalPrice);
            return balanceUpdated;
        }

        return false; // 주식 구매 실패
    }

    // 사용자가 주식을 보유하고 있는지 확인하는 메서드
    public boolean isStockHeld(int userId, String selectedStockTicker) {
        return stockDAO.isStockHeld(userId, selectedStockTicker);
    }

    // 주식 수량 업데이트 메서드
    public boolean updateStockQuantity(int userId, String selectedStockTicker, int quantityToBuy) {
        String sql = "UPDATE stocks_held SET quantity = quantity + ? WHERE user_id = ? AND stock_ticker = ?";
        try (Connection conn = DBUtil.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantityToBuy);
            ps.setInt(2, userId);
            ps.setString(3, selectedStockTicker);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 주식 추가 메서드
    public boolean addStock(int userId, String selectedStockTicker, double price, int quantityToBuy) {
        return stockDAO.addStock(userId, selectedStockTicker, price, quantityToBuy);
    }

    public List<String> getTop10Stocks() {
        List<StockDTO> stockList = stockDAO.getAllStocks(); // stockList 받아오기
        List<String> top10Names = new ArrayList<>();

        for (int i = 0; i < Math.min(10, stockList.size()); i++) {
            top10Names.add(stockList.get(i).getStockName());
        }

        return top10Names;
    }

    // 주식 매도 메서드
    public boolean sellStock(int userId, String stockTicker, double price, int quantity) {
        Connection conn = null;
        PreparedStatement psUpdateStock = null;
        PreparedStatement psUpdateBalance = null;
        ResultSet rs = null;

        try {
            // 1. 수량 확인
            int currentQuantity = getUserStockQuantity(userId, stockTicker);
            if (currentQuantity < quantity) {
                return false; // 수량 부족
            }

            // 2. 현재 주가 조회 (가격은 호출 시 전달된 값)
            double totalSellAmount = price * quantity;

            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            // 3. 주식 수량 차감
            String sqlUpdateStock = "UPDATE stocks_held SET quantity = quantity - ? WHERE user_id = ? AND stock_ticker = ?";
            psUpdateStock = conn.prepareStatement(sqlUpdateStock);
            psUpdateStock.setInt(1, quantity);
            psUpdateStock.setInt(2, userId);
            psUpdateStock.setString(3, stockTicker);
            int stockUpdated = psUpdateStock.executeUpdate();

            if (stockUpdated == 0) {
                conn.rollback();
                return false;
            }

            // 4. 잔액 증가
            double userBalance = userDAO.getBalance(userId);
            double newBalance = userBalance + totalSellAmount;

            if (!userDAO.updateBalance(userId, newBalance)) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            DBUtil.dbDisconnect(conn, psUpdateStock, rs);
        }

        return false;
    }

    // 사용자가 보유한 주식 수량 조회
    public int getUserStockQuantity(int userId, String stockTicker) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        int quantity = 0;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT quantity FROM user_stocks WHERE user_id = ? AND stock_ticker = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, stockTicker);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                quantity = rs.getInt("quantity");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return quantity;
    }
}
