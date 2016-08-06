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

import cn.xuhongxu.Assist.ExamArrangement;
import cn.xuhongxu.Assist.PlanChildCourse;
import cn.xuhongxu.Assist.PlanCourse;
import cn.xuhongxu.xiaoya.R;

/**
 * Created by xuhongxu on 16/5/10.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class PlanChildCourseRecycleAdapter
        extends RecyclerView.Adapter<PlanChildCourseRecycleAdapter.PlanChildCourseViewHolder> {

    private List<PlanChildCourse> values;
    private Context context;
    private PlanCourse course;

    public PlanChildCourseRecycleAdapter(Context c, PlanCourse course, List<PlanChildCourse> items) {
        values = items;
        context = c;
        this.course = course;
    }

    @Override
    public PlanChildCourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.plan_child_course_list_item,
                        parent,
                        false);
        return new PlanChildCourseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlanChildCourseViewHolder holder, int position) {

        PlanChildCourse item = values.get(position);

        holder.teacher.setText(item.getTeacher());
        holder.location.setText(context.getString(R.string.location) + ": " + item.getLocation());
        holder.time.setText(context.getString(R.string.time) + ": " + item.getTime());
        if ("".equals(item.getSelectionCount())) {
            holder.card.setCardElevation(0);
            holder.item.setClickable(false);
            holder.item.setTranslationX(50);
            holder.remain.setVisibility(View.GONE);
            holder.count.setVisibility(View.GONE);
            holder.id.setVisibility(View.GONE);
        } else {
            holder.card.setCardElevation(8);
            holder.item.setClickable(true);
            holder.item.setTranslationX(0);
            holder.remain.setVisibility(View.VISIBLE);
            holder.count.setVisibility(View.VISIBLE);
            holder.id.setVisibility(View.VISIBLE);
            holder.id.setText(context.getString(R.string.class_id) + ": " + item.getSubClassCode());
            holder.remain.setText(context.getString(R.string.remaining_selection) + ": " + item.getRemainingSelection());
            holder.count.setText(context.getString(R.string.selected_count) + ": " +
                    item.getSelectionCount() + "/" + item.getMaxSelection());
        }
        if ("选中".equals(course.getSelectingStatus()) && course.getClassCode().equals(item.getClassCode())) {
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

    public final static class PlanChildCourseViewHolder extends RecyclerView.ViewHolder {
        public TextView id, teacher, time, location, remain, count, chosen;
        public LinearLayout item;
        public CardView card;

        public PlanChildCourseViewHolder(View itemView) {
            super(itemView);

            id = (TextView) itemView.findViewById(R.id.childCourseID);
            teacher = (TextView) itemView.findViewById(R.id.childCourseTeacher);
            time = (TextView) itemView.findViewById(R.id.childCourseTime);
            location = (TextView) itemView.findViewById(R.id.childCourseLocation);
            remain = (TextView) itemView.findViewById(R.id.childCourseRemain);
            count = (TextView) itemView.findViewById(R.id.childCourseCount);
            chosen = (TextView) itemView.findViewById(R.id.childCourseChosen);
            item = (LinearLayout) itemView.findViewById(R.id.child_course_item);
            card = (CardView) itemView.findViewById(R.id.card_view);
        }
    }
}
