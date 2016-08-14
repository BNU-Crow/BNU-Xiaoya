package cn.xuhongxu.xiaoya.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.xuhongxu.Assist.Semester;
import cn.xuhongxu.Assist.TableCourse;
import cn.xuhongxu.xiaoya.Model.TimeTable;
import cn.xuhongxu.xiaoya.R;
import cn.xuhongxu.xiaoya.View.TimeTableView;
import cn.xuhongxu.xiaoya.View.YaHorizontalScrollView;
import cn.xuhongxu.xiaoya.View.YaScrollView;
import cn.xuhongxu.xiaoya.YaApplication;

public class TimetableActivity extends AppCompatActivity {

    YaScrollView classScroll, numberScroll;
    YaHorizontalScrollView classHScroll, weekScroll;
    TextView title;
    TimeTableView table;
    YaApplication app;
    private static final int LOGIN_REQUEST = 1;
    private ArrayList<Semester> semesterList;
    private ArrayList<TableCourse> tableCourses;

    private SharedPreferences preferences;

    private int weekCount = 0;
    private int currentWeek = 0;
    private int shownWeek = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bmob.initialize(this, getString(R.string.bmob_key));
        BmobPush.startWork(this);

        preferences =
                getSharedPreferences(getString(R.string.preference_key),
                        Context.MODE_PRIVATE);

        app = (YaApplication) getApplication();

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TimetableActivity.this);
                builder.setTitle(R.string.choose_week);
                CharSequence[] items = new CharSequence[weekCount];
                for (int i = 1; i <= weekCount; ++i) {
                    if (i == currentWeek) {
                        items[i - 1] = getString(R.string.prefix_week) + i + getString(R.string.suffix_week) + " " + getString(R.string.is_current);
                    } else {
                        items[i - 1] = getString(R.string.prefix_week) + i + getString(R.string.suffix_week);
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

        weekScroll = (YaHorizontalScrollView) findViewById(R.id.weekScroll);
        numberScroll = (YaScrollView) findViewById(R.id.numberScroll);
        classScroll = (YaScrollView) findViewById(R.id.classScroll);
        classHScroll = (YaHorizontalScrollView) findViewById(R.id.classHScroll);

        weekScroll.setOnScrollListener(new YaHorizontalScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(int x, int y, int oldX, int oldY) {
                if (classHScroll.getScrollX() != x) {
                    classHScroll.setScrollX(x);
                }
            }
        });

        numberScroll.setOnScrollListener(new YaScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(int x, int y, int oldX, int oldY) {
                if (classScroll.getScrollY() != y) {
                    classScroll.setScrollY(y);
                }
            }
        });

        classScroll.setOnScrollListener(new YaScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(int x, int y, int oldX, int oldY) {
                if (y >= oldY) {
                    fab.hide();
                } else {
                    fab.show();
                }
                if (numberScroll.getScrollY() != y) {
                    numberScroll.setScrollY(y);
                }
            }
        });

        classHScroll.setOnScrollListener(new YaHorizontalScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(int x, int y, int oldX, int oldY) {
                if (x >= oldX) {
                    fab.hide();
                } else {
                    fab.show();
                }
                if (weekScroll.getScrollX() != x) {
                    weekScroll.setScrollX(x);
                }
            }
        });

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

    private int calcWeek() {
        currentWeek = preferences.getInt("current_week", 1);
        Calendar now = Calendar.getInstance();
        now.setFirstDayOfWeek(Calendar.MONDAY);
        int year = preferences.getInt("year", now.get(Calendar.YEAR));
        int month = preferences.getInt("month", now.get(Calendar.MONTH));
        int date = preferences.getInt("date", now.get(Calendar.DATE));
        Calendar thatDay = Calendar.getInstance();
        thatDay.setFirstDayOfWeek(Calendar.MONDAY);
        thatDay.set(year, month, date);
        int diffWeek = now.get(Calendar.WEEK_OF_YEAR) - thatDay.get(Calendar.WEEK_OF_YEAR);
        currentWeek += diffWeek;
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

        table.classes.clear();

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
                    index = s.indexOf("[", start);
                    String dayPart = s.substring(start, index).trim();
                    if (dayPart.equals("一")) {
                        day = 0;
                    } else if (dayPart.equals("二")) {
                        day = 1;
                    } else if (dayPart.equals("三")) {
                        day = 2;
                    } else if (dayPart.equals("四")) {
                        day = 3;
                    } else if (dayPart.equals("五")) {
                        day = 4;
                    } else if (dayPart.equals("六")) {
                        day = 5;
                    } else {
                        day = 6;
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

                    table.classes.add(new TimeTableView.Rectangle(course.getName()
                            + "\n\n" + course.getTeacher() + "\n" + loc, day, startN, endN));

                    if (index == -1) {
                        break;
                    }
                    start = index + 1;

                } else {
                    start = s.indexOf(",", index + 1) + 1;
                    if (start == 0) {
                        break;
                    }
                }

                index = s.indexOf("周", start);

            }
        }

        table.invalidate();
    }

    private class GetTableTask extends AsyncTask<Semester, Void, String> {

        @Override
        protected String doInBackground(Semester... params) {
            try {
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
                    editor.remove("current_week");
                    editor.remove("year");
                    editor.remove("month");
                    editor.remove("date");
                    editor.remove("share_code");
                    editor.apply();
                    parseTable(calcWeek());
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

    private static Object fromString(String s) throws IOException,
            ClassNotFoundException {
        byte[] data = Base64.decode(s, Base64.DEFAULT);
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }

    private static String toString(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
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
                                int i = shareCode.indexOf("：");
                                if (i != -1) {
                                    shareCode = shareCode.substring(i + 1);
                                }
                                BmobQuery<TimeTable> query = new BmobQuery<>();
                                final String finalShareCode = shareCode;
                                query.getObject(shareCode, new QueryListener<TimeTable>() {
                                    @Override
                                    public void done(TimeTable timeTable, BmobException e) {
                                        if (e == null) {
                                            try {
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString("share_code", finalShareCode);
                                                editor.remove("current_week");
                                                editor.remove("year");
                                                editor.remove("month");
                                                editor.remove("date");
                                                editor.apply();
                                                tableCourses = (ArrayList<TableCourse>) fromString(timeTable.getContent());
                                                parseTable(calcWeek());
                                            } catch (Exception e1) {
                                                e1.printStackTrace();
                                                Snackbar.make(findViewById(R.id.timetable_layout), R.string.import_error, Snackbar.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Snackbar.make(findViewById(R.id.timetable_layout), R.string.import_error, Snackbar.LENGTH_LONG).show();
                                        }
                                    }
                                });
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
            String shareCode = preferences.getString("share_code", "");
            if (shareCode.isEmpty()) {
                try {
                    TimeTable data = new TimeTable();
                    data.setContent(toString(tableCourses));
                    data.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("share_code", s);
                                editor.apply();
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.this_is_timetable_share_code) + "：" + s);
                                sendIntent.setType("text/plain");
                                startActivity(sendIntent);
                            } else {
                                Snackbar.make(findViewById(R.id.timetable_layout), R.string.share_error, Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                } catch (IOException e) {
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
        }

        return super.onOptionsItemSelected(item);
    }
}
