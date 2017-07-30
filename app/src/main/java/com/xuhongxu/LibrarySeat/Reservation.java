package com.xuhongxu.LibrarySeat;

/**
 * Created by xuhon on 5/17/2017.
 */

public class Reservation {
    public int id;
    public String receipt;
    public String onDate;
    public int seatId;
    public String status;
    public String location;
    public String begin;
    public String end;
    public boolean userEnded;
    public String message;
    public String checkedIn;

    public Reservation(int id, String receipt, String onDate, int seatId, String status, String location, String begin, String end, boolean userEnded, String message, String checkedIn) {
        this.id = id;
        this.receipt = receipt;
        this.onDate = onDate;
        this.seatId = seatId;
        this.status = status;
        this.location = location;
        this.begin = begin;
        this.end = end;
        this.userEnded = userEnded;
        this.message = message;
        this.checkedIn = checkedIn;
    }
}
