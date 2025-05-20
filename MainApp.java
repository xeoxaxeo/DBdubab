import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class MainApp {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/db_project?serverTimezone=Asia/Seoul&useSSL=false&allowPublicKeyRetrieval=true";
        String user = "root"; // 예시
        String password = "yHG55838088@"; // 예시

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            PharmacyDAO dao = new PharmacyDAO(conn);
            Scanner scanner = new Scanner(System.in);

            System.out.println("=== 약국 검색 메뉴 ===");
            System.out.println("1. 현재 운영 중인 약국 조회");
            System.out.println("2. 지역 기반 약국 조회");
            System.out.print("선택 (1 또는 2): ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // 버퍼 클리어

            if (choice == 1) {
                dao.findActivePharmaciesNow();
            } else if (choice == 2) {
                System.out.print("지역 키워드를 입력하세요 (예: 신촌, 연희동 등): ");
                String region = scanner.nextLine();
                dao.findActivePharmaciesByRegion(region);
            } else {
                System.out.println("잘못된 선택입니다.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
