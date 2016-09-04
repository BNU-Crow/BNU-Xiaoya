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

import cn.xuhongxu.Assist.ElectiveCourse;
import cn.xuhongxu.Assist.PlanCourse;
import cn.xuhongxu.xiaoya.R;

/**
 * Created by xuhongxu on 16/5/10.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class ElectiveCourseRecycleAdapter
        extends RecyclerView.Adapter<ElectiveCourseRecycleAdapter.ElectiveCourseViewHolder> {

    private List<ElectiveCourse> values;
    private Context context;

    public ElectiveCourseRecycleAdapter(Context c, List<ElectiveCourse> items) {
        values = items;
        context = c;
    }

    @Override
    public ElectiveCourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.elective_course_list_item,
                        parent,
                        false);
        return new ElectiveCourseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ElectiveCourseViewHolder holder, int position) {

        ElectiveCourse item = values.get(position);

        holder.teacher.setText(item.getTeacher());
        holder.location.setText(context.getString(R.string.location) + ": " + item.getLocation());
        holder.time.setText(context.getString(R.string.time) + ": " + item.getTime());
        if ("".equals(item.getName())) {
            holder.card.setCardElevation(0);
            holder.item.setClickable(false);
            holder.item.setTranslationX(50);
            holder.name.setVisibility(View.GONE);
            holder.remain.setVisibility(View.GONE);
            holder.count.setVisibility(View.GONE);
            holder.id.setVisibility(View.GONE);
        } else {
            holder.name.setText(item.getName());
            holder.card.setCardElevation(8);
            holder.item.setClickable(true);
            holder.item.setTranslationX(0);
            holder.name.setVisibility(View.VISIBLE);
            holder.remain.setVisibility(View.VISIBLE);
            holder.count.setVisibility(View.VISIBLE);
            holder.id.setVisibility(View.VISIBLE);
            holder.id.setText(context.getString(R.string.class_id) + ": " + item.getClassNumber());
            holder.remain.setText(context.getString(R.string.remaining_selection) + ": " + item.getRemainingSelection());
            holder.count.setText(context.getString(R.string.selected_count) + ": " +
                    item.getSelectionCount() + "/" + item.getMaxSelection());
        }
        if (item.getChosen().contains("选中")) {
            holder.teacher.setTextColor(Color.BLUE);
            holder.chosen.setVisibility(View.VISIBLE);
        } else {
            holder.teacher.setTextColor(Color.BLACK);
            holder.chosen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount(){
        return values.size();
    }

    public final static class ElectiveCourseViewHolder extends RecyclerView.ViewHolder {
        public TextView name, id, teacher, time, location, remain, count, chosen;
        public LinearLayout item;
        public CardView card;

        public ElectiveCourseViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.courseName);
            id = (TextView) itemView.findViewById(R.id.courseID);
            teacher = (TextView) itemView.findViewById(R.id.courseTeacher);
            time = (TextView) itemView.findViewById(R.id.courseTime);
            location = (TextView) itemView.findViewById(R.id.courseLocation);
            remain = (TextView) itemView.findViewById(R.id.courseRemain);
            count = (TextView) itemView.findViewById(R.id.courseCount);
            chosen = (TextView) itemView.findViewById(R.id.courseChosen);
            item = (LinearLayout) itemView.findViewById(R.id.course_item);
            card = (CardView) itemView.findViewById(R.id.card_view);
        }
    }
}
