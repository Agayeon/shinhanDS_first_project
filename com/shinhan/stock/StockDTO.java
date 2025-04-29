package com.shinhan.stock;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor  // 모든 필드를 포함한 생성자를 Lombok으로 생성
public class StockDTO {
    private String stockCode;
    private String stockName;
    private double stockPrice;
    private int stockQuantity; // 주식의 수량 (매수/매도 관련)

    // 보유 주식 조회 시 사용할 생성자 (stockCode, stockName, stockQuantity만 받는 생성자)
    public StockDTO(String stockCode, String stockName, int stockQuantity) {
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.stockQuantity = stockQuantity;
    }

    // 주식 매수 메소드
    public void buyStock(int quantity) {
        this.stockQuantity += quantity; // 매수 시 수량 증가
    }

    // 주식 매도 메소드
    public void sellStock(int quantity) {
        if (this.stockQuantity >= quantity) {
            this.stockQuantity -= quantity; // 매도 시 수량 감소
        } else {
            System.out.println("매도할 주식이 부족합니다.");
        }
    }

    // 주식 정보 출력 메소드
    public void displayStockInfo() {
        System.out.println("주식 코드: " + stockCode);
        System.out.println("주식 이름: " + stockName);
        System.out.println("주식 가격: " + stockPrice);
        System.out.println("보유 수량: " + stockQuantity);
    }
}
