package com.xuhongxu.xiaoya.Helper;

/**
 * Created by xuhon on 5/17/2017.
 */

public class Building {
    public String name;
    public String id;

    public Building(String name, String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }
}
