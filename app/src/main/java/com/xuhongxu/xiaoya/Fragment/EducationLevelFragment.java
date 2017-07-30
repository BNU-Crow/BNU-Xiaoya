package com.xuhongxu.xiaoya.Fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;

import com.xuhongxu.Assist.EducationLevel;
import com.xuhongxu.xiaoya.Adapter.EducationLevelRecyclerViewAdapter;
import com.xuhongxu.xiaoya.R;
import com.xuhongxu.xiaoya.YaApplication;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class EducationLevelFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private YaApplication app = null;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    ArrayList<EducationLevel> levels = new ArrayList<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EducationLevelFragment() {
    }

    private class QueryTask extends AsyncTask<Void, Void, ArrayList<EducationLevel>> {

        @Override
        protected ArrayList<EducationLevel> doInBackground(Void... voids) {
            if (app != null) {
                try {
                    return app.getAssist().getEducationLevels();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<EducationLevel> result) {
            progressBar.setVisibility(View.GONE);
            levels.clear();
            if (result != null) {
                levels.addAll(result);
            }
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_educationlevel_list, container, false);

        app = (YaApplication) getActivity().getApplication();
        // Set the adapter
        Context context = view.getContext();
        progressBar = (ProgressBar) view.findViewById(R.id.loading);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new EducationLevelRecyclerViewAdapter(levels, mListener));
        progressBar.setVisibility(View.VISIBLE);
        new QueryTask().execute();
        return view;
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(EducationLevel item);
    }
}
