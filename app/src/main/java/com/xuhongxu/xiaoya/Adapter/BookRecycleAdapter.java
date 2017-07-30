package com.xuhongxu.xiaoya.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import com.xuhongxu.xiaoya.Fragment.LibraryFragment;
import com.xuhongxu.xiaoya.R;

/**
 * Created by xuhongxu on 16/5/10.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class BookRecycleAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final int TYPE_BOOK = 0;
    public final int TYPE_LOAD = 1;

    private List<LibraryFragment.Book> values;
    private Context context;
    OnLoadMoreListener loadMoreListener;
    boolean isLoading = false, isMoreDataAvailable = true;

    public BookRecycleAdapter(Context ct, List<LibraryFragment.Book> items) {
        values = items;
        context = ct;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_BOOK) {
            View itemView = inflater.inflate(R.layout.list_item_book, parent, false);
            return new BookItemViewHolder(itemView);
        } else {
            return new LoadHolder(inflater.inflate(R.layout.row_load, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        if(position>=getItemCount()-1 && isMoreDataAvailable && !isLoading && loadMoreListener!=null){
            isLoading = true;
            loadMoreListener.onLoadMore();
        }

        if(getItemViewType(position) == TYPE_BOOK) {
            LibraryFragment.Book item = values.get(position);

            BookItemViewHolder holder = (BookItemViewHolder) viewHolder;

            holder.title.setText(item.title);
            holder.author.setText("作者：" + item.author);
            holder.publisher.setText("出版社：" + item.publisher + " (" + item.year + ")");
            holder.isbn.setText("ISBN：" + item.isbn);
            holder.position.setText("索书号：" + item.position);
            holder.status.setText(item.status);

            holder.card.setOnClickListener(v -> {
                String isbn = item.isbn.replace("-", "").replace(" ", "").trim();

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri content_url = Uri.parse("http://book.douban.com/isbn/" + isbn);
                intent.setData(content_url);
                context.startActivity(Intent.createChooser(intent, "请选择浏览器"));
            });
        }
    }
    @Override
    public int getItemViewType(int position) {
        if(values.get(position).type == 0){
            return TYPE_BOOK;
        }else{
            return TYPE_LOAD;
        }
    }

    @Override
    public int getItemCount(){
        return values.size();
    }

    public final static class BookItemViewHolder extends RecyclerView.ViewHolder {
        public TextView title, author, publisher, isbn, position, status;
        public LinearLayout card;

        public BookItemViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            author = (TextView) itemView.findViewById(R.id.author);
            publisher = (TextView) itemView.findViewById(R.id.publisher);
            isbn = (TextView) itemView.findViewById(R.id.isbn);
            position = (TextView) itemView.findViewById(R.id.position);
            status = (TextView) itemView.findViewById(R.id.status);
            card = (LinearLayout) itemView.findViewById(R.id.card_content);
        }
    }

    static class LoadHolder extends RecyclerView.ViewHolder{
        public LoadHolder(View itemView) {
            super(itemView);
        }
    }

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }

    /* notifyDataSetChanged is final method so we can't override it
         call adapter.notifyDataChanged(); after update the list
         */
    public void notifyDataChanged(){
        notifyDataSetChanged();
        isLoading = false;
    }


    public interface OnLoadMoreListener{
        void onLoadMore();
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }
}
