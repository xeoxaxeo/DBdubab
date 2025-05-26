package dbproject.dbdubab;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PharmacyManager {
    private final Connection conn;

    public PharmacyManager(Connection conn) {
        this.conn = conn;
    }

    public void createPharmacy(String pharmacyId, String name, String address, String phone, String zipCode, float longitude, float latitude, boolean isOperating) {
        String sql = "INSERT INTO pharmacy (pharmacy_id, name, address, phone, zip_code, longitude, latitude, is_operating) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pharmacyId);
            pstmt.setString(2, name);
            pstmt.setString(3, address);
            pstmt.setString(4, phone);
            pstmt.setString(5, zipCode);
            pstmt.setFloat(6, longitude);
            pstmt.setFloat(7, latitude);
            pstmt.setBoolean(8, isOperating);
            int rows = pstmt.executeUpdate();
            System.out.println("Pharmacy Record ADDED -> " + rows + " rows");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePharmacy(String pharmacyId, String newName, String newAddress, String newPhone, String newZipCode, float newLongitude, float newLatitude, boolean newIsOperating) {
        String sql = "UPDATE pharmacy SET name=?, address=?, phone=?, zip_code=?, longitude=?, latitude=?, is_operating=? WHERE pharmacy_id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, newAddress);
            pstmt.setString(3, newPhone);
            pstmt.setString(4, newZipCode);
            pstmt.setFloat(5, newLongitude);
            pstmt.setFloat(6, newLatitude);
            pstmt.setBoolean(7, newIsOperating);
            pstmt.setString(8, pharmacyId);
            int rows = pstmt.executeUpdate();
            System.out.println("Pharmacy Record UPDATED -> " + rows + " rows");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePharmacy(String pharmacyId) {
        String sql = "DELETE FROM pharmacy WHERE pharmacy_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pharmacyId);
            int rows = pstmt.executeUpdate();
            System.out.println("Pharmacy Record DELETED -> " + rows + " rows");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
