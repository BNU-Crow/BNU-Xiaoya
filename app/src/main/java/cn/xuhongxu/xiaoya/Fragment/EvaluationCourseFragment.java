package cn.xuhongxu.xiaoya.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;

import java.io.IOException;

import cn.xuhongxu.Assist.EvaluationCourse;
import cn.xuhongxu.Assist.NeedLoginException;
import cn.xuhongxu.xiaoya.Adapter.EvaluationCourseRecycleAdapter;
import cn.xuhongxu.xiaoya.Listener.RecyclerItemClickListener;
import cn.xuhongxu.xiaoya.R;
import cn.xuhongxu.xiaoya.YaApplication;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class EvaluationCourseFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    private static final int EVALUATE_ALL = -1;
    private OnListFragmentInteractionListener mListener;

    private YaApplication app;
    private int itemPosition = 0;

    private GetEvaluatingCoursesTask getTask;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(R.string.Evaluate);
    }

    private class GetEvaluatingCoursesTask extends AsyncTask<Boolean, Void, Integer> {

        private boolean first = false;

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Integer doInBackground(Boolean... params) {
            if (params.length > 0) {
                first = params[0];
            }
            View view = getView();
            assert view != null;
            try {
                app.setEvaluationCourses(
                        app.getAssist().getEvaluatingCourses(
                                app.getEvaluationItemList().get(itemPosition)
                        )
                );
                return 0;
            } catch (NeedLoginException needLogin) {
                return R.string.login_timeout;
            } catch (IOException e) {
                return R.string.network_error;
            } catch (Exception e) {
                Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
            return -1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            progressBar.setIndeterminate(false);
            progressBar.setVisibility(View.GONE);
            if (result == 0) {
                updateItems(true, first);
            } else if (result == R.string.login_timeout || result == R.string.network_error) {
                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                mListener.onReLogin(false);
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void updateItems(boolean updated, boolean first) {
        if (updated) {
            EvaluationCourseRecycleAdapter adapter =
                    new EvaluationCourseRecycleAdapter(getActivity(), app.getEvaluationCourses());
            recyclerView.setAdapter(adapter);
        }
        View view = getView();
        if (view != null) {
            final SharedPreferences preferences =
                    getActivity().getSharedPreferences(getString(R.string.preference_key),
                            Context.MODE_PRIVATE);
            if (preferences.getBoolean("showEvaluationCourseTip", true)) {
                int showTextId = R.string.evaluate_tip;
                if (!first) {
                    showTextId = R.string.updated;
                }
                Snackbar snackbar = Snackbar.make(view, showTextId, Snackbar.LENGTH_SHORT);
                if (first) {
                    snackbar.setAction(R.string.do_not_show_again, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("showEvaluationCourseTip", false);
                            editor.apply();
                        }
                    });
                }
                snackbar.show();
            }
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EvaluationCourseFragment() {
    }

    @SuppressWarnings("unused")
    public static EvaluationCourseFragment newInstance(int pos) {
        EvaluationCourseFragment fragment = new EvaluationCourseFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, pos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_evaluation, container, false);

        app = (YaApplication) getActivity().getApplication();

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.evaluation_swipe_refresh_layout);
        recyclerView = (RecyclerView) v.findViewById(R.id.evaluation_recycler_view);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);

        app.getEvaluationCourses().clear();
        EvaluationCourseRecycleAdapter adapter =
                new EvaluationCourseRecycleAdapter(getActivity(), app.getEvaluationCourses());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        showEvaluateDialog(position, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog = ProgressDialog.show(getContext(),
                                        getString(R.string.evaluating),
                                        app.getEvaluationCourses().get(position).getName(), true);
                                new EvaluateTask().execute(position, 5 - which, 0);
                            }
                        });
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                }));

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        getTask = new GetEvaluatingCoursesTask();
        getTask.execute(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTask = new GetEvaluatingCoursesTask();
                getTask.execute(false);
            }

        });
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            itemPosition = getArguments().getInt(ARG_POSITION);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.evaluation_course_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            getTask = new GetEvaluatingCoursesTask();
            getTask.execute(false);
        } else if (id == R.id.action_evaluate_all) {
            showEvaluateDialog(EVALUATE_ALL, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AVAnalytics.onEvent(getContext(), getString(R.string.action_evaluate_all));
                    progressDialog = new ProgressDialog(getContext());
                    progressDialog.setTitle(getString(R.string.evaluating));
                    progressDialog.setMessage(getString(R.string.action_evaluate_all));
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.setMax(app.getEvaluationCourses().size());
                    progressDialog.show();
                    for (int pos = 0; pos < app.getEvaluationCourses().size(); ++pos) {
                        new EvaluateTask().execute(pos, 5 - which, -1);
                    }
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (getTask != null) {
            getTask.cancel(true);
        }
    }

    public interface OnListFragmentInteractionListener {
        void onReLogin(boolean back);
    }

    private class EvaluateTask extends AsyncTask<Integer, Void, Integer> {

        private boolean all = false;
        private int pos = 0;

        @Override
        protected Integer doInBackground(Integer... params) {
            if (params.length > 2) {
                all = params[2] == EVALUATE_ALL;
            }
            pos = params[0];
            View view = getView();
            assert view != null;
            try {
                app.getAssist().evaluateCourse(
                        app.getEvaluationItemList().get(itemPosition),
                        app.getEvaluationCourses().get(params[0]),
                        params[1]
                );
                return 0;
            } catch (NeedLoginException needLogin) {
                return R.string.login_timeout;
            } catch (IOException e) {
                return R.string.network_error;
            } catch (Exception e) {
                Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
            return -1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == 0) {
                View view = getView();
                assert view != null;
                if (all) {
                    EvaluationCourse course = app.getEvaluationCourses().get(pos);
                    progressDialog.incrementProgressBy(1);
                    progressDialog.setMessage(getString(R.string.succeed_evaluate)
                                    + ": "
                                    + course.getName()
                                    + "("
                                    + course.getTeacherName()
                                    + ")");
                    if (pos != app.getEvaluationCourses().size() - 1) {
                        return;
                    }
                }
                progressDialog.dismiss();
                Snackbar.make(view, R.string.succeed_evaluate, Snackbar.LENGTH_SHORT).show();
                getTask = new GetEvaluatingCoursesTask();
                getTask.execute(true);
            } else if (result == R.string.login_timeout || result == R.string.network_error) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                mListener.onReLogin(false);
            }
        }
    }

    public void showEvaluateDialog(final int pos, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (pos == EVALUATE_ALL) {
            builder.setTitle(R.string.choose_all_evaluation_level);
        } else {
            EvaluationCourse course = app.getEvaluationCourses().get(pos);
            builder.setTitle(
                    getString(R.string.choose_evaluation_level) + ": "
                            + course.getName()
                            + "(" + course.getTeacherName() + ")");
        }
        builder.setItems(R.array.stars, listener);
        Dialog dialog = builder.create();
        dialog.show();
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
