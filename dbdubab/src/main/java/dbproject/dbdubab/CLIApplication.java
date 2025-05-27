package dbproject.dbdubab;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import dbproject.dbdubab.PharmacyDAO;
import dbproject.dbdubab.AreaDayStats;
import dbproject.dbdubab.EmergencyManager;
import dbproject.dbdubab.PharmacyManager;
import dbproject.dbdubab.RollupManager;

public class CLIApplication {
    private static final String URL = "jdbc:mysql://localhost:3306/db_project"
            + "?useUnicode=true"
            + "&characterEncoding=UTF-8"
            + "&serverTimezone=Asia/Seoul";
    private static final String USER = "root";
    private static final String PASS = "0000";

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC 드라이버 로드 실패: " + e.getMessage());
            return;
        }

        String action = args[0];
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            switch (action) {
                case "active" -> {
                    System.out.println("=== 현재 운영 중인 서대문구 약국 목록 조회 ===");
                    new PharmacyDAO(conn).findActivePharmaciesNow();
                }
                case "stats" -> {
                    int minCount = 1;
                    if (args.length >= 2) {
                        try {
                            minCount = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            System.err.println("minCount는 정수여야 합니다. 기본값 1 사용.");
                        }
                    }
                    System.out.printf("=== 지역/요일별 운영 약국 수 통계 (minCount ≥ %d) ===%n", minCount);
                    List<AreaDayStats> stats = new PharmacyDAO(conn).countByAreaAndDayWithNames(minCount);
                    System.out.printf("%-10s | %-2s | %4s | %s%n", "지역", "요일", "개수", "약국이름");
                    System.out.println("-----------+------+----+-----------------------------");
                    for (AreaDayStats r : stats) {
                        System.out.printf("%-10s | %-2s | %4d | %s%n",
                                r.getArea(), r.getDay(), r.getCount(), r.getNames());
                    }
                }
                case "regionActive" -> {
                    if (args.length < 2) {
                        System.err.println("지역 키워드를 입력하세요.");
                        printUsage();
                    } else {
                        String regionKeyword = args[1];
                        System.out.printf("=== '%s' 지역에서 현재 운영 중인 약국 목록 조회 ===%n", regionKeyword);
                        new PharmacyDAO(conn).findActivePharmaciesByRegion(regionKeyword);
                    }
                }
                case "operatingRank" -> {
                    if (args.length < 2) {
                        System.err.println("지역 키워드를 입력하세요.");
                        printUsage();
                    } else {
                        String regionKeyword = args[1];
                        System.out.printf("=== '%s' 지역 약국 운영시간 순위 조회 ===%n", regionKeyword);
                        new PharmacyDAO(conn).findPharmaciesByOperatingTimeWithRank(regionKeyword);
                    }
                }
                case "detail" -> {
                    if (args.length < 2) {
                        System.err.println("약국 ID를 입력하세요.");
                        printUsage();
                    } else {
                        String pharmacyId = args[1];
                        System.out.println("=== 약국 상세 정보 ===");
                        new PharmacyDAO(conn).getPharmacyDetails(pharmacyId);
                    }
                }
                case "longhours" -> {
                    System.out.println("=== 평균 운영시간보다 긴 약국 목록 ===");
                    new PharmacyDAO(conn).getLongOperatingPharmacies();
                }
                case "rollup" -> {
                    System.out.println("=== [지역+요일] 기준 다차원 약국 집계 ===");
                    new RollupManager(conn).showRegionDayRollup();
                }
                case "emergency-insert" -> {
                    if (args.length != 7) {
                        System.out.println("사용법: emergency-insert <id> <name> <address> <phone> <longitude> <latitude>");
                        break;
                    }
                    new EmergencyManager(conn).createEmergencyStore(
                            args[1], args[2], args[3], args[4],
                            Float.parseFloat(args[5]), Float.parseFloat(args[6])
                    );
                }
                case "emergency-update" -> {
                    if (args.length != 7) {
                        System.out.println("사용법: emergency-update <id> <name> <address> <phone> <longitude> <latitude>");
                        break;
                    }
                    new EmergencyManager(conn).updateEmergencyStore(
                            args[1], args[2], args[3], args[4],
                            Float.parseFloat(args[5]), Float.parseFloat(args[6])
                    );
                }
                case "emergency-delete" -> {
                    if (args.length != 2) {
                        System.out.println("사용법: emergency-delete <id>");
                        break;
                    }
                    new EmergencyManager(conn).deleteEmergencyStore(args[1]);
                }
                case "pharmacy-insert" -> {
                    if (args.length != 9) {
                        System.out.println("사용법: pharmacy-insert <id> <name> <address> <phone> <zipcode> <longitude> <latitude> <isOperating>");
                        break;
                    }
                    new PharmacyManager(conn).createPharmacy(
                            args[1], args[2], args[3], args[4], args[5],
                            Float.parseFloat(args[6]), Float.parseFloat(args[7]),
                            Boolean.parseBoolean(args[8])
                    );
                }
                case "pharmacy-update" -> {
                    if (args.length != 9) {
                        System.out.println("사용법: pharmacy-update <id> <name> <address> <phone> <zipcode> <longitude> <latitude> <isOperating>");
                        break;
                    }
                    new PharmacyManager(conn).updatePharmacy(
                            args[1], args[2], args[3], args[4], args[5],
                            Float.parseFloat(args[6]), Float.parseFloat(args[7]),
                            Boolean.parseBoolean(args[8])
                    );
                }
                case "pharmacy-delete" -> {
                    if (args.length != 2) {
                        System.out.println("사용법: pharmacy-delete <id>");
                        break;
                    }
                    new PharmacyManager(conn).deletePharmacy(args[1]);
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
        System.out.println("  regionActive <region> → 지정 지역 운영 약국 조회 (없으면 편의점 목록 출력)");
        System.out.println("  operatingRank <region> → 운영시간 순위 조회");
        System.out.println("  detail <pharmacyId>  → 약국 상세 정보 조회");
        System.out.println("  longhours            → 평균 운영시간보다 긴 약국 조회");
        System.out.println("  rollup               → [ 지역+요일 ] 다차원 집계");
        System.out.println("  emergency-insert     → 응급약국 등록");
        System.out.println("  emergency-update     → 응급약국 수정");
        System.out.println("  emergency-delete     → 응급약국 삭제");
        System.out.println("  pharmacy-insert      → 약국 등록");
        System.out.println("  pharmacy-update      → 약국 수정");
        System.out.println("  pharmacy-delete      → 약국 삭제");
    }
}
