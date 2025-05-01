package com.shinhan.stock;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.shinhan.stock.StockService;

public class StockView {

    // 메인 메소드 흐름 진행 순서
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserService userService = new UserService();
        boolean isRunning = true;

        // StockService 객체를 메소드 밖에서 한번만 생성
        StockService stockService = new StockService();  

        while (isRunning) {
            System.out.println("안녕하세요. 원하시는 번호를 입력해주세요.");
            System.out.println("1. 회원 가입");
            System.out.println("2. 로그인");
            System.out.println("3. 종료");
            System.out.println("======================");
            System.out.print("실행할 번호 선택: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // 버퍼 비우기

            switch (choice) {
                case 1:  // 회원 가입
                    System.out.print("사용자 이름(ID): ");    
                    String username = scanner.nextLine();
                    System.out.print("비밀번호(PW): ");
                    String password = scanner.nextLine();
                    System.out.print("잔액(balance): ");
                    double balance = scanner.nextDouble();
                    boolean isRegistered = userService.registerUser(username, password, balance);
                    if (isRegistered) {
                        System.out.println("회원 가입이 성공적으로 진행되셨습니다!");
                    } else {
                        System.out.println("회원 가입이 실패했습니다. 다시 시도해주세요.");
                    }
                    break;

                case 2:  // 로그인
                    System.out.print("사용자 이름(ID) 입력: ");
                    username = scanner.nextLine();
                    System.out.print("비밀번호(PW): ");
                    password = scanner.nextLine();
                    UserDTO loggedInUser = userService.loginUser(username, password);
                    if (loggedInUser != null) {
                        System.out.println("======================");
                        System.out.println(loggedInUser.getUsername() + "님 환영합니다.");
                        
                        // 로그인 성공 후 이 메뉴들 실행
                        boolean logIn = true;
                        while(logIn) {
                            System.out.println("원하시는 메뉴 번호를 선택하세요.");
                            System.out.println("1. 잔액 조회 2. 주식 매수 3. 주식 매도");
                            System.out.println("4. 보유 주식 조회 5. 로그아웃");
                            System.out.println("6. 입금하기 7. 출금하기");
                            System.out.println("======================");
                            System.out.print("선택할 메뉴 번호 : ");
                            
                            int menuChoice = scanner.nextInt();
                            scanner.nextLine();
                            
                            switch (menuChoice) {
                                case 1: 
                                    double currentBalance = userService.getBalance(loggedInUser.getUserId());
                                    System.out.println(loggedInUser.getUsername() + "님의 현재 보유 잔액은 :" + currentBalance + " 원 입니다.");
                                    System.out.println("======================");
                                    break;

                                 // 주식 매수 부분 수정
                                case 2:  // 주식 매수
                                    System.out.println("==========신한DS 증권의 국내증시==========");
                                    System.out.println("  주식ticker    종목      현재가");

                                    // DB에서 주식 목록 가져오기
                                    Connection conn = null;
                                    Statement stmt = null;
                                    ResultSet rs = null;

                                    try {
                                        conn = DBUtil.getConnection();  // DB 연결
                                        String sql = "SELECT stock_id, stock_ticker, stock_name, price FROM stocks WHERE stock_id > 3"; // stock_id 1, 2, 3 제외
                                        stmt = conn.createStatement();
                                        rs = stmt.executeQuery(sql);

                                        int index = 1;
                                        List<String> top10Stocks = new ArrayList<>();
                                        while (rs.next()) {
                                            int stockId = rs.getInt("stock_id");
                                            String stockTicker = rs.getString("stock_ticker");
                                            String stockName = rs.getString("stock_name");
                                            double price = rs.getDouble("price");

                                            top10Stocks.add(stockTicker + " - " + stockName + " - " + price);
                                            System.out.println(index + ". " + stockTicker + " - " + stockName + " - " + price);
                                            index++;
                                        }

                                        System.out.print("매수할 주식의 번호를 선택하세요: ");
                                        int stockChoice = scanner.nextInt();
                                        scanner.nextLine();  // 버퍼 비우기

                                        if (stockChoice < 1 || stockChoice > top10Stocks.size()) {
                                            System.out.println("잘못된 선택입니다.");
                                            break;
                                        }

                                        String selectedStock = top10Stocks.get(stockChoice - 1);
                                        String[] stockParts = selectedStock.split(" - ");
                                        String selectedStockTicker = stockParts[0];

                                        // 주식 가격 가져오기
                                        double price = 0;
                                        try {
                                            // 가격 정보는 이미 출력된 상태라서 파싱만 필요
                                            price = Double.parseDouble(stockParts[2].trim());
                                        } catch (NumberFormatException e) {
                                            System.out.println("유효하지 않은 주식 가격입니다.");
                                            break;
                                        }

                                        System.out.println("선택한 주식: " + selectedStockTicker + " 가격: " + price + "원");
                                        System.out.print("매수할 수량을 입력하세요: ");
                                        int quantityToBuy = scanner.nextInt();

                                        // 수량 유효성 검사
                                        if (quantityToBuy <= 0) {
                                            System.out.println("매수 수량은 1주 이상이어야 합니다.");
                                            break;
                                        }

                                        // 잔액 확인
                                        double getbalance = userService.getBalance(loggedInUser.getUserId());
                                        double totalCost = price * quantityToBuy;
                                        if (getbalance < totalCost) {
                                            System.out.println("잔액이 부족하여 매수를 진행할 수 없습니다.");
                                            break;
                                        }

                                        // UserDTO 객체와 StockDTO 객체를 생성
                                        UserDTO user = loggedInUser;
                                        StockDTO stock = new StockDTO();
                                        stock.setStockTicker(selectedStockTicker);
                                        stock.setPrice(price);
                                        stock.setQuantity(quantityToBuy);

                                        // 매수 로직 추가 (예: 주식 데이터베이스에 저장)
                                        boolean buySuccess = stockService.buyStock(user, stock);  // 인자에 UserDTO와 StockDTO를 전달
                                        if (buySuccess) {
                                            System.out.println("주식 매수 성공!");
                                        } else {
                                            System.out.println("주식 매수에 실패했습니다.");
                                        }

                                    } catch (Exception e) {
                                        System.out.println("주식 목록을 불러오는 중 오류가 발생했습니다.");
                                        e.printStackTrace();
                                    } finally {
                                        try {
                                            if (rs != null) rs.close();
                                            if (stmt != null) stmt.close();
                                            if (conn != null) conn.close();
                                        } catch (Exception e) {
                                            System.out.println("DB 연결을 종료하는 중 오류가 발생했습니다.");
                                        }
                                    }
                                    break;





                                 // 주식 매도
                                 // 주식 매도
                                case 3:  // 주식 매도
                                    System.out.print("매도할 주식의 티커를 입력하세요: ");
                                    String stockTickerToSell = scanner.nextLine();  // 주식 티커 
                                    System.out.print("매도할 주식의 수량을 입력하세요: ");
                                    int quantityToSell = scanner.nextInt();  // 주식 수량 입력 받기
                                    scanner.nextLine();  // 버퍼 비우기

                                    // 보유한 주식 수량을 조회
                                    int userStockQuantity = stockService.getUserStockQuantity(loggedInUser.getUserId(), stockTickerToSell);
                                    
                                    if (userStockQuantity == 0) {
                                        System.out.println("보유하고 있는 주식이 없습니다.");
                                        break;
                                    }

                                    if (quantityToSell <= 0 || quantityToSell > userStockQuantity) {
                                        System.out.println("매도할 수량이 잘못되었거나 보유 수량을 초과했습니다.");
                                        break;
                                    }

                                    // 주식의 가격을 입력받는 부분 추가
                                    System.out.print("매도할 주식의 가격을 입력하세요: ");
                                    double priceToSell = scanner.nextDouble();  // 주식 가격 입력 받기
                                    scanner.nextLine();  // 버퍼 비우기

                                    // 매도 로직 (주식 매도 처리)
                                    boolean sellSuccess = stockService.sellStock(loggedInUser.getUserId(), stockTickerToSell, priceToSell, quantityToSell);
                                    if (sellSuccess) {
                                        System.out.println(quantityToSell + "주가 성공적으로 매도되었습니다.");
                                    } else {
                                        System.out.println("주식 매도에 실패했습니다.");
                                    }
                                    break;


                               
                                    


                                case 4: 
                                    System.out.println("보유 주식은 :" + ' ' + " 입니다.");
                                    break;

                                case 5: 
                                    System.out.println(loggedInUser.getUsername() + "님 로그아웃 되셨습니다.");
                                    System.out.println("======================");
                                    logIn = false;
                                    break;

                                case 6: 
                                    System.out.println(loggedInUser.getUsername() + "님 얼마를 입금할까요?");
                                    double depositPay = scanner.nextDouble();
                                    boolean depositSuccess = userService.deposit(loggedInUser.getUserId(), depositPay);
                                    if (depositSuccess) {
                                        System.out.println(loggedInUser.getUsername() + "님 " + depositPay + "원이 성공적으로 입금되셨습니다.");
                                    } else {
                                        System.out.println("입금이 실패됐어요! 다시 시도해주세요.");
                                    }
                                    break;

                                case 7: 
                                    System.out.println(loggedInUser.getUsername() + "님 얼마를 출금할까요?");
                                    double withdrawPay = scanner.nextDouble();
                                    
                                    // 출금 처리
                                    boolean withdrawSuccess = userService.withdraw(loggedInUser.getUserId(), withdrawPay);
                                    
                                    if (withdrawSuccess) {
                                        System.out.println(loggedInUser.getUsername() + "님 " + withdrawPay + " 원이 성공적으로 출금되셨습니다.");
                                    } else {
                                        System.out.println("출금이 실패됐어요! 다시 시도해주세요.");
                                    }
                                    break;


                                default: 
                                    System.out.println("잘못된 입력입니다.");
                                    break;
                            }
                        }
                    } else {
                        System.out.println("로그인이 실패되었습니다.");
                    }
                    break;

                // 프로그램 종료
                case 3:  
                    isRunning = false;
                    System.out.println("프로그램이 종료됩니다.");
                    break;

                default:
                    System.out.println("잘못된 입력입니다.");
            }
        }

        scanner.close();
    }
}
