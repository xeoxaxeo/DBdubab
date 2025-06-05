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
        String sql = """
                SELECT
                  region,
                  day_of_week,
                  pharmacy_count
                FROM (
                  SELECT
                    COALESCE(
                      SUBSTRING_INDEX(SUBSTRING_INDEX(address, ' ', 2), ' ', -1),
                      '전체'
                    ) AS region,
                    COALESCE(oh.day_of_week, '전체') AS day_of_week,
                    COUNT(*) AS pharmacy_count
                  FROM pharmacy p
                  JOIN open_hours oh ON p.pharmacy_id = oh.pharmacy_id
                  GROUP BY region, day_of_week WITH ROLLUP
                ) AS sub
                WHERE region = '서대문구'
                ORDER BY
                  FIELD(day_of_week, '월','화','수','목','금','토','일','전체')
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("지역\t요일\t약국 수");
            while (rs.next()) {
                String region      = rs.getString("region");
                String dayOfWeek   = rs.getString("day_of_week");
                int pharmacyCount  = rs.getInt("pharmacy_count");

                System.out.printf("%s\t%s\t%d%n", region, dayOfWeek, pharmacyCount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
