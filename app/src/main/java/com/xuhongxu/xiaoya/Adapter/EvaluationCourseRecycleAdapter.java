package com.xuhongxu.xiaoya.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.xuhongxu.Assist.EvaluationCourse;
import com.xuhongxu.xiaoya.R;

/**
 * Created by xuhongxu on 16/5/10.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class EvaluationCourseRecycleAdapter
        extends RecyclerView.Adapter<EvaluationCourseRecycleAdapter.EvaluationItemViewHolder> {

    private List<EvaluationCourse> values;
    private Context context;

    public EvaluationCourseRecycleAdapter(Context c, List<EvaluationCourse> items) {
        values = items;
        context = c;
    }

    @Override
    public EvaluationItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.list_item_evaluation_course,
                        parent,
                        false);
        return new EvaluationItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EvaluationItemViewHolder holder, int position) {

        EvaluationCourse item = values.get(position);

        holder.courseNameView.setText(item.getName());

        holder.courseTeacherNameView.setText(item.getTeacherName());

        holder.courseCreditView.setText(context.getString(R.string.credit) + ": " + item.getCredit());

        if (item.isEvaluated()) {
            holder.statusImage.setImageResource(R.drawable.ic_done_black);
        }
    }

    @Override
    public int getItemCount(){
        return values.size();
    }

    public final static class EvaluationItemViewHolder extends RecyclerView.ViewHolder {
        public TextView courseNameView, courseTeacherNameView, courseCreditView;
        public ImageView statusImage;

        public EvaluationItemViewHolder(View itemView) {
            super(itemView);

            courseNameView = (TextView) itemView.findViewById(R.id.evalCourseName);
            courseTeacherNameView = (TextView) itemView.findViewById(R.id.evalCourseTeacherName);
            courseCreditView = (TextView) itemView.findViewById(R.id.evalCourseCredit);
            statusImage = (ImageView) itemView.findViewById(R.id.statusImage);
        }
    }
}
