package cn.xuhongxu.xiaoya.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.xuhongxu.Assist.ExamRound;
import cn.xuhongxu.Assist.ExamScore;
import cn.xuhongxu.xiaoya.R;

/**
 * Created by xuhongxu on 16/5/10.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class ScoreRecycleAdapter
        extends RecyclerView.Adapter<ScoreRecycleAdapter.ScoreItemViewHolder> {

    private List<ExamScore> values;
    private Context context;

    public ScoreRecycleAdapter(Context ct, List<ExamScore> items) {
        values = items;
        context = ct;
    }

    @Override
    public ScoreItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.score_list_item,
                        parent,
                        false);
        return new ScoreItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ScoreItemViewHolder holder, int position) {

        ExamScore item = values.get(position);

        holder.name.setText(item.getCourseName());
        holder.finalScore.setText(item.getScore());
        holder.usual.setText(context.getString(R.string.usual_score) + ": " + item.getUsualScore());
        holder.exam.setText(context.getString(R.string.exam_score) + ": " + item.getExamScore());
        holder.credit.setText(context.getString(R.string.credit) + ": " + item.getCourseCredit());
        holder.classification.setText(item.getClassification());
    }

    @Override
    public int getItemCount(){
        return values.size();
    }

    public final static class ScoreItemViewHolder extends RecyclerView.ViewHolder {
        public TextView name, credit, classification, usual, exam, finalScore;

        public ScoreItemViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.scoreName);
            credit = (TextView) itemView.findViewById(R.id.scoreCredit);
            classification = (TextView) itemView.findViewById(R.id.scoreClassification);
            usual = (TextView) itemView.findViewById(R.id.scoreUsual);
            exam = (TextView) itemView.findViewById(R.id.scoreExam);
            finalScore = (TextView) itemView.findViewById(R.id.scoreFinal);
        }
    }
}
