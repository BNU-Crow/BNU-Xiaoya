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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import cn.xuhongxu.Assist.Semester;
import cn.xuhongxu.Assist.TableCourse;
import cn.xuhongxu.xiaoya.Helper.TimetableHelper;
import cn.xuhongxu.xiaoya.R;
import cn.xuhongxu.xiaoya.View.TimeTableView;
import cn.xuhongxu.xiaoya.YaApplication;

public class TimetableActivity extends AppCompatActivity {

    TextView title;
    TimeTableView table;
    YaApplication app;
    private static final int LOGIN_REQUEST = 1;
    private ArrayList<Semester> semesterList;
    private HashSet<String> history;

    private SharedPreferences preferences;

    TimetableHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timetable);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            helper = new TimetableHelper(this);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        preferences =
                getSharedPreferences(getString(R.string.preference_key),
                        Context.MODE_PRIVATE);

        history = (HashSet<String>) preferences.getStringSet("history", new HashSet<String>());

        app = (YaApplication) getApplication();

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TimetableActivity.this);
                builder.setTitle(R.string.choose_week);
                CharSequence[] items = new CharSequence[helper.getWeekCount()];
                for (int i = 1; i <= helper.getWeekCount(); ++i) {
                    items[i - 1] = getString(R.string.prefix_week) + i + getString(R.string.suffix_week);
                    if (i == helper.getCurrentWeek()) {
                        items[i - 1] = items[i - 1] + " " + getString(R.string.is_current);
                    }
                    if (i == helper.getShownWeek()) {
                        items[i - 1] = items[i - 1] + " " + getString(R.string.is_shown);
                    }
                }
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        helper.setShownWeek(i + 1);
                        parseTable(helper.getShownWeek());
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


        if (helper.isEmpty()) {
            Snackbar.make(findViewById(R.id.timetable_layout), R.string.please_import_timetable, Snackbar.LENGTH_LONG).show();
        } else {
            parseTable(helper.calcWeek());
        }

    }

    private void parseTable(int week) {
        if (week == helper.getCurrentWeek()) {
            title.setText(getString(R.string.prefix_week) + week + getString(R.string.suffix_week) + " " + getString(R.string.is_current));
        } else {
            title.setText(getString(R.string.prefix_week) + week + getString(R.string.suffix_week));
        }

        table.setClasses(helper.parseTable(week));
        table.invalidate();
    }

    private class GetTableTask extends AsyncTask<Semester, Void, String> {

        @Override
        protected String doInBackground(Semester... params) {
            try {
                helper.setStudentName(app.getAssist().getStudentDetails().getName());
                helper.setTableCourses(app.getAssist().getTableCourses(params[0]));
            } catch (Exception e) {
                e.printStackTrace();
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
                    os.writeObject(helper.getTableCourses());
                    os.close();
                    fos.close();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("name", helper.getStudentName());
                    editor.remove("current_week");
                    editor.remove("year");
                    editor.remove("month");
                    editor.remove("date");
                    editor.remove("share_code");
                    editor.apply();
                    parseTable(helper.calcWeek());
                    Snackbar.make(findViewById(R.id.timetable_layout), R.string.import_success, Snackbar.LENGTH_LONG).show();
                } catch (Exception e) {
                    Snackbar.make(findViewById(R.id.timetable_layout), R.string.write_error, Snackbar.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else {
                Log.e("err:", result);
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
                e.printStackTrace();
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
                Log.e("err:", result);
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


    void importShareCode(String shareCode, boolean resetCurrentWeek) {
        int i = shareCode.indexOf("：");
        if (i != -1) {
            shareCode = shareCode.substring(i + 1).trim();
        }
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>("TimeTable");
        final String finalShareCode = shareCode;
        final boolean reset = resetCurrentWeek;
        avQuery.getInBackground(shareCode, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                try {

                    helper.setStudentName(helper.tableFromString(avObject.getString("Content")));

                    history.add(helper.getStudentName() + " " + avObject.getObjectId());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putStringSet("history", history);
                    editor.apply();

                    FileOutputStream fos = TimetableActivity.this.openFileOutput("timetable", Context.MODE_PRIVATE);
                    ObjectOutputStream os = new ObjectOutputStream(fos);
                    os.writeObject(helper.getTableCourses());
                    os.close();
                    fos.close();

                    editor.putString("name", helper.getStudentName());
                    if (reset) {
                        editor.remove("current_week");
                        editor.remove("year");
                        editor.remove("month");
                        editor.remove("date");
                    }
                    editor.putString("share_code", finalShareCode);
                    editor.apply();

                    parseTable(helper.calcWeek());
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
                                importShareCode(shareCode, true);
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
            if (!helper.isEmpty()) {
                helper.setCurrentWeek(helper.getShownWeek());
                parseTable(helper.getShownWeek());
                Calendar now = Calendar.getInstance();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("current_week", helper.getCurrentWeek());
                editor.putInt("year", now.get(Calendar.YEAR));
                editor.putInt("month", now.get(Calendar.MONTH));
                editor.putInt("date", now.get(Calendar.DATE));
                editor.apply();
            }
        } else if (id == R.id.action_share) {
            // 分享
            if (!helper.isEmpty()) {
                String shareCode = preferences.getString("share_code", "");
                if (shareCode.isEmpty()) {
                    try {
                        final AVObject data = new AVObject("TimeTable");
                        data.put("Content", helper.tableToString());
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
            }
        } else if (id == R.id.action_import_history) {
            AlertDialog.Builder builder = new AlertDialog.Builder(TimetableActivity.this);
            builder.setTitle(R.string.choose_history);
            CharSequence[] items = new CharSequence[history.size()];
            final ArrayList<String> historyArr = new ArrayList<>(history);
            int i = 0;
            int current = -1;
            for (String code : historyArr) {
                if (code.equals(helper.getStudentName() + " " + preferences.getString("share_code", ""))) {
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
                    importShareCode(code.split("\\s+")[1], false);
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
}
