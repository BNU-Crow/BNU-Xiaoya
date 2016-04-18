package cn.xuhongxu.Assist;

/**
 * Created by xuhongxu on 16/4/5.
 *
 * StudentInfo
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class StudentInfo {
    // 教务学号 xh
    private String id;
    // 年级 nj
    private String grade;
    // 专业名称 zymc
    private String speciality;
    // 专业代码 zydm
    private String specialityCode;
    // 学年 xn
    private String academicYear;
    // 学期 xq_m
    private String schoolTerm;

    public StudentInfo(String id, String grade, String speciality, String specialityCode,
                       String academicYear, String schoolTerm) {
        this.id = id.trim();
        this.grade = grade.trim();
        this.speciality = speciality.trim();
        this.specialityCode = specialityCode.trim();
        this.academicYear = academicYear.trim();
        this.schoolTerm = schoolTerm.trim();
    }

    public String getId() {
        return id;
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

    public String getAcademicYear() {
        return academicYear;
    }

    public String getSchoolTerm() {
        return schoolTerm;
    }

    @Override
    public String toString() {
        return "StudentInfo{" +
                "id='" + id + '\'' +
                ", grade='" + grade + '\'' +
                ", speciality='" + speciality + '\'' +
                ", specialityCode='" + specialityCode + '\'' +
                ", academicYear='" + academicYear + '\'' +
                ", schoolTerm='" + schoolTerm + '\'' +
                '}';
    }
}
