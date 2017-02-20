package cn.xuhongxu.xiaoya.Helper;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by xuhon on 2017/2/20.
 */

public class Room implements Parcelable {
    public String building;
    public String rooms;

    public Room() {

    }

    public String[] getRoomList() {
        return rooms.split("; ");
    }

    protected Room(Parcel in) {
        building = in.readString();
        rooms = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(building);
        dest.writeString(rooms);
    }

    public static final Parcelable.Creator<Room> CREATOR = new Parcelable.Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };
}

