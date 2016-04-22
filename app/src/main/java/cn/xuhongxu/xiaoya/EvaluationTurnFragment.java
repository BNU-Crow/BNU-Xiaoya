package cn.xuhongxu.xiaoya;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;

import cn.xuhongxu.Assist.NeedLoginException;


public class EvaluationTurnFragment extends SwipeRefreshListFragment {

    private YaApplication app;
    private OnFragmentInteractionListener mListener;

    private static final String LOG_TAG = EvaluationTurnFragment.class.getSimpleName();


    private class GetEvaluationTask extends AsyncTask<Boolean, Void, Integer> {
        private boolean updated = false;

        GetEvaluationTask() {
            setRefreshing(true);
        }

        @Override
        protected Integer doInBackground(Boolean... params) {
            if (params.length > 0) {
                updated = params[0];
            }
            View view = getView();
            assert view != null;
            try {
                app.setEvaluationItemList(app.getAssist().getEvaluateList());
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
            setRefreshing(false);
            if (result == 0) {
                updateItems(updated);
            } else if (result == R.string.login_timeout || result == R.string.network_error) {
                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                mListener.onReLogin(false);
            }
        }
    }

    private void updateItems() {
        updateItems(false);
    }

    private void updateItems(boolean updated) {
        EvaluationListAdapter adapter =
                new EvaluationListAdapter(getActivity(), app.getEvaluationItemList());
        setListAdapter(adapter);
        View view = getView();
        if (view != null) {
            final SharedPreferences preferences =
                    getActivity().getSharedPreferences(getString(R.string.preference_key),
                            Context.MODE_PRIVATE);
            if (preferences.getBoolean("showEvaluationTip", true)) {
                int showTextId = R.string.select_evaluation_turn;
                if (updated) {
                    showTextId = R.string.updated;
                }
                Snackbar snackbar = Snackbar.make(view, showTextId, Snackbar.LENGTH_SHORT);
                if (!updated) {
                    snackbar.setAction(R.string.do_not_show_again, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("showEvaluationTip", false);
                            editor.apply();
                        }
                    });
                }
                snackbar.show();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        app = (YaApplication) getActivity().getApplication();

        if (app.getEvaluationItemList() == null) {
            new GetEvaluationTask().execute(false);
        } else {
            updateItems();
        }

        setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetEvaluationTask().execute(true);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.evaluation_item_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            new GetEvaluationTask().execute(true);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (isRefreshing()) return;
        mListener.onEvaluateCourse(position);
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
    }

    public interface OnFragmentInteractionListener {
        void onReLogin(boolean logout);
        void onEvaluateCourse(int pos);
    }

    @Override
    public void onPause() {
        super.onPause();
        getSwipeRefreshLayout().setRefreshing(false);
        getSwipeRefreshLayout().destroyDrawingCache();
        getSwipeRefreshLayout().clearAnimation();
    }
}
