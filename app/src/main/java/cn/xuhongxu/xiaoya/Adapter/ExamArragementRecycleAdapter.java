package cn.xuhongxu.xiaoya.Adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import java.util.List;

import cn.xuhongxu.Assist.ExamArrangement;
import cn.xuhongxu.xiaoya.R;

/**
 * Created by xuhongxu on 16/5/10.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class ExamArragementRecycleAdapter
        extends RecyclerView.Adapter<ExamArragementRecycleAdapter.ExamArrangementViewHolder> {

    private List<ExamArrangement> values;
    private Context context;

    public ExamArragementRecycleAdapter(Context c, List<ExamArrangement> items) {
        values = items;
        context = c;
    }

    @Override
    public ExamArrangementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.exam_arrangement_list_item,
                        parent,
                        false);
        return new ExamArrangementViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ExamArrangementViewHolder holder, int position) {

        ExamArrangement item = values.get(position);

        holder.courseName.setText(item.getCourseName());
        holder.location.setText(context.getString(R.string.location) + ": " + item.getLocation());
        holder.time.setText(context.getString(R.string.time) + ": " + item.getTime());
        holder.seat.setText(context.getString(R.string.seatNumber) + ": " + item.getSeat());
    }

    @Override
    public int getItemCount(){
        return values.size();
    }

    public final static class ExamArrangementViewHolder extends RecyclerView.ViewHolder {
        public TextView courseName, time, location, seat;

        public ExamArrangementViewHolder(View itemView) {
            super(itemView);

            courseName = (TextView) itemView.findViewById(R.id.examCourseName);
            time = (TextView) itemView.findViewById(R.id.examTime);
            location = (TextView) itemView.findViewById(R.id.examLocation);
            seat = (TextView) itemView.findViewById(R.id.examSeat);
        }
    }
}
