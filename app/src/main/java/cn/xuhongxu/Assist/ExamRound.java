package cn.xuhongxu.Assist;

/**
 * Created by xuhongxu on 16/4/6.
 *
 * Exam Round
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class ExamRound {
    private String code = "";
    private String name = "";
    private int year = 0;
    private int term = 0;
    private int round = 0;
    private String yearName = "";
    private String termName = "";
    private String roundName = "";

    public String getCode() {
        return code;
    }

    private void getItemName() {
        int p1 = name.indexOf("学年");
        int p2 = name.indexOf("学期");
        yearName = name.substring(0, p1);
        termName = name.substring(p1 + 2, p2);
        roundName = name.substring(p2 + 2);
    }

    void setCode(String code) {
        this.code = code.trim();
        String []res = code.split(",");
        year = Integer.valueOf(res[0]);
        term = Integer.valueOf(res[1]);
        round = Integer.valueOf(res[2]);
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name.trim();
        getItemName();
    }

    public int getYear() {
        return year;
    }

    public int getTerm() {
        return term;
    }

    public int getRound() {
        return round;
    }

    public String getYearName() {
        return yearName;
    }

    public String getRoundName() {
        return roundName;
    }

    public String getTermName() {
        return termName;
    }

    @Override
    public String toString() {
        return "ExamRound{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", year='" + year + '\'' +
                ", term='" + term + '\'' +
                ", round='" + round + '\'' +
                '}';
    }
}
