package cn.xuhongxu.xiaoya.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.xuhongxu.Assist.Semester;
import cn.xuhongxu.Assist.TableCourse;
import cn.xuhongxu.xiaoya.R;
import cn.xuhongxu.xiaoya.View.TimeTableView;
import cn.xuhongxu.xiaoya.View.YaHorizontalScrollView;
import cn.xuhongxu.xiaoya.View.YaScrollView;
import cn.xuhongxu.xiaoya.YaApplication;

public class TimetableActivity extends AppCompatActivity {

    TextView title;
    TimeTableView table;
    YaApplication app;
    private static final int LOGIN_REQUEST = 1;
    private ArrayList<Semester> semesterList;
    private ArrayList<TableCourse> tableCourses;
    private HashSet<String> history;

    private SharedPreferences preferences;

    private int weekCount = 0;
    private int currentWeek = 0;
    private int shownWeek = 0;

    String studentName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AVAnalytics.trackAppOpened(getIntent());

        setContentView(R.layout.activity_timetable);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences =
                getSharedPreferences(getString(R.string.preference_key),
                        Context.MODE_PRIVATE);

        history = (HashSet<String>) preferences.getStringSet("history", new HashSet<String>());
        studentName = preferences.getString("name", "");


        app = (YaApplication) getApplication();

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TimetableActivity.this);
                builder.setTitle(R.string.choose_week);
                CharSequence[] items = new CharSequence[weekCount];
                for (int i = 1; i <= weekCount; ++i) {
                    items[i - 1] = getString(R.string.prefix_week) + i + getString(R.string.suffix_week);
                    if (i == currentWeek) {
                        items[i - 1] = items[i - 1] + " " + getString(R.string.is_current);
                    }
                    if (i == shownWeek) {
                        items[i - 1] = items[i - 1] + " " + getString(R.string.is_shown);
                    }
                }
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        shownWeek = i + 1;
                        parseTable(shownWeek);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                Dialog dialog = builder.create();
                dialog.show();
            }
        });

        table = (TimeTableView) findViewById(R.id.timetable);
        title = (TextView) findViewById(R.id.timetable_title);

        if (studentName.isEmpty()) {
            Snackbar.make(findViewById(R.id.timetable_layout), R.string.please_import_timetable, Snackbar.LENGTH_LONG).show();
        } else {
            try {
                FileInputStream fis = openFileInput("timetable");
                ObjectInputStream is = new ObjectInputStream(fis);
                tableCourses = (ArrayList<TableCourse>) is.readObject();
                is.close();
                fis.close();
                parseTable(calcWeek());
            } catch (Exception e) {
                e.printStackTrace();
                Snackbar.make(findViewById(R.id.timetable_layout), R.string.please_import_timetable, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private int px(float dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }

    private int calcWeek() {
        currentWeek = preferences.getInt("current_week", 1);
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
        currentWeek += diffWeek;
        if (currentWeek <= 0) {
            currentWeek = 1;
        }
        shownWeek = currentWeek;

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("current_week", currentWeek);
        editor.putInt("year", now.get(Calendar.YEAR));
        editor.putInt("month", now.get(Calendar.MONTH));
        editor.putInt("date", now.get(Calendar.DATE));
        editor.apply();

        return currentWeek;
    }

    private void parseTable(int week) {

        if (week == currentWeek) {
            title.setText(getString(R.string.prefix_week) + week + getString(R.string.suffix_week) + " " + getString(R.string.is_current));
        } else {
            title.setText(getString(R.string.prefix_week) + week + getString(R.string.suffix_week));
        }

        List<TimeTableView.Rectangle> classes = new ArrayList<>();

        weekCount = 0;

        for (TableCourse course : tableCourses) {
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
                        if (week1 > weekCount) {
                            weekCount = week1;
                        }
                        if (week == week1) {
                            isIn = true;
                            break;
                        }
                    } else {
                        int week1 = Integer.valueOf(part.substring(0, si).trim());
                        int week2 = Integer.valueOf(part.substring(si + 1).trim());
                        if (week2 > weekCount) {
                            weekCount = week2;
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
                        String[] nParts = nPart.split("-");
                        startN = Integer.valueOf(nParts[0]) - 1;
                        endN = Integer.valueOf(nParts[1]) - 1;

                        start = index + 1;
                        index = s.indexOf(",", start);
                        String loc = "";
                        if (index == -1) {
                            loc = s.substring(start);
                        } else {
                            loc = s.substring(start, index);
                        }

                        classes.add(new TimeTableView.Rectangle(course.getName()
                                + "\n\n" + course.getTeacher() + "\n" + loc, day, startN, endN));

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

        table.setClasses(classes);
        table.invalidate();
    }

    private class GetTableTask extends AsyncTask<Semester, Void, String> {

        @Override
        protected String doInBackground(Semester... params) {
            try {
                studentName = app.getAssist().getStudentDetails().getName();
                tableCourses = app.getAssist().getTableCourses(params[0]);
            } catch (Exception e) {
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                try {
                    FileOutputStream fos = TimetableActivity.this.openFileOutput("timetable", Context.MODE_PRIVATE);
                    ObjectOutputStream os = new ObjectOutputStream(fos);
                    os.writeObject(tableCourses);
                    os.close();
                    fos.close();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("name", studentName);
                    editor.remove("current_week");
                    editor.remove("year");
                    editor.remove("month");
                    editor.remove("date");
                    editor.remove("share_code");
                    editor.apply();
                    parseTable(calcWeek());
                    Snackbar.make(findViewById(R.id.timetable_layout), R.string.import_success, Snackbar.LENGTH_LONG).show();
                } catch (Exception e) {
                    Snackbar.make(findViewById(R.id.timetable_layout), R.string.write_error, Snackbar.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else {
                Snackbar.make(findViewById(R.id.timetable_layout), R.string.network_error, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private class GetSemesterTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                semesterList = app.getAssist().getSemesters();
            } catch (Exception e) {
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null && semesterList != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TimetableActivity.this);
                builder.setTitle(R.string.choose_term);
                CharSequence[] items = new CharSequence[semesterList.size()];
                int i = 0;
                for (Semester semester : semesterList) {
                    items[i++] = semester.getName();
                }
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Semester semester = semesterList.get(i);
                        new GetTableTask().execute(semester);
                    }
                });
                Dialog dialog = builder.create();
                dialog.show();
            } else {
                Snackbar.make(findViewById(R.id.timetable_layout), R.string.network_error, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_REQUEST) {
            if (resultCode == RESULT_OK) {
                new GetSemesterTask().execute();
            } else {
                Snackbar.make(findViewById(R.id.timetable_layout), R.string.login_error, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.timetable, menu);
        return true;
    }

    private String tableToString() {
        String res = "{\"name\":\"" + studentName + "\",\"table\":[";
        for (TableCourse c : tableCourses) {
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

    void importShareCode(String shareCode) {
        int i = shareCode.indexOf("：");
        if (i != -1) {
            shareCode = shareCode.substring(i + 1).trim();
        }
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>("TimeTable");
        final String finalShareCode = shareCode;
        avQuery.getInBackground(shareCode, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                try {

                    studentName = tableFromString(avObject.getString("Content"));

                    history.add(studentName + " " + avObject.getObjectId());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putStringSet("history", history);
                    editor.apply();

                    FileOutputStream fos = TimetableActivity.this.openFileOutput("timetable", Context.MODE_PRIVATE);
                    ObjectOutputStream os = new ObjectOutputStream(fos);
                    os.writeObject(tableCourses);
                    os.close();
                    fos.close();

                    editor.putString("name", studentName);
                    editor.remove("current_week");
                    editor.remove("year");
                    editor.remove("month");
                    editor.remove("date");
                    editor.putString("share_code", finalShareCode);
                    editor.apply();

                    parseTable(calcWeek());
                    Snackbar.make(findViewById(R.id.timetable_layout), R.string.import_success, Snackbar.LENGTH_LONG).show();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    Snackbar.make(findViewById(R.id.timetable_layout), R.string.import_error, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_import) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.import_timetable);
            builder.setItems(R.array.import_timetable, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    AVAnalytics.onEvent(getApplicationContext(), getString(R.string.action_import));
                    if (i == 0) {
                        // 教务网
                        if (app.getAssist() == null) {
                            // 登录
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.putExtra("justLogin", true);
                            startActivityForResult(intent, LOGIN_REQUEST);
                        } else {
                            new GetSemesterTask().execute();
                        }
                    } else {
                        // 分享码
                        AlertDialog.Builder builder = new AlertDialog.Builder(TimetableActivity.this);
                        builder.setTitle(R.string.import_timetable);

                        final EditText input = new EditText(TimetableActivity.this);
                        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        builder.setView(input);

                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String shareCode = input.getText().toString();
                                importShareCode(shareCode);
                            }
                        });
                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }
                }

            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            Dialog dialog = builder.create();
            dialog.show();

        } else if (id == R.id.action_set_current) {
            preferences =
                    getSharedPreferences(getString(R.string.preference_key),
                            Context.MODE_PRIVATE);
            currentWeek = shownWeek;
            parseTable(shownWeek);
            Calendar now = Calendar.getInstance();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("current_week", currentWeek);
            editor.putInt("year", now.get(Calendar.YEAR));
            editor.putInt("month", now.get(Calendar.MONTH));
            editor.putInt("date", now.get(Calendar.DATE));
            editor.apply();
        } else if (id == R.id.action_share) {
            // 分享
            String shareCode = preferences.getString("share_code", "");
            if (shareCode.isEmpty()) {
                try {
                    final AVObject data = new AVObject("TimeTable");
                    data.put("Content", tableToString());
                    data.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("share_code", data.getObjectId());
                                editor.apply();
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.this_is_timetable_share_code) + "：" + data.getObjectId());
                                sendIntent.setType("text/plain");
                                startActivity(sendIntent);
                            } else {
                                Snackbar.make(findViewById(R.id.timetable_layout), R.string.share_error, Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Snackbar.make(findViewById(R.id.timetable_layout), R.string.share_error, Snackbar.LENGTH_LONG).show();
                }
            } else {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.this_is_timetable_share_code) + "：" + shareCode);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        } else if (id == R.id.action_import_history) {
           // TODO: history List
            AlertDialog.Builder builder = new AlertDialog.Builder(TimetableActivity.this);
            builder.setTitle(R.string.choose_history);
            CharSequence[] items = new CharSequence[history.size()];
            final ArrayList<String> historyArr = new ArrayList<>(history);
            int i = 0;
            int current = -1;
            for (String code : historyArr) {
                String name = code.split("\\s+")[0];
                if (name.equals(studentName)) {
                    current = i;
                    items[i++] = code.split("\\s+")[0] + " " + getString(R.string.is_current);
                } else {
                    items[i++] = code.split("\\s+")[0];
                }
            }
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String code = historyArr.get(i);
                    importShareCode(code.split("\\s+")[1]);
                }
            });
            final int finalCurrent = current;
            builder.setPositiveButton(R.string.delete_current, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (finalCurrent != -1) {
                        historyArr.remove(finalCurrent);
                        history = new HashSet<>(historyArr);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putStringSet("history", history);
                        editor.apply();
                        Snackbar.make(findViewById(R.id.timetable_layout), R.string.delete_ok, Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(findViewById(R.id.timetable_layout), R.string.no_current, Snackbar.LENGTH_LONG).show();
                    }
                }
            });
            Dialog dialog = builder.create();
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AVAnalytics.onResume(this);
    }
}
