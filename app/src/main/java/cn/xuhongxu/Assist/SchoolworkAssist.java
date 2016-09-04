package cn.xuhongxu.Assist;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xuhongxu on 16/4/2.
 *
 * SchoolworkAssist
 *
 * @author Hongxu Xu
 * @version 0.1
 */

public class SchoolworkAssist implements Parcelable {

    public static final String MESSAGE_ASSIST = "cn.xuhongxu.Assist";

    // HEADER: Content-Type
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    // HEADER: REFERER
    public static final String HEADER_REFERER = "Referer";
    public static final String REFERER = "http://zyfw.bnu.edu.cn";

    // URL: Login
    private static final String LOGIN_URL  = "http://cas.bnu.edu.cn/cas/login?service=http%3A%2F%2Fzyfw.bnu.edu.cn%2F" +
            "MainFrm.text";
    // URL: Get Student Info
    private static final String STUDENT_INFO_URL = "http://zyfw.bnu.edu.cn/STU_DynamicInitDataAction.do?classPath=" +
            "com.kingosoft.service.jw.student.pyfa.CourseInfoService&xn=2015&xq_m=1";
    // URL: Get Des Key
    private static final String DESKEY_URL = "http://zyfw.bnu.edu.cn/custom/js/SetKingoEncypt.jsp?random=";
    // URL: Get Data Table
    private static final String TABLE_URL = "http://zyfw.bnu.edu.cn/taglib/DataTable.jsp?tableId=";
    // URL: Cancel Course
    private static final String CANCEL_COURSE_URL = "http://zyfw.bnu.edu.cn/jw/common/cancelElectiveCourse.action";
    // URL: Select Elective Course
    private static final String SELECT_ELECTIVE_COURSE_URL = "http://zyfw.bnu.edu.cn/jw/common/" +
            "saveElectiveCourse.action";
    // URL: Query Selection Result
    private static final String SELECTION_RESULT_URL = "http://zyfw.bnu.edu.cn/student/wsxk.zxjg10139.jsp?" +
            "menucode=JW130404&random=";
    // URL: Get Drop ArrayList Data
    private static final String DROPLIST_URL = "http://zyfw.bnu.edu.cn/frame/droplist/getDropLists.action";
    // URL: Get Exam Score
    private static final String EXAM_SCORE_URL = "http://zyfw.bnu.edu.cn/student/xscj.stuckcj_data.jsp";
    // URL: Get Evaluate ArrayList
    private static final String EVALUATE_LIST_URL = "http://zyfw.bnu.edu.cn/jw/wspjZbpjWjdc/getPjlcInfo.action";
    // URL: Get Evaluate Form
    private static final String EVALUATE_FORM_URL = "http://zyfw.bnu.edu.cn/student/wspj_tjzbpj_wjdcb_pj.jsp?";
    // URL: Save Evaluate Form
    private static final String EVALUATE_SAVE_URL = "http://zyfw.bnu.edu.cn/jw/wspjZbpjWjdc/save.action";
    // URL: Get Teacher Info
    private static final String TEACHER_INFO_URL = "http://zyfw.bnu.edu.cn/TeacherBaseinfoActoin.do" +
            "?hidOption=ViewData&gh=";
    // URL: Get Student Details
    private static final String STUDENT_DETAILS_URL = "http://zyfw.bnu.edu.cn/STU_BaseInfoAction.do?" +
            "hidOption=InitData&menucode_current=JW13020101";
    // URL: Get Timetable
    private static final String TIMETABLE_URL
            = "http://zyfw.bnu.edu.cn/wsxk/xkjg.ckdgxsxdkchj_data10319.jsp?params=";
    // URL: Get Select Info
    private static final String SELECT_INFO_URL
            = "http://zyfw.bnu.edu.cn/jw/common/getWsxkTimeRange.action?xktype=2";

    // TABLE_ID: Course ArrayList
    private static final String COURSE_LIST_TABLE_ID = "5327018";
    // TABLE_ID: Cancel Course ArrayList
    private static final String CANCEL_LIST_TABLE_ID = "6093";
    // TABLE_ID: Elective Course ArrayList
    private static final String ELECTIVE_COURSE_LIST_TABLE_ID = "5327095";
    // TABLE_ID: Planed Course Details ArrayList
    private static final String PLAN_COURSE_CLASSES_TABLE_ID = "6142";
    // TABLE_ID: Exam Arrangement ArrayList
    private static final String EXAM_ARRAGEMENT_TABLE_ID = "2538";
    // TABLE_ID: Evaluate Course ArrayList
    private static final String EVALUATE_COURSE_LIST_TABLE_ID = "50058";

    // DROP_LIST: Exam Turn
    private static final String EXAM_DROP_NAME = "Ms_KSSW_FBXNXQKSLC";

    private String username;
    private String password;

    private int timeout = 30000;
    private boolean everSucceed = false;

    private Map<String, String> cookies;
    private String lt, excution;
    private StudentInfo studentInfo;
    private SelectInfo selectInfo;

    protected SchoolworkAssist(Parcel in) {
        username = in.readString();
        password = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(password);
    }

    public static final Parcelable.Creator<SchoolworkAssist> CREATOR = new Parcelable.Creator<SchoolworkAssist>() {
        @Override
        public SchoolworkAssist createFromParcel(Parcel in) {
            return new SchoolworkAssist(in);
        }

        @Override
        public SchoolworkAssist[] newArray(int size) {
            return new SchoolworkAssist[size];
        }
    };
    public SchoolworkAssist(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

    private void fetchLoginParams() throws IOException {
        lt = "LT-NeusoftAlwaysValidTicket";
        excution = "e1s1";

        Connection.Response res = Jsoup.connect(LOGIN_URL).timeout(getTimeout()).method(Connection.Method.GET).execute();
        if (res.statusCode() != 200) {
            throw new ConnectException("Failed to get login params");
        }
        cookies = res.cookies();
        /*
        String text = res.body();

        Pattern pattern = Pattern.compile("input type=\"hidden\" name=\"lt\" value=\"(.*)\"");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find() && matcher.groupCount() > 0) {
            lt = matcher.group(1);
        } else {
            throw new ConnectException("Failed to get login param 'lt'");
        }

        pattern = Pattern.compile("input type=\"hidden\" name=\"execution\" value=\"(.*)\"");
        matcher = pattern.matcher(text);
        if (matcher.find() && matcher.groupCount() > 0) {
            excution = matcher.group(1);
        } else {
            throw new ConnectException("Failed to get login param 'execution'");
        }

         */
    }

    private void fetchStudentInfo() throws IOException, NeedLoginException {
        Connection.Response res = Jsoup.connect(STUDENT_INFO_URL)
                .timeout(getTimeout())
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE)
                .header(HEADER_REFERER, REFERER)
                .cookies(getCookies())
                .method(Connection.Method.POST).execute();
        if (!isLogin(res.body())) {
            throw new NeedLoginException();
        }
        Document doc = res.parse();
        studentInfo = new StudentInfo(doc.getElementsByTag("xh").text(),
                doc.getElementsByTag("nj").text(),
                doc.getElementsByTag("zymc").text(),
                doc.getElementsByTag("zydm").text(),
                doc.getElementsByTag("xn").text(),
                doc.getElementsByTag("xq_m").text());
    }

