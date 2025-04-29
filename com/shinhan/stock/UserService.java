package com.shinhan.stock;

public class UserService {
    private UserDAO userDAO = new UserDAO();  // DB와 연결하기 위한 DAO 객체

    // 회원 가입 (DB에 저장)
    public boolean registerUser(String username, String password, double balance) {
        UserDTO newUser = new UserDTO(username, password, balance, 0);  // user_id는 DB에서 시퀀스로 자동 생성됨
        return userDAO.insertUser(newUser);  // DB에 저장
    }

    // 로그인 (DB에서 사용자 조회 후 확인)
    public UserDTO loginUser(String username, String password) {
        UserDTO user = userDAO.getUserByUsername(username);  // DB에서 조회
        if (user != null && user.getPassword().equals(password)) {
            return user;  // 로그인 성공
        }
        return null;  // 로그인 실패
    }

    // 잔액 조회
    public double getBalance(int userId) {
        return userDAO.getBalance(userId);  // DB에서 잔액 조회
    }

    // 입금
    public boolean deposit(int userId, double amount) {
        return userDAO.deposit(userId, amount);  // DB에서 잔액 업데이트
    }

    // 출금
    public boolean withdraw(int userId, double amount) {
        return userDAO.withdraw(userId, amount);  // DB에서 잔액 업데이트
    }
}
