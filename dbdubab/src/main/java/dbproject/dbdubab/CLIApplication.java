package dbproject.dbdubab;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CLIApplication {
    private static final String URL = "jdbc:mysql://localhost:3306/db_project"
            + "?useUnicode=true"
            + "&characterEncoding=UTF-8"
            + "&serverTimezone=Asia/Seoul";

    // 로컬 환경에 맞게 설정
    private static final String USER = "root";
    private static final String PASS = "0000";

    public static void main(String[] args) {
        // 여기에 기능 하나씩 추가

        // 1. 운영 중인 약국 조회
        if (args.length != 1 || !"active".equals(args[0])) {
            printUsage();
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC 드라이버 로드 실패: " + e.getMessage());
            return;
        }

        System.out.println("=== 현재 운영 중인 서대문구 약국 목록 조회 ===");
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            PharmacyDAO dao = new PharmacyDAO(conn);
            dao.findActivePharmaciesNow();
        } catch (SQLException e) {
            System.err.println("DB 작업 중 오류 발생: " + e.getMessage());
        }
    }

    // 여기에 사용법 하나씩 추가
    private static void printUsage() {
        System.out.println("Usage: java -jar DBdubab.jar active");
        System.out.println("  active  → 운영 중인 약국 조회");
    }
}