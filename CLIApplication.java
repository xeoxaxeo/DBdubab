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
    private static final String USER = "dbdubob";
    private static final String PASS = "testpw";

    public static void main(String[] args) {
        if (args.length != 1) {
            printUsage();
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC 드라이버 로드 실패: " + e.getMessage());
            return;
        }

        // 1
        if ("active".equals(args[0])) {
            System.out.println("=== 현재 운영 중인 서대문구 약국 목록 조회 ===");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
                PharmacyDAO dao = new PharmacyDAO(conn);
                dao.findActivePharmaciesNow();
            } catch (SQLException e) {
                System.err.println("DB 작업 중 오류 발생: " + e.getMessage());
            }
        }
        // 2. emergency CRUD
        else if ("emergency-insert".equals(args[0])) {
            EmergencyManager manager = new EmergencyManager();
            manager.connect();
            manager.createEmergencyStore("PHMH1234", "GS_TEST", "서울특별시 테스트구 테스트길", "02-123-4567", 123f, 456f);
            manager.disconnect();
        } else if ("emergency-update".equals(args[0])) {
            EmergencyManager manager = new EmergencyManager();
            manager.connect();
            manager.updateEmergencyStore("PHMH1234", "GS_UPDATE", "서울특별시 업데이트구 업데이트길", "02-987-6543", 456f, 123f);
            manager.disconnect();
        } else if ("emergency-delete".equals(args[0])) {
            EmergencyManager manager = new EmergencyManager();
            manager.connect();
            manager.deleteEmergencyStore("PHMH1234");
            manager.disconnect();
        }
        // 3. pharmacy CRUD
        else if ("pharmacy-insert".equals(args[0])) {
            PharmacyManager manager = new PharmacyManager();
            manager.connect();
            manager.createPharmacy("P0001", "테스트약국", "서울특별시 테스트로 123", "02-1111-2222", "12345", 127.001f, 37.123f, true);
            manager.disconnect();
        } else if ("pharmacy-update".equals(args[0])) {
            PharmacyManager manager = new PharmacyManager();
            manager.connect();
            manager.updatePharmacy("P0001", "업데이트약국", "서울특별시 업데이트로 456", "02-9999-8888", "54321", 127.100f, 37.200f, true);
            manager.disconnect();
        } else if ("pharmacy-delete".equals(args[0])) {
            PharmacyManager manager = new PharmacyManager();
            manager.connect();
            manager.deletePharmacy("P0001");
            manager.disconnect();
        }
        
        // 4. roll up
        else if ("rollup".equals(args[0])) {
            System.out.println("=== [지역+요일] 기준 다차원 약국 집계 ===");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
                RollupManager rollupManager = new RollupManager(conn);
                rollupManager.showRegionDayRollup();
            } catch (SQLException e) {
                System.err.println("DB 작업 중 오류 발생: " + e.getMessage());
            }
        }
        
        // ETC...
        else {
            printUsage();
        }
    }

    
    // 여기에 사용법 하나씩 추가
    private static void printUsage() {
        System.out.println("Usage: java -jar DBdubab.jar [명령어]");
        System.out.println("  active             → 운영 중인 약국 조회");
        System.out.println("  emergency-insert   → 응급약국 등록");
        System.out.println("  emergency-update   → 응급약국 수정");
        System.out.println("  emergency-delete   → 응급약국 삭제");
        System.out.println("  pharmacy-insert    → 약국 등록");
        System.out.println("  pharmacy-update    → 약국 수정");
        System.out.println("  pharmacy-delete    → 약국 삭제");
        System.out.println("  rollup             → [지역 + 요일] 기준 다차원 약국 집계");
    }
}