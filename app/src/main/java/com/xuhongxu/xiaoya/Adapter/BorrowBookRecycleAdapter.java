package com.xuhongxu.xiaoya.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.xuhongxu.xiaoya.Helper.BorrowBook;
import com.xuhongxu.xiaoya.R;

/**
 * Created by xuhongxu on 16/5/10.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class BorrowBookRecycleAdapter
        extends RecyclerView.Adapter<BorrowBookRecycleAdapter.BookItemViewHolder> {

    private List<BorrowBook> values;
    private Context context;
    private int selected = -1;

    public void reset() {
        selected = -1;
    }

    public BorrowBookRecycleAdapter(Context ct, List<BorrowBook> items) {
        values = items;
        context = ct;
    }

    @Override
    public BookItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.list_item_borrow,
                        parent,
                        false);
        return new BookItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final BookItemViewHolder holder, int position) {

        BorrowBook item = values.get(position);

        holder.title.setText(item.title);
        holder.author.setText("作者：" + item.author);
        holder.pos.setText("索书号：" + item.position);
        holder.building.setText("分馆：" + item.building);
        holder.returnDate.setText("应还日期：" + item.returnDate);
    }

    @Override
    public int getItemCount(){
        return values.size();
    }

    public final static class BookItemViewHolder extends RecyclerView.ViewHolder {
        public TextView title, author, pos, building, returnDate;

        public BookItemViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            author = (TextView) itemView.findViewById(R.id.author);
            pos = (TextView) itemView.findViewById(R.id.position);
            building = (TextView) itemView.findViewById(R.id.building);
            returnDate = (TextView) itemView.findViewById(R.id.returnDate);
        }
    }
}
