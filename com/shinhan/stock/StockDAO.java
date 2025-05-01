package com.shinhan.stock;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockDAO {

    // DB 연결
    private Connection getConnection() throws SQLException {
        return DBUtil.getConnection();  // DBUtil에서 연결을 가져옴
    }

    // 주식 보유 여부 확인
    public boolean isStockHeld(int userId, String selectedStockTicker) {
        String sql = "SELECT COUNT(*) FROM stocks_held WHERE user_id = ? AND stock_ticker = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, selectedStockTicker);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 주식 추가
 // 주식 추가 또는 수량 업데이트
    public boolean addStock(int userId, String stockTicker, double price, int quantity) {
        // 1. 주식이 이미 보유되고 있는지 확인
        String checkSql = "SELECT quantity FROM stocks_held WHERE user_id = ? AND stock_ticker = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, userId);
            ps.setString(2, stockTicker);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // 2. 이미 보유하고 있다면 수량을 업데이트
                int currentQuantity = rs.getInt("quantity");
                int newQuantity = currentQuantity + quantity;

                String updateSql = "UPDATE stocks_held SET quantity = ? WHERE user_id = ? AND stock_ticker = ?";
                try (PreparedStatement ps2 = conn.prepareStatement(updateSql)) {
                    ps2.setInt(1, newQuantity);
                    ps2.setInt(2, userId);
                    ps2.setString(3, stockTicker);
                    int rowsUpdated = ps2.executeUpdate();
                    return rowsUpdated > 0;
                }
            } else {
                // 3. 보유하고 있지 않다면 새로 추가
                String insertSql = "INSERT INTO stocks_held (user_id, stock_ticker, price, quantity) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps2 = conn.prepareStatement(insertSql)) {
                    ps2.setInt(1, userId);
                    ps2.setString(2, stockTicker);
                    ps2.setDouble(3, price);
                    ps2.setInt(4, quantity);
                    int rowsInserted = ps2.executeUpdate();
                    return rowsInserted > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    // 주식 수량 업데이트
    public boolean updateStockQuantity(int userId, String stockTicker, int quantityToBuy) {
        String sql = "UPDATE stocks_held SET quantity = quantity + ? WHERE user_id = ? AND stock_ticker = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantityToBuy);
            ps.setInt(2, userId);
            ps.setString(3, stockTicker);
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 주식 가격 조회
    public double getStockPrice(String stockTicker) {
        String sql = "SELECT price FROM stock_prices WHERE stock_ticker = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, stockTicker);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // 상위 10개 주식 목록 가져오기
    public List<StockDTO> getAllStocks() {
        List<StockDTO> stockList = new ArrayList<>();
        String sql = "SELECT stock_id, stock_name, price FROM stocks"; // stock_id, stock_name, stock_price 컬럼에 맞게 수정
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String stockId = rs.getString("stock_id");
                String stockName = rs.getString("stock_name");
                double stockPrice = rs.getDouble("price");

                // StockDTO 객체를 생성하여 리스트에 추가
                StockDTO stock = new StockDTO();
                stock.setStockId(stockId);      // 변경된 부분
                stock.setStockName(stockName);  // 변경된 부분
                stock.setPrice(stockPrice);
                stockList.add(stock);  // 수정: stocks -> stock
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return stockList;
    }



    // 주식의 현재 가격 조회
    public double getCurrentPrice(String stockId) {
        String sql = "SELECT price FROM stocks WHERE stock_id = ?";
        double stockPrice = 0.0;
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, stockId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                stockPrice = rs.getDouble("price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return stockPrice;
    }
    
    
 // 주식 번호와 주식 티커 매핑
    public boolean sellStock(int userId, int stockNumber, int quantityToSell) {
        // 주식 번호에 해당하는 티커를 가져오는 쿼리
        String getStockTickerSql = "SELECT stock_ticker FROM stock_list WHERE stock_number = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(getStockTickerSql)) {
            ps.setInt(1, stockNumber);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String stockTicker = rs.getString("stock_ticker");

                // 주식 보유 수량 확인
                String sql = "SELECT quantity FROM stocks_held WHERE user_id = ? AND stock_ticker = ?";
                try (PreparedStatement ps2 = conn.prepareStatement(sql)) {
                    ps2.setInt(1, userId);
                    ps2.setString(2, stockTicker);
                    ResultSet rs2 = ps2.executeQuery();

                    if (rs2.next()) {
                        int quantityHeld = rs2.getInt("quantity");

                        // 보유 수량이 매도하려는 수량보다 적으면 실패
                        if (quantityHeld < quantityToSell) {
                            return false;
                        }

                        // 수량 업데이트
                        String updateSql = "UPDATE stocks_held SET quantity = quantity - ? WHERE user_id = ? AND stock_ticker = ?";
                        try (PreparedStatement ps3 = conn.prepareStatement(updateSql)) {
                            ps3.setInt(1, quantityToSell);
                            ps3.setInt(2, userId);
                            ps3.setString(3, stockTicker);
                            int rowsUpdated = ps3.executeUpdate();
                            return rowsUpdated > 0;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


}
