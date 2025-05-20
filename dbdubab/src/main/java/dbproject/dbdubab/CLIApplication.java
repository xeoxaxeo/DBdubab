package dbproject.dbdubab;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class CLIApplication {
    private static final String URL = "jdbc:mysql://localhost:3306/db_project"
            + "?useUnicode=true"
            + "&characterEncoding=UTF-8"
            + "&serverTimezone=Asia/Seoul";

    // 로컬 환경에 맞게 설정
    private static final String USER = "root";
    private static final String PASS = "0000";

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String action = args[0];

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC 드라이버 로드 실패: " + e.getMessage());
            return;
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            PharmacyDAO dao = new PharmacyDAO(conn);

            // case에 기능 추가
            switch (action) {
                case "active" -> {
                    System.out.println("=== 현재 운영 중인 서대문구 약국 목록 조회 ===");
                    dao.findActivePharmaciesNow();
                }
                case "stats" -> {
                    int minCount = 1;
                    if (args.length >= 2) {
                        try {
                            minCount = Integer.parseInt(args[1]);
                        } catch (NumberFormatException ex) {
                            System.err.println("minCount는 정수여야 합니다. 기본값 1 사용.");
                        }
                    }
                    System.out.printf("=== 지역/요일별 운영 약국 수 통계 (minCount ≥ %d) ===%n", minCount);

                    List<AreaDayStats> stats = dao.countByAreaAndDayWithNames(minCount);

                    // 표 헤더
                    System.out.printf("%-10s | %-2s | %4s | %s%n", "지역", "요일", "개수", "약국이름");
                    System.out.println("-----------+------+----+-----------------------------");
                    // 데이터 출력
                    for (AreaDayStats r : stats) {
                        System.out.printf("%-10s | %-2s | %4d | %s%n",
                                r.getArea(), r.getDay(), r.getCount(), r.getNames()
                        );
                    }
                }
                default -> {
                    System.err.println("지정되지 않은 action: " + action);
                    printUsage();
                }
            }

        } catch (SQLException e) {
            System.err.println("DB 오류: " + e.getMessage());
        }
    }

    private static void printUsage() {
        System.out.println("Usage: java -jar DBdubab.jar <action> [parameters]");
        System.out.println("  active               → 운영 중인 약국 조회");
        System.out.println("  stats [minCount]     → 지역/요일별 약국 수 통계 (HAVING COUNT ≥ minCount)");
    }
}