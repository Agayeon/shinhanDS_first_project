package com.shinhan.stock;

public class UserStockDTO {
    private String user_id;
    private String stock_ticker;
    private String stock_name;
    private int quantity;
    private double average_buy_price;

    // 기본 생성자
    public UserStockDTO() {}

    // 전체 생성자
    public UserStockDTO(String user_id, String stock_ticker, String stock_name, int quantity, double average_buy_price) {
        this.user_id = user_id;
        this.stock_ticker = stock_ticker;
        this.stock_name = stock_name;
        this.quantity = quantity;
        this.average_buy_price = average_buy_price;
    }

    // Getter & Setter
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getStock_ticker() {
        return stock_ticker;
    }

    public void setStock_ticker(String stock_ticker) {
        this.stock_ticker = stock_ticker;
    }

    public String getStock_name() {
        return stock_name;
    }

    public void setStock_name(String stock_name) {
        this.stock_name = stock_name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getAverage_buy_price() {
        return average_buy_price;
    }

    public void setAverage_buy_price(double average_buy_price) {
        this.average_buy_price = average_buy_price;
    }

    // 빌더 스타일 메서드 (선택 사항이지만 유지)
    public static UserStockDTO builder() {
        return new UserStockDTO();
    }

    public UserStockDTO user_id(String user_id) {
        this.user_id = user_id;
        return this;
    }

    public UserStockDTO stock_ticker(String stock_ticker) {
        this.stock_ticker = stock_ticker;
        return this;
    }

    public UserStockDTO stock_name(String stock_name) {
        this.stock_name = stock_name;
        return this;
    }

    public UserStockDTO quantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public UserStockDTO average_buy_price(double average_buy_price) {
        this.average_buy_price = average_buy_price;
        return this;
    }

	public String getStockTicker() {
		// TODO Auto-generated method stub
		return stock_ticker;
	}

	public int getStockName() {
		// TODO Auto-generated method stub
		return 0;
	}
}
