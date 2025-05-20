package dbproject.dbdubab;

import dbproject.dbdubab.TimeUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;

public class PharmacyDAO {

    private final Connection conn;

    public PharmacyDAO(Connection conn) {
        this.conn = conn;
    }

    public void findActivePharmaciesNow() throws SQLException {
        String sql = """
            SELECT p.pharmacy_id, p.name, p.address, p.phone
              FROM active_pharmacy p
              JOIN open_hours o
                ON p.pharmacy_id = o.pharmacy_id
             WHERE o.day_of_week = ?
               AND ? BETWEEN o.start_time AND o.end_time
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String currentDay = TimeUtil.getCurrentDayOfWeekKor();
            Time currentTime = Time.valueOf(LocalTime.now());

            pstmt.setString(1, currentDay);
            pstmt.setTime(2, currentTime);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    System.out.printf(
                            "약국ID: %s, 이름: %s, 주소: %s, 전화: %s%n",
                            rs.getString("pharmacy_id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("phone")
                    );
                }
            }
        }
    }
}
