package cn.xuhongxu.xiaoya.Fragment;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.xuhongxu.Assist.ExamScore;
import cn.xuhongxu.Assist.NeedLoginException;
import cn.xuhongxu.xiaoya.Adapter.ScoreRecycleAdapter;
import cn.xuhongxu.xiaoya.Listener.RecyclerItemClickListener;
import cn.xuhongxu.xiaoya.R;
import cn.xuhongxu.xiaoya.YaApplication;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScoreFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScoreFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private YaApplication app;

    private GetExamScoreTask task;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    ProgressBar progressBar;

    private int year, term;
    private Button yearView;

    private String statistics;

    public ScoreFragment() {
        // Required empty public constructor
    }

    public static ScoreFragment newInstance() {
        return new ScoreFragment();
    }

    private class GetExamScoreTask extends AsyncTask<Boolean, Void, String> {
        private boolean first = false;

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(Boolean... params) {
            if (params.length > 0) {
                first = params[0];
            }
            View view = getView();
            assert view != null;
            try {
                if (year == 0) {
                    app.setExamScores(app.getAssist().getExamScores());
                } else {
                    app.setExamScores(app.getAssist().getExamScores(year, term));
                }
            } catch (NeedLoginException needLogin) {
                return getString(R.string.login_timeout);
            } catch (IOException e) {
                return getString(R.string.network_error);
            } catch (Exception e) {
                return e.getMessage();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setIndeterminate(false);
            progressBar.setVisibility(View.GONE);
            if (result.equals("")) {
                updateItems(true, first);
            } else if (result.equals(getString(R.string.login_timeout)) || result.equals(getString(R.string.network_error))) {
                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                mListener.onReLogin(false);
                swipeRefreshLayout.setRefreshing(false);
            } else {
                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void updateItems(boolean updated, boolean first) {
        if (updated) {
            ScoreRecycleAdapter adapter =
                    new ScoreRecycleAdapter(getActivity(), app.getExamScores());
            recyclerView.setAdapter(adapter);
        }

        View view = getView();
        if (view != null) {
            final SharedPreferences preferences =
                    getActivity().getSharedPreferences(getString(R.string.preference_key),
                            Context.MODE_PRIVATE);
            if (preferences.getBoolean("showScoreTip", true)) {
                int showTextId = R.string.select_exam_round;
                if (!first) {
                    showTextId = R.string.updated;
                }
                Snackbar snackbar = Snackbar.make(view, showTextId, Snackbar.LENGTH_SHORT);
                if (first) {
                    snackbar.setAction(R.string.do_not_show_again, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("showScoreTip", false);
                            editor.apply();
                        }
                    });
                }
                snackbar.show();
            }
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    void setTerm() {
        this.year = 0;
        this.term = 0;
        yearView.setText(R.string.all);
    }

    void setTerm(int year, int term) {
        this.year = year;
        this.term = term;
        yearView.setText("" + year + "-" + (year + 1) + getString(R.string.school_year) + "  " + (term == 1 ? "春季学期" : "秋季学期"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_score, container, false);

        app = (YaApplication) getActivity().getApplication();

        yearView = (Button) v.findViewById(R.id.scoreYear);

        final ArrayList<CharSequence> years = new ArrayList<>();
        years.add(getString(R.string.all));
        for (
                int i = Integer.valueOf(app.getStudentInfo().getGrade());
                i <= Integer.valueOf(app.getStudentInfo().getAcademicYear());
                ++i
                ) {
            years.add("" + i + "-" + (i + 1) + getString(R.string.school_year) + "  " + getString(R.string.fall_term));
            years.add("" + i + "-" + (i + 1) + getString(R.string.school_year) + "  " + getString(R.string.spring_term));
        }

        yearView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.choose_term);
                CharSequence[] items = new CharSequence[years.size()];
                years.toArray(items);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            setTerm();
                        } else {
                            String year = String.valueOf(years.get(i));
                            year = year.substring(0, year.indexOf("-"));
                            setTerm(Integer.valueOf(year), (i - 1) % 2);
                        }
                        task = new GetExamScoreTask();
                        task.execute(false);
                    }
                });
                Dialog dialog = builder.create();
                dialog.show();
            }
        });

        setTerm(Integer.valueOf(app.getStudentInfo().getAcademicYear()), Integer.valueOf(app.getStudentInfo().getSchoolTerm()));

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.score_swipe_refresh_layout);
        recyclerView = (RecyclerView) v.findViewById(R.id.score_recycler_view);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);
        ScoreRecycleAdapter adapter =
                new ScoreRecycleAdapter(getActivity(), app.getExamScores());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }

                }));
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        if (app.getExamScores().isEmpty()) {
            progressBar.setIndeterminate(true);
            task = new GetExamScoreTask();
            task.execute(true);
        } else {
            progressBar.setVisibility(View.GONE);
            updateItems(false, true);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                task = new GetExamScoreTask();
                task.execute(false);
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (task != null) {
            task.cancel(true);
        }
    }

    public interface OnFragmentInteractionListener {
        void onReLogin(boolean back);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.score, menu);
    }

    private String fmtD(Double d) {
        return String.format(Locale.getDefault(), "%.2f", d);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            task = new GetExamScoreTask();
            task.execute(false);
        } else if (id == R.id.action_gpa) {
            List<ExamScore> scores = app.getExamScores();
            Double gpa = 0.0, gpaP = 0.0;
            Double gpa4 = 0.0, gpa4P = 0.0;
            Double totalCredit = 0.0, totalCreditP = 0.0;
            int failedCount = 0, failedCountP = 0;
            int goodCount = 0, goodCountP = 0;
            int outCount = 0, outCountP = 0;
            for (ExamScore score : scores) {
                double s;
                double c;
                c = Double.valueOf(score.getCourseCredit());
                try {
                    s = Double.valueOf(score.getScore());
                } catch (Exception e) {
                    e.printStackTrace();
                    s = 85.0;
                }
                if (s >= 60) {
                    if (s >= 90) {
                        ++goodCount;
                        if (score.isProfessional()) {
                            ++goodCountP;
                        }
                    }
                    if (s == 100) {
                        ++outCount;
                        if (score.isProfessional()) {
                            ++outCountP;
                        }
                    }
                    int s4 = (int) ((s - 50) / 10);
                    if (s4 >= 5) {
                        s4 = 4;
                    }
                    gpa4 += s4 * c;
                    if (score.isProfessional()) {
                        gpa4P += s4 * c;
                    }
                } else {
                    ++failedCount;
                    if (score.isProfessional()) {
                        ++failedCountP;
                    }
                }
                gpa += s * c;
                if (score.isProfessional()) {
                    gpaP += s * c;
                }
                totalCredit += Double.valueOf(score.getCourseCredit());
                if (score.isProfessional()) {
                    totalCreditP += Double.valueOf(score.getCourseCredit());
                }
            }

            if (totalCredit == 0) totalCredit = 1.0;
            if (totalCreditP == 0) totalCreditP = 1.0;

            gpa /= totalCredit;
            gpa4 /= totalCredit;
            gpaP /= totalCreditP;
            gpa4P /= totalCreditP;

            final String dialogTitle = yearView.getText() + "  " + getString(R.string.score_statistics);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(dialogTitle);

            statistics = getString(R.string.all_course) + "\n\n" +
                    getString(R.string.average_GPA) + ": " + fmtD(gpa) + "\n" +
                    getString(R.string.standard_GPA) + ": " + fmtD(gpa * 4 / 100) + "\n" +
                    getString(R.string.four_GPA) + ": " + fmtD(gpa4) + "\n" +
                    getString(R.string.failed_count) + ": " + failedCount + "\n" +
                    getString(R.string.good_count) + ": " + goodCount + "\n" +
                    getString(R.string.out_count) + ": " + outCount + "\n\n\n" +
                    getString(R.string.professional_course) + "\n\n" +
                    getString(R.string.average_GPA) + ": " + fmtD(gpaP) + "\n" +
                    getString(R.string.standard_GPA) + ": " + fmtD(gpaP * 4 / 100) + "\n" +
                    getString(R.string.four_GPA) + ": " + fmtD(gpa4P) + "\n" +
                    getString(R.string.failed_count) + ": " + failedCountP + "\n" +
                    getString(R.string.good_count) + ": " + goodCountP + "\n" +
                    getString(R.string.out_count) + ": " + outCountP + "\n";
            builder.setMessage(statistics);
            builder.setPositiveButton(R.string.share, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, dialogTitle);
                    sendIntent.putExtra(Intent.EXTRA_TITLE, dialogTitle);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, dialogTitle + "\n\n" + statistics);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                }
            });
            Dialog dialog = builder.create();
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }
}
