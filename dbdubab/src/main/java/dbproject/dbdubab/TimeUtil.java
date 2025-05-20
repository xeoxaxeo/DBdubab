package dbproject.dbdubab;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.Map;

public class TimeUtil {
    private static final Map<DayOfWeek, String> dayOfWeekKorMap = Map.of(
            DayOfWeek.MONDAY,    "월",
            DayOfWeek.TUESDAY,   "화",
            DayOfWeek.WEDNESDAY, "수",
            DayOfWeek.THURSDAY,  "목",
            DayOfWeek.FRIDAY,    "금",
            DayOfWeek.SATURDAY,  "토",
            DayOfWeek.SUNDAY,    "일"
    );

    public static String getCurrentDayOfWeekKor() {
        return dayOfWeekKorMap.get(LocalDate.now().getDayOfWeek());
    }
}
