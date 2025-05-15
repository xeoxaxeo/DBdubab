package dbproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class emergencyManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db_project?useSSL=false&characterEncoding=utf8&serverTimezone=Asia/Seoul";
    private static final String DB_USER = "dbdubob";
    private static final String DB_PASSWORD = "testpw";

    private Connection conn;

    public emergencyManager() {
        try {
            // Load JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Success to load JDBC Driver!");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Connect Database
    public void connect() {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Success to connect JDBC Driver!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Disconnect Database
    public void disconnect() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Quit JDBC Driver Connection...");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Insert -- Emergency Store Info
    public void createEmergencyStore(String storeId, String storeName, String address, String phone, float longitude, float latitude) {
        String sql = "INSERT INTO emergency_store (store_id, store_name, address, phone, longitude, latitude) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, storeId);
            pstmt.setString(2, storeName);
            pstmt.setString(3, address);
            pstmt.setString(4, phone);
            pstmt.setFloat(5, longitude);
            pstmt.setFloat(6, latitude);
            
            int rows = pstmt.executeUpdate();
            System.out.println("Emergency Store Record Successfully ADDED -> (" + rows + " rows)");
            pstmt.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update -- Emergency Store Info
    public void updateEmergencyStore(String storeId, String newName, String newAddress, String newPhone, float newLongitude, float newLatitude) {
        String sql = "UPDATE emergency_store SET store_name = ?, address = ?, phone = ?, longitude = ?, latitude = ? WHERE store_id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newName);
            pstmt.setString(2, newAddress);
            pstmt.setString(3, newPhone);
            pstmt.setFloat(4, newLongitude);
            pstmt.setFloat(5, newLatitude);
            pstmt.setString(6, storeId);
            
            int rows = pstmt.executeUpdate();
            System.out.println("Emergency Store Record Successfully Updated -> (" + rows + " rows)");
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete -- Emergency Store Info
    public void deleteEmergencyStore(String storeId) {
        String sql = "DELETE FROM emergency_store WHERE store_id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, storeId);
            int rows = pstmt.executeUpdate();
            System.out.println("Emergency Store Record Successfully Deleted -> (" + rows + " rows)");
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
    	emergencyManager manager = new emergencyManager();
        manager.connect();

        // Test CRUD
        manager.createEmergencyStore("PHMH1234", "GS_TEST", "서울특별시 테스트구 테스트길", "02-123-4567", 123f, 456f);
        manager.updateEmergencyStore("PHMH1234", "GS_UPDATE", "서울특별시 업데이트구 업데이트길", "02-987-6543", 456f, 123f);
        manager.deleteEmergencyStore("PHMH1234");

        manager.disconnect();
    }
}

