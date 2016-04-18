package cn.xuhongxu.Assist;

/**
 * Created by xuhongxu on 16/4/6.
 *
 * SelectionResult
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class SelectionResult {
    private String number = "";
    private String name = "";
    private String credit = "";
    private String classification = "";
    private String teacher = "";
    private String classCode = "";
    private String className = "";
    private String selectingMethod = "";
    private String selectionCount = "";
    private String maxSelection = "";
    private String remainingSelection = "";
    private String timeLocation = "";
    private String code = "";
    private String teacherCode = "";

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getCredit() {
        return credit;
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

    public String getClassName() {
        return className;
    }

    public String getSelectingMethod() {
        return selectingMethod;
    }

    public String getSelectionCount() {
        return selectionCount;
    }

    public String getMaxSelection() {
        return maxSelection;
    }

    public String getRemainingSelection() {
        return remainingSelection;
    }

    public String getTimeLocation() {
        return timeLocation;
    }

    public String getCode() {
        return code;
    }

    void setNumber(String number) {
        this.number = number;
    }

    void setName(String name) {
        this.name = name;
    }

    void setCredit(String credit) {
        this.credit = credit;
    }

    void setClassification(String classification) {
        this.classification = classification;
    }

    void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    void setClassName(String className) {
        this.className = className;
    }

    void setSelectingMethod(String selectingMethod) {
        this.selectingMethod = selectingMethod;
    }

    void setSelectionCount(String selectionCount) {
        this.selectionCount = selectionCount;
    }

    void setMaxSelection(String maxSelection) {
        this.maxSelection = maxSelection;
    }

    void setRemainingSelection(String remainingSelection) {
        this.remainingSelection = remainingSelection;
    }

    void setTimeLocation(String timeLocation) {
        this.timeLocation = timeLocation;
    }

    void setCode(String code) {
        this.code = code;
    }

    public String getTeacherCode() {
        return teacherCode;
    }

    void setTeacherCode(String teacherCode) {
        this.teacherCode = teacherCode;
    }

    @Override
    public String toString() {
        return "SelectionResult{" +
                "number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", credit='" + credit + '\'' +
                ", classification='" + classification + '\'' +
                ", teacher='" + teacher + '\'' +
                ", classCode='" + classCode + '\'' +
                ", className='" + className + '\'' +
                ", selectingMethod='" + selectingMethod + '\'' +
                ", selectionCount='" + selectionCount + '\'' +
                ", maxSelection='" + maxSelection + '\'' +
                ", remainingSelection='" + remainingSelection + '\'' +
                ", timeLocation='" + timeLocation + '\'' +
                ", code='" + code + '\'' +
                ", teacherCode='" + teacherCode + '\'' +
                '}';
    }
}
