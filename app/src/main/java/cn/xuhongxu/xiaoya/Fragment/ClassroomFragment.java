package cn.xuhongxu.xiaoya.Fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.xuhongxu.xiaoya.Adapter.RoomRecycleAdapter;
import cn.xuhongxu.xiaoya.Helper.Building;
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
    Spinner spinner;
    ArrayList<Building> buildings = new ArrayList<>();
    ArrayList<Room> roomList = new ArrayList<>();
    ArrayList<Room> shownRooms = new ArrayList<>();
    RoomRecycleAdapter adapter;
    ArrayAdapter<Building> spinnerAdapter;

    int start = 1, end = 2;
    int timeout = 30000;

    public ClassroomFragment() {
        // Required empty public constructor
    }

    boolean isInPeriod(int h, int m, int startH, int startM, int endH, int endM) {
        return !((h == startH && m < startM) || h < startH || h > endH || (h == endH && m >= endM));
    }


    private List<Building> getBuildings() {
        List<Building> buildings = new ArrayList<>();
        try {
            Connection.Response res =
                    Jsoup.connect("http://202.112.88.59:8082/buildings").method(Connection.Method.GET).execute();
            String body = res.body();
            String[] fields = body.split(",");
            for (int i = 0; i < fields.length; i += 2) {
                buildings.add(new Building(fields[i + 1], fields[i]));
            }
        } catch (Exception e) {
            e.printStackTrace();
            buildings.add(new Building(e.getLocalizedMessage(), "0"));
        }
        return buildings;
    }

    private List<Room> getRooms(String id) {
        List<Room> rooms = new ArrayList<>();
        try {
            Connection.Response res = Jsoup.connect("http://202.112.88.59:8082/building/" + id)
                    .method(Connection.Method.GET).execute();

            String body = res.body();
            String[] fields = body.split(";");
            for (String field : fields) {
                String[] roomInfo = field.trim().split(",");
                if (roomInfo.length != 17) continue;
                rooms.add(new Room(roomInfo[0], new boolean[]{
                        roomInfo[5].equals("1"),
                        roomInfo[6].equals("1"),
                        roomInfo[7].equals("1"),
                        roomInfo[8].equals("1"),
                        roomInfo[9].equals("1"),
                        roomInfo[10].equals("1"),
                        roomInfo[11].equals("1"),
                        roomInfo[12].equals("1"),
                        roomInfo[13].equals("1"),
                        roomInfo[14].equals("1"),
                        roomInfo[15].equals("1"),
                        roomInfo[16].equals("1"),
                }));
            }

        } catch (Exception e) {
            e.printStackTrace();
            rooms.add(new Room("加载失败", new boolean[12]));
        }
        return rooms;
    }

    private class QueryBuildingTask extends AsyncTask<String, Void, List<Building>> {

        @Override
        protected List<Building> doInBackground(String... strings) {
            return getBuildings();
        }

        @Override
        protected void onPostExecute(List<Building> result) {
            progressBar.setVisibility(GONE);
            buildings.clear();
            buildings.addAll(result);
            spinnerAdapter.notifyDataSetChanged();
            if (buildings.size() > 0)
                spinner.setSelection(0);
        }
    }

    private class QueryRoomTask extends AsyncTask<String, Void, List<Room>> {

        @Override
        protected List<Room> doInBackground(String... strings) {
            return getRooms(strings[0]);
        }

        @Override
        protected void onPostExecute(List<Room> result) {
            progressBar.setVisibility(GONE);
            roomList.clear();
            roomList.addAll(result);
            filterRooms();
        }
    }

    private void filterRooms() {
        shownRooms.clear();
        for (Room room : roomList) {
            boolean add = true;
            for (int i = start; i <= end; ++i) {
                if (!room.noCourse[i - 1]) {
                    add = false;
                    break;
                }
            }
            if (add) {
                shownRooms.add(room);
            }
        }
        adapter.reset();
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_classroom, container, false);

        multiSlider = (MultiSlider)v.findViewById(R.id.range_slider);
        multiSlider.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                if (thumbIndex == 0) {
                    start = value;
                } else {
                    end = value;
                }
                periodText.setText("查看 第 " + start + " - " + end + " 节");
                filterRooms();
            }
        });

        spinner = (Spinner) v.findViewById(R.id.spinner);
        periodText = (TextView) v.findViewById(R.id.currentPeriod);
        progressBar = (ProgressBar) v.findViewById(R.id.loading);
        recyclerView = (RecyclerView) v.findViewById(R.id.room_list);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new RoomRecycleAdapter(getContext(), shownRooms);
        recyclerView.setAdapter(adapter);

        spinnerAdapter = new ArrayAdapter<Building>(
                getContext(),
                R.layout.support_simple_spinner_dropdown_item,
                buildings
        );
        spinner.setAdapter(spinnerAdapter);

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
        } else {
            start = -1;
        }

        if (start == -1) {
            periodText.setText("今天的课已结束");
            start = 12;
        } else {
            periodText.setText("当前 第 " + start + " 节");
        }
        end = start + 1;
        if (end > 12) end = 12;

        multiSlider.getThumb(0).setValue(start);
        multiSlider.getThumb(1).setValue(end);
        periodText.setText("第 " + start + " - " + end + " 节");

        if (buildings.isEmpty()) {
            progressBar.setVisibility(VISIBLE);
            new QueryBuildingTask().execute();
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                progressBar.setVisibility(VISIBLE);
                new QueryRoomTask().execute(buildings.get(position).id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

}
