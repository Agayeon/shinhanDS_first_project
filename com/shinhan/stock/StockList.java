package com.shinhan.stock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.shinhan.stock.DBUtil;  // DBUtil 클래스는 DB 연결을 담당합니다.

public class StockList {

    public static void main(String[] args) throws IOException, SQLException {
        // 주식 데이터 크롤링
        String stockCode = "095570"; // 예시 주식 코드
        String stockName = "Samsung"; // 예시 주식 이름
        String price = f_stockPrice(stockCode); // 주식 가격 크롤링
        
        // 크롤링한 주식 정보를 DB에 추가
        insertStockToDB(stockCode, stockName, price);
    }

    // 주식 가격을 크롤링하는 함수
    public static String f_stockPrice(String stockCode) {
        String currentPrice = "";

        try {
            String url = "https://finance.naver.com/item/main.nhn?code=" + stockCode;
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36")
                    .get();

            Elements priceEl = doc.select(".no_today .blind"); // 현재가 위치
            currentPrice = priceEl.get(0).text(); // 첫 번째 blind 클래스가 현재가

            System.out.println("현재가: " + currentPrice + "원");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return currentPrice;
    }

    // 주식 정보를 DB에 저장하는 함수
    public static void insertStockToDB(String stockTicker, String stockName, String price) throws SQLException {
        String sql = "INSERT INTO stocks (stock_ticker, stock_name, price) VALUES (?, ?, ?)";

        // DB 연결
        Connection conn = DBUtil.getConnection();
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement(sql);
            st.setString(1, stockTicker);
            st.setString(2, stockName);
            st.setString(3, price);  // 가격을 문자열로 입력할 경우 나중에 DECIMAL로 변환 가능합니다.

            int result = st.executeUpdate();
            if (result > 0) {
                System.out.println("주식 정보가 성공적으로 DB에 추가되었습니다.");
            } else {
                System.out.println("주식 정보를 DB에 추가하는데 실패했습니다.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.dbDisconnect(conn, st, null);
        }
    }
}
