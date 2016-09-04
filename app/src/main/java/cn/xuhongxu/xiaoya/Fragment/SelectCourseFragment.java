package cn.xuhongxu.xiaoya.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import cn.xuhongxu.xiaoya.Activity.MainActivity;
import cn.xuhongxu.xiaoya.Adapter.ViewPagerFragmentAdapter;
import cn.xuhongxu.xiaoya.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectCourseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SelectCourseFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public SelectCourseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(R.string.Select);
        tabLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        tabLayout.setVisibility(View.GONE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_select_course, container, false);
        MainActivity activity = (MainActivity) getActivity();
        tabLayout = activity.getTabLayout();
        viewPager = (ViewPager) v.findViewById(R.id.viewPager);
        ViewPagerFragmentAdapter adapter = new ViewPagerFragmentAdapter(getChildFragmentManager());
        adapter.add(PlanCourseFragment.class, getString(R.string.plan_course));
        adapter.add(PlanCourseFragment.class, getString(R.string.elective_course));
        adapter.add(SelectResultFragment.class, getString(R.string.select_result));
        adapter.add(PlanCourseFragment.class, getString(R.string.cancel_course));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
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
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
    }
}
