package cn.xuhongxu.Assist;

/**
 * Created by xuhongxu on 16/4/5.
 *
 * ElectiveCourse
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class ElectiveCourse {
    // 课程 kc
    private String name = "";
    // 上课班级 skbj
    private String classNumber = "";
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
    // 课程代码 kcdm
    private String code = "";
    // 课程类别1 kclb1
    private String type1 = "";
    // 课程类别2 kclb2
    private String type2 = "";
    // 课程类别3 kclb3
    private String type3 = "";
    // 考核方式 examType
    private String examType = "";
    // 限选人数 xxrs
    private String maxSelection = "";
    // 已选人数 yxrs
    private String selectionCount = "";
    // 可选人数 kxrs
    private String remainingSelection = "";
    // 上课方式 skfs
    private String method = "";
    // 上课时间 sksj
    private String time = "";
    // 上课地点 skdd
    private String location = "";
    // 选中
    private String chosen = "";

    public String getName() {
        return name;
    }

    public String getClassNumber() {
        return classNumber;
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

    public String getExamType() {
        return examType;
    }

    public String getMaxSelection() {
        return maxSelection;
    }

    public String getSelectionCount() {
        return selectionCount;
    }

    public String getRemainingSelection() {
        return remainingSelection;
    }

    public String getMethod() {
        return method;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    void setName(String name) {
        this.name = name.trim();
    }

    void setClassNumber(String classNumber) {
        this.classNumber = classNumber.trim();
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

    void setExamType(String examType) {
        this.examType = examType.trim();
    }

    void setMaxSelection(String maxSelection) {
        this.maxSelection = maxSelection.trim();
    }

    void setSelectionCount(String selectionCount) {
        this.selectionCount = selectionCount.trim();
    }

    void setRemainingSelection(String remainingSelection) {
        this.remainingSelection = remainingSelection.trim();
    }

    void setMethod(String method) {
        this.method = method.trim();
    }

    void setTime(String time) {
        this.time = time.trim();
    }

    void setLocaiton(String locaiton) {
        this.location = locaiton.trim();
    }

    public String getTeacherCode() {
        return teacherCode;
    }

    void setTeacherCode(String teacherCode) {
        this.teacherCode = teacherCode;
    }

    @Override
    public String toString() {
        return "ElectiveCourse{" +
                "name='" + name + '\'' +
                ", classNumber='" + classNumber + '\'' +
                ", credit='" + credit + '\'' +
                ", period='" + period + '\'' +
                ", classification='" + classification + '\'' +
                ", teacher='" + teacher + '\'' +
                ", teacherCode='" + teacherCode + '\'' +
                ", classCode='" + classCode + '\'' +
                ", code='" + code + '\'' +
                ", type1='" + type1 + '\'' +
                ", type2='" + type2 + '\'' +
                ", type3='" + type3 + '\'' +
                ", examType='" + examType + '\'' +
                ", maxSelection='" + maxSelection + '\'' +
                ", selectionCount='" + selectionCount + '\'' +
                ", remainingSelection='" + remainingSelection + '\'' +
                ", method='" + method + '\'' +
                ", time='" + time + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    public String getChosen() {
        return chosen;
    }

    public void setChosen(String chosen) {
        this.chosen = chosen;
    }
}
