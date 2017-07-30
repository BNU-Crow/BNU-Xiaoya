package com.xuhongxu.Assist;

/**
 * Created by xuhongxu on 16/4/5.
 *
 * PlanChildCourse
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class PlanChildCourse {
    // 当前班级子代码 curent_skbjdm
    private String subClassCode = "";
    // 上课班级名称 skbjmc
    private String className = "";
    // 校区名称 xqmc
    private String schoolName = "";
    // 上课班组代码 skbzmc
    private String groupName = "";
    // 选课人数上限 xkrssx
    private String maxSelection = "";
    // 选课人数/免听 xkrs
    private String selectionCount = "";
    // 可选人数 kxrs
    private String remainingSelection = "";
    // 任课教师 rkjs
    private String teacher = "";
    private String teacherCode = "";
    // 上课方式名称 skfs_mc
    private String methodName = "";
    // 上课时间 sksj
    private String time = "";
    // 上课地点 skdd
    private String location = "";
    // 是否选择 ischk
    private boolean isSelected = false;
    // 上课班级代码 skbjdm
    private String classCode = "";
    // 上课方式代码 skfs_m
    private String methodCode = "";
    // 校区代码 xqdm
    private String schoolCode = "";
    // 上课班组代码 skbzdm
    private String groupCode = "";

    public String getSubClassCode() {
        return subClassCode;
    }

    public String getClassName() {
        return className;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getGroupName() {
        return groupName;
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

    public String getTeacher() {
        return teacher;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getClassCode() {
        return classCode;
    }

    public String getMethodCode() {
        return methodCode;
    }

    public String getSchoolCode() {
        return schoolCode;
    }

    public String getGroupCode() {
        return groupCode;
    }

    void setSubClassCode(String subClassCode) {
        this.subClassCode = subClassCode.trim();
    }

    void setClassName(String className) {
        this.className = className.trim();
    }

    void setSchoolName(String schoolName) {
        this.schoolName = schoolName.trim();
    }

    void setGroupName(String groupName) {
        this.groupName = groupName.trim();
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

    void setTeacher(String teacher) {
        this.teacher = teacher.trim();
    }

    void setMethodName(String methodName) {
        this.methodName = methodName.trim();
    }

    void setTime(String time) {
        this.time = time.trim();
    }

    void setLocation(String location) {
        this.location = location.trim();
    }

    void setSelected(boolean selected) {
        isSelected = selected;
    }

    void setClassCode(String classCode) {
        this.classCode = classCode.trim();
    }

    void setMethodCode(String methodCode) {
        this.methodCode = methodCode.trim();
    }

    void setSchoolCode(String schoolCode) {
        this.schoolCode = schoolCode.trim();
    }

    void setGroupCode(String groupCode) {
        this.groupCode = groupCode.trim();
    }

    public String getTeacherCode() {
        return teacherCode;
    }

    void setTeacherCode(String teacherCode) {
        this.teacherCode = teacherCode;
    }

    @Override
    public String toString() {
        return "PlanChildCourse{" +
                "subClassCode='" + subClassCode + '\'' +
                ", className='" + className + '\'' +
                ", schoolName='" + schoolName + '\'' +
                ", groupName='" + groupName + '\'' +
                ", maxSelection='" + maxSelection + '\'' +
                ", selectionCount='" + selectionCount + '\'' +
                ", remainingSelection='" + remainingSelection + '\'' +
                ", teacher='" + teacher + '\'' +
                ", teacherCode='" + teacherCode + '\'' +
                ", methodName='" + methodName + '\'' +
                ", time='" + time + '\'' +
                ", location='" + location + '\'' +
                ", isSelected=" + isSelected +
                ", classCode='" + classCode + '\'' +
                ", methodCode='" + methodCode + '\'' +
                ", schoolCode='" + schoolCode + '\'' +
                ", groupCode='" + groupCode + '\'' +
                '}';
    }
}
