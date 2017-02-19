package cn.xuhongxu.xiaoya.Fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.xuhongxu.xiaoya.Adapter.BookRecycleAdapter;
import cn.xuhongxu.xiaoya.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LibraryFragment extends Fragment {

    public class Book {
        public int type = 0;
        public String title = "";
        public String author = "";
        public String publisher = "";
        public String position = "";
        public String year = "";
        public String isbn = "";
        public String status = "";
    }

    private Button searchButton;
    private EditText editText;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private BookRecycleAdapter adapter;

    private Map<String, String> cookies;
    private int timeout = 10000;
    private String key = "";
    private InitTask initTask;
    private ArrayList<Book> books = new ArrayList<>();

    public LibraryFragment() {
        // Required empty public constructor
    }

    void init() {
        try {
            Connection.Response res = Jsoup.connect("http://opac.lib.bnu.edu.cn:8080/F/")
                    .timeout(timeout)
                    .method(Connection.Method.GET)
                    .execute();
            cookies = res.cookies();
            String html = res.body();

            Pattern pattern = Pattern.compile("http://opac.lib.bnu.edu.cn:8080/F/(.*?)\\?';");
            Matcher matcher = pattern.matcher(html);
            if (matcher.find() && matcher.groupCount() > 0) {
                key = matcher.group(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class InitTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            init();
            return null;
        }
    }

    ArrayList<Book> search(String keyword, String index) {
        String url;
        if (index.equals("1")) {
            url = "http://opac.lib.bnu.edu.cn:8080/F/" + key + "?func=find-b&find_code=WRD" +
                    "&request=" + keyword + "&local_base=BNU03&adjacent=N";
        } else {
            url = "http://opac.lib.bnu.edu.cn:8080/F/" + key
                    + "?func=short-jump&jump=" + index;
        }
        ArrayList<Book> tbooks = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).get();

            Elements items = doc.getElementsByClass("items");
            for (Element item : items) {
                Book book = new Book();
                Elements titleEls = item.getElementsByClass("itemtitle");
                if (titleEls.size() > 0) {
                    book.title = titleEls.get(0).child(0).text();
                    book.title = book.title.replace("&nbsp;", " ");
                }
                Elements vals = item.getElementsByClass("content");
                if (vals.size() > 5) {
                    book.author = vals.get(0).text();
                    book.position = vals.get(1).text();
                    if (book.position.length() > 0) {
                        if (book.position.toCharArray()[0] <= 'H') {
                            book.position += " 四楼借阅";
                        } else {
                            book.position += " 五楼借阅";
                        }
                        if (book.position.toCharArray()[0] <= 'I') {
                            book.position += " 六楼阅览";
                        } else {
                            book.position += " 七楼阅览";
                        }
                    }
                    book.publisher = vals.get(2).text();
                    book.year = vals.get(3).text();
                    book.isbn = vals.get(5).text();
                    Elements statusEls = item.getElementsByClass("libs");
                    if (statusEls.size() > 0) {
                        book.status = statusEls.get(0).child(0).text();
                        tbooks.add(book);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tbooks;
    }

    private class SearchTask extends AsyncTask<String, Void, ArrayList<Book>> {
        @Override
        protected ArrayList<Book> doInBackground(String... strings) {
            return search(strings[0], strings[1]);
        }

        @Override
        protected void onPostExecute(ArrayList<Book> result) {
            if (books.size() != 0) {
                books.remove(books.size() - 1);
            }
            progressBar.setVisibility(View.GONE);
            if (result.size() == 0) {
                adapter.setMoreDataAvailable(false);
            } else {
                books.addAll(result);
            }
            adapter.notifyDataSetChanged();
            adapter.notifyDataChanged();
        }
    }

    private void loadMore(int index) {
        Book loadBook = new Book();
        loadBook.type = 1;
        books.add(loadBook);
        adapter.notifyItemInserted(books.size() - 1);
        new SearchTask().execute(editText.getText().toString(), String.valueOf(index));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_library, container, false);
        searchButton = (Button) v.findViewById(R.id.search);

        recyclerView = (RecyclerView) v.findViewById(R.id.books_recycler);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BookRecycleAdapter(getActivity(), books);
        adapter.setLoadMoreListener(new BookRecycleAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        int index = books.size() + 1;
                        loadMore(index);
                    }
                });
            }
        });
        recyclerView.setAdapter(adapter);

        progressBar = (ProgressBar) v.findViewById(R.id.loadingBooks);
        editText = (EditText) v.findViewById(R.id.bookName);

        initTask = new InitTask();
        initTask.execute();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (initTask.getStatus() != AsyncTask.Status.FINISHED) {
                    Snackbar.make(v, "请等待初始化完毕！", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                adapter.setMoreDataAvailable(true);
                books.clear();
                progressBar.setVisibility(View.VISIBLE);
                new SearchTask().execute(editText.getText().toString(), "1");

            }
        });

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        AVAnalytics.onFragmentEnd(getClass().getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        AVAnalytics.onFragmentStart(getClass().getName());
    }
}
