package com.shinhan.stock;

public class StockDTO {
    private String stockTicker;  // 주식 티커
    private String stockName;    // 주식 이름 추가
    private double price;        // 주식 가격
    private int quantity;        // 주식 수량
    private String stockId;

    // 기본 생성자
    public StockDTO() {}

    // 생성자
    public StockDTO(String stockTicker, String stockName, double price, int quantity) {
        this.stockTicker = stockTicker;
        this.stockName = stockName;
        this.price = price;
        this.quantity = quantity;
    }

    // getter, setter
    public String getStockTicker() {
        return stockTicker;
    }

    public void setStockTicker(String stockTicker) {
        this.stockTicker = stockTicker;
    }

    public String getStockName() {  // 추가된 getter
        return stockName;
    }

    public void setStockName(String stockName) {  // 추가된 setter
        this.stockName = stockName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    @Override
    public String toString() {
        return "StockDTO{" +
               "stockTicker='" + stockTicker + '\'' +
               ", stockName='" + stockName + '\'' +  // 추가된 부분
               ", price=" + price +
               ", quantity=" + quantity +
               '}';
    }
}
