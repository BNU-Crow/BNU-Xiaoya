package cn.xuhongxu.Assist;

/**
 * Created by xuhon on 2016/8/11.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class Semester {
    private String code = "";
    private String name = "";
    private String year = "";
    private String term = "";

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
        year = code.substring(0, code.indexOf(",")).trim();
        term = code.substring(code.indexOf(",") + 1).trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public String getTerm() {
        return term;
    }
}
