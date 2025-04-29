package com.shinhan.stock;

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

                                case 2:  // 주식 매수
                                    System.out.print("매수할 주식 이름을 입력하세요: ");
                                    String stockNameToBuy = scanner.nextLine();
                                    System.out.print("매수할 수량을 입력하세요: ");
                                    int quantityToBuy = scanner.nextInt();

                                    // StockList에서 주식 가격을 가져옵니다.
                                    String priceString = StockList.f_stockPrice(stockNameToBuy);  // StockList에서 가격 가져오는 메소드
                                    if (priceString == null || priceString.isEmpty()) {
                                        System.out.println("주식 가격을 가져올 수 없습니다.");
                                        break;
                                    }
                                    double priceToBuy = 0;
                                    try {
                                        priceToBuy = Double.parseDouble(priceString.replace(",", ""));  // 가격 변환
                                    } catch (NumberFormatException e) {
                                        System.out.println("가격 변환에 실패했습니다. 매수를 취소합니다.");
                                        break;
                                    }

                                    // StockService 객체를 사용하여 주식 매수
                                    boolean buySuccess = stockService.buyStock(loggedInUser.getUserId(), stockNameToBuy, quantityToBuy, priceToBuy);
                                    if (buySuccess) {
                                        System.out.println("주식 매수 성공!");
                                    } else {
                                        System.out.println("주식 매수에 실패했습니다.");
                                    }
                                    break;

                                case 3:  // 주식 매도
                                    System.out.print("매도할 주식 이름을 입력하세요: ");
                                    String stockNameToSell = scanner.nextLine();
                                    System.out.print("매도할 수량을 입력하세요: ");
                                    int quantityToSell = scanner.nextInt();
                                    System.out.print("매도할 주식의 가격을 입력하세요: ");
                                    double priceToSell = scanner.nextDouble();

                                    boolean sellSuccess = stockService.sellStock(loggedInUser.getUserId(), stockNameToSell, quantityToSell, priceToSell);
                                    if (sellSuccess) {
                                        System.out.println("주식 매도 성공!");
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
                                    boolean withdrawSuccess = userService.deposit(loggedInUser.getUserId(), withdrawPay);
                                    if (withdrawSuccess) {
                                        System.out.println(loggedInUser.getUsername() + "님" + withdrawPay + " 원이 성공적으로 출금되셨습니다.");
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
