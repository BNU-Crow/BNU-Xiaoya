package cn.xuhongxu.Assist;

/**
 * Created by xuhongxu on 16/4/5.
 */
public class CancelCourse {
    // 课程名称 kc
    private String name = "";
    // 校区 school_name
    private String schoolName = "";
    // 学分 xf
    private String credit = "";
    // 总学时 zxs
    private String period = "";
    // 类别 lb
    private String classification = "";
    // 任课教师 rkjs
    private String teacher = "";
    private String teacherCode = "";
    // 上课班级代码 skbjdm
    private String classCode = "";
    // 显示的上课班级代码 show_skbjdm
    private String shownClassCode = "";
    // 课程代码 kcdm
    private String code = "";
    // 课程类别1 kclb1
    private String type1 = "";
    // 课程类别2 kclb2
    private String type2 = "";
    // 考核方式 khfs
    private String examType = "";
    // 选课方式 xkfs
    private String selectingMethod = "";
    // 选课状态 xk_status
    private String selectingStatus = "";
    // 上课时间地点 sksjdd
    private String timeLocation = "";

    public String getName() {
        return name;
    }

    public String getSchoolName() {
        return schoolName;
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

    public String getTeacher() {
        return teacher;
    }

    public String getClassCode() {
        return classCode;
    }

    public String getShownClassCode() {
        return shownClassCode;
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

    public String getExamType() {
        return examType;
    }

    public String getSelectingMethod() {
        return selectingMethod;
    }

    public String getSelectingStatus() {
        return selectingStatus;
    }

    public String getTimeLocation() {
        return timeLocation;
    }

    void setName(String name) {
        this.name = name.trim();
    }

    void setSchoolName(String schoolName) {
        this.schoolName = schoolName.trim();
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

    void setTeacher(String teacher) {
        this.teacher = teacher.trim();
    }

    void setClassCode(String classCode) {
        this.classCode = classCode.trim();
    }

    void setShownClassCode(String shownClassCode) {
        this.shownClassCode = shownClassCode.trim();
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

    void setExamType(String examType) {
        this.examType = examType.trim();
    }

    void setSelectingMethod(String selectingMethod) {
        this.selectingMethod = selectingMethod.trim();
    }

    void setSelectingStatus(String selectingStatus) {
        this.selectingStatus = selectingStatus.trim();
    }

    void setTimeLocation(String timeLocation) {
        this.timeLocation = timeLocation.trim();
    }

    public String getTeacherCode() {
        return teacherCode;
    }

    void setTeacherCode(String teacherCode) {
        this.teacherCode = teacherCode;
    }

    @Override
    public String toString() {
        return "CancelCourse{" +
                "name='" + name + '\'' +
                ", schoolName='" + schoolName + '\'' +
                ", credit='" + credit + '\'' +
                ", period='" + period + '\'' +
                ", classification='" + classification + '\'' +
                ", teacher='" + teacher + '\'' +
                ", teacherCode='" + teacherCode + '\'' +
                ", classCode='" + classCode + '\'' +
                ", shownClassCode='" + shownClassCode + '\'' +
                ", code='" + code + '\'' +
                ", type1='" + type1 + '\'' +
                ", type2='" + type2 + '\'' +
                ", examType='" + examType + '\'' +
                ", selectingMethod='" + selectingMethod + '\'' +
                ", selectingStatus='" + selectingStatus + '\'' +
                ", timeLocation='" + timeLocation + '\'' +
                '}';
    }
}
