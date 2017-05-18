package cn.xuhongxu.LibrarySeat;

/**
 * Created by xuhon on 5/17/2017.
 */

public class Room {
    public int roomId;
    public String name;
    public String floor;
    public String reserved;
    public String inUse;
    public String away;
    public String totalSeats;
    public String free;

    public Room(int roomId, String name, String floor, String reserved, String inUse, String away, String totalSeats, String free) {
        this.roomId = roomId;
        this.name = name;
        this.floor = floor;
        this.reserved = reserved;
        this.inUse = inUse;
        this.away = away;
        this.totalSeats = totalSeats;
        this.free = free;
    }


    @Override
    public String toString() {
        return name;
    }
}
