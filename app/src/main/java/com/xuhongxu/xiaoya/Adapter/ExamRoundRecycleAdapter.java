package com.xuhongxu.xiaoya.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.xuhongxu.Assist.ExamRound;
import com.xuhongxu.xiaoya.R;

/**
 * Created by xuhongxu on 16/5/10.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class ExamRoundRecycleAdapter
        extends RecyclerView.Adapter<ExamRoundRecycleAdapter.ExamRoundItemViewHolder> {

    private List<ExamRound> values;
    private Context context;

    public ExamRoundRecycleAdapter(Context ct, List<ExamRound> items) {
        values = items;
        context = ct;
    }

    @Override
    public ExamRoundItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.list_item_exam_round,
                        parent,
                        false);
        return new ExamRoundItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ExamRoundItemViewHolder holder, int position) {

        ExamRound item = values.get(position);

        holder.title.setText(item.getName());
        holder.year.setText(context.getString(R.string.school_year) + ": " + item.getYearName());
        holder.term.setText(context.getString(R.string.school_term) + ": " + item.getTermName());
        holder.round.setText(context.getString(R.string.type) + ": " + item.getRoundName());
    }

    @Override
    public int getItemCount(){
        return values.size();
    }

    public final static class ExamRoundItemViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, term, round;

        public ExamRoundItemViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.examRoundTitle);
            year = (TextView) itemView.findViewById(R.id.examRoundYear);
            term = (TextView) itemView.findViewById(R.id.examRoundTerm);
            round = (TextView) itemView.findViewById(R.id.examRoundName);
        }
    }
}
