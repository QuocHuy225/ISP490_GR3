package com.mycompany.isp490_gr3.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * =====================================================
 * DBContext - QUẢN LÝ KẾT NỐI CƠ SỞ DỮ LIỆU
 * 
 * Chức năng: Cung cấp kết nối đến MySQL database
 * Database: MySQL
 * 
 * Chức năng chính:
 * - Tạo và quản lý kết nối database
 * - Cấu hình connection string
 * - Xử lý pool connection
 * =====================================================
 */
public class DBContext {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/clinicdb?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "123456"; // Change this to your MySQL password

    public static Connection getConnection() throws SQLException {
        try {
            System.out.println("=== Database Connection Attempt ===");
            System.out.println("URL: " + JDBC_URL);
            System.out.println("User: " + JDBC_USER);
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
            System.out.println("Database connection successful!");
            return conn;
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found:");
            e.printStackTrace();
            throw new SQLException("MySQL JDBC Driver not found", e);
        } catch (SQLException e) {
            System.out.println("Database connection failed:");
            e.printStackTrace();
            throw e;
        }
    }
} 