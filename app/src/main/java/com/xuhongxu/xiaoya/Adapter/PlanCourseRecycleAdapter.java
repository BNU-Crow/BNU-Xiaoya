package com.xuhongxu.xiaoya.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import com.xuhongxu.Assist.PlanCourse;
import com.xuhongxu.xiaoya.R;

/**
 * Created by xuhongxu on 16/5/10.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class PlanCourseRecycleAdapter
        extends RecyclerView.Adapter<PlanCourseRecycleAdapter.PlanCourseViewHolder> {

    private List<PlanCourse> values;
    private Context context;

    public PlanCourseRecycleAdapter(Context ct, List<PlanCourse> items) {
        values = items;
        context = ct;
    }

    @Override
    public PlanCourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.list_item_plan_course,
                        parent,
                        false);
        return new PlanCourseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlanCourseViewHolder holder, int position) {

        PlanCourse item = values.get(position);

        holder.name.setText(item.getCourseName());
        holder.period.setText(context.getString(R.string.period) + ": " + item.getPeriod());
        holder.credit.setText(context.getString(R.string.credit) + ": " + item.getCredit());
        holder.type.setText(context.getString(R.string.type) + ": " + item.getClassification());

        if ("选中".equals(item.getSelectingStatus())) {
            holder.chosenInfo.setVisibility(View.VISIBLE);
            holder.teacher.setText(context.getString(R.string.teacher) + ": " + item.getTeacher());
            holder.name.setTextColor(Color.BLUE);
        } else {
            holder.chosenInfo.setVisibility(View.GONE);
            holder.name.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount(){
        return values.size();
    }

    public final static class PlanCourseViewHolder extends RecyclerView.ViewHolder {
        public TextView name, period, credit, type, teacher;
        public LinearLayout chosenInfo;

        public PlanCourseViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.courseName);
            period = (TextView) itemView.findViewById(R.id.coursePeriod);
            credit = (TextView) itemView.findViewById(R.id.courseCredit);
            type = (TextView) itemView.findViewById(R.id.courseType);
            teacher = (TextView) itemView.findViewById(R.id.courseTeacher);
            chosenInfo = (LinearLayout) itemView.findViewById(R.id.chosen_info);
        }
    }
}
