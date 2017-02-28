package cn.xuhongxu.xiaoya.Fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.avos.avoscloud.AVAnalytics;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.xuhongxu.Assist.LoginException;
import cn.xuhongxu.xiaoya.Activity.LoginActivity;
import cn.xuhongxu.xiaoya.Adapter.ExerciseRecordRecycleAdapter;
import cn.xuhongxu.xiaoya.Helper.ExerciseRecord;
import cn.xuhongxu.xiaoya.R;
import cn.xuhongxu.xiaoya.YaApplication;

import static cn.xuhongxu.Assist.SchoolworkAssist.CONTENT_TYPE;
import static cn.xuhongxu.Assist.SchoolworkAssist.HEADER_CONTENT_TYPE;
import static cn.xuhongxu.Assist.SchoolworkAssist.HEADER_REFERER;
import static cn.xuhongxu.Assist.SchoolworkAssist.REFERER;
import static cn.xuhongxu.Assist.SchoolworkAssist.USER_AGENT;
import static cn.xuhongxu.Assist.SchoolworkAssist.USER_AGENT_HEADER;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExerciseFragment extends Fragment {

    YaApplication app;
    private SharedPreferences preferences;
    View view = null;
    Map<String, String> cookies;
    int timeout = 10000;

    RecyclerView recyclerView;
    ProgressBar progressBar;
    Switch recordSwitch;
    ExerciseRecordRecycleAdapter adapter;

    String lt, excution;
    String username, password;
    ArrayList<ExerciseRecord> exerciseRecords = new ArrayList<>();
    ArrayList<ExerciseRecord> records = null;
    ArrayList<ExerciseRecord> allRecords = null;

    public static final String LOGIN_URL = "http://cas.bnu.edu.cn/cas/login?service" +
            "=http://jsty.bnu.edu.cn/cas/default.aspx";

    public ExerciseFragment() {
        // Required empty public constructor
    }

    private void fetchLoginParams() throws IOException {
        lt = "LT-NeusoftAlwaysValidTicket";
        excution = "e1s1";

        Connection.Response res = Jsoup.connect(LOGIN_URL).header(USER_AGENT_HEADER, USER_AGENT)
                .timeout(timeout).method(Connection.Method.GET).execute();
        if (res.statusCode() != 200) {
            throw new ConnectException("Failed to get login params");
        }
        cookies = res.cookies();
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

    }

    void login() {
        try {
            preferences =
                    getActivity().getSharedPreferences(getString(R.string.preference_key),
                            Context.MODE_PRIVATE);

            username = preferences.getString("username", "");
            if (preferences.getBoolean("remember", false)) {
                   password = preferences.getString("password", "");
            } else {
                if (view != null) {
                    Snackbar.make(view, R.string.please_login_first, Snackbar.LENGTH_LONG).show();
                }
            }

            fetchLoginParams();

            Connection.Response res = Jsoup.connect(LOGIN_URL)
                    .timeout(timeout)
                    .cookies(cookies)
                    .header(USER_AGENT_HEADER, USER_AGENT)
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE)
                    .data("username", username)
                    .data("password", password)
                    .data("code", "code")
                    .data("lt", lt)
                    .data("execution", excution)
                    .data("_eventId", "submit")
                    .method(Connection.Method.POST)
                    .execute();

            cookies.putAll(res.cookies());

            Document doc = res.parse();

            if (doc.outerHtml().contains("京师体育-学生刷卡达标记录")) {
                Element table = doc.getElementsByClass("jt").first();
                for (Element tr : table.getElementsByTag("tr")) {
                    if (tr.hasClass("ziti")) continue;
                    ExerciseRecord record = new ExerciseRecord();
                    record.date = tr.getElementsByTag("td").get(0).text();
                    record.building = tr.getElementsByTag("td").get(1).text();
                    record.enterTime = tr.getElementsByTag("td").get(2).text();
                    record.leaveTime = tr.getElementsByTag("td").get(3).text();
                    record.status = tr.getElementsByTag("td").get(4).text();
                    record.type = 0;
                    records.add(record);
                }
            }

            doc = Jsoup.connect("http://jsty.bnu.edu.cn/index.aspx")
                    .timeout(timeout)
                    .cookies(cookies)
                    .get();

            if (doc.outerHtml().contains("学生刷卡记录")) {
                Element table = doc.getElementsByClass("jt").first();
                for (Element tr : table.getElementsByTag("tr")) {
                    if (tr.hasClass("ziti")) continue;
                    ExerciseRecord record = new ExerciseRecord();
                    record.time = tr.getElementsByTag("td").get(4).text();
                    record.desp = tr.getElementsByTag("td").get(6).text();
                    record.type = 1;
                    allRecords.add(record);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class LoginTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... bools) {
            login();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);
            exerciseRecords.clear();
            exerciseRecords.addAll(records);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_exercise, container, false);
        view = v;
        recyclerView = (RecyclerView) v.findViewById(R.id.record_list);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ExerciseRecordRecycleAdapter(getContext(), exerciseRecords);
        recyclerView.setAdapter(adapter);
        progressBar = (ProgressBar) v.findViewById(R.id.loading);
        recordSwitch = (Switch) v.findViewById(R.id.record_switch);
        recordSwitch.setChecked(true);
        recordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                exerciseRecords.clear();
                if (b) {
                    exerciseRecords.addAll(records);
                } else {
                    exerciseRecords.addAll(allRecords);
                }
                adapter.notifyDataSetChanged();
            }
        });
        if (records != null && allRecords != null) {
            exerciseRecords.clear();
            exerciseRecords.addAll(records);
            adapter.notifyDataSetChanged();
        } else {
            records = new ArrayList<>();
            allRecords = new ArrayList<>();
            progressBar.setVisibility(View.VISIBLE);
            new LoginTask().execute();
        }
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        AVAnalytics.onFragmentEnd(getClass().getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        AVAnalytics.onFragmentStart(getClass().getName());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            records = savedInstanceState.getParcelableArrayList("RECORDS");
            allRecords = savedInstanceState.getParcelableArrayList("ALL_RECORDS");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("RECORDS", records);
        outState.putParcelableArrayList("ALL_RECORDS", allRecords);
    }
}
