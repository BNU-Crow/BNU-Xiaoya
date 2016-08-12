package cn.xuhongxu.Assist;

/**
 * Created by xuhongxu on 16/4/7.
 *
 * ExamScore
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class ExamScore {
    // 学期
    private String term = "";
    // 课程
    private String courseName = "";
    // 学分
    private String courseCredit = "";
    // 类别
    private String classification = "";
    // 初修
    private boolean firstLearn = false;
    // 平时成绩
    private String usualScore = "";
    // 期末成绩
    private String examScore = "";
    // 综合成绩
    private String score = "";
    // 主修
    private boolean major = false;

    public String getTerm() {
        return term;
    }

    void setTerm(String term) {
        this.term = term.trim();
    }

    public String getCourseName() {
        return courseName;
    }

    void setCourseName(String courseName) {
        this.courseName = courseName.trim();
    }

    public String getCourseCredit() {
        return courseCredit;
    }

    void setCourseCredit(String courseCredit) {
        this.courseCredit = courseCredit.trim();
    }

    public String getClassification() {
        return classification;
    }

    void setClassification(String classification) {
        this.classification = classification.trim();
    }

    public boolean isFirstLearn() {
        return firstLearn;
    }

    void setFirstLearn(boolean firstLearn) {
        this.firstLearn = firstLearn;
    }

    public String getUsualScore() {
        return usualScore;
    }

    void setUsualScore(String usualScore) {
        this.usualScore = usualScore.trim();
    }

    public String getExamScore() {
        return examScore;
    }

    void setExamScore(String examScore) {
        this.examScore = examScore.trim();
    }

    public String getScore() {
        return score;
    }

    void setScore(String score) {
        this.score = score.trim();
    }

    public boolean isMajor() {
        return major;
    }

    void setMajor(boolean major) {
        this.major = major;
    }

    @Override
    public String toString() {
        return "ExamScore{" +
                "term='" + term + '\'' +
                ", courseName='" + courseName + '\'' +
                ", courseCredit='" + courseCredit + '\'' +
                ", classification='" + classification + '\'' +
                ", firstLearn=" + firstLearn +
                ", usualScore='" + usualScore + '\'' +
                ", examScore='" + examScore + '\'' +
                ", score='" + score + '\'' +
                ", major=" + major +
                '}';
    }

    public boolean isProfessional() {
        return classification.contains("专业") || classification.contains("院系");
    }
}
