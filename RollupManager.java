package dbproject.dbdubab;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RollupManager {
	
    private final Connection conn;

    public RollupManager(Connection conn) {
        this.conn = conn;
    }

    public void showRegionDayRollup() {
        String sql =
            "SELECT " +
            "  SUBSTRING_INDEX(SUBSTRING_INDEX(address, ' ', 2), ' ', -1) AS region, " +
            "  oh.day_of_week, " +
            "  COUNT(DISTINCT p.pharmacy_id) AS pharmacy_count " +
            "FROM pharmacy p " +
            "JOIN open_hours oh ON p.pharmacy_id = oh.pharmacy_id " +
            "GROUP BY ROLLUP(region, oh.day_of_week) " +
            "ORDER BY " +
            "  region IS NULL, region, " +
            "  (oh.day_of_week IS NULL), " +
            "  FIELD(oh.day_of_week, '월', '화', '수', '목', '금', '토', '일')";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            System.out.println("지역\t요일\t약국 수");
            while (rs.next()) {
                String region = rs.getString("region");
                String dayOfWeek = rs.getString("day_of_week");
                int count = rs.getInt("pharmacy_count");

                if (region == null) region = "전체";
                if (dayOfWeek == null) dayOfWeek = "전체";
                System.out.printf("%s\t%s\t%d\n", region, dayOfWeek, count);
            }
        } catch (SQLException e) {
            System.err.println("[오류] ROLLUP 통계 조회 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}