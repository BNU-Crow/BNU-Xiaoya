package cn.xuhongxu.xiaoya.Helper;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by xuhon on 2017/2/20.
 */

public class BorrowBook implements Parcelable {
    public String title, author;
    public String position;
    public String building;
    public String returnDate;

    public BorrowBook() {

    }

    protected BorrowBook(Parcel in) {
        title = in.readString();
        author = in.readString();
        position = in.readString();
        building = in.readString();
        returnDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(position);
        dest.writeString(building);
        dest.writeString(returnDate);
    }

    public static final Parcelable.Creator<BorrowBook> CREATOR = new Parcelable.Creator<BorrowBook>() {
        @Override
        public BorrowBook createFromParcel(Parcel in) {
            return new BorrowBook(in);
        }

        @Override
        public BorrowBook[] newArray(int size) {
            return new BorrowBook[size];
        }
    };
}


