package cn.xuhongxu.LibrarySeat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuhon on 5/17/2017.
 */

public class SeatLayout {
    public int id;
    public String name;
    public int cols;
    public int rows;
    public List<SeatLayoutItem> layout;

    public SeatLayout(int id, String name, int cols, int rows) {
        this.id = id;
        this.name = name;
        this.cols = cols;
        this.rows = rows;
        layout = new ArrayList<>();
    }
}
