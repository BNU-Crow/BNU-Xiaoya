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
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;

import java.io.IOException;

import cn.xuhongxu.Assist.CancelCourse;
import cn.xuhongxu.Assist.ElectiveCourse;
import cn.xuhongxu.Assist.NeedLoginException;
import cn.xuhongxu.Assist.Result;
import cn.xuhongxu.xiaoya.Adapter.CancelCourseRecycleAdapter;
import cn.xuhongxu.xiaoya.Adapter.ElectiveCourseRecycleAdapter;
import cn.xuhongxu.xiaoya.Listener.RecyclerItemClickListener;
import cn.xuhongxu.xiaoya.R;
import cn.xuhongxu.xiaoya.YaApplication;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CancelCourseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CancelCourseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CancelCourseFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private YaApplication app;

    private GetCancelCourseTask task;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;

    public CancelCourseFragment() {
        // Required empty public constructor
    }

    public static CancelCourseFragment newInstance() {
        return new CancelCourseFragment();
    }

    private class GetCancelCourseTask extends AsyncTask<Boolean, Void, String> {
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
                app.setCancelCourses(app.getAssist().getCancelCourses());
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
            CancelCourseRecycleAdapter adapter =
                    new CancelCourseRecycleAdapter(getActivity(), app.getCancelCourses());
            recyclerView.setAdapter(adapter);
        }

        View view = getView();
        if (view != null && first) {
            Snackbar snackbar = Snackbar.make(view, R.string.updated, Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
        swipeRefreshLayout.setRefreshing(false);
    }


    private class CancelTask extends AsyncTask<Integer, Void, String> {

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
                Result r = app.getAssist().cancelCourse(app.getCancelCourses().get(params[0]));
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
                task = new GetCancelCourseTask();
                task.execute(false);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cancel_course, container, false);

        app = (YaApplication) getActivity().getApplication();
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.cancel_course_swipe_refresh_layout);
        recyclerView = (RecyclerView) v.findViewById(R.id.cancel_course_recycler_view);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);
        CancelCourseRecycleAdapter adapter =
                new CancelCourseRecycleAdapter(getActivity(), app.getCancelCourses());
        recyclerView.setAdapter(adapter);


        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        final CancelCourse course = app.getCancelCourses().get(position);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.cancel_confirm);
                        builder.setMessage(course.getName() + " ("
                                + course.getTeacher() + ") ");
                        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog = ProgressDialog.show(getContext(),
                                        getString(R.string.canceling),
                                        course.getName() + " (" +
                                                course.getTeacher() + ")", true);
                                new CancelTask().execute(position);
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
        if (app.getCancelCourses().isEmpty()) {
            progressBar.setIndeterminate(true);
            task = new GetCancelCourseTask();
            task.execute(true);
        } else {
            progressBar.setVisibility(View.GONE);
            updateItems(false, true);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                task = new GetCancelCourseTask();
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
        inflater.inflate(R.menu.only_refresh_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            task = new GetCancelCourseTask();
            task.execute(false);
        }

        return super.onOptionsItemSelected(item);
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
