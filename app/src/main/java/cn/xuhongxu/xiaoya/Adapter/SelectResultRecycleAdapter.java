package cn.xuhongxu.xiaoya.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.xuhongxu.Assist.PlanCourse;
import cn.xuhongxu.Assist.SelectionResult;
import cn.xuhongxu.xiaoya.R;

/**
 * Created by xuhongxu on 16/5/10.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class SelectResultRecycleAdapter
        extends RecyclerView.Adapter<SelectResultRecycleAdapter.SelectResultViewHolder> {

    private List<SelectionResult> values;
    private Context context;

    public SelectResultRecycleAdapter(Context ct, List<SelectionResult> items) {
        values = items;
        context = ct;
    }

    @Override
    public SelectResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.select_result_list_item,
                        parent,
                        false);
        return new SelectResultViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SelectResultViewHolder holder, int position) {

        SelectionResult item = values.get(position);

        holder.name.setText(item.getName());
        holder.selection.setText(context.getString(R.string.selected_count) + ": " +
                item.getSelectionCount() + "/" + item.getMaxSelection());
        holder.credit.setText(context.getString(R.string.credit) + ": " + item.getCredit());
        holder.type.setText(context.getString(R.string.type) + ": " + item.getClassification());
        holder.chosenInfo.setVisibility(View.VISIBLE);
        holder.teacher.setText(context.getString(R.string.teacher) + ": " + item.getTeacher());
        holder.name.setTextColor(Color.BLUE);
    }

    @Override
    public int getItemCount(){
        return values.size();
    }

    public final static class SelectResultViewHolder extends RecyclerView.ViewHolder {
        public TextView name, selection, credit, type, teacher;
        public LinearLayout chosenInfo;

        public SelectResultViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.courseName);
            selection = (TextView) itemView.findViewById(R.id.selection);
            credit = (TextView) itemView.findViewById(R.id.courseCredit);
            type = (TextView) itemView.findViewById(R.id.courseType);
            teacher = (TextView) itemView.findViewById(R.id.courseTeacher);
            chosenInfo = (LinearLayout) itemView.findViewById(R.id.chosen_info);
        }
    }
}
