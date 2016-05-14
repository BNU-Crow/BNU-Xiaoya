package cn.xuhongxu.xiaoya.Adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

import cn.xuhongxu.Assist.EvaluationItem;
import cn.xuhongxu.xiaoya.R;

/**
 * Created by xuhongxu on 16/5/10.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class EvaluationRecycleAdapter
        extends RecyclerView.Adapter<EvaluationRecycleAdapter.EvaluationItemViewHolder> {

    private List<EvaluationItem> values;
    private Context context;

    public EvaluationRecycleAdapter(Context c, List<EvaluationItem> items) {
        values = items;
        context = c;
    }

    @Override
    public EvaluationItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.evaluation_list_item,
                        parent,
                        false);
        return new EvaluationItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EvaluationItemViewHolder holder, int position) {

        EvaluationItem item = values.get(position);

        holder.title.setText(item.getName());
        int itemYear = item.getYear();
        holder.year.setText(context.getString(R.string.school_year) + ": " + itemYear + "-" + (itemYear + 1));
        holder.term.setText(context.getString(R.string.school_term) + ": " + item.getTermName());

        DateFormat format = DateFormat.getDateTimeInstance();
        holder.start.setText(context.getString(R.string.start_date) + ": " + format.format(item.getStartDate()));
        holder.end.setText(context.getString(R.string.end_date) + ": " + format.format(item.getEndDate()));
    }

    @Override
    public int getItemCount(){
        return values.size();
    }

    public final static class EvaluationItemViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, term, start, end;

        public EvaluationItemViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.evalListTitle);
            year = (TextView) itemView.findViewById(R.id.evalListYear);
            term = (TextView) itemView.findViewById(R.id.evalListTerm);
            start = (TextView) itemView.findViewById(R.id.evalStartDate);
            end = (TextView) itemView.findViewById(R.id.evalEndDate);
        }
    }
}
