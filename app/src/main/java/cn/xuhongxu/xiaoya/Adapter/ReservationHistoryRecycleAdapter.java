package cn.xuhongxu.xiaoya.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import cn.xuhongxu.LibrarySeat.ReservationHistory;
import cn.xuhongxu.xiaoya.R;

/**
 * Created by xuhongxu on 16/5/10.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class ReservationHistoryRecycleAdapter
        extends RecyclerView.Adapter<ReservationHistoryRecycleAdapter.ItemViewHolder> {

    private List<ReservationHistory> values;
    private Context context;
    OnClickListener listener;

    public interface OnClickListener {
        void onClicked(ReservationHistory history);
    }

    public ReservationHistoryRecycleAdapter(Context ct, List<ReservationHistory> items, OnClickListener listener) {
        values = items;
        context = ct;
        this.listener = listener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.reservation_history_list_item,
                        parent,
                        false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        ReservationHistory item = values.get(position);

        holder.date.setText(item.date);
        holder.begin.setText(item.begin);
        holder.end.setText(item.end);
        holder.status.setText(item.state);
        holder.location.setText(item.location);
        if (item.state.equals("RESERVE")) {
            holder.cancel.setVisibility(View.VISIBLE);
        } else {
            holder.cancel.setVisibility(View.GONE);
        }
        holder.cancel.setOnClickListener(v -> listener.onClicked(item));
    }

    @Override
    public int getItemCount(){
        return values.size();
    }

    public final static class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView location, status, begin, end, date;
        public Button cancel;

        public ItemViewHolder(View itemView) {
            super(itemView);

            cancel = (Button) itemView.findViewById(R.id.cancel_button);
            location = (TextView) itemView.findViewById(R.id.location);
            status = (TextView) itemView.findViewById(R.id.status);
            begin = (TextView) itemView.findViewById(R.id.begin);
            end = (TextView) itemView.findViewById(R.id.end);
            date = (TextView) itemView.findViewById(R.id.date);
        }
    }
}
