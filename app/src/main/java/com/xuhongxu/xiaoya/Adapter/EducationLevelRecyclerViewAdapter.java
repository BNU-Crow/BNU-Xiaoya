package com.xuhongxu.xiaoya.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xuhongxu.Assist.EducationLevel;
import com.xuhongxu.xiaoya.Fragment.EducationLevelFragment;
import com.xuhongxu.xiaoya.R;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link EducationLevel} and makes a call to the
 * specified {@link com.xuhongxu.xiaoya.Fragment.EducationLevelFragment.OnListFragmentInteractionListener}.
 */
public class EducationLevelRecyclerViewAdapter extends RecyclerView.Adapter<EducationLevelRecyclerViewAdapter.ViewHolder> {

    private final List<EducationLevel> mValues;
    private final EducationLevelFragment.OnListFragmentInteractionListener mListener;

    public EducationLevelRecyclerViewAdapter(List<EducationLevel> items, EducationLevelFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_name, parent, false);
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
        public EducationLevel mItem;

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
