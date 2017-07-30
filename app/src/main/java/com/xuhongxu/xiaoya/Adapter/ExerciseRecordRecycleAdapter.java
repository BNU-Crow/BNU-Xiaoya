package com.xuhongxu.xiaoya.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.xuhongxu.xiaoya.Helper.ExerciseRecord;
import com.xuhongxu.xiaoya.R;

/**
 * Created by xuhongxu on 16/5/10.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class ExerciseRecordRecycleAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ExerciseRecord> values;
    private Context context;

    public ExerciseRecordRecycleAdapter(Context ct, List<ExerciseRecord> items) {
        values = items;
        context = ct;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.list_item_exercise,
                            parent,
                            false);
            return new ExerciseRecordViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.list_item_all_exercise,
                            parent,
                            false);
            return new AllExerciseRecordViewHolder(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return values.get(position).type;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {

        ExerciseRecord item = values.get(position);

        if (item.type == 0) {

            ExerciseRecordViewHolder holder = (ExerciseRecordViewHolder) viewHolder;

            holder.date.setText(item.date + " " + item.building);
            holder.enter.setText(item.enterTime);
            holder.leave.setText(item.leaveTime);
            holder.status.setText(item.status);

        } else {

            AllExerciseRecordViewHolder holder = (AllExerciseRecordViewHolder) viewHolder;

            holder.time.setText(item.time);
            holder.desp.setText(item.desp);
        }
    }

    @Override
    public int getItemCount(){
        return values.size();
    }

    public final static class ExerciseRecordViewHolder extends RecyclerView.ViewHolder {
        public TextView date, building, enter, leave, status;
        public CardView card;

        public ExerciseRecordViewHolder(View itemView) {
            super(itemView);

            card = (CardView) itemView.findViewById(R.id.card_view);
            date = (TextView) itemView.findViewById(R.id.date);
            building = (TextView) itemView.findViewById(R.id.building);
            enter = (TextView) itemView.findViewById(R.id.enter);
            leave = (TextView) itemView.findViewById(R.id.leave);
            status = (TextView) itemView.findViewById(R.id.status);
        }
    }
    public final static class AllExerciseRecordViewHolder extends RecyclerView.ViewHolder {
        public TextView time, desp;
        public CardView card;

        public AllExerciseRecordViewHolder(View itemView) {
            super(itemView);

            card = (CardView) itemView.findViewById(R.id.card_view);
            time = (TextView) itemView.findViewById(R.id.time);
            desp = (TextView) itemView.findViewById(R.id.desp);
        }
    }
}
