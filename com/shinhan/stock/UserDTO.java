package com.shinhan.stock;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    private String username;  // 사용자 이름 (ID)
    private String password;  // 비밀번호
    private double balance;   // 잔액
    private int user_id;       // 사용자 ID (고유값)
    
    public int getUserId() {
        return user_id; // user_id 필드 값을 반환
    }
}
