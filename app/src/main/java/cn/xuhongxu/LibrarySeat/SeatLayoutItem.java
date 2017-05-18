package cn.xuhongxu.LibrarySeat;

/**
 * Created by xuhon on 5/17/2017.
 */

public class SeatLayoutItem {
    public int id;
    public String name;
    public String type;
    public String status;
    public boolean power;

    public SeatLayoutItem(int id, String name, String type, String status, boolean power) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
        this.power = power;
    }
}
