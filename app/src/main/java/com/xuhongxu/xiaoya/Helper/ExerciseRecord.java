package com.xuhongxu.xiaoya.Helper;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xuhon on 2017/2/27.
 */

public class ExerciseRecord implements Parcelable {
    public String date;
    public String building;
    public String enterTime;
    public String leaveTime;
    public String status;

    public String time;
    public String desp;
    public int type;

    public ExerciseRecord() {}

    protected ExerciseRecord(Parcel in) {
        type = in.readInt();
        date = in.readString();
        building = in.readString();
        enterTime = in.readString();
        leaveTime = in.readString();
        status = in.readString();
        time = in.readString();
        desp = in.readString();
    }

    public static final Creator<ExerciseRecord> CREATOR = new Creator<ExerciseRecord>() {
        @Override
        public ExerciseRecord createFromParcel(Parcel in) {
            return new ExerciseRecord(in);
        }

        @Override
        public ExerciseRecord[] newArray(int size) {
            return new ExerciseRecord[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(type);
        parcel.writeString(date);
        parcel.writeString(building);
        parcel.writeString(enterTime);
        parcel.writeString(leaveTime);
        parcel.writeString(status);
        parcel.writeString(time);
        parcel.writeString(desp);
    }
}
