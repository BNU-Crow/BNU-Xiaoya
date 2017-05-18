package cn.xuhongxu.LibrarySeat;

/**
 * Created by xuhon on 5/17/2017.
 */

public class ReservationHistory {
    public String id, date, begin, end, location, state;

    public ReservationHistory(String id, String date, String begin, String end, String location, String state) {
        this.id = id;
        this.date = date;
        this.begin = begin;
        this.end = end;
        this.location = location;
        this.state = state;
    }
}
