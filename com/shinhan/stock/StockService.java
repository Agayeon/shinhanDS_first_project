package com.shinhan.stock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StockService {

	 private StockDAO stockDAO = new StockDAO();
    private static UserDAO userDAO = new UserDAO();


    // 주식 매수
    public boolean buyStock(int userId, String stockTicker, int quantity, double price) {
        // 잔액 확인
        double balance = userDAO.getBalance(userId);
        double totalCost = price * quantity;
        if (balance < totalCost) {
            System.out.println("잔액이 부족하여 매수를 진행할 수 없습니다.");
            return false;
        }

        // 주식 매수 처리
        stockDAO.buyStock(userId, stockTicker, quantity);
        userDAO.updateBalance(userId, balance - totalCost);

        System.out.println(userId + " 사용자에게 " + stockTicker + " 주식 " + quantity + "개를 매수했습니다.");
        return true;
    }

    // 주식 매도
    public boolean sellStock(int user_id, String stockTicker, int quantity, double price) {
        String priceString = StockList.f_stockPrice(stockTicker);
        if (priceString == null || priceString.isEmpty()) {
            System.out.println("주식 가격을 가져올 수 없어 매도를 취소합니다.");
            return false;
        }

        try {
            price = Double.parseDouble(priceString.replace(",", ""));
        } catch (NumberFormatException e) {
            System.out.println("가격 변환에 실패했습니다. 매도를 취소합니다.");
            return false;
        }

        System.out.println("주식 매도 처리: " + stockTicker + " - " + quantity + "주, 가격: " + price + "원");

        // 주식 매도 처리
        stockDAO.sellStock(user_id, stockTicker, quantity);  // 주식 매도 처리
        userDAO.updateBalance(user_id, userDAO.getBalance(user_id) + (price * quantity));  // 잔액 업데이트

        System.out.println(user_id + " 사용자에게 " + stockTicker + " 주식 " + quantity + "개를 매도했습니다.");
        return true;
    }
    
    // 보유 주식 조회
    public List<StockDTO> getStockHeld(int user_id) {
        List<StockDTO> holdings = new ArrayList<>();
        String query = """
            SELECT h.stock_ticker, s.stock_name, h.quantity
            FROM stock_holdings h
            JOIN stock_list s ON h.stock_ticker = s.stock_ticker
            WHERE h.user_id = ?
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, user_id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String ticker = rs.getString("stock_ticker");
                String stockName = rs.getString("stock_name");
                int qty = rs.getInt("quantity");
                holdings.add(new StockDTO(ticker, stockName, qty));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return holdings;
    }


    // 상위 10개 주식 목록 조회
    public List<String> Top10StockTickers() {
        return StockList.Top10StockTickers(); // 수정된 메소드 이름
    }

    // 입금하기
    public boolean deposit(int userId, double amount) {
        if (amount <= 0) {
            System.out.println("입금 금액은 0보다 커야 합니다.");
            return false;
        }
        return userDAO.deposit(userId, amount);
    }

    // 출금하기
    public boolean withdraw(int userId, double amount) {
        if (amount <= 0) {
            System.out.println("출금 금액은 0보다 커야 합니다.");
            return false;
        }
        double currentBalance = userDAO.getBalance(userId);
        if (currentBalance < amount) {
            System.out.println("잔액이 부족하여 출금을 진행할 수 없습니다.");
            return false;
        }
        return userDAO.withdraw(userId, amount);
    }

    // 잔액 조회
    public double getBalance(int userId) {
        return userDAO.getBalance(userId);
    }
}
