package com.shinhan.stock;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.shinhan.stock.StockDAO;
import com.shinhan.stock.StockDAO;
import com.shinhan.stock.StockDAO;
import com.shinhan.stock.StockDAO;
import com.shinhan.stock.StockDAO;
import com.shinhan.stock.StockDAO;

public class StockDAO {

    // 주식 목록 조회 (모든 주식)
    public static List<StockDTO> getAllStocks() {
        List<StockDTO> stocks = new ArrayList<>();
        String query = "SELECT stock_ticker, stock_name, price FROM stocks";  // DB 쿼리 예시

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String ticker = rs.getString("stock_ticker");
                String name = rs.getString("stock_name");
                double price = rs.getDouble("price");

                StockDTO stock = new StockDTO(ticker, name, price, 0);
                stocks.add(stock);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stocks;
    }

    // 특정 주식 조회 (티커로 주식 찾기)
    public static StockDTO getStockByTicker(String ticker) {
        String query = "SELECT stock_ticker, stock_name, price FROM stocks WHERE stock_ticker = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, ticker);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String stockTicker = rs.getString("stock_ticker");
                String stockName = rs.getString("stock_name");
                double price = rs.getDouble("price");

                return new StockDTO(stockTicker, stockName, price, 0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
 // 주식 매수
    public void buyStock(int user_id, String stockTicker, int quantity) {
        String query = "MERGE INTO stock_holdings sh " +
                       "USING (SELECT ? AS user_id, ? AS stock_ticker FROM dual) src " +
                       "ON (sh.user_id = src.user_id AND sh.stock_ticker = src.stock_ticker) " +
                       "WHEN MATCHED THEN " +
                       "    UPDATE SET sh.quantity = sh.quantity + ? " +
                       "WHEN NOT MATCHED THEN " +
                       "    INSERT (user_id, stock_ticker, quantity) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, user_id);
            stmt.setString(2, stockTicker);
            stmt.setInt(3, quantity);
            stmt.setInt(4, user_id);
            stmt.setString(5, stockTicker);
            stmt.setInt(6, quantity);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 주식 매도
    public void sellStock(int userId, String stockTicker, int quantity) {
        String query = "UPDATE stock_holdings SET quantity = quantity - ? WHERE user_id = ? AND stock_ticker = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, userId);
            stmt.setString(3, stockTicker);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 보유 주식 조회
 // 보유 주식 조회
    public List<StockDTO> getStockHeld(int userId) {
        List<StockDTO> holdings = new ArrayList<>();
        String query = "SELECT sh.stock_ticker, s.stock_name, sh.quantity " +
                       "FROM stocks_held sh " +
                       "JOIN stocks s ON sh.stock_ticker = s.stock_ticker " +
                       "WHERE sh.user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String ticker = rs.getString("stock_ticker");
                String stockName = rs.getString("stock_name");
                int quantity = rs.getInt("quantity");

                // StockDTO를 사용하여 보유 주식 정보를 반환
                holdings.add(new StockDTO(ticker, stockName, quantity));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return holdings;
    }

    


}
