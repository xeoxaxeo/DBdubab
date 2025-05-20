import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

public class TimeUtil {
    private static final Map<DayOfWeek, String> dayOfWeekKorMap = new HashMap<>();
    static {
        dayOfWeekKorMap.put(DayOfWeek.MONDAY, "월");
        dayOfWeekKorMap.put(DayOfWeek.TUESDAY, "화");
        dayOfWeekKorMap.put(DayOfWeek.WEDNESDAY, "수");
        dayOfWeekKorMap.put(DayOfWeek.THURSDAY, "목");
        dayOfWeekKorMap.put(DayOfWeek.FRIDAY, "금");
        dayOfWeekKorMap.put(DayOfWeek.SATURDAY, "토");
        dayOfWeekKorMap.put(DayOfWeek.SUNDAY, "일");
    }

    public static String getCurrentDayOfWeekKor() {
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek day = now.getDayOfWeek();
        return dayOfWeekKorMap.get(day);
    }

    public static String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}

