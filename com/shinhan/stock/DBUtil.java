package com.shinhan.stock;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {
	// DB 연결
	public static Connection getConnection() {
		Connection conn = null;
		String url= "jdbc:oracle:thin:@localhost:1521:xe";
		String userid="c##stock", userpass="1234";
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, userid, userpass);
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}
	
	// DB 연결 해제
	public static void dbDisconnect(Connection conn, Statement st, ResultSet rs) {
		try {
			if(rs!=null) rs.close(); //작은 거 먼저 닫아야 함
			if(st!=null) st.close();
			if(conn!=null) conn.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
}

