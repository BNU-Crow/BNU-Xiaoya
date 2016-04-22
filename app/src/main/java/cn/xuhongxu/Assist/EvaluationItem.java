package cn.xuhongxu.Assist;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by xuhongxu on 16/4/7.
 *
 * EvaluationItem
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class EvaluationItem implements Parcelable {
    private String name = "";
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

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

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
        this.termName = termName.substring(termName.indexOf("季学期") - 1);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.phase);
        dest.writeInt(this.year);
        dest.writeInt(this.term);
        dest.writeString(this.termName);
        dest.writeLong(startDate != null ? startDate.getTime() : -1);
        dest.writeLong(endDate != null ? endDate.getTime() : -1);
        dest.writeString(this.code);
        dest.writeString(this.sfwjpj);
        dest.writeString(this.sfkpsj);
        dest.writeString(this.sfzbpj);
        dest.writeString(this.pjfsbz);
    }

    public EvaluationItem() {
    }

    protected EvaluationItem(Parcel in) {
        this.name = in.readString();
        this.phase = in.readString();
        this.year = in.readInt();
        this.term = in.readInt();
        this.termName = in.readString();
        long tmpStartDate = in.readLong();
        this.startDate = tmpStartDate == -1 ? null : new Date(tmpStartDate);
        long tmpEndDate = in.readLong();
        this.endDate = tmpEndDate == -1 ? null : new Date(tmpEndDate);
        this.code = in.readString();
        this.sfwjpj = in.readString();
        this.sfkpsj = in.readString();
        this.sfzbpj = in.readString();
        this.pjfsbz = in.readString();
    }

    public static final Parcelable.Creator<EvaluationItem> CREATOR = new Parcelable.Creator<EvaluationItem>() {
        @Override
        public EvaluationItem createFromParcel(Parcel source) {
            return new EvaluationItem(source);
        }

        @Override
        public EvaluationItem[] newArray(int size) {
            return new EvaluationItem[size];
        }
    };
}
