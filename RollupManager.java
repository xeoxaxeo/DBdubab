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
        // 'address'에서 '구'를 추출 -> 지역구(서대문구...)로 사용
        String sql =
            "SELECT " +
            "  SUBSTRING_INDEX(SUBSTRING_INDEX(address, ' ', 2), ' ', -1) AS region, " +
            "  oh.day_of_week, " +
            "  COUNT(*) AS pharmacy_count " +
            "FROM pharmacy p " +
            "JOIN open_hours oh ON p.pharmacy_id = oh.pharmacy_id " +
            "GROUP BY ROLLUP(region, oh.day_of_week) " +
            "ORDER BY region, oh.day_of_week";

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
            e.printStackTrace();
        }
    }
}
