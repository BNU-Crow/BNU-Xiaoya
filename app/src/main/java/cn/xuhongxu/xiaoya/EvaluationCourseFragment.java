package cn.xuhongxu.xiaoya;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;

import cn.xuhongxu.Assist.EvaluatingCourse;
import cn.xuhongxu.Assist.NeedLoginException;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class EvaluationCourseFragment extends SwipeRefreshListFragment {

    private static final String ARG_POSITION = "position";
    private static final String LOG_TAG = EvaluationCourseFragment.class.getSimpleName();
    private static final int EVALUATE_ALL = -1;
    private OnListFragmentInteractionListener mListener;

    private YaApplication app;
    private int itemPosition = 0;


    private class GetEvaluatingCoursesTask extends AsyncTask<Boolean, Void, Integer> {

        private boolean updated = false;

        @Override
        protected Integer doInBackground(Boolean... params) {
            if (params.length > 0) {
                updated = params[0];
            }
            View view = getView();
            assert view != null;
            try {
                app.setEvaluatingCourses(
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
            if (result == 0) {
                updateItems(updated);
            } else if (result == R.string.login_timeout || result == R.string.network_error) {
                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                mListener.onReLogin(false);
            }
        }
    }

    private void updateItems(boolean updated) {
        EvaluationCourseListAdapter adapter =
                new EvaluationCourseListAdapter(getActivity(), app.getEvaluatingCourses());
        setListAdapter(adapter);
        View view = getView();
        if (view != null) {
            final SharedPreferences preferences =
                    getActivity().getSharedPreferences(getString(R.string.preference_key),
                            Context.MODE_PRIVATE);
            if (preferences.getBoolean("showEvaluationCourseTip", true)) {
                int showTextId = R.string.select_evaluation_course;
                if (updated) {
                    showTextId = R.string.updated;
                }
                Snackbar snackbar = Snackbar.make(view, showTextId, Snackbar.LENGTH_SHORT);
                if (!updated) {
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
        setRefreshing(false);
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        app = (YaApplication) getActivity().getApplication();

        setRefreshing(true);
        new GetEvaluatingCoursesTask().execute(false);

        setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetEvaluatingCoursesTask().execute(true);
            }
        });
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
            setRefreshing(true);
            new GetEvaluatingCoursesTask().execute(true);
        } else if (id == R.id.action_evaluate_all) {
            setRefreshing(true);
            showEvaluateDialog(-1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (int pos = 0; pos < app.getEvaluatingCourses().size(); ++pos) {
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
    }

    public interface OnListFragmentInteractionListener {
        void onReLogin(boolean logout);
    }

    @Override
    public void onPause() {
        super.onPause();
        getSwipeRefreshLayout().setRefreshing(false);
        getSwipeRefreshLayout().destroyDrawingCache();
        getSwipeRefreshLayout().clearAnimation();
    }

    @Override
    public void onListItemClick(ListView l, View v, final int position, long id) {
        if (isRefreshing()) return;
        showEvaluateDialog(EVALUATE_ALL, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                setRefreshing(true);
                new EvaluateTask().execute(position, 5 - which, EVALUATE_ALL);
            }
        });
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
                        app.getEvaluatingCourses().get(params[0]),
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
            if (result == 0) {
                View view = getView();
                assert view != null;
                if (all) {
                        EvaluatingCourse course = app.getEvaluatingCourses().get(pos);
                        Snackbar.make(view,
                                getString(R.string.succeed_evaluate)
                                        + ": "
                                        + course.getName()
                                        + "("
                                        + course.getTeacherName()
                                        + ")",
                                Snackbar.LENGTH_SHORT
                        ).show();
                    if (pos != app.getEvaluatingCourses().size() - 1) {
                        return;
                    }
                }
                Snackbar.make(view, R.string.succeed_evaluate, Snackbar.LENGTH_SHORT).show();
                new GetEvaluatingCoursesTask().execute(true);
            } else if (result == R.string.login_timeout || result == R.string.network_error) {
                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                mListener.onReLogin(false);
            }
        }
    }

    public void showEvaluateDialog(final int pos, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (pos == EVALUATE_ALL) {
            builder.setTitle(getString(R.string.choose_evaluation_level));
        } else {
            EvaluatingCourse course = app.getEvaluatingCourses().get(pos);
            builder.setTitle(
                    getString(R.string.choose_evaluation_level) + ": "
                            + course.getName()
                            + "(" + course.getTeacherName() + ")");
        }
        builder.setItems(R.array.stars, listener);
        Dialog dialog = builder.create();
        dialog.show();
    }

}
