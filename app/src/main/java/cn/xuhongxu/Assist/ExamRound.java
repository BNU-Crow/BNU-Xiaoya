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

    public String getCode() {
        return code;
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
