package cn.xuhongxu.xiaoya.Adapter;

import android.content.Context;
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

        holder.title.setText(item.building);

        String[] rooms = item.getRoomList();
        String detailText = "";
        SortedMap<String, String> roomType = new TreeMap<>();
        HashMap<String, Integer> roomN = new HashMap<>();

        for (String r : rooms) {
            String k;
            switch (item.building) {
                case "邱季端体育馆":
                    k = r.substring(3, 5);
                    r = r.substring(3);
                    break;
                case "科技楼C区":
                    k = r.substring(5, 6);
                    r = r.substring(5);
                    break;
                default:
                    k = r.substring(0, 2);
                    break;
            }
            if (roomType.containsKey(k)) {
                if (roomN.get(k) % 4 == 0) {
                    roomType.put(k, roomType.get(k) + "\n" + r);
                } else {
                    roomType.put(k, roomType.get(k) + "\t\t" + r);
                }
                roomN.put(k, roomN.get(k) + 1);
            } else {
                roomType.put(k, r);
                roomN.put(k, 1);
            }
        }

        for (Map.Entry<String, String> entry : roomType.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            detailText += value + "\n\n";
        }

        holder.detail.setText(detailText);

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
                    notifyItemChanged(prev);
                }
                // Set the current position to "expanded"
                notifyItemChanged(selected);
            }
        });
    }

    @Override
    public int getItemCount(){
        return values.size();
    }

    public final static class RoomItemViewHolder extends RecyclerView.ViewHolder {
        public TextView title, detail;
        public LinearLayout card;

        public RoomItemViewHolder(View itemView) {
            super(itemView);

            card = (LinearLayout) itemView.findViewById(R.id.card_content);
            title = (TextView) itemView.findViewById(R.id.title);
            detail = (TextView) itemView.findViewById(R.id.detail);
        }
    }
}
