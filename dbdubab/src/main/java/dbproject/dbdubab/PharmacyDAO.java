package dbproject.dbdubab;

import dbproject.dbdubab.TimeUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

    // 지역/요일별 운영 약국 수 통계
    // @param minCount: 최소 개수 기준. 기본은 1
    public List<AreaDayStats> countByAreaAndDayWithNames(int minCount) throws SQLException {
        String sql = """
            SELECT
              -- 1) 세 번째 토큰 뽑기 → 2) 앞 3글자만 취해서 area로
              ANY_VALUE(
                SUBSTRING_INDEX(
                    SUBSTRING_INDEX(p.address, ' ', 3),
                ' ', -1)
              ) AS area,
              o.day_of_week AS day,
              COUNT(*) AS cnt,
              GROUP_CONCAT(p.name ORDER BY p.name SEPARATOR ', ') AS names
            FROM pharmacy p
              JOIN open_hours o ON p.pharmacy_id = o.pharmacy_id
            WHERE p.is_operating = TRUE
            GROUP BY
                SUBSTRING(
                    SUBSTRING_INDEX(
                        SUBSTRING_INDEX(p.address, ' ', 3),
                    ' ', -1),
                1, 3),
                o.day_of_week
            HAVING cnt >= ?
            ORDER BY area ASC,
                     FIELD(o.day_of_week, '월','화','수','목','금','토','일')
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, minCount);
            try (ResultSet rs = ps.executeQuery()) {
                List<AreaDayStats> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new AreaDayStats(
                            rs.getString("area"),
                            rs.getString("day"),
                            rs.getInt("cnt"),
                            rs.getString("names")
                    ));
                }
                return list;
            }
        }
    }
}
