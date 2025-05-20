package dbproject.dbdubab;

public class AreaDayStats {
    private final String area;
    private final String day;
    private final int count;
    private final String names;

    public AreaDayStats(String area, String day, int count, String names) {
        this.area   = area;
        this.day    = day;
        this.count  = count;
        this.names  = names;
    }

    public String getArea()  { return area; }
    public String getDay()   { return day; }
    public int    getCount(){ return count; }
    public String getNames(){ return names; }
}
