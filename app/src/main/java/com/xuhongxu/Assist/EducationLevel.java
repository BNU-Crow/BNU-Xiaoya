package com.xuhongxu.Assist;

/**
 * Created by xuhon on 2017/3/11.
 */

public class EducationLevel {
    private String code = "";
    private String name = "";

    @Override
    public String toString() {
        return "EduLevel{" +
                "code='" + getCode() + '\'' +
                ", name='" + getName() + '\'' +
                '}';
    }

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
        this.name = name;
    }
}
