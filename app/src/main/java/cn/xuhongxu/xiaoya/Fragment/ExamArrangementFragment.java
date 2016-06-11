package cn.xuhongxu.xiaoya.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

import java.io.IOException;

import cn.xuhongxu.Assist.NeedLoginException;
import cn.xuhongxu.xiaoya.Adapter.ExamArragementRecycleAdapter;
import cn.xuhongxu.xiaoya.Listener.RecyclerItemClickListener;
import cn.xuhongxu.xiaoya.R;
import cn.xuhongxu.xiaoya.YaApplication;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ExamArrangementFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    private OnListFragmentInteractionListener mListener;

    private YaApplication app;
    private int itemPosition = 0;

    private GetExamArrangementTask getTask;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    ProgressBar progressBar;

    private class GetExamArrangementTask extends AsyncTask<Boolean, Void, Integer> {

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
                app.setExamArrangement(
                        app.getAssist().getExamArrangement(
                                app.getExamRounds().get(itemPosition)
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
            ExamArragementRecycleAdapter adapter =
                    new ExamArragementRecycleAdapter(getActivity(), app.getExamArrangement());
            recyclerView.setAdapter(adapter);
        }
        View view = getView();
        if (view != null) {
                Snackbar snackbar = Snackbar.make(view, R.string.updated, Snackbar.LENGTH_SHORT);
                snackbar.show();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ExamArrangementFragment() {
    }

    @SuppressWarnings("unused")
    public static ExamArrangementFragment newInstance(int pos) {
        ExamArrangementFragment fragment = new ExamArrangementFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, pos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_exam_round, container, false);

        app = (YaApplication) getActivity().getApplication();

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.exam_round_swipe_refresh_layout);
        recyclerView = (RecyclerView) v.findViewById(R.id.exam_round_recycler_view);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);

        app.getExamArrangement().clear();
        ExamArragementRecycleAdapter adapter =
                new ExamArragementRecycleAdapter(getActivity(), app.getExamArrangement());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                }));

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        getTask = new GetExamArrangementTask();
        getTask.execute(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTask = new GetExamArrangementTask();
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
        inflater.inflate(R.menu.only_refresh_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            getTask = new GetExamArrangementTask();
            getTask.execute(false);
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

}
