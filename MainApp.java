import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

public class MainApp {
    public static void main(String[] args) {
        Properties props = new Properties();

        try (FileInputStream fis = new FileInputStream("application.properties")) {
            props.load(fis);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                PharmacyDAO dao = new PharmacyDAO(conn);
                Scanner scanner = new Scanner(System.in);

                System.out.println("=== 약국 검색 메뉴 ===");
                System.out.println("1. 현재 운영 중인 약국 조회");
                System.out.println("2. 지역 기반 약국 조회");
                System.out.println("3. 지역 기반 운영시간 순위 조회");
                System.out.print("선택 (1, 2 또는 3): ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // 버퍼 클리어

                switch (choice) {
                    case 1:
                        dao.findActivePharmaciesNow();
                        break;
                    case 2:
                        System.out.print("지역 키워드를 입력하세요 (예: 신촌, 연희동 등): ");
                        String region1 = scanner.nextLine();
                        dao.findActivePharmaciesByRegion(region1);
                        break;
                    case 3:
                        System.out.print("지역 키워드를 입력하세요 (예: 신촌, 연희동 등): ");
                        String region2 = scanner.nextLine();
                        dao.findPharmaciesByOperatingTimeWithRank(region2);
                        break;
                    default:
                        System.out.println("잘못된 선택입니다.");
                }

            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
