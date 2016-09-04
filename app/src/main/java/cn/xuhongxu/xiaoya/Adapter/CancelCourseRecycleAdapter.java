package cn.xuhongxu.xiaoya.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.xuhongxu.Assist.CancelCourse;
import cn.xuhongxu.Assist.ElectiveCourse;
import cn.xuhongxu.xiaoya.R;

/**
 * Created by xuhongxu on 16/5/10.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class CancelCourseRecycleAdapter
        extends RecyclerView.Adapter<CancelCourseRecycleAdapter.CancelCourseViewHolder> {

    private List<CancelCourse> values;
    private Context context;

    public CancelCourseRecycleAdapter(Context c, List<CancelCourse> items) {
        values = items;
        context = c;
    }

    @Override
    public CancelCourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.cancel_course_list_item,
                        parent,
                        false);
        return new CancelCourseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CancelCourseViewHolder holder, int position) {

        CancelCourse item = values.get(position);

        holder.name.setText(item.getName());
        holder.credit.setText(context.getString(R.string.credit) + ": " + item.getCredit());
        holder.period.setText(context.getString(R.string.period) + ": " + item.getPeriod());
        holder.type.setText(context.getString(R.string.type) + ": " + item.getClassification());
        holder.chosenInfo.setVisibility(View.VISIBLE);
        holder.teacher.setText(context.getString(R.string.teacher) + ": " + item.getTeacher());
        holder.name.setTextColor(Color.BLUE);
    }

    @Override
    public int getItemCount(){
        return values.size();
    }

    public final static class CancelCourseViewHolder extends RecyclerView.ViewHolder {
        public TextView name, period, credit, type, teacher;
        public LinearLayout chosenInfo;

        public CancelCourseViewHolder(View itemView) {
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
