package cn.xuhongxu.xiaoya.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;

import java.io.IOException;

import cn.xuhongxu.Assist.NeedLoginException;
import cn.xuhongxu.Assist.PlanChildCourse;
import cn.xuhongxu.Assist.PlanCourse;
import cn.xuhongxu.Assist.Result;
import cn.xuhongxu.xiaoya.Adapter.ExamArragementRecycleAdapter;
import cn.xuhongxu.xiaoya.Adapter.PlanChildCourseRecycleAdapter;
import cn.xuhongxu.xiaoya.Listener.RecyclerItemClickListener;
import cn.xuhongxu.xiaoya.R;
import cn.xuhongxu.xiaoya.YaApplication;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PlanChildCourseFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    private OnListFragmentInteractionListener mListener;

    private YaApplication app;
    private int itemPosition = 0;
    private PlanCourse planCourse;

    private GetPlanChildCourseTask getTask;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(planCourse.getCourseName());
    }

    private class GetPlanChildCourseTask extends AsyncTask<Boolean, Void, Integer> {

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
                app.setPlanChildCourses(app.getAssist().getPlanChildCourses(planCourse));
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
    private class SelectTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(Integer... params) {
            View view = getView();
            assert view != null;
            if (params.length < 1) {
                return "-1";
            }
            try {
                Result r = app.getAssist().selectPlanCourse(planCourse, app.getPlanChildCourses().get(params[0]));
                return r.getMessage();
            } catch (NeedLoginException needLogin) {
                return getString(R.string.login_timeout);
            } catch (IOException e) {
                return getString(R.string.network_error);
            } catch (Exception e) {
                Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
            return "-1";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals(getString(R.string.login_timeout)) || result.equals(getString(R.string.network_error))) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                mListener.onReLogin(false);
            } else if (!"-1".equals(result)) {
                View view = getView();
                assert view != null;
                progressDialog.dismiss();
                Snackbar.make(view, result, Snackbar.LENGTH_LONG).show();
                getTask = new GetPlanChildCourseTask();
                getTask.execute(false);
            }
        }
    }

    private void updateItems(boolean updated, boolean first) {
        if (updated) {
            PlanChildCourseRecycleAdapter adapter =
                    new PlanChildCourseRecycleAdapter(getActivity(), planCourse,
                            app.getPlanChildCourses());
            recyclerView.setAdapter(adapter);
        }
        View view = getView();
        if (view != null && first) {
                Snackbar snackbar = Snackbar.make(view, R.string.updated, Snackbar.LENGTH_SHORT);
                snackbar.show();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlanChildCourseFragment() {
    }

    @SuppressWarnings("unused")
    public static PlanChildCourseFragment newInstance(int pos) {
        PlanChildCourseFragment fragment = new PlanChildCourseFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, pos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_plan_child_course, container, false);

        getActivity().setTitle(planCourse.getCourseName());

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.plan_child_course_swipe_refresh_layout);
        recyclerView = (RecyclerView) v.findViewById(R.id.plan_child_course_recycler_view);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);

        app.getPlanChildCourses().clear();
        PlanChildCourseRecycleAdapter adapter =
                new PlanChildCourseRecycleAdapter(getActivity(), planCourse, app.getPlanChildCourses());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        final PlanChildCourse childCourse = app.getPlanChildCourses().get(position);
                        if ("".equals(childCourse.getSelectionCount())) {
                            return;
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.select_confirm);
                        builder.setMessage(planCourse.getCourseName() + " ("
                                + childCourse.getTeacher() + ") - "
                                + childCourse.getLocation()
                                + " - " + childCourse.getTime());
                        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog = ProgressDialog.show(getContext(),
                                        getString(R.string.selecting),
                                        planCourse.getCourseName() + " (" +
                                                childCourse.getTeacher() + ")", true);
                                new SelectTask().execute(position);
                            }
                        });
                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        Dialog dialog = builder.create();
                        dialog.show();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                }));

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        getTask = new GetPlanChildCourseTask();
        getTask.execute(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTask = new GetPlanChildCourseTask();
                getTask.execute(true);
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
            app = (YaApplication) getActivity().getApplication();
            planCourse = app.getPlanCourses().get(itemPosition);
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
        inflater.inflate(R.menu.only_refresh_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            getTask = new GetPlanChildCourseTask();
            getTask.execute(true);
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
