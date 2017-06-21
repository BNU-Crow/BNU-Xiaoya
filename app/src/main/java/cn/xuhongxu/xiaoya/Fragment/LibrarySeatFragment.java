package cn.xuhongxu.xiaoya.Fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.xuhongxu.LibrarySeat.Reservation;
import cn.xuhongxu.LibrarySeat.ReservationHistory;
import cn.xuhongxu.LibrarySeat.SeatClient;
import cn.xuhongxu.xiaoya.Activity.ChooseSeatActivity;
import cn.xuhongxu.xiaoya.Activity.ToolboxActivity;
import cn.xuhongxu.xiaoya.Adapter.ReservationHistoryRecycleAdapter;
import cn.xuhongxu.xiaoya.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LibrarySeatFragment extends Fragment {

    public static final String LOGIN = "login";
    public static final String LOAD_RESERVATION = "load_reservation";
    public static final String CANCEL = "cancel";
    public static final String CANCEL_ID = "cancelid";
    public static final String CHECKIN = "checkin";
    public static final String LEAVE = "leave";
    public static final String STOP = "stop";

    SeatClient client;

    ProgressBar progressBar;
    RecyclerView recyclerView;
    private ReservationHistoryRecycleAdapter adapter;
    TextView receipt, startTime, endTime, date, status;

    Reservation currentReservation = null;
    List<ReservationHistory> reservationHistories = new ArrayList<>();

    public LibrarySeatFragment() {
        // Required empty public constructor
    }

    class QueryTask extends AsyncTask<String, Void, String> {

        private String action = "";

        @Override
        protected String doInBackground(String... params) {
            action = params[0];
            if (action.equals(LOGIN)) {
                return client.login();
            } else if (action.equals(LOAD_RESERVATION)) {
                currentReservation = client.getCurrentReservation();
                reservationHistories.clear();
                reservationHistories.addAll(client.getReservationHistory(1, 10));
            } else if(action.equals(CANCEL)) {
                return client.cancelReservation();
            } else if (action.equals(CANCEL_ID)) {
                return client.cancelReservation(params[1]);
            } else if (action.equals(CHECKIN)) {
                return client.checkIn();
            } else if (action.equals(LEAVE)) {
                return client.leave();
            } else if (action.equals(STOP)) {
                return client.stop();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.INVISIBLE);
            if (action.equals(LOGIN)) {
                if (s != null) {
                    Snackbar.make(getView(), s, Snackbar.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    new QueryTask().execute(LOAD_RESERVATION);
                }
            } else if (action.equals(LOAD_RESERVATION)) {
                receipt.setText(currentReservation.receipt);
                startTime.setText(currentReservation.begin);
                endTime.setText(currentReservation.end);
                date.setText(currentReservation.onDate);
                status.setText(currentReservation.status);
                adapter.notifyDataSetChanged();
            } else {
                if (s != null) {
                    Snackbar.make(getView(), s, Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }

    ToolboxActivity.LibraryLoginListener listener = new ToolboxActivity.LibraryLoginListener() {
        @Override
        public void logined(String usnm, String pwd) {
            client = new SeatClient(usnm, pwd);
            progressBar.setVisibility(View.VISIBLE);
            new QueryTask().execute(LOGIN);
        }
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_library_seat, container, false);


        SharedPreferences preferences =
                getActivity().getSharedPreferences(getString(R.string.library_key),
                        Context.MODE_PRIVATE);

        String username = preferences.getString("username", "");
        String password = preferences.getString("password", "");

        ToolboxActivity activity = (ToolboxActivity)getActivity();
        activity.addLibraryLoginListener(listener);

        progressBar = (ProgressBar) v.findViewById(R.id.loading);
        receipt = (TextView) v.findViewById(R.id.receipt);
        startTime = (TextView) v.findViewById(R.id.startTime);
        endTime = (TextView) v.findViewById(R.id.endTime);
        date = (TextView) v.findViewById(R.id.date);
        status = (TextView) v.findViewById(R.id.status);
        recyclerView = (RecyclerView)v.findViewById(R.id.history);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ReservationHistoryRecycleAdapter(getContext(), reservationHistories, history -> new QueryTask().execute(CANCEL_ID, history.id));
        recyclerView.setAdapter(adapter);

        if (!(username.isEmpty() && password.isEmpty())) {
            client = new SeatClient(username, password);
            progressBar.setVisibility(View.VISIBLE);
            new QueryTask().execute(LOGIN);
        }

        Button refresh = (Button) v.findViewById(R.id.refresh_button);
        Button cancel = (Button) v.findViewById(R.id.cancel_button);
        Button checkin = (Button) v.findViewById(R.id.checkin_button);
        Button leave = (Button) v.findViewById(R.id.leave_button);
        Button stop = (Button) v.findViewById(R.id.stop_button);
        Button choose = (Button) v.findViewById(R.id.choose_button);

        refresh.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            new QueryTask().execute(LOGIN);
        });

        cancel.setOnClickListener(view -> {
            if (client.getToken().isEmpty()) return;
            progressBar.setVisibility(View.VISIBLE);
            new QueryTask().execute(CANCEL);
        });
        checkin.setOnClickListener(view -> {
            if (client.getToken().isEmpty()) return;
            progressBar.setVisibility(View.VISIBLE);
            new QueryTask().execute(CHECKIN);
        });
        leave.setOnClickListener(view -> {
            if (client.getToken().isEmpty()) return;
            progressBar.setVisibility(View.VISIBLE);
            new QueryTask().execute(LEAVE);
        });
        stop.setOnClickListener(view -> {
            if (client.getToken().isEmpty()) return;
            progressBar.setVisibility(View.VISIBLE);
            new QueryTask().execute(STOP);
        });
        choose.setOnClickListener(view -> {
            if (client.getToken().isEmpty()) return;
            Intent intent = new Intent(getContext(), ChooseSeatActivity.class);
            intent.putExtra("Client", client.getToken());
            startActivity(intent);
        });

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ToolboxActivity activity = (ToolboxActivity) getActivity();
        activity.removeLibraryLoginListener(listener);
    }
}
