package cn.xuhongxu.Assist;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by xuhon on 2016/8/11.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class TableCourse implements Serializable {
    private String code = "";
    private String name = "";
    private String credit = "";
    private String teacher = "";
    private String locationTime = "";

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name = name.substring(name.indexOf("]") + 1);
        this.name = name;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        teacher = teacher.substring(teacher.indexOf("]") + 1);
        this.teacher = teacher;
    }

    public String getLocationTime() {
        return locationTime;
    }

    public void setLocationTime(String locationTime) {
        this.locationTime = locationTime;
    }

    @Override
    public String toString() {
        return "TableCourse{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", credit='" + credit + '\'' +
                ", teacher='" + teacher + '\'' +
                ", locationTime='" + locationTime + '\'' +
                '}';
    }

}