    private boolean isLogin(String body) {
        if (body.contains("统一身份认证平台")) {
            return false;
        }
        everSucceed = true;
        return true;
    }

    private String getMD5(String p) throws SecurityException {
        String res;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            res = new BigInteger(1, messageDigest.digest(p.getBytes())).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException("Failed to encrypt params");
        }
        return res;
    }

    private EncryptedParam encryptParams(String params) throws IOException, SecurityException, NeedLoginException {
        Connection.Response res = Jsoup.connect(DESKEY_URL + (int)(Math.random() * 10000000))
                .timeout(getTimeout())
                .cookies(getCookies())
                .method(Connection.Method.GET).execute();
        if (!isLogin(res.body())) {
            throw new NeedLoginException();
        }
        Pattern pattern = Pattern.compile("var _deskey = '(.*)';");
        Matcher matcher = pattern.matcher(res.body());
        String desKey;
        if (matcher.find() && matcher.groupCount() > 0) {
            desKey = matcher.group(1);
        } else {
            throw new ConnectException("Failed to get encrypting key");
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("zh_CN"));
        String timestamp = dateFormat.format(new Date());
        String paramsMd5 = getMD5(params);
        String timestampMd5 = getMD5(timestamp);
        String token = getMD5(paramsMd5 + timestampMd5);
        String encrypt = Des.strEnc(params, desKey, null, null);

        return new EncryptedParam(Base64.encodeToString(encrypt.getBytes(), Base64.DEFAULT), token, timestamp);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public StudentInfo getStudentInfo() throws IOException, NeedLoginException {
        if (studentInfo == null) {
            fetchStudentInfo();
        }
        return studentInfo;
    }

    public void login() throws IOException, LoginException {

        fetchLoginParams();

        Document doc = Jsoup.connect(LOGIN_URL)
                .timeout(getTimeout())
                .cookies(getCookies())
                .header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1")
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE)
                .data("username", getUsername())
                .data("password", getPassword())
                .data("code", "code")
                .data("lt", lt)
                .data("execution", excution)
                .data("_eventId", "submit")
                .post();

        if (doc.getElementById("frmbody") == null) {
            Element msg = doc.getElementById("msg");
            if (msg == null) {
                throw new LoginException("登录错误");
            } else {
                String msgText = msg.text();
                msgText = msgText.substring(0, msgText.length() - 1);
                if ("登陆失败".contentEquals(msgText)) {
                    msgText = "用户名密码错误";
                }
                throw new LoginException(msgText);
            }

        }
        everSucceed = false;
    }

    public ArrayList<PlanCourse> getPlanCourses(boolean showAll) throws IOException, NeedLoginException, JSONException {

        fetchSelectInfo();

        Connection conn = Jsoup.connect(TABLE_URL + COURSE_LIST_TABLE_ID)
                .timeout(getTimeout())
                .cookies(getCookies())
                .header(HEADER_REFERER, REFERER)
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE)
                .data("initQry", "0")
                .data("xktype", "2")
                .data("xh", getStudentInfo().getId())
                .data("xn", /*getStudentInfo().getAcademicYear()*/selectInfo.getYear())
                .data("xq", /*getStudentInfo().getSchoolTerm()*/selectInfo.getXqM())
                .data("nj", getStudentInfo().getGrade())
                .data("zydm", getStudentInfo().getSpecialityCode())
                .data("kcfw", "zxbnj")
                .data("kkdw_range", "all")
                .data("njzy", getStudentInfo().getGrade() + "|" + getStudentInfo().getSpecialityCode());
                /*
                .data("items", "")
                .data("is_xjls", "undefined")
                .data("lbgl", "")
                .data("kcmc", "")
                .data("sel_cddwdm", "")
                .data("menucode_current", "JW130403")
                .data("btnFilter", "类别过滤")
                .data("btnSubmit", "提交");
                */
        if (!showAll) {
            conn.data("xwxmkc", "on");
        }
        Document doc = conn.post();
        if (!isLogin(doc.outerHtml())) {
            throw new NeedLoginException();
        }
        ArrayList<PlanCourse> courses = new ArrayList<>();
        for (int i = 0; ; ++i) {
            String prefix = "tr" + i + "_";
            PlanCourse course = new PlanCourse();

            Element courseNameEl = doc.getElementById(prefix + "kc");
            if (courseNameEl == null) {
                break;
            }

            Element typeEl = doc.getElementById(prefix + "lb");

            Element teacherEl = doc.getElementById(prefix + "rkjs");

            if (courseNameEl.childNodeSize() > 0) {
                String courseName = courseNameEl.child(0).text();
                courseName = courseName.substring(courseName.indexOf("]") + 1);
                course.setName(courseName);
            }
            course.setCredit(doc.getElementById(prefix + "xf").text());
            course.setPeriod(doc.getElementById(prefix + "zxs").text());
            if (typeEl.childNodeSize() > 0) {
                course.setClassification(typeEl.child(0).text());
            }
            course.setSelectingStatus(doc.getElementById(prefix + "xk_status").text());
            course.setClassCode(doc.getElementById(prefix + "skbjdm").text());
            if (teacherEl.childNodeSize() > 0) {
                String teacher = teacherEl.child(0).text();
                int pos = teacher.indexOf("]");
                String teacherID = teacherEl.child(0).attr("onclick");
                int idBeginPos = teacherID.indexOf("'") + 1;
                int idEndPos = teacherID.indexOf("'", idBeginPos);
                course.setTeacherCode(teacherID.substring(idBeginPos, idEndPos));
                course.setTeacher(teacher.substring(pos + 1));
            }
            course.setCode(doc.getElementById(prefix + "kcdm").text());
            course.setType1(doc.getElementById(prefix + "kclb1").text());
            course.setType2(doc.getElementById(prefix + "kclb2").text());
            course.setType3(doc.getElementById(prefix + "kclb3").text());
            course.setKcxz(doc.getElementById(prefix + "kcxz").text());
            course.setExamType(doc.getElementById(prefix + "khfs").text());
            courses.add(course);
        }

        return courses;
    }

    public ArrayList<PlanChildCourse> getPlanChildCourses(PlanCourse course) throws IOException, NeedLoginException, JSONException {

        fetchSelectInfo();

        String params = String.format("xn=%s&xq_m=%s&xh=%s&kcdm=%s&skbjdm=&xktype=2&kcfw=zxbnj",
                selectInfo.getYear(),
                selectInfo.getXqM(),
                /*
                getStudentInfo().getAcademicYear(),
                getStudentInfo().getSchoolTerm(),
                */
                getStudentInfo().getId(),
                course.getCode());
        Document doc = Jsoup.connect(TABLE_URL + PLAN_COURSE_CLASSES_TABLE_ID + "&" + params)
                .timeout(getTimeout())
                .cookies(getCookies())
                .header(HEADER_REFERER, REFERER)
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE)
                .data("initQry", "0")
                .data("electiveCourseForm.xktype", "2")
                .data("electiveCourseForm.kcdm", course.getCode())
                /*
                .data("electiveCourseForm.xh", getStudentInfo().getId())
                .data("electiveCourseForm.xn", getStudentInfo().getAcademicYear())
                .data("electiveCourseForm.xq", getStudentInfo().getSchoolTerm())
                .data("electiveCourseForm.nj", getStudentInfo().getGrade())
                .data("electiveCourseForm.zydm", getStudentInfo().getSpecialityCode())
                .data("electiveCourseForm.kclb1", course.getType1())
                .data("electiveCourseForm.kclb2", course.getType2())
                .data("electiveCourseForm.kclb3", "")
                .data("electiveCourseForm.khfs", course.getExamType())
                .data("electiveCourseForm.skbjdm", "")
                .data("electiveCourseForm.skbzdm", "")
                .data("electiveCourseForm.xf", course.getCredit())
                .data("electiveCourseForm.is_checkTime", "1")
                .data("kknj", "")
                .data("kkzydm", "")
                .data("txt_skbjdm", "")
                .data("electiveCourseForm.xk_points", "0")
                .data("electiveCourseForm.is_buy_book", "")
                .data("electiveCourseForm.is_cx", "")
                .data("electiveCourseForm.is_yxtj", "")
                .data("menucode_current", "JW130403")
                 */
                .post();
        if (!isLogin(doc.outerHtml())) {
            throw new NeedLoginException();
        }
        ArrayList<PlanChildCourse> classes = new ArrayList<>();
        for (int i = 0; ; ++i) {
            String prefix = "tr" + i + "_";
            PlanChildCourse childCourse = new PlanChildCourse();

            Element classCodeEl = doc.getElementById(prefix + "curent_skbjdm");
            if (classCodeEl == null) {
                break;
            }

            Element classNameEl = doc.getElementById(prefix + "skbjmc");

            Element classTeacherEl = doc.getElementById(prefix + "rkjs");

            Element classChkEl = doc.getElementById(prefix + "ischk");

            childCourse.setSubClassCode(classCodeEl.text());
            if (classNameEl.childNodeSize() > 0) {
                childCourse.setClassName(classNameEl.child(0).text());
            }
            childCourse.setSchoolName(doc.getElementById(prefix + "xqmc").text());
            childCourse.setGroupName(doc.getElementById(prefix + "skbzmc").text());
            childCourse.setMaxSelection(doc.getElementById(prefix + "xkrssx").text());
            childCourse.setSelectionCount(doc.getElementById(prefix + "xkrs").text());
            childCourse.setRemainingSelection(doc.getElementById(prefix + "kxrs").text());
            if (classTeacherEl.childNodeSize() > 0) {
                String teacher = classTeacherEl.child(0).text();
                int pos = teacher.indexOf("]");
                String teacherID = classTeacherEl.child(0).attr("onclick");
                int idBeginPos = teacherID.indexOf("\"") + 1;
                int idEndPos = teacherID.indexOf("\"", idBeginPos);
                childCourse.setTeacherCode(teacherID.substring(idBeginPos, idEndPos));
                childCourse.setTeacher(teacher.substring(pos + 1));
            }
            childCourse.setMethodName(doc.getElementById(prefix + "skfs_mc").text());
            childCourse.setTime(doc.getElementById(prefix + "sksj").text());
            childCourse.setLocation(doc.getElementById(prefix + "skdd").text());
            if (classChkEl.childNodeSize() > 0) {
                childCourse.setSelected(doc.getElementById(prefix + "ischk").child(0).hasAttr("checked"));
            }
            childCourse.setClassCode(doc.getElementById(prefix + "skbjdm").text());
            childCourse.setMethodCode(doc.getElementById(prefix + "skfs_m").text());
            childCourse.setSchoolCode(doc.getElementById(prefix + "xqdm").text());
            childCourse.setGroupCode(doc.getElementById(prefix + "skbzdm").text());

            classes.add(childCourse);
        }

        return classes;
    }

    public ArrayList<ElectiveCourse> getElectiveCourses(boolean showAll) throws IOException, NeedLoginException, JSONException {

        fetchSelectInfo();

        Connection conn = Jsoup.connect(TABLE_URL + ELECTIVE_COURSE_LIST_TABLE_ID)
                .timeout(getTimeout())
                .cookies(getCookies())
                .header(HEADER_REFERER, REFERER)
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE)
                .data("initQry", "0")
                .data("xktype", "2")
                .data("xh", getStudentInfo().getId())
                .data("xn", /*getStudentInfo().getAcademicYear()*/selectInfo.getYear())
                .data("xq", /*getStudentInfo().getSchoolTerm()*/selectInfo.getXqM())
                .data("nj", getStudentInfo().getGrade())
                .data("zydm", getStudentInfo().getSpecialityCode())
                .data("kcfw", "zxggrx")
                /*
                .data("kcdm", "")
                .data("kclb1", "")
                .data("kclb2", "")
                .data("khfs", "")
                .data("skbjdm", "")
                .data("skbzdm", "")
                .data("xf", "")
                .data("items", "")
                .data("is_xjls", "undefined")
                .data("kcmc", "")
                .data("menucode_current", "JW130415")
                 */
                .data("njzy", getStudentInfo().getGrade() + "|" + getStudentInfo().getSpecialityCode());
        if (!showAll) {
            conn.data("xwxmkc", "on");
        }
        Document doc = conn.post();
        if (!isLogin(doc.outerHtml())) {
            throw new NeedLoginException();
        }
        ArrayList<ElectiveCourse> courses = new ArrayList<>();
        for (int i = 0; ; ++i) {
            String prefix = "tr" + i + "_";
            ElectiveCourse course = new ElectiveCourse();

            Element courseNameEl = doc.getElementById(prefix + "kc");
            if (courseNameEl == null) {
                break;
            }

            Element teacherEl = doc.getElementById(prefix + "rkjs");

            if (courseNameEl.childNodeSize() > 0) {
                String courseName = courseNameEl.child(0).text();
                courseName = courseName.substring(courseName.indexOf("]") + 1);
                course.setName(courseName);
            }
            course.setClassNumber(doc.getElementById(prefix + "skbj").text());
            course.setCredit(doc.getElementById(prefix + "xf").text());
            course.setPeriod(doc.getElementById(prefix + "zxs").text());
            course.setClassification(doc.getElementById(prefix + "lb").text());
            if (teacherEl.childNodeSize() > 0) {
                String teacher = teacherEl.child(0).text();
                int pos = teacher.indexOf("]");
                String teacherID = teacherEl.child(0).attr("onclick");
                int idBeginPos = teacherID.indexOf("'") + 1;
                int idEndPos = teacherID.indexOf("'", idBeginPos);
                course.setTeacherCode(teacherID.substring(idBeginPos, idEndPos));
                course.setTeacher(teacher.substring(pos + 1));
            }
            course.setClassCode(doc.getElementById(prefix + "skbjdm").text());
            course.setCode(doc.getElementById(prefix + "kcdm").text());
            course.setType1(doc.getElementById(prefix + "kclb1").text());
            course.setType2(doc.getElementById(prefix + "kclb2").text());
            course.setType3(doc.getElementById(prefix + "kclb3").text());
            course.setExamType(doc.getElementById(prefix + "khfs").text());
            course.setMaxSelection(doc.getElementById(prefix + "xxrs").text());
            course.setSelectionCount(doc.getElementById(prefix + "yxrs").text());
            course.setRemainingSelection(doc.getElementById(prefix + "kxrs").text());
            course.setMethod(doc.getElementById(prefix + "skfs").text());
            course.setTime(doc.getElementById(prefix + "sksj").text());
            course.setLocaiton(doc.getElementById(prefix + "skdd").text());
            courses.add(course);
        }
        return courses;
    }

    public ArrayList<CancelCourse> getCancelCourses() throws IOException, NeedLoginException, JSONException {

        fetchSelectInfo();

        Document doc = Jsoup.connect(TABLE_URL + CANCEL_LIST_TABLE_ID)
                .timeout(getTimeout())
                .cookies(getCookies())
                .header(HEADER_REFERER, REFERER)
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE)
                .data("xktype", "5")
                .data("xh", getStudentInfo().getId())
                .data("xn", /*getStudentInfo().getAcademicYear()*/selectInfo.getYear())
                .data("xq", /*getStudentInfo().getSchoolTerm()*/selectInfo.getXqM())
                .data("nj", getStudentInfo().getGrade())
                .data("zydm", getStudentInfo().getSpecialityCode())
                /*
                .data("items", "")
                .data("menucode_current", "JW130406")
                .data("btnQry", "检索")
                */
                .data("kcfw", "All")
                .post();
        if (!isLogin(doc.outerHtml())) {
            throw new NeedLoginException();
        }
        ArrayList<CancelCourse> courses = new ArrayList<>();
        for (int i = 0; ; ++i) {
            String prefix = "tr" + i + "_";
            CancelCourse course = new CancelCourse();

            Element courseNameEl = doc.getElementById(prefix + "kc");
            if (courseNameEl == null) {
                break;
            }

            Element teacherEl = doc.getElementById(prefix + "rkjs");

            Element timeLocationEl = doc.getElementById(prefix + "sksjdd");

            if (courseNameEl.childNodeSize() > 0) {
                String courseName = courseNameEl.child(0).text();
                courseName = courseName.substring(courseName.indexOf("]") + 1);
                course.setName(courseName);
            }
            course.setSchoolName(doc.getElementById(prefix + "school_name").text());
            course.setCredit(doc.getElementById(prefix + "xf").text());
            course.setPeriod(doc.getElementById(prefix + "zxs").text());
            course.setClassification(doc.getElementById(prefix + "lb").text());
            if (teacherEl.childNodeSize() > 0) {
                String teacher = teacherEl.child(0).text();
                int pos = teacher.indexOf("]");
                String teacherID = teacherEl.child(0).attr("onclick");
                int idBeginPos = teacherID.indexOf("'") + 1;
                int idEndPos = teacherID.indexOf("'", idBeginPos);
                course.setTeacherCode(teacherID.substring(idBeginPos, idEndPos));
                course.setTeacher(teacher.substring(pos + 1));
            }
            course.setClassCode(doc.getElementById(prefix + "skbjdm").text());
            course.setShownClassCode(doc.getElementById(prefix + "show_skbjdm").text());
            course.setCode(doc.getElementById(prefix + "kcdm").text());
            course.setType1(doc.getElementById(prefix + "kclb1").text());
            course.setType2(doc.getElementById(prefix + "kclb2").text());
            course.setExamType(doc.getElementById(prefix + "khfs").text());
            course.setSelectingMethod(doc.getElementById(prefix + "xkfs").text());
            course.setSelectingStatus(doc.getElementById(prefix + "xk_status").text());
            if (timeLocationEl.childNodeSize() > 0) {
                course.setTimeLocation(timeLocationEl.child(0).text());
            }
            courses.add(course);
        }

        return courses;
    }

    public Result cancelCourse(CancelCourse course)
            throws IOException, NeedLoginException, JSONException {

        fetchSelectInfo();

        String params = String.format("xn=%s&xq=%s&xh=%s&kcdm=%s&skbjdm=%s&xktype=5",
                /*getStudentInfo().getAcademicYear(),
                getStudentInfo().getSchoolTerm(),*/
                selectInfo.getYear(),
                selectInfo.getXqM(),
                getStudentInfo().getId(),
                course.getCode(),
                course.getClassCode());
        EncryptedParam encryptedParams = encryptParams(params);
        Connection.Response res = Jsoup.connect(CANCEL_COURSE_URL)
                .timeout(getTimeout())
                .cookies(getCookies())
                .header(HEADER_REFERER, REFERER)
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE)
                .data("params", encryptedParams.getParams())
                .data("token", encryptedParams.getToken())
                .data("timestamp", encryptedParams.getTimestamp())
                .method(Connection.Method.POST)
                .execute();
        if (!isLogin(res.body())) {
            throw new NeedLoginException();
        }
        return new Result(res.body());
    }

    public SelectInfo fetchSelectInfo() throws IOException, NeedLoginException, JSONException {
        if (selectInfo != null) {
            return selectInfo;
        }
        Document doc = Jsoup.connect(SELECT_INFO_URL)
                .timeout(getTimeout())
                .cookies(getCookies())
                .header(HEADER_REFERER, REFERER)
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE)
                .post();
        if (!isLogin(doc.outerHtml())) {
            throw new NeedLoginException();
        }
        selectInfo = new SelectInfo(doc.body().html());
        return selectInfo;
    }

    public Result selectPlanCourse(PlanCourse course, PlanChildCourse childCourse)
            throws IOException, NeedLoginException, JSONException {

        fetchSelectInfo();

        String params = String.format("xktype=%s&xn=%s&xq=%s&xh=%s&nj=%s&zydm=%s&kcdm=%s" +
                "&kclb1=%s&kclb2=%s&kclb3=&khfs=%s&skbjdm=%s&skbzdm=&xf=%s&is_checkTime=1" +
                "&kknj=&kkzydm=&txt_skbjdm=&xk_points=0&is_buy_book=0&is_cx=0&is_yxtj=1" +
                "&menucode_current=JW130403&kcfw=zxbnj",
                selectInfo.getXktype(),
                //getStudentInfo().getAcademicYear(),
                //getStudentInfo().getSchoolTerm(),
                selectInfo.getYear(),
                selectInfo.getXqM(),
                getStudentInfo().getId(),
                getStudentInfo().getGrade(),
                getStudentInfo().getSpecialityCode(),
                course.getCode(),
                course.getType1(),
                course.getType2(),
                course.getExamType(),
                childCourse.getClassCode(),
                course.getCredit());

        EncryptedParam encryptedParams = encryptParams(params);
        Connection.Response res = Jsoup.connect(SELECT_ELECTIVE_COURSE_URL)
                .timeout(getTimeout())
                .cookies(getCookies())
                .header(HEADER_REFERER, REFERER)
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE)
                .data("params", encryptedParams.getParams())
                .data("token", encryptedParams.getToken())
                .data("timestamp", encryptedParams.getTimestamp())
                .method(Connection.Method.POST)
                .execute();
        if (!isLogin(res.body())) {
            throw new NeedLoginException();
        }
        return new Result(res.body());
    }

    public Result selectElectiveCourse(ElectiveCourse course)
            throws IOException, NeedLoginException, JSONException {
        String params = String.format("xktype=%s&initQry=0&xh=%s&xn=%s&xq=%s&nj=%s&zydm=%s" +
                "&kcdm=%s&kclb1=%s&kclb2=%s&khfs=%s&skbjdm=%s&skbzdm=&xf=%s&kcfw=zxggrx" +
                "&njzy=%s&items=&is_xjls=undefined&kcmc=&t_skbh=&menucode_current=JW130415",
                selectInfo.getXktype(),
                getStudentInfo().getId(),
                //getStudentInfo().getAcademicYear(),
                //getStudentInfo().getSchoolTerm(),
                selectInfo.getYear(),
                selectInfo.getXqM(),
                getStudentInfo().getGrade(),
                getStudentInfo().getSpecialityCode(),
                course.getCode(),
                course.getType1(),
                course.getType2(),
                course.getExamType(),
                course.getClassCode(),
                course.getCredit(),
                getStudentInfo().getGrade() + "|" + getStudentInfo().getSpecialityCode());
        EncryptedParam encryptedParams = encryptParams(params);
        Connection.Response res = Jsoup.connect(SELECT_ELECTIVE_COURSE_URL)
                .timeout(getTimeout())
                .cookies(getCookies())
                .header(HEADER_REFERER, REFERER)
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE)
                .data("params", encryptedParams.getParams())
                .data("token", encryptedParams.getToken())
                .data("timestamp", encryptedParams.getTimestamp())
                .method(Connection.Method.POST)
                .execute();
        if (!isLogin(res.body())) {
            throw new NeedLoginException();
        }
        return new Result(res.body());
    }

    public ArrayList<SelectionResult> getSelectionResult() throws IOException, NeedLoginException {
        Document doc = Jsoup.connect(SELECTION_RESULT_URL + Math.random() * 1000000)
                .timeout(20000)
                .cookies(getCookies())
                .header(HEADER_REFERER, REFERER)
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE)
                .get();
        if (!isLogin(doc.outerHtml())) {
            throw new NeedLoginException();
        }
        Element table = doc.getElementById("reportArea").getElementsByTag("tbody").get(0);
        Elements tr = table.getElementsByTag("tr");
        ArrayList<SelectionResult> selectionResults = new ArrayList<>();
        for (Element row: tr) {
            SelectionResult selectionResult = new SelectionResult();
            Elements td = row.getElementsByTag("td");

            selectionResult.setNumber(td.get(1).text());

            String courseName = td.get(2).child(0).text();
            courseName = courseName.substring(courseName.indexOf("]") + 1);
            selectionResult.setName(courseName);

            selectionResult.setCredit(td.get(3).text());
            selectionResult.setClassification(td.get(4).text());

            String teacherID = td.get(5).child(0).attr("onclick");
            int idBeginPos = teacherID.indexOf("\"") + 1;
            int idEndPos = teacherID.indexOf("\"", idBeginPos);
            selectionResult.setTeacherCode(teacherID.substring(idBeginPos, idEndPos));
            selectionResult.setTeacher(td.get(5).child(0).text());

            selectionResult.setClassCode(td.get(6).text());
            selectionResult.setClassName(td.get(7).text());
            selectionResult.setSelectingMethod(td.get(8).text());
            selectionResult.setSelectionCount(td.get(9).text());
            selectionResult.setMaxSelection(td.get(10).text());
            selectionResult.setRemainingSelection(td.get(11).text());
            selectionResult.setTimeLocation(td.get(12).child(0).text());
            selectionResult.setCode(td.get(13).text());
            selectionResults.add(selectionResult);
        }
        return selectionResults;
    }

    public ArrayList<ExamRound> getExamRounds() throws IOException, NeedLoginException, JSONException {
        Connection.Response res = Jsoup.connect(DROPLIST_URL)
                .timeout(getTimeout())
                .cookies(getCookies())
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE)
                .header(HEADER_REFERER, REFERER)
                .data("comboBoxName", EXAM_DROP_NAME)
                .method(Connection.Method.POST)
                .execute();
        if (!isLogin(res.body())) {
            throw new NeedLoginException();
        }
        JSONArray rounds = new JSONArray(res.body());
        ArrayList<ExamRound> examRounds = new ArrayList<>();
        for (int i = 0; i < rounds.length(); ++i) {
            JSONObject round = rounds.getJSONObject(i);
            ExamRound examRound = new ExamRound();
            examRound.setCode(round.getString("code"));
            examRound.setName(round.getString("name"));
            examRounds.add(examRound);
        }

        Collections.sort(examRounds, new Comparator<ExamRound>() {
            @Override
            public int compare(ExamRound e1, ExamRound e2) {
                return e2.getCode().compareTo(e1.getCode());
                /*
                int cmp = -Integer.valueOf(e1.getYear()).compareTo(e2.getYear());
                if (cmp == 0) {
                    cmp = -Integer.valueOf(e1.getTerm()).compareTo(e2.getTerm());
                }
                if (cmp == 0) {
                    cmp = Integer.valueOf(e1.getRound()).compareTo(e2.getRound());
                }
                return cmp;*/
            }
        });
        return examRounds;
    }

    public ArrayList<ExamArrangement> getExamArrangement(ExamRound examRound) throws IOException, NeedLoginException {
        Document doc = Jsoup.connect(TABLE_URL + EXAM_ARRAGEMENT_TABLE_ID)
                .timeout(getTimeout())
                .cookies(getCookies())
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE)
                .header(HEADER_REFERER, REFERER)
                .data("xh", "")
                .data("xn", String.valueOf(examRound.getYear()))
                .data("xq", String.valueOf(examRound.getTerm()))
                .data("kslc", String.valueOf(examRound.getRound()))
                .data("xnxqkslc", examRound.getCode())
                .post();
        if (!isLogin(doc.outerHtml())) {
            throw new NeedLoginException();
        }
        ArrayList<ExamArrangement> arrangements = new ArrayList<>();
        for (int i = 0; ; ++i) {
            String prefix = "tr" + i + "_";
            ExamArrangement arrangement = new ExamArrangement();

            Element courseNameEl = doc.getElementById(prefix + "kc");
            if (courseNameEl == null) {
                break;
            }
            arrangement.setCourse(courseNameEl.text());
            arrangement.setCredit(doc.getElementById(prefix + "xf").text());
            arrangement.setClassification(doc.getElementById(prefix + "lb").text());
            arrangement.setExamType(doc.getElementById(prefix + "khfs").text());
            arrangement.setTimeString(doc.getElementById(prefix + "kssj").text());
            arrangement.setLocation(doc.getElementById(prefix + "ksdd").text());
            arrangement.setSeat(doc.getElementById(prefix + "zwh").text());
            arrangements.add(arrangement);
        }

        Collections.sort(arrangements, new Comparator<ExamArrangement>() {
            @Override
            public int compare(ExamArrangement e1, ExamArrangement e2) {
                boolean late1 = e1.getEndTime().before(Calendar.getInstance());
                boolean late2 = e2.getEndTime().before(Calendar.getInstance());
                if (late1 && late2) {
                    return e2.getEndTime().compareTo(e1.getEndTime());
                } else if (!late1 && !late2) {
                    return e1.getBeginTime().compareTo(e1.getBeginTime());
                } else if(late1 && !late2) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        return arrangements;
    }

    public ArrayList<ExamScore> getExamScores() throws IOException, NeedLoginException {
        return getExamScores(0, 0);
    }

    public ArrayList<ExamScore> getExamScores(int year, int term) throws IOException, NeedLoginException {
        Connection conn = Jsoup.connect(EXAM_SCORE_URL)
                .timeout(getTimeout())
                .cookies(getCookies())
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE)
                .header(HEADER_REFERER, REFERER)
                .data("ysyx", "yscj")
                .data("userCode", getStudentInfo().getId())
                .data("zfx", "0")
                .data("ysyxS", "on")
                .data("sjxzS", "on")
                .data("zfxS", "on");
        if (year == 0) {
            conn.data("sjxz", "sjxz1");
        } else {
            conn.data("sjxz", "sjxz3")
                    .data("xn", String.valueOf(year))
                    .data("xn1", String.valueOf(year + 1))
                    .data("xq", String.valueOf(term));
        }
        Document doc = conn.post();
        if (!isLogin(doc.outerHtml())) {
            throw new NeedLoginException();
        }
        ArrayList<ExamScore> examScores = new ArrayList<>();
        Element table = doc.getElementsByTag("tbody").get(0);
        Elements rows = table.getElementsByTag("tr");
        String lastTerm = "";
        for (Element tr : rows) {
            ExamScore examScore = new ExamScore();
            Elements columns = tr.getElementsByTag("td");
            String currentTerm = columns.get(0).text().trim();
            if (currentTerm.isEmpty()) {
                currentTerm = lastTerm;
            } else {
                lastTerm = currentTerm;
            }
            examScore.setTerm(currentTerm);
            examScore.setCourseName(columns.get(1).text());
            examScore.setCourseCredit(columns.get(2).text());
            examScore.setClassification(columns.get(3).text());
            examScore.setFirstLearn("初修".equals(columns.get(4).text().trim()));
            examScore.setUsualScore(columns.get(5).text());
            examScore.setExamScore(columns.get(6).text());
            examScore.setScore(columns.get(7).text());
            examScore.setMajor("主修".equals(columns.get(8).text().trim()));
            examScores.add(examScore);
        }
        Collections.sort(examScores, new Comparator<ExamScore>() {
            @Override
            public int compare(ExamScore e1, ExamScore e2) {
                return e1.getScore().compareTo(e2.getScore());
            }
        });
        return examScores;
    }

    public ArrayList<EvaluationItem> getEvaluateList()
            throws IOException, NeedLoginException, JSONException {
        Connection.Response res = Jsoup.connect(EVALUATE_LIST_URL)
                .timeout(getTimeout())
                .cookies(getCookies())
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE)
                .header(HEADER_REFERER, REFERER)
                .data("pjzt_m", "20")
                .method(Connection.Method.POST)
                .execute();
        if (!isLogin(res.body())) {
            throw new NeedLoginException();
        }
        Pattern pattern = Pattern.compile("<option value='(\\{.*?\\})'>(.*?)</option>");
        Matcher matcher = pattern.matcher(res.body());
        ArrayList<EvaluationItem> evaluationItems = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("zh_CN"));
        while (matcher.find()) {
            String json = matcher.group(1);
            String name = matcher.group(2);
            JSONObject evaluateInfo = new JSONObject(json);
            EvaluationItem item = new EvaluationItem();
            item.setName(name);
            item.setCode(evaluateInfo.getString("lcdm"));
            item.setStartDate(dateFormat.parse(evaluateInfo.getString("qsrq"), new ParsePosition(0)));
            item.setEndDate(dateFormat.parse(evaluateInfo.getString("jsrq"), new ParsePosition(0)));
            item.setYear(evaluateInfo.getInt("xn"));
            item.setTerm(evaluateInfo.getInt("xq_m"));
            item.setTermName(evaluateInfo.getString("lcqc"));
            item.setPhase(evaluateInfo.getString("lcjc"));
            item.setSfkpsj(evaluateInfo.getString("sfkpsj"));
            item.setSfwjpj(evaluateInfo.getString("sfwjpj"));
            item.setSfzbpj(evaluateInfo.getString("sfzbpj"));
            item.setPjfsbz(evaluateInfo.getString("pjfsbz"));
            evaluationItems.add(item);
        }
        return evaluationItems;
    }

    public ArrayList<EvaluationCourse> getEvaluatingCourses(EvaluationItem evaluation)
            throws IOException, NeedLoginException, JSONException {
        Document doc = Jsoup.connect(TABLE_URL + EVALUATE_COURSE_LIST_TABLE_ID)
                .timeout(getTimeout())
                .cookies(getCookies())
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE)
                .header(HEADER_REFERER, REFERER)
                .data("xn", String.valueOf(evaluation.getYear()))
                .data("xq", String.valueOf(evaluation.getTerm()))
                .data("pjlc", evaluation.getCode())
                .data("sfzbpj", evaluation.getSfzbpj())
                .data("sfwjpj", evaluation.getSfwjpj())
                .data("pjzt_m", "20")
                .data("pjfsbz", evaluation.getPjfsbz())
                .post();
        if (!isLogin(doc.outerHtml())) {
            throw new NeedLoginException();
        }
        ArrayList<EvaluationCourse> courses = new ArrayList<>();
        for (int i = 0; ; ++i) {
            String prefix = "tr" + i + "_";

            Element evaluateEl = doc.getElementById(prefix + "wjdc");
            if (evaluateEl == null) {
                break;
            }

            String json = evaluateEl.child(0).attr("onclick");
            json = json.substring(13, json.length() - 6).replace("\\\"", "\"");
            EvaluationCourse course = new EvaluationCourse();
                JSONObject courseInfo = new JSONObject(json);
                course.setCode(courseInfo.getString("kcdm"));
                course.setStatusCode(courseInfo.getString("pjzt_m"));
                course.setClassificationCode(courseInfo.getString("pjlb_m"));
                course.setClassification(courseInfo.getString("pjlbmc"));
                course.setTeacherID(courseInfo.getString("jsid"));
                course.setTeacherNumber(courseInfo.getString("gh"));
                course.setTeacherName(courseInfo.getString("xm"));
                course.setSfzjjs(courseInfo.getString("sfzjjs"));
                course.setYhdm(courseInfo.getString("yhdm"));
                course.setName(courseInfo.getString("kcmc"));
                course.setCredit(courseInfo.getString("xf"));
                course.setYhdm(courseInfo.getString("yhdm"));
                course.setClassCode(courseInfo.getString("skbjdm"));
                course.setEvaluated("1".equals(courseInfo.getString("ypflag")));
                courses.add(course);

        }
        return courses;
    }

    public Result evaluateCourse(EvaluationItem evaluation, EvaluationCourse course, int score)
            throws IOException, NeedLoginException, JSONException {

        if (score > 5) {
            score = 5;
        } else if (score < 1) {
            score = 1;
        }

        Document doc = Jsoup.connect(EVALUATE_FORM_URL)
                .timeout(getTimeout())
                .cookies(getCookies())
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE)
                .header(HEADER_REFERER, REFERER)
                .data("pjlc", evaluation.getCode())
                .data("kcdm", course.getCode() )
                .data("jsid", course.getTeacherID())
                .data("sfzjjs", course.getSfzjjs())
                .data("jsgh", course.getTeacherNumber())
                .data("jsxm", course.getTeacherName())
                .data("pjlb_m", course.getClassificationCode())
                .data("pjlbmc", course.getClassification())
                .data("xn", String.valueOf(evaluation.getYear()))
                .data("xq", String.valueOf(evaluation.getTerm()))
                .data("kcmc", course.getName())
                .data("xf", course.getCredit())
                .data("userCode", getStudentInfo().getId())
                .data("sfzbpj", evaluation.getSfzbpj())
                .data("sfwjpj", evaluation.getSfwjpj())
                .data("pjzt_m", course.getStatusCode())
                .data("pjfsbz", evaluation.getPjfsbz())
                .data("ypflag", course.isEvaluated() ? "1" : "0")
                .data("mode", "0")
                .data("skbjdm", course.getClassCode())
                .get();
        if (!isLogin(doc.outerHtml())) {
            throw new NeedLoginException();
        }
        Element zbmbEl = doc.getElementById("zbmb");
        Element wjmbEl = doc.getElementById("wjmb");
        String zbmb = "005", wjmb = "002";
        if (zbmbEl != null) {
            zbmb = zbmbEl.val();
        }
        if (wjmbEl != null) {
            wjmb = wjmbEl.val();
        }
        Element zbSizeEl = doc.getElementById("zbSize");
        int radioSize = 0;
        if (zbSizeEl != null) {
            radioSize = Integer.valueOf(zbSizeEl.val());
        }
        ArrayList<String> radioID = new ArrayList<>();
        for (int i = 0; i < radioSize; ++i) {
            String el = "cj" + i + ">";
            Elements radios = doc.getElementsByAttributeValue("name", el);
            if (!radios.isEmpty()) {
                radioID.add(
                        String.valueOf(score)
                        + "@" + radios.get(0).val().split("@")[1] + "@0"
                        + String.valueOf(6 - score)
                );
            }
        }
        Element wjSizeEl = doc.getElementById("wjSize");
        int textSize = 0;
        if (wjSizeEl != null) {
            textSize = Integer.valueOf(wjSizeEl.val());
        }
        ArrayList<String> textID = new ArrayList<>();
        String remark;

        if (score < 3) {
            remark = "@#@优点很少，收获不多，建议老师充分调动课堂，创新教学方式，继续努力!";
        } else {
            remark = "@#@优点很多，收获很多，没有建议，继续努力!";
        }

        for (int i = 0; i < textSize; ++i) {
            String el = "area" + i;
            Elements areas = doc.getElementsByAttributeValue("name", el);
            if (!areas.isEmpty()) {
                textID.add(areas.get(0).attr("tmbh") + remark);
            }
        }

        String commitRadio = TextUtils.join(";", radioID);
        String commitText = TextUtils.join(";", textID);

        commitRadio = URLEncoder.encode(commitRadio, "UTF-8");
        commitText = URLEncoder.encode(commitText, "UTF-8");
        commitRadio = URLEncoder.encode(commitRadio, "UTF-8");
        commitText = URLEncoder.encode(commitText, "UTF-8");

        Connection.Response res = Jsoup.connect(EVALUATE_SAVE_URL)
                .timeout(getTimeout())
                .cookies(getCookies())
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE)
                .header(HEADER_REFERER, REFERER)
                .data("wspjZbpjWjdcForm.pjlb_m", course.getClassificationCode())
                .data("wspjZbpjWjdcForm.sfzjjs", course.getSfzjjs())
                .data("wspjZbpjWjdcForm.xn", String.valueOf(evaluation.getYear()))
                .data("wspjZbpjWjdcForm.xq", String.valueOf(evaluation.getTerm()))
                .data("wspjZbpjWjdcForm.pjlc", evaluation.getCode())
                .data("wspjZbpjWjdcForm.pjzt_m", course.getStatusCode())
                .data("wspjZbpjWjdcForm.userCode", getStudentInfo().getId())
                .data("wspjZbpjWjdcForm.kcdm", course.getCode())
                .data("wspjZbpjWjdcForm.skbjdm", course.getClassCode())
                .data("wspjZbpjWjdcForm.pjfsbz", evaluation.getPjfsbz())
                .data("wspjZbpjWjdcForm.jsid", course.getTeacherID())
                .data("wspjZbpjWjdcForm.zbmb_m", zbmb)
                .data("wspjZbpjWjdcForm.wjmb_m", wjmb)
                .data("wspjZbpjWjdcForm.commitZB", commitRadio)
                .data("wspjZbpjWjdcForm.commitWJText", commitText)
                .data("wspjZbpjWjdcForm.commitWJSelect", "")
                .data("pycc", "1")
                .method(Connection.Method.POST)
                .execute();
        if (!isLogin(res.body())) {
            throw new NeedLoginException();
        }
        return new Result(res.body());
    }

    public StudentDetails getStudentDetails() throws IOException, NeedLoginException {
        Document doc = Jsoup.connect(STUDENT_DETAILS_URL)
                .timeout(getTimeout())
                .cookies(getCookies())
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE)
                .header(HEADER_REFERER, REFERER)
                .post();
        if (!isLogin(doc.outerHtml())) {
            throw new NeedLoginException();
        }
        StudentDetails details = new StudentDetails();

        Element info = doc.getElementsByTag("info").get(0);
        details.setAddress(info.getElementsByTag("txdz").get(0).text());
        details.setAvatarID(info.getElementsByTag("zpid").get(0).text());
        details.setBirthday(info.getElementsByTag("csrq").get(0).text());
        details.setClassName(info.getElementsByTag("bjmc").get(0).text());
        details.setCollege(info.getElementsByTag("yxb").get(0).text());
        details.setCollegeWill(info.getElementsByTag("zymc").get(0).text());
        details.setCultureStandard(info.getElementsByTag("whcd").get(0).text());
        details.setEducationLevel(info.getElementsByTag("pycc").get(0).text());
        details.setEmail(info.getElementsByTag("dzyx").get(0).text());
        details.setGaokaoID(info.getElementsByTag("gkksh").get(0).text());
        details.setGender(info.getElementsByTag("xb").get(0).text());
        details.setId(info.getElementsByTag("yhxh").get(0).text());
        details.setIdNumber(info.getElementsByTag("sfzjh").get(0).text());
        details.setMiddleSchool(info.getElementsByTag("sydw").get(0).text());
        details.setMobile(info.getElementsByTag("dh").get(0).text());
        details.setName(info.getElementsByTag("xm").get(0).text());
        details.setNationality(info.getElementsByTag("mz").get(0).text());
        details.setNumber(info.getElementsByTag("xh").get(0).text());
        details.setPinyin(info.getElementsByTag("xmpy").get(0).text());
        details.setRegistrationGrade(info.getElementsByTag("rxnj").get(0).text());
        details.setRegistrationTime(info.getElementsByTag("bdtime").get(0).text());
        details.setSchoolSystem(info.getElementsByTag("xz").get(0).text());
        details.setSpeciality(info.getElementsByTag("lqzy").get(0).text());

        return details;
    }

    public ArrayList<Semester> getSemesters() throws IOException, NeedLoginException, JSONException {
        Connection.Response res = Jsoup.connect(DROPLIST_URL)
                .timeout(getTimeout())
                .cookies(getCookies())
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE)
                .header(HEADER_REFERER, REFERER)
                .data("comboBoxName", "Ms_KBBP_FBXQLLJXAP")
                .method(Connection.Method.POST)
                .execute();
        if (!isLogin(res.body())) {
            throw new NeedLoginException();
        }
        ArrayList<Semester> semesters = new ArrayList<>();
        JSONArray list = new JSONArray(res.body());
        for (int i = 0; i < list.length(); ++i) {
            JSONObject obj = list.getJSONObject(i);
            Semester semester = new Semester();
            semester.setCode(obj.getString("code"));
            semester.setName(obj.getString("name"));
            semesters.add(semester);
        }
        return semesters;
    }

    public ArrayList<TableCourse> getTableCourses(Semester semester) throws IOException, NeedLoginException {
        String params = "xn=" + semester.getYear()
                + "&xq=" + semester.getTerm()
                + "&xh=" + getStudentInfo().getId();
        params = Base64.encodeToString(params.getBytes(), Base64.DEFAULT).trim();

        Document doc = Jsoup.connect(TIMETABLE_URL + params)
                .timeout(getTimeout())
                .cookies(getCookies())
                .header(HEADER_REFERER, "http://zyfw.bnu.edu.cn/student/xkjg.wdkb.jsp?menucode=JW130418")
                .get();
        if (!isLogin(doc.html())) {
            throw new NeedLoginException();
        }

        ArrayList<TableCourse> tableCourses = new ArrayList<>();
        Element table = doc.getElementsByTag("tbody").get(0);
        Elements rows = table.getElementsByTag("tr");
        for (Element tr : rows) {
            TableCourse course = new TableCourse();
            Elements columns = tr.getElementsByTag("td");
            course.setCode(columns.get(13).text());
            course.setCredit(columns.get(2).text());
            course.setLocationTime(columns.get(5).text());
            course.setName(columns.get(0).text());
            course.setTeacher(columns.get(4).text());
            tableCourses.add(course);
        }
        return tableCourses;
    }

    public boolean isEverSucceed() {
        return everSucceed;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
