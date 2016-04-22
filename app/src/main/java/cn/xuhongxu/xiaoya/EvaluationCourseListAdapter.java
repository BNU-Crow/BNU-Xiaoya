package cn.xuhongxu.xiaoya;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

import cn.xuhongxu.Assist.EvaluatingCourse;
import cn.xuhongxu.Assist.EvaluationItem;

/**
 * Created by xuhongxu on 16/4/22.
 * <p/>
 * EvaluationCourseListAdapter
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class EvaluationCourseListAdapter extends ArrayAdapter<EvaluatingCourse> {

    private List<EvaluatingCourse> values;

    public EvaluationCourseListAdapter(Context context, List<EvaluatingCourse> items) {
        super(context, R.layout.evaluation_course_list_item, items);
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
            v = vi.inflate(R.layout.evaluation_course_list_item, null);
        }

        EvaluatingCourse item = values.get(position);

        TextView courseNameView = (TextView) v.findViewById(R.id.evalCourseName);
        courseNameView.setText(item.getName());

        TextView courseTeacherNameView = (TextView) v.findViewById(R.id.evalCourseTeacherName);
        courseTeacherNameView.setText(item.getTeacherName());

        TextView courseCreditView = (TextView) v.findViewById(R.id.evalCourseCredit);
        courseCreditView.setText(getContext().getString(R.string.credit) + ": " + item.getCredit());

        ImageView statusImage = (ImageView) v.findViewById(R.id.statusImage);

        if (item.isEvaluated()) {
            statusImage.setImageResource(R.drawable.ic_done_black);
        }

        return v;
    }

}
