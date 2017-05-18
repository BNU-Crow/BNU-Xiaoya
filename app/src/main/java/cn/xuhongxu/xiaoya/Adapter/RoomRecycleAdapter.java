package cn.xuhongxu.xiaoya.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import cn.xuhongxu.xiaoya.Fragment.ClassroomFragment;
import cn.xuhongxu.xiaoya.Helper.Room;
import cn.xuhongxu.xiaoya.R;

/**
 * Created by xuhongxu on 16/5/10.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class RoomRecycleAdapter
        extends RecyclerView.Adapter<RoomRecycleAdapter.RoomItemViewHolder> {

    private List<Room> values;
    private Context context;
    private int selected = -1;

    public void reset() {
        selected = -1;
    }

    public RoomRecycleAdapter(Context ct, List<Room> items) {
        values = items;
        context = ct;
    }

    @Override
    public RoomItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.room_list_item,
                        parent,
                        false);
        return new RoomItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RoomItemViewHolder holder, int position) {

        Room item = values.get(position);

        holder.title.setText(item.name);

        for (int i = 0; i < 12; ++i) {
            holder.course[i].setBackgroundResource(
                    item.noCourse[i] ? R.color.noCourse : R.color.hasCourse
            );
            holder.course[i].setTextColor(ContextCompat.getColor(context,
                    item.noCourse[i] ? R.color.colorBlack : R.color.colorWhite
            ));
        }
        if (position == selected) {
            holder.detail.setVisibility(View.VISIBLE);
        } else {
            holder.detail.setVisibility(View.GONE);
        }

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int prev = selected;
                selected = holder.getAdapterPosition();
                // Check for an expanded view, collapse if you find one
                if (prev >= 0) {
                    int tempPrev = prev;
                    if (prev % 2 == 1) prev -= 1;
                    if (prev + 1 >= values.size())
                        notifyItemChanged(prev);
                    else
                        notifyItemRangeChanged(prev, 2);
                    prev = tempPrev;
                }
                // Set the current position to "expanded"
                if (prev == selected) {
                    selected = -1;
                } else {
                    int tempSelected = selected;
                    if (tempSelected % 2 == 1) tempSelected -= 1;
                    if (tempSelected + 1 >= values.size())
                        notifyItemChanged(tempSelected);
                    else
                        notifyItemRangeChanged(tempSelected, 2);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public final static class RoomItemViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView[] course = new TextView[12];
        public LinearLayout card;
        public LinearLayout detail;

        public RoomItemViewHolder(View itemView) {
            super(itemView);

            card = (LinearLayout) itemView.findViewById(R.id.card_content);
            detail = (LinearLayout) itemView.findViewById(R.id.details);
            title = (TextView) itemView.findViewById(R.id.title);
            course[0] = (TextView) itemView.findViewById(R.id.course1);
            course[1] = (TextView) itemView.findViewById(R.id.course2);
            course[2] = (TextView) itemView.findViewById(R.id.course3);
            course[3] = (TextView) itemView.findViewById(R.id.course4);
            course[4] = (TextView) itemView.findViewById(R.id.course5);
            course[5] = (TextView) itemView.findViewById(R.id.course6);
            course[6] = (TextView) itemView.findViewById(R.id.course7);
            course[7] = (TextView) itemView.findViewById(R.id.course8);
            course[8] = (TextView) itemView.findViewById(R.id.course9);
            course[9] = (TextView) itemView.findViewById(R.id.course10);
            course[10] = (TextView) itemView.findViewById(R.id.course11);
            course[11] = (TextView) itemView.findViewById(R.id.course12);
        }
    }
}
