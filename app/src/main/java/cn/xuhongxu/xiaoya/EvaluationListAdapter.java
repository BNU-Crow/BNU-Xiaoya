package cn.xuhongxu.xiaoya;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

import cn.xuhongxu.Assist.EvaluationItem;

/**
 * Created by xuhongxu on 16/4/20.
 * <p/>
 * EvaluationListAdapter
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class EvaluationListAdapter extends ArrayAdapter<EvaluationItem> {

    private List<EvaluationItem> values;

    public EvaluationListAdapter(Context context, List<EvaluationItem> items) {
        super(context, R.layout.evaluation_list_item, items);
        values = items;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = getContext();

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.evaluation_list_item, null);
        }

        EvaluationItem item = values.get(position);

        TextView title = (TextView) v.findViewById(R.id.evalListTitle);
        title.setText(item.getName());
        TextView year = (TextView) v.findViewById(R.id.evalListYear);
        int itemYear = item.getYear();
        year.setText(context.getString(R.string.school_year) + ": " + itemYear + "-" + (itemYear + 1));
        TextView term = (TextView) v.findViewById(R.id.evalListTerm);
        term.setText(context.getString(R.string.school_term) + ": " + item.getTermName());

        DateFormat format = DateFormat.getDateTimeInstance();
        TextView start = (TextView) v.findViewById(R.id.evalStartDate);
        start.setText(context.getString(R.string.start_date) + ": " + format.format(item.getStartDate()));
        TextView end = (TextView) v.findViewById(R.id.evalEndDate);
        end.setText(context.getString(R.string.end_date) + ": " + format.format(item.getEndDate()));

        return v;
    }

}
