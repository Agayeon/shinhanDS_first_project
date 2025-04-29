package com.shinhan.stock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
 

public class StockList {

	public static void main(String[] args) throws IOException {
		URL url = new URL("https://data.krx.co.kr/comm/bldAttendant/getJsonData.cmd");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36");
        conn.setRequestProperty("Referer", "https://data.krx.co.kr/"); // 중요
        conn.setDoOutput(true);

        String postData = "bld=dbms/MDC/STAT/standard/MDCSTAT01901&mktId=STK";

        try (OutputStream os = conn.getOutputStream()) {
            os.write(postData.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        System.out.println("Response Code: " + responseCode); // 디버깅용 출력

        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject json = new JSONObject(response.toString());
            JSONArray items = json.getJSONArray("OutBlock_1");

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                System.out.print (item.getString("ISU_SRT_CD") + " - " 
                         + item.getString("ISU_ABBRV") + "-----");
                
                String price =  f_stockPrice(item.getString("ISU_SRT_CD"));
                System.out.println(price);
            }
        } else {
            System.out.println("Failed to fetch data: " + responseCode);
        }
        //f_stockPrice("005930");//// 삼성전자

	}
	
	// 주식 시세가 가져오기
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
	
	// 상위 10개 주식 티커 가져오기
	public static List<String> Top10StockTickers() {
		List<String> stockTickers = new ArrayList<>();
		  try {
	            URL url = new URL("https://data.krx.co.kr/comm/bldAttendant/getJsonData.cmd");
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

	            conn.setRequestMethod("POST");
	            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36");
	            conn.setRequestProperty("Referer", "https://data.krx.co.kr/");
	            conn.setDoOutput(true);

	            String postData = "bld=dbms/MDC/STAT/standard/MDCSTAT01901&mktId=STK";

	            try (OutputStream os = conn.getOutputStream()) {
	                os.write(postData.getBytes(StandardCharsets.UTF_8));
	            }

	            int responseCode = conn.getResponseCode();

	            if (responseCode == 200) {
	                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
	                String inputLine;
	                StringBuilder response = new StringBuilder();

	                while ((inputLine = in.readLine()) != null) {
	                    response.append(inputLine);
	                }
	                in.close();

	                JSONObject json = new JSONObject(response.toString());
	                JSONArray items = json.getJSONArray("OutBlock_1");

	                for (int i = 0; i < items.length(); i++) {
	                    JSONObject item = items.getJSONObject(i);
	                    stockTickers.add(item.getString("ISU_SRT_CD"));
	                }
	            } else {
	                System.out.println("Failed to fetch data: " + responseCode);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		return stockTickers;
	}
	
	
}