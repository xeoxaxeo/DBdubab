import java.sql.*;

public class PharmacyDAO {
    private Connection conn;

    public PharmacyDAO(Connection conn) {
        this.conn = conn;
    }

    public void findActivePharmaciesNow() throws SQLException {
        String sql = "SELECT p.pharmacy_id, p.name, p.address, p.phone " +
                "FROM active_pharmacy p " +
                "JOIN open_hours o ON p.pharmacy_id = o.pharmacy_id " +
                "WHERE o.day_of_week = ? AND ? BETWEEN o.start_time AND o.end_time";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // 현재 요일과 시간 구하기
            String currentDay = TimeUtil.getCurrentDayOfWeekKor();
            String currentTime = TimeUtil.getCurrentTime();

            pstmt.setString(1, currentDay);
            pstmt.setString(2, currentTime);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    System.out.printf("약국ID: %s, 이름: %s, 주소: %s, 전화: %s%n",
                            rs.getString("pharmacy_id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("phone"));
                }
            }
        }
    }
    public void findActivePharmaciesByRegion(String regionKeyword) throws SQLException {
        String sql = "SELECT p.pharmacy_id, p.name, p.address, p.phone " +
                "FROM active_pharmacy p " +
                "JOIN open_hours o ON p.pharmacy_id = o.pharmacy_id " +
                "WHERE o.day_of_week = ? " +
                "AND ? BETWEEN o.start_time AND o.end_time " +
                "AND p.address LIKE ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String currentDay = TimeUtil.getCurrentDayOfWeekKor();
            String currentTime = TimeUtil.getCurrentTime();

            pstmt.setString(1, currentDay);
            pstmt.setString(2, currentTime);
            pstmt.setString(3, "%" + regionKeyword + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println("해당 시간과 지역에 운영 중인 약국을 찾지 못했습니다.");
                    findNearbyEmergencyStores(regionKeyword);
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

    public void findPharmaciesByOperatingTimeWithRank() throws SQLException {
        String sql =
                "SELECT RANK() OVER (ORDER BY SUM(TIME_TO_SEC(TIMEDIFF(o.end_time, o.start_time))) DESC) AS `rank`, " +
                        "       p.pharmacy_id, p.name, p.address, p.phone, " +
                        "       SEC_TO_TIME(SUM(TIME_TO_SEC(TIMEDIFF(o.end_time, o.start_time)))) AS total_operating_time " +
                        "FROM active_pharmacy p " +
                        "JOIN open_hours o ON p.pharmacy_id = o.pharmacy_id " +
                        "GROUP BY p.pharmacy_id, p.name, p.address, p.phone " +
                        "ORDER BY SUM(TIME_TO_SEC(TIMEDIFF(o.end_time, o.start_time))) DESC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\n운영시간 순위 (전체):");
                while (rs.next()) {
                    int rank = rs.getInt("rank");
                    String id = rs.getString("pharmacy_id");
                    String name = rs.getString("name");
                    String address = rs.getString("address");
                    String phone = rs.getString("phone");
                    String rawTime = rs.getString("total_operating_time"); // e.g. "74:30:00"

                    String[] parts = rawTime.split(":");
                    int hours = Integer.parseInt(parts[0]);
                    int minutes = Integer.parseInt(parts[1]);

                    System.out.printf("순위 %d | 약국ID: %s | 이름: %s | 주소: %s | 전화: %s | 총 운영시간: %d시간 %d분%n",
                            rank, id, name, address, phone, hours, minutes);
                }
            }
        }
    }
    public void findPharmaciesByOperatingTimeWithRank(String regionKeyword) throws SQLException {
        String sql =
                "SELECT RANK() OVER (ORDER BY SUM(TIME_TO_SEC(TIMEDIFF(o.end_time, o.start_time))) DESC) AS `rank`, " +
                        "       p.pharmacy_id, p.name, p.address, p.phone, " +
                        "       SEC_TO_TIME(SUM(TIME_TO_SEC(TIMEDIFF(o.end_time, o.start_time)))) AS total_operating_time " +
                        "FROM active_pharmacy p " +
                        "JOIN open_hours o ON p.pharmacy_id = o.pharmacy_id " +
                        "WHERE p.address LIKE ? " +
                        "GROUP BY p.pharmacy_id, p.name, p.address, p.phone " +
                        "ORDER BY SUM(TIME_TO_SEC(TIMEDIFF(o.end_time, o.start_time))) DESC";

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
}
