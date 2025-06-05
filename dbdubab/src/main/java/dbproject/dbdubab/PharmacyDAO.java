package dbproject.dbdubab;

import dbproject.dbdubab.TimeUtil;

import java.sql.*;
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
        SELECT p.*
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
                System.out.println("약국ID | 이름 | 주소 | 전화 | 우편번호 | 경도 | 위도 | 운영 여부");
                System.out.println("------------------------------------------------------------");

                while (rs.next()) {
                    String pharmacyId = rs.getString("pharmacy_id");
                    String name = rs.getString("name");
                    String address = rs.getString("address");
                    String phone = rs.getString("phone");
                    String zipCode = rs.getString("zip_code");
                    double longitude = rs.getDouble("longitude");
                    double latitude = rs.getDouble("latitude");
                    boolean isOperating = rs.getBoolean("is_operating");

                    String operatingText = isOperating ? "운영" : "미운영";

                    System.out.printf(
                            "%s | %s | %s | %s | %s | %.6f | %.6f | %s%n",
                            pharmacyId,
                            name,
                            address,
                            phone,
                            zipCode,
                            longitude,
                            latitude,
                            operatingText
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

    // 약국 상세 정보 조회
    public void getPharmacyDetails(String pharmacyId) throws SQLException{
        String sql1 = """
        SELECT pharmacy_id, name, address, phone, zip_code, longitude, latitude
        FROM pharmacy WHERE pharmacy_id = ?
        """;
        String sql2 = """
        SELECT day_of_week, start_time, end_time
        FROM open_hours WHERE pharmacy_id = ?
        ORDER BY FIELD(day_of_week, '월', '화', '수', '목', '금', '토', '일')
        """;
        String sql3 = """
        SELECT is_open_sunday, is_open_holiday FROM holiday_schedule WHERE pharmacy_id = ?
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql1)) {
            pstmt.setString(1, pharmacyId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.printf("약국ID: %s\n이름: %s\n주소: %s\n전화: %s\n우편번호: %s\n위도: %.6f\n경도: %.6f\n",
                            rs.getString("pharmacy_id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("phone"),
                            rs.getString("zip_code"),
                            rs.getDouble("latitude"),
                            rs.getDouble("longitude"));
                } else {
                    System.out.println("해당 ID의 약국을 찾을 수 없습니다.");
                    return;
                }
            }
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql2)) {
            pstmt.setString(1, pharmacyId);
            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\n요일별 운영 시간:");
                while (rs.next()) {
                    System.out.printf("- %s: %s ~ %s\n",
                            rs.getString("day_of_week"),
                            rs.getString("start_time"),
                            rs.getString("end_time"));
                }
            }
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql3)) {
            pstmt.setString(1, pharmacyId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String sunday = rs.getBoolean("is_open_sunday") ? "운영" : "미운영";
                    String holiday = rs.getBoolean("is_open_holiday") ? "운영" : "미운영";
                    System.out.println("\n휴일 운영 여부:");
                    System.out.printf("- 일요일: %s\n- 공휴일: %s\n", sunday, holiday);
                } else {
                    System.out.println("\n(휴일 운영 정보가 없습니다.)");
                }
            }
        }
    }
    public void getLongOperatingPharmacies() throws SQLException {
        String sql = """
            SELECT p.pharmacy_id, p.name, p.address,
                   SEC_TO_TIME(SUM(TIME_TO_SEC(TIMEDIFF(o.end_time, o.start_time)))) AS total_hours
            FROM pharmacy p
            JOIN open_hours o ON p.pharmacy_id = o.pharmacy_id
            WHERE p.is_operating = TRUE
            GROUP BY p.pharmacy_id, p.name, p.address
            HAVING SUM(TIME_TO_SEC(TIMEDIFF(o.end_time, o.start_time))) >
                   (
                     SELECT AVG(total_duration) FROM (
                        SELECT SUM(TIME_TO_SEC(TIMEDIFF(end_time, start_time))) AS total_duration
                        FROM open_hours
                        GROUP BY pharmacy_id
                     ) AS avg_table
                   )
            ORDER BY total_hours DESC
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.printf("약국ID: %s | 이름: %s | 주소: %s | 총 운영 시간: %s%n",
                            rs.getString("pharmacy_id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("total_hours"));
                }
            }
        }
    }

    public void findActivePharmaciesByRegion(String regionKeyword) throws SQLException {
        String sql = """
                SELECT p.pharmacy_id, p.name, p.address, p.phone
                FROM active_pharmacy p
                JOIN open_hours o ON p.pharmacy_id = o.pharmacy_id
                WHERE
                  o.day_of_week = ELT(
                    WEEKDAY(CURDATE()) + 1,
                    '월','화','수','목','금','토','일'
                  )
                  AND CURTIME() BETWEEN o.start_time AND o.end_time
                  AND p.address LIKE CONCAT('%', ?, '%')
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, regionKeyword);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println("해당 시간과 지역에 운영 중인 약국을 찾지 못했습니다.");
                    findNearbyEmergencyStores(regionKeyword);
                    return;
                }

                System.out.println("\n현재 '" + regionKeyword + "' 지역에서 운영 중인 약국 목록:");
                while (rs.next()) {
                    System.out.printf("약국ID: %s | 이름: %s | 주소: %s | 전화: %s%n",
                            rs.getString("pharmacy_id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("phone"));
                }
            }
        }
    }

    public void findNearbyEmergencyStores(String regionKeyword) throws SQLException {
        String sql = "SELECT store_id, store_name, address, phone " +
                "FROM emergency_store " +
                "WHERE address LIKE ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + regionKeyword + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println("해당 지역 근처에 편의점도 없습니다.");
                    return;
                }

                System.out.println("\n근처 편의점 목록:");
                while (rs.next()) {
                    System.out.printf("편의점ID: %s | 이름: %s | 주소: %s | 전화: %s%n",
                            rs.getString("store_id"),
                            rs.getString("store_name"),
                            rs.getString("address"),
                            rs.getString("phone"));
                }
            }
        }
    }

    public void findPharmaciesByOperatingTimeWithRank(String regionKeyword) throws SQLException {
        String sql =
        """
        SELECT RANK() OVER (ORDER BY SUM(TIME_TO_SEC(TIMEDIFF(o.end_time, o.start_time))) DESC) AS `rank`
             , p.pharmacy_id, p.name, p.address, p.phone
             , SEC_TO_TIME(SUM(TIME_TO_SEC(TIMEDIFF(o.end_time, o.start_time)))) AS total_operating_time
        FROM active_pharmacy p
        JOIN open_hours o ON p.pharmacy_id = o.pharmacy_id
        WHERE p.address LIKE ?
        GROUP BY p.pharmacy_id, p.name, p.address, p.phone
        ORDER BY SUM(TIME_TO_SEC(TIMEDIFF(o.end_time, o.start_time))) DESC
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + regionKeyword + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\n운영시간 순위 (긴 순):");
                while (rs.next()) {
                    System.out.printf("순위 %d | 약국ID: %s | 이름: %s | 주소: %s | 전화: %s | 총 운영시간: %s%n",
                            rs.getInt("rank"),
                            rs.getString("pharmacy_id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("phone"),
                            rs.getString("total_operating_time"));
                }
            }
        }
    }
    public void showOpenPharmaciesAt(String day, String time) throws SQLException {
        String query = """
        SELECT p.pharmacy_id, p.name, p.address, p.phone
        FROM active_pharmacy p
        JOIN open_hours o ON p.pharmacy_id = o.pharmacy_id
        WHERE o.day_of_week = ? AND TIME(?) BETWEEN o.start_time AND o.end_time
    """;

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, day);
            pstmt.setString(2, time);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println("해당 시간에 운영 중인 약국이 없습니다.");
                    return;
                }
                System.out.println("\n운영 중인 약국 목록:");
                while (rs.next()) {
                    System.out.printf("약국ID: %s | 이름: %s | 주소: %s | 전화: %s%n",
                            rs.getString("pharmacy_id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("phone"));
                }
            }
        }
    }
}
