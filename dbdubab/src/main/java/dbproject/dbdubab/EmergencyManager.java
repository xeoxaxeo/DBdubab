package dbproject.dbdubab;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EmergencyManager {
    private final Connection conn;

    // 생성자에서 Connection 주입
    public EmergencyManager(Connection conn) {
        this.conn = conn;
    }

    // Insert -- Emergency Store Info
    public void createEmergencyStore(String storeId, String storeName, String address, String phone, float longitude, float latitude) {
        String sql = "INSERT INTO emergency_store (store_id, store_name, address, phone, longitude, latitude) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, storeId);
            pstmt.setString(2, storeName);
            pstmt.setString(3, address);
            pstmt.setString(4, phone);
            pstmt.setFloat(5, longitude);
            pstmt.setFloat(6, latitude);
            int rows = pstmt.executeUpdate();
            System.out.println("Emergency Store Record ADDED -> " + rows + " rows");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update -- Emergency Store Info
    public void updateEmergencyStore(String storeId, String newName, String newAddress, String newPhone, float newLongitude, float newLatitude) {
        String sql = "UPDATE emergency_store SET store_name=?, address=?, phone=?, longitude=?, latitude=? WHERE store_id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, newAddress);
            pstmt.setString(3, newPhone);
            pstmt.setFloat(4, newLongitude);
            pstmt.setFloat(5, newLatitude);
            pstmt.setString(6, storeId);
            int rows = pstmt.executeUpdate();
            System.out.println("Emergency Store Record UPDATED -> " + rows + " rows");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete -- Emergency Store Info
    public void deleteEmergencyStore(String storeId) {
        String sql = "DELETE FROM emergency_store WHERE store_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, storeId);
            int rows = pstmt.executeUpdate();
            System.out.println("Emergency Store Record DELETED -> " + rows + " rows");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
