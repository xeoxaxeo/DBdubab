package dbproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class pharmacyManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db_project";
    private static final String DB_USER = "dbdubob";  // MySQL User Name
    private static final String DB_PASSWORD = "testpw";  // MySQL PW

    private Connection conn;

    public pharmacyManager() {
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

    // Inconnect Database
    public void disconnect() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Close JDBC Driver Connection...");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Insert -- Pharmacy Info
    public void createPharmacy(String pharmacy_id, String name, String address, String phone, String zip_code, float longitude, float latitude, boolean is_operating) {
        String sql = "INSERT INTO pharmacy (pharmacy_id, name, address, phone, zip_code, longitude, latitude, is_operating) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, pharmacy_id);
            pstmt.setString(2, name);
            pstmt.setString(3, address);
            pstmt.setString(4, phone);
            pstmt.setString(5, zip_code);
            pstmt.setFloat(6, longitude);
            pstmt.setFloat(7, latitude);
            pstmt.setBoolean(8, is_operating);
            
            int rows = pstmt.executeUpdate();
            System.out.println("Pharmacy Record Successfully ADDED -> (" + rows + "rows)");
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Udpate -- Pharmacy Info
    public void updatePharmacy(String pharmacy_id, String new_name, String new_address, String new_phone, String new_zipcode, float new_longitude, float new_latitude, boolean new_isOperating) {
        String sql = "UPDATE pharmacy SET name = ?, address = ?, phone = ?, zip_code = ?, longitude = ?, latitude = ?, is_operating = ? WHERE pharmacy_id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, new_name);
            pstmt.setString(2, new_address);
            pstmt.setString(3, new_phone);
            pstmt.setString(4, new_zipcode);
            pstmt.setFloat(5, new_longitude);
            pstmt.setFloat(6, new_latitude);
            pstmt.setBoolean(7, new_isOperating);
            pstmt.setString(8, pharmacy_id);
            
            int rows = pstmt.executeUpdate();
            System.out.println("Pharmacy Record Successfully UPDATED -> (" + rows + "rows)");
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //  Delete -- Pharmacy Info
    public void deletePharmacy(String pharmacy_id) {
        String sql = "DELETE FROM pharmacy WHERE pharmacy_id = ?";
        
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, pharmacy_id);
            
            int rows = pstmt.executeUpdate();
            System.out.println("Pharmacy Record Successfully DELETED ->  (" + rows + "rows)");
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        pharmacyManager manager = new pharmacyManager();
        manager.connect();

        // Let's test...
        manager.createPharmacy("C1234", "test약국", "서울특별시 서대문구 연희로 1234", "02-123-4567", "12345", 123.45f, 78.910f, true);
        manager.updatePharmacy("C1234", "update약국", "서울특별시 서대문구 연세로 5678", "02-456-7891", "54321", 654.321f, 10.987f, true);
        manager.deletePharmacy("C1234");
        
        manager.disconnect();
    }
}

