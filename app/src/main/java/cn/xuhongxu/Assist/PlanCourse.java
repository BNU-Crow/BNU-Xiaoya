package cn.xuhongxu.Assist;

/**
 * Created by xuhongxu on 16/4/5.
 *
 * PlanCourse
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class PlanCourse {
    // 名称 kc
    private String name = "";
    // 学分 xf
    private String credit = "";
    // 总学时 zxs
    private String period = "";
    // 类别 lb
    private String classification = "";
    // 选课状态 xk_status
    private String selectingStatus = "";
    // 上课班级代码 skbjdm
    private String classCode = "";
    // 任课教师 rkjs
    private String teacher = "";
    private String teacherCode = "";
    // 课程代码 kcdm
    private String code = "";
    // 课程类别1 kclb1
    private String type1 = "";
    // 课程类别2 kclb2
    private String type2 = "";
    // 课程类别3 kclb3
    private String type3 = "";
    // Unknown kcxz
    private String kcxz = "";
    // 考核方式 examType
    private String examType = "";

    public String getName() {
        return name;
    }

    public String getCredit() {
        return credit;
    }

    public String getPeriod() {
        return period;
    }

    public String getClassification() {
        return classification;
    }

    public String getSelectingStatus() {
        return selectingStatus;
    }

    public String getClassCode() {
        return classCode;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getCode() {
        return code;
    }

    public String getType1() {
        return type1;
    }

    public String getType2() {
        return type2;
    }

    public String getType3() {
        return type3;
    }

    public String getKcxz() {
        return kcxz;
    }

    public String getExamType() {
        return examType;
    }

    void setName(String name) {
        this.name = name.trim();
    }

    void setCredit(String credit) {
        this.credit = credit.trim();
    }

    void setPeriod(String period) {
        this.period = period.trim();
    }

    void setClassification(String classification) {
        this.classification = classification.trim();
    }

    void setSelectingStatus(String selectingStatus) {
        this.selectingStatus = selectingStatus.trim();
    }

    void setClassCode(String classCode) {
        this.classCode = classCode.trim();
    }

    void setTeacher(String teacher) {
        this.teacher = teacher.trim();
    }

    void setCode(String code) {
        this.code = code.trim();
    }

    void setType1(String type1) {
        this.type1 = type1.trim();
    }

    void setType2(String type2) {
        this.type2 = type2.trim();
    }

    void setType3(String type3) {
        this.type3 = type3.trim();
    }

    void setKcxz(String kcxz) {
        this.kcxz = kcxz.trim();
    }

    void setExamType(String examType) {
        this.examType = examType.trim();
    }

    public String getTeacherCode() {
        return teacherCode;
    }

    void setTeacherCode(String teacherCode) {
        this.teacherCode = teacherCode;
    }

    @Override
    public String toString() {
        return "PlanCourse{" +
                "name='" + name + '\'' +
                ", credit='" + credit + '\'' +
                ", period='" + period + '\'' +
                ", classification='" + classification + '\'' +
                ", selectingStatus='" + selectingStatus + '\'' +
                ", classCode='" + classCode + '\'' +
                ", teacher='" + teacher + '\'' +
                ", teacherCode='" + teacherCode + '\'' +
                ", code='" + code + '\'' +
                ", type1='" + type1 + '\'' +
                ", type2='" + type2 + '\'' +
                ", type3='" + type3 + '\'' +
                ", kcxz='" + kcxz + '\'' +
                ", examType='" + examType + '\'' +
                '}';
    }
}
