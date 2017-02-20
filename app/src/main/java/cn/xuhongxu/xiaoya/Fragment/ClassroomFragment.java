package cn.xuhongxu.xiaoya.Fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.xuhongxu.xiaoya.Adapter.RoomRecycleAdapter;
import cn.xuhongxu.xiaoya.Helper.Room;
import cn.xuhongxu.xiaoya.R;
import io.apptik.widget.MultiSlider;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClassroomFragment extends Fragment {

    MultiSlider multiSlider;
    RecyclerView recyclerView;
    TextView periodText;
    ProgressBar progressBar;
    Button queryButton;
    ArrayList<Room> roomList = new ArrayList<>();
    RoomRecycleAdapter adapter;

    int start = 1, end = 2;
    int timeout = 30000;

    class Info {
        public String xn = "", xq = "";
        public String week = "", day = "";
    }


    public ClassroomFragment() {
        // Required empty public constructor
    }

    boolean isInPeriod(int h, int m, int startH, int startM, int endH, int endM) {
        return !((h == startH && m < startM) || h < startH || h > endH || (h == endH && m >= endM));
    }

    ArrayList<Room> getRoom(Info info) {
        ArrayList<Room> rooms = new ArrayList<>();
        String p = "";
        for (int i = start; i <= end; ++i) {
            if (i < 10) {
                p += "0" + i;
            } else {
                p += i;
            }
            if (i != end) {
                p += ",";
            }
        }
        try {
            Document doc = Jsoup.connect("http://zyfw.prsc.bnu.edu.cn/public/dykb.kxjsi_data.gs1.jsp")
                    .timeout(timeout)
                    .data("hidweeks", info.day)
                    .data("hidjcs", p)
                    .data("hidMIN", "0")
                    .data("hidMAX", "800")
                    .data("sybm_m", "00")
                    .data("xn", info.xn)
                    .data("xn1", String.valueOf(Integer.valueOf(info.xn) + 1))
                    .data("xq_m", info.xq)
                    .data("sel_zc", info.week)
                    .data("selXQ", "0")
                    .data("selGS", "1")
                    .post();

            Element table = doc.getElementsByTag("tbody").first();
            if (table != null) {
                for (Element tr : table.getElementsByTag("tr")) {
                    try {
                        Room room = new Room();
                        room.building = tr.child(2).text();
                        room.rooms = tr.child(3).text();
                        rooms.add(room);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    Info getDate() {
        Info info = new Info();
        try {
            Connection.Response res = Jsoup.connect("http://zyfw.prsc.bnu.edu.cn/jw/common/showYearTerm.action")
                    .timeout(timeout)
                    .method(Connection.Method.POST)
                    .execute();
            JSONObject obj = new JSONObject(res.body());
            info.xn = obj.getString("xn");
            info.xq = obj.getString("xqM");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dddd", Locale.CHINA);
            res = Jsoup.connect("http://zyfw.prsc.bnu.edu.cn/public/getTeachingWeekByDate.action")
                    .timeout(timeout)
                    .data("xn", info.xn)
                    .data("xq_m", info.xq)
                    .data("hidOption", "getWeek")
                    .data("hdrq", df.format(new Date()))
                    .method(Connection.Method.POST)
                    .execute();
            String[] date = res.body().split("@");
            info.week = date[0];
            info.day = date[1];
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return info;
    }

    private class QueryTask extends AsyncTask<Void, Void, ArrayList<Room>> {

        @Override
        protected ArrayList<Room> doInBackground(Void... voids) {
            return getRoom(getDate());
        }

        @Override
        protected void onPostExecute(ArrayList<Room> result) {
            progressBar.setVisibility(GONE);
            roomList.clear();
            roomList.addAll(result);
            adapter.reset();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_classroom, container, false);


        periodText = (TextView)v.findViewById(R.id.period) ;
        multiSlider = (MultiSlider)v.findViewById(R.id.range_slider);
        queryButton = (Button)v.findViewById(R.id.query);
        progressBar = (ProgressBar) v.findViewById(R.id.loading);
        recyclerView = (RecyclerView) v.findViewById(R.id.room_list);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RoomRecycleAdapter(getContext(), roomList);
        recyclerView.setAdapter(adapter);

        multiSlider.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                if (thumbIndex == 0) {
                    start = value;
                } else {
                    end = value;
                }
                periodText.setText("第 " + start + " - " + end + " 节");
            }
        });

        Calendar calendar = Calendar.getInstance();
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);

        if (isInPeriod(h, m, 8, 0, 8, 45)) {
            start = 1;
        } else if (isInPeriod(h, m, 8, 45, 9, 40)) {
            start = 2;
        } else if (isInPeriod(h, m, 9, 40, 10, 45)) {
            start = 3;
        } else if (isInPeriod(h, m, 10, 45, 11, 40)) {
            start = 4;
        } else if (isInPeriod(h, m, 11, 40, 14, 15)) {
            start = 5;
        } else if (isInPeriod(h, m, 14, 15, 15, 10)) {
            start = 6;
        } else if (isInPeriod(h, m, 15, 10, 16, 15)) {
            start = 7;
        } else if (isInPeriod(h, m, 16, 15, 17, 10)) {
            start = 8;
        } else if (isInPeriod(h, m, 17, 10, 18, 45)) {
            start = 9;
        } else if (isInPeriod(h, m, 18, 45, 19, 40)) {
            start = 10;
        } else if (isInPeriod(h, m, 19, 40, 20, 35)) {
            start = 11;
        } else if (isInPeriod(h, m, 20, 35, 21, 30)) {
            start = 12;
        }

        end = start + 1;
        if (end > 12) end = 12;

        multiSlider.getThumb(0).setValue(start);
        multiSlider.getThumb(1).setValue(end);
        periodText.setText("第 " + start + " - " + end + " 节");

        if (roomList.isEmpty()) {
            progressBar.setVisibility(VISIBLE);
            new QueryTask().execute();
        }

        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(VISIBLE);
                new QueryTask().execute();
            }
        });

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
            roomList = savedInstanceState.getParcelableArrayList("ROOMS");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("ROOMS", roomList);
    }
}
