package cn.xuhongxu.xiaoya.Helper;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by xuhon on 2017/2/20.
 */

public class Room {
    public String name;
    public boolean[] noCourse;

    public Room() {

    }

    public Room(String name, boolean[] noCourse) {
        this.name = name;
        this.noCourse = noCourse;
    }

}

