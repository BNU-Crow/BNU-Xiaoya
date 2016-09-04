package cn.xuhongxu.Assist;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xuhon on 2016/9/4.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class SelectInfo {

    private String isNjactive = "";
    private String isValidTimeRange = "";
    private String isXjls = "";
    private String isYxqxxk = "";
    // jssj
    private String endTime = "";
    private String lx = "";
    // nj
    private String grade = "";
    private String pycc = "";
    private String pyccM = "";
    // qssj
    private String startTime = "";
    private String xh = "";
    private String xkdesc = "";
    private String xktype = "";
    // xn
    private String year = "";
    private String xnxqDesc = "";
    private String xqM = "";
    private String xqName = "";

    SelectInfo (String res) throws JSONException {
        JSONObject jsonObject = new JSONObject(res);
        String result = jsonObject.getString("result");
        JSONObject resObject = new JSONObject(result);
        isNjactive = resObject.getString("isNjactive").trim();
        isValidTimeRange = resObject.getString("isValidTimerange").trim();
        isXjls = resObject.getString("isXjls").trim();
        isYxqxxk = resObject.getString("isYxqxxk").trim();
        endTime = resObject.getString("jssj").trim();
        lx = resObject.getString("lx").trim();
        grade = resObject.getString("nj").trim();
        pycc = resObject.getString("pycc").trim();
        pyccM = resObject.getString("pyccM").trim();
        startTime = resObject.getString("qssj").trim();
        xh = resObject.getString("xh").trim();
        xkdesc = resObject.getString("xkdesc").trim();
        xktype = resObject.getString("xktype").trim();
        year = resObject.getString("xn").trim();
        xnxqDesc = resObject.getString("xnxqDesc").trim();
        xqM = resObject.getString("xqM").trim();
        xqName = resObject.getString("xqName").trim();

    }

    public String getIsNjactive() {
        return isNjactive;
    }

    public String getIsValidTimeRange() {
        return isValidTimeRange;
    }

    public String getIsXjls() {
        return isXjls;
    }

    public String getIsYxqxxk() {
        return isYxqxxk;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getLx() {
        return lx;
    }

    public String getGrade() {
        return grade;
    }

    public String getPycc() {
        return pycc;
    }

    public String getPyccM() {
        return pyccM;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getXh() {
        return xh;
    }

    public String getXkdesc() {
        return xkdesc;
    }

    public String getXktype() {
        return xktype;
    }

    public String getYear() {
        return year;
    }

    public String getXnxqDesc() {
        return xnxqDesc;
    }

    public String getXqM() {
        return xqM;
    }

    public String getXqName() {
        return xqName;
    }
}
