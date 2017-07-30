package com.xuhongxu.Assist;

/**
 * Created by xuhongxu on 16/4/5.
 *
 * GradeInfo
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class GradeInfo {
    // 年级 nj
    private String grade = "";
    // 专业名称 zymc
    private String speciality = "";
    // 专业代码 zydm
    private String specialityCode = "";

    public GradeInfo() {

    }

    public GradeInfo(String grade, String speciality, String specialityCode) {
        this.grade = grade.trim();
        this.speciality = speciality.trim();
        this.specialityCode = specialityCode.trim();
    }

    public String getGrade() {
        return grade;
    }

    public String getSpeciality() {
        return speciality;
    }

    public String getSpecialityCode() {
        return specialityCode;
    }

    @Override
    public String toString() {
        return "StudentInfo{" +
                ", grade='" + grade + '\'' +
                ", speciality='" + speciality + '\'' +
                ", specialityCode='" + specialityCode + '\'' +
                '}';
    }
}
