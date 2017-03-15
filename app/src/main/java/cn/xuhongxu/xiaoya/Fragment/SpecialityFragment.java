package cn.xuhongxu.xiaoya.Fragment;

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

import cn.xuhongxu.Assist.Speciality;
import cn.xuhongxu.xiaoya.Adapter.SpecialityRecyclerViewAdapter;
import cn.xuhongxu.xiaoya.R;
import cn.xuhongxu.xiaoya.YaApplication;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SpecialityFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private YaApplication app = null;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    ArrayList<Speciality> specs = new ArrayList<>();

    String grade = "", level = "", dept = "";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SpecialityFragment() {
    }

    public static SpecialityFragment newInstance(String grade, String level, String dept) {
        SpecialityFragment fragment = new SpecialityFragment();
        Bundle bundle = new Bundle();
        bundle.putString("grade", grade);
        bundle.putString("level", level);
        bundle.putString("dept", dept);
        fragment.setArguments(bundle);
        return fragment;
    }

    private class QueryTask extends AsyncTask<Void, Void, ArrayList<Speciality>> {

        @Override
        protected ArrayList<Speciality> doInBackground(Void... voids) {
            if (app != null) {
                try {
                    return app.getAssist().getSpecialites(grade, level, dept);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Speciality> result) {
            progressBar.setVisibility(View.GONE);
            specs.clear();
            if (result != null) {
                specs.addAll(result);
            }
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        grade = getArguments().getString("grade", "");
        level = getArguments().getString("level", "");
        dept = getArguments().getString("dept", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spec_list, container, false);

        app = (YaApplication) getActivity().getApplication();
        // Set the adapter
        Context context = view.getContext();
        progressBar = (ProgressBar) view.findViewById(R.id.loading);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new SpecialityRecyclerViewAdapter(specs, mListener));
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
        void onListFragmentInteraction(Speciality item);
    }
}
