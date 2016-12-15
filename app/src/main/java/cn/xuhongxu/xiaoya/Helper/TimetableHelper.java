package cn.xuhongxu.xiaoya.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.xuhongxu.Assist.TableCourse;
import cn.xuhongxu.xiaoya.R;
import cn.xuhongxu.xiaoya.View.TimeTableView;

/**
 * Created by xuhon on 2016/9/11.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class TimetableHelper {
    private Context mContext;
    private SharedPreferences preferences;
    private ArrayList<TableCourse> tableCourses;
    private String studentName;
    private int weekCount = 0;
    private int currentWeek = 0;
    private int shownWeek = 0;
    private boolean empty = false;

    public TimetableHelper(Context context) throws IOException, ClassNotFoundException {
        mContext = context;
        preferences =
                mContext.getSharedPreferences(mContext.getString(R.string.preference_key),
                        Context.MODE_PRIVATE);
        setStudentName(preferences.getString("name", ""));

        if (getStudentName().isEmpty()) {
            empty = true;
        } else {
            FileInputStream fis = mContext.openFileInput("timetable");
            ObjectInputStream is = new ObjectInputStream(fis);
            setTableCourses((ArrayList<TableCourse>) is.readObject());
            is.close();
            fis.close();
        }
    }

    public boolean isEmpty() {
        return empty;
    }

    public int calcWeek() {
        setCurrentWeek(preferences.getInt("current_week", 1));
        Calendar now = Calendar.getInstance();
        now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE));
        now.setFirstDayOfWeek(Calendar.MONDAY);
        int year = preferences.getInt("year", now.get(Calendar.YEAR));
        int month = preferences.getInt("month", now.get(Calendar.MONTH));
        int date = preferences.getInt("date", now.get(Calendar.DATE));
        Calendar thatDay = Calendar.getInstance();
        thatDay.setFirstDayOfWeek(Calendar.MONDAY);
        thatDay.set(year, month, date, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE));
        int diffWeek = now.get(Calendar.WEEK_OF_YEAR) - thatDay.get(Calendar.WEEK_OF_YEAR);
        setCurrentWeek(getCurrentWeek() + diffWeek);
        if (getCurrentWeek() <= 0) {
            setCurrentWeek(1);
        }
        setShownWeek(getCurrentWeek());

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("current_week", getCurrentWeek());
        editor.putInt("year", now.get(Calendar.YEAR));
        editor.putInt("month", now.get(Calendar.MONTH));
        editor.putInt("date", now.get(Calendar.DATE));
        editor.apply();

        return getCurrentWeek();
    }

    public ArrayList<TimeTableView.Rectangle> parseTable(int week) {

        ArrayList<TimeTableView.Rectangle> classes = new ArrayList<>();

        setWeekCount(0);

        for (TableCourse course : getTableCourses()) {
            String s = course.getLocationTime();
            int start = 0;
            int index = s.indexOf("周");
            while (index != -1) {
                String weekPart = s.substring(start, index).trim();
                String[] weekParts = weekPart.split(",");
                boolean isIn = false;
                for (String part : weekParts) {
                    int si = part.indexOf("-");
                    if (si == -1) {
                        int week1 = Integer.valueOf(part);
                        if (week1 > getWeekCount()) {
                            setWeekCount(week1);
                        }
                        if (week == week1) {
                            isIn = true;
                            break;
                        }
                    } else {
                        int week1 = Integer.valueOf(part.substring(0, si).trim());
                        int week2 = Integer.valueOf(part.substring(si + 1).trim());
                        if (week2 > getWeekCount()) {
                            setWeekCount(week2);
                        }
                        if (week <= week2 && week >= week1) {
                            isIn = true;
                            break;
                        }
                    }
                }


                if (isIn) {
                    int day = 0;
                    int startN = 0, endN = 0;

                    start = index + 1;


                    if (s.substring(start, start + 1).equals("(")) {
                        isIn = false;
                        // 单双周
                        if ((s.substring(start + 1, start + 2).equals("单") && week % 2 == 1) || (s.substring(start + 1, start + 2).equals("双") && week % 2 == 0)) {
                            start = s.indexOf(")", start) + 1;
                            isIn = true;
                        }
                    }

                    if (isIn) {

                        index = s.indexOf("[", start);
                        String dayPart = s.substring(start, index).trim();
                        switch (dayPart) {
                            case "一":
                                day = 0;
                                break;
                            case "二":
                                day = 1;
                                break;
                            case "三":
                                day = 2;
                                break;
                            case "四":
                                day = 3;
                                break;
                            case "五":
                                day = 4;
                                break;
                            case "六":
                                day = 5;
                                break;
                            default:
                                day = 6;
                                break;
                        }

                        start = index + 1;
                        index = s.indexOf("]", start);
                        String nPart = s.substring(start, index);
                        if (nPart.contains("-")) {
                            String[] nParts = nPart.split("-");
                            startN = Integer.valueOf(nParts[0]) - 1;
                            endN = Integer.valueOf(nParts[1]) - 1;
                        } else {
                            startN = Integer.valueOf(nPart) - 1;
                            endN = Integer.valueOf(nPart) - 1;
                        }

                        start = index + 1;
                        index = s.indexOf(",", start);
                        String loc = "";
                        if (index == -1) {
                            loc = s.substring(start);
                        } else {
                            loc = s.substring(start, index);
                        }

                        TimeTableView.Rectangle info = new TimeTableView.Rectangle(course.getName()
                                + "\n\n" + course.getTeacher() + "\n" + loc, day, startN, endN);
                        info.name = course.getName();
                        info.teacher = course.getTeacher();
                        info.loc = loc;
                        classes.add(info);

                        if (index == -1) {
                            break;
                        }
                        start = index + 1;
                    }
                }

                if (!isIn) {
                    start = s.indexOf(",", index + 1) + 1;
                    if (start == 0) {
                        break;
                    }
                }

                index = s.indexOf("周", start);

            }
        }

        return classes;
    }

    public String tableToString() {
        String res = "{\"name\":\"" + getStudentName() + "\",\"table\":[";
        for (TableCourse c : getTableCourses()) {
            res += c.toString() + ",";
        }
        return res.substring(0, res.length() - 1) + "]}";
    }

    public String tableFromString(String s) throws JSONException {
        tableCourses.clear();
        JSONObject jsonObject = new JSONObject(s);
        String name = jsonObject.getString("name");
        JSONArray arr = jsonObject.getJSONArray("table");
        for (int i = 0; i < arr.length(); ++i) {
            JSONObject o = arr.getJSONObject(i);
            TableCourse tc = new TableCourse();
            tc.setCode(o.getString("code"));
            tc.setTeacher(o.getString("teacher"));
            tc.setLocationTime(o.getString("locationTime"));
            tc.setCredit(o.getString("credit"));
            tc.setName(o.getString("name"));
            tableCourses.add(tc);
        }
        return name;
    }

    public ArrayList<TableCourse> getTableCourses() {
        return tableCourses;
    }

    public void setTableCourses(ArrayList<TableCourse> tableCourses) {
        this.tableCourses = tableCourses;
    }

    public int getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(int weekCount) {
        this.weekCount = weekCount;
    }

    public int getCurrentWeek() {
        return currentWeek;
    }

    public void setCurrentWeek(int currentWeek) {
        this.currentWeek = currentWeek;
    }

    public int getShownWeek() {
        return shownWeek;
    }

    public void setShownWeek(int shownWeek) {
        this.shownWeek = shownWeek;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
