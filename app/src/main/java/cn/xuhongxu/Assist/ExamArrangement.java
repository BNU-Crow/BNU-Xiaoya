package cn.xuhongxu.Assist;

/**
 * Created by xuhongxu on 16/4/7.
 *
 * ExamArrangement
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class ExamArrangement {
    // 课程名称 kc
    private String course;
    // 学分 xf
    private String credit;
    // 类别 lb
    private String classification;
    // 考核方式 khfs
    private String examType;
    // 考试时间 kssj
    private String time;
    // 考试地点 ksdd
    private String location;
    // 座位号 zwh
    private String seat;
    private String courseName;

    public String getCourse() {
        return course;
    }

    void setCourse(String course) {
        this.course = course.trim();
        courseName = course.substring(course.indexOf("]") + 1);
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCredit() {
        return credit;
    }

    void setCredit(String credit) {
        this.credit = credit.trim();
    }

    public String getClassification() {
        return classification;
    }

    void setClassification(String classification) {
        this.classification = classification.trim();
    }

    public String getExamType() {
        return examType;
    }

    void setExamType(String examType) {
        this.examType = examType.trim();
    }

    public String getTime() {
        return time;
    }

    void setTime(String time) {
        this.time = time.trim();
    }

    public String getLocation() {
        return location;
    }

    void setLocation(String location) {
        this.location = location.trim();
    }

    public String getSeat() {
        return seat;
    }

    void setSeat(String seat) {
        this.seat = seat.trim();
    }

    @Override
    public String toString() {
        return "ExamArrangement{" +
                "course='" + course + '\'' +
                ", credit='" + credit + '\'' +
                ", classification='" + classification + '\'' +
                ", examType='" + examType + '\'' +
                ", time='" + time + '\'' +
                ", location='" + location + '\'' +
                ", seat='" + seat + '\'' +
                '}';
    }
}
