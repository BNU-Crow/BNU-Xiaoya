package cn.xuhongxu.Assist;


import java.util.Date;

/**
 * Created by xuhongxu on 16/4/7.
 *
 * EvaluationItem
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class EvaluationItem {
    // 评教轮次阶段 lcjc
    private String phase = "";
    // 学年 xn
    private int year = 0;
    // 学期 xq_m
    private int term = 0;
    // 评教轮次学期 lcqc
    private String termName = "";
    // 起始日期 qsrq
    private Date startDate;
    // 结束日期 jsrq
    private Date endDate;
    // 评教轮次代码 lcdm
    private String code = "";
    // sfwjpj
    private String sfwjpj = "";
    // sfkpsj
    private String sfkpsj = "";
    // sfzbpj
    private String sfzbpj = "";
    // pjfsbz
    private String pjfsbz = "";

    public String getPhase() {
        return phase;
    }

    void setPhase(String phase) {
        this.phase = phase;
    }

    public int getYear() {
        return year;
    }

    void setYear(int year) {
        this.year = year;
    }

    public int getTerm() {
        return term;
    }

    void setTerm(int term) {
        this.term = term;
    }

    public String getTermName() {
        return termName;
    }

    void setTermName(String termName) {
        this.termName = termName;
    }

    public Date getStartDate() {
        return startDate;
    }

    void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getCode() {
        return code;
    }

    void setCode(String code) {
        this.code = code;
    }

    public String getSfwjpj() {
        return sfwjpj;
    }

    void setSfwjpj(String sfwjpj) {
        this.sfwjpj = sfwjpj;
    }

    public String getSfkpsj() {
        return sfkpsj;
    }

    void setSfkpsj(String sfkpsj) {
        this.sfkpsj = sfkpsj;
    }

    public String getSfzbpj() {
        return sfzbpj;
    }

    void setSfzbpj(String sfzbpj) {
        this.sfzbpj = sfzbpj;
    }

    public String getPjfsbz() {
        return pjfsbz;
    }

    void setPjfsbz(String pjfsbz) {
        this.pjfsbz = pjfsbz;
    }

    @Override
    public String toString() {
        return "EvaluationItem{" +
                "phase='" + phase + '\'' +
                ", year=" + year +
                ", term=" + term +
                ", termName='" + termName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", code='" + code + '\'' +
                ", sfwjpj='" + sfwjpj + '\'' +
                ", sfkpsj='" + sfkpsj + '\'' +
                ", sfzbpj='" + sfzbpj + '\'' +
                ", pjfsbz='" + pjfsbz + '\'' +
                '}';
    }
}
