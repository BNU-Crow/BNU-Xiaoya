package cn.xuhongxu.xiaoya.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.xuhongxu.Assist.Department;
import cn.xuhongxu.Assist.EducationLevel;
import cn.xuhongxu.Assist.Speciality;
import cn.xuhongxu.xiaoya.Fragment.DepartmentFragment;
import cn.xuhongxu.xiaoya.Fragment.SpecialityFragment;
import cn.xuhongxu.xiaoya.R;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Speciality} and makes a call to the
 * specified {@link SpecialityFragment.OnListFragmentInteractionListener}.
 */
public class SpecialityRecyclerViewAdapter extends RecyclerView.Adapter<SpecialityRecyclerViewAdapter.ViewHolder> {

    private final List<Speciality> mValues;
    private final SpecialityFragment.OnListFragmentInteractionListener mListener;

    public SpecialityRecyclerViewAdapter(List<Speciality> items, SpecialityFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.name_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).getName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public Speciality mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view.findViewById(R.id.item);
            mContentView = (TextView) view.findViewById(R.id.name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
