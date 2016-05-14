package cn.xuhongxu.Assist;

/**
 * Created by xuhongxu on 16/4/8.
 *
 * EvaluationCourse
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class EvaluationCourse {
    // 评教状态代码 pjzt_m
    private String statusCode = "";
    // 评教类别代码 pjlb_m
    private String classificationCode = "";
    // 评教类别名称 pjlbmc
    private String classification = "";
    // 教师代码 jsid
    private String teacherID = "";
    // 教师工号 gh
    private String teacherNumber = "";
    // 教师姓名 xm
    private String teacherName = "";
    // sfzjjs
    private String sfzjjs = "";
    // 课程代码 kcdm
    private String code = "";
    // yhdm
    private String yhdm = "";
    // 课程名称 kcmc
    private String name = "";
    // 学分 xf
    private String credit = "";
    // 是否已评价 ypflag
    private boolean evaluated = false;
    // 上课班级代码 skbjdm
    private String classCode = "";

    public String getStatusCode() {
        return statusCode;
    }

    void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getClassificationCode() {
        return classificationCode;
    }

    void setClassificationCode(String classificationCode) {
        this.classificationCode = classificationCode;
    }

    public String getClassification() {
        return classification;
    }

    void setClassification(String classification) {
        this.classification = classification;
    }

    public String getTeacherID() {
        return teacherID;
    }

    void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getTeacherNumber() {
        return teacherNumber;
    }

    void setTeacherNumber(String teacherNumber) {
        this.teacherNumber = teacherNumber;
    }

    public String getTeacherName() {
        return teacherName;
    }

    void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getSfzjjs() {
        return sfzjjs;
    }

    void setSfzjjs(String sfzjjs) {
        this.sfzjjs = sfzjjs;
    }

    public String getCode() {
        return code;
    }

    void setCode(String code) {
        this.code = code;
    }

    public String getYhdm() {
        return yhdm;
    }

    void setYhdm(String yhdm) {
        this.yhdm = yhdm;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getCredit() {
        return credit;
    }

    void setCredit(String credit) {
        this.credit = credit;
    }

    public boolean isEvaluated() {
        return evaluated;
    }

    void setEvaluated(boolean evaluated) {
        this.evaluated = evaluated;
    }

    public String getClassCode() {
        return classCode;
    }

    void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    @Override
    public String toString() {
        return "EvaluationCourse{" +
                "statusCode='" + statusCode + '\'' +
                ", classificationCode='" + classificationCode + '\'' +
                ", classification='" + classification + '\'' +
                ", teacherID='" + teacherID + '\'' +
                ", teacherNumber='" + teacherNumber + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", sfzjjs='" + sfzjjs + '\'' +
                ", code='" + code + '\'' +
                ", yhdm='" + yhdm + '\'' +
                ", name='" + name + '\'' +
                ", credit='" + credit + '\'' +
                ", evaluated=" + evaluated +
                ", classCode='" + classCode + '\'' +
                '}';
    }
}
