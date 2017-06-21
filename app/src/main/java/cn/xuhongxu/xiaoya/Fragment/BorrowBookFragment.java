package cn.xuhongxu.xiaoya.Fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.avos.avoscloud.AVAnalytics;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.xuhongxu.xiaoya.Activity.ToolboxActivity;
import cn.xuhongxu.xiaoya.Adapter.BorrowBookRecycleAdapter;
import cn.xuhongxu.xiaoya.Helper.BorrowBook;
import cn.xuhongxu.xiaoya.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BorrowBookFragment extends Fragment {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    Button loginButton, renewButton;
    String key = "";

    private Map<String, String> cookies;
    private ArrayList<BorrowBook> borrowBooks = new ArrayList<>();
    private BorrowBookRecycleAdapter adapter;

    private int timeout = 30000;
    boolean isShown = false;

    public BorrowBookFragment() {
        // Required empty public constructor
    }

    ArrayList<BorrowBook> login(String usnm, String pwd) {
        ArrayList<BorrowBook> books = new ArrayList<>();
        String localKey = "";
        try {
            Connection.Response res = Jsoup.connect("http://opac.lib.bnu.edu.cn:8080/F/?func=bor-loan&adm_library=")
                    .timeout(timeout)
                    .method(Connection.Method.GET)
                    .execute();
            Pattern pattern = Pattern.compile("http://opac.lib.bnu.edu.cn:8080/F/(.*?)\\?func");
            Matcher matcher = pattern.matcher(res.body());
            if (matcher.find() && matcher.groupCount() > 0) {
                localKey = matcher.group(1);
            }
            cookies = res.cookies();
            res = Jsoup.connect("http://opac.lib.bnu.edu.cn:8080/pds")
                    .timeout(timeout)
                    .cookies(cookies)
                    .data("func", "login")
                    .data("calling_system", "bnuhome")
                    .data("term1", "short")
                    .data("selfreg", "")
                    .data("bor_id", usnm)
                    .data("bor_verification", pwd)
                    .data("url", "http://opac.lib.bnu.edu.cn:8080/F/" + localKey + "?func=bor-info")
                    .method(Connection.Method.POST)
                    .execute();
            String loginURL = "";
            pattern = Pattern.compile("href=\"(.*?)\">Click");
            matcher = pattern.matcher(res.body());
            if (matcher.find() && matcher.groupCount() > 0) {
                loginURL = matcher.group(1);
            }
            res = Jsoup.connect("http://opac.lib.bnu.edu.cn:8080" + loginURL).timeout(timeout)
                    .cookies(cookies)
                    .method(Connection.Method.GET)
                    .execute();
            Document doc = Jsoup.connect("http://opac.lib.bnu.edu.cn:8080/F/"
                    + localKey + "?func=bor-loan&adm_library=BNU51")
                    .timeout(timeout)
                    .get();
            try {
                Element table = null;
                for (Element t : doc.getElementsByTag("table")) {
                    Element noEl = t.getElementsByClass("text3").first();
                    if (noEl != null && noEl.text().trim().equals("No.")) {
                        table = t;
                    }
                }
                if (table == null) {
                    return books;
                }
                for (Element tr : table.getElementsByTag("tr")) {
                    if ("tr1".equals(tr.className())) continue;
                    BorrowBook book = new BorrowBook();
                    Elements tds = tr.getElementsByTag("td");
                    book.author = tds.get(2).text();
                    book.title = tds.get(3).text();
                    book.returnDate = tds.get(5).text();
                    book.building = tds.get(7).text();
                    book.position = tds.get(8).text();
                    books.add(book);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            localKey = "";
        }
        key = localKey;
        return books;
    }

    private class LoginTask extends AsyncTask<String, Void, ArrayList<BorrowBook>> {
        @Override
        protected ArrayList<BorrowBook> doInBackground(String... strings) {
            return login(strings[0], strings[1]);
        }

        @Override
        protected void onPostExecute(ArrayList<BorrowBook> result) {
            progressBar.setVisibility(View.GONE);
            borrowBooks.clear();
            borrowBooks.addAll(result);
            adapter.notifyDataSetChanged();
        }
    }

    String renew() {
        String info = "成功！";
        try {
            Document doc = Jsoup.connect("http://opac.lib.bnu.edu.cn:8080/F/" + key
                    + "?func=bor-renew-all&adm_library=BNU51")
                    .timeout(timeout)
                    .get();
            info = doc.getElementsByClass("title").first().text() + "\n\n";
            try {
                Element table = doc.getElementsByTag("table").get(4);
                for (Element tr : table.getElementsByTag("tr")) {
                    if ("tr1".equals(tr.className())) continue;
                    BorrowBook book = new BorrowBook();
                    Elements tds = tr.getElementsByTag("td");
                    info += tds.get(1).text() + "：" + tds.get(8).text() + "\n";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    private class RenewTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            return renew();
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle("提示");
            alert.setMessage(result);
            alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            alert.show();

            SharedPreferences preferences =
                    getActivity().getSharedPreferences(getString(R.string.library_key),
                            Context.MODE_PRIVATE);

            String username = preferences.getString("username", "");
            String password = preferences.getString("password", "");

            if (preferences.contains("username") && preferences.contains("password")) {
                progressBar.setVisibility(View.VISIBLE);
                new LoginTask().execute(username, password);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            key = savedInstanceState.getString("KEY", "");
            borrowBooks = savedInstanceState.getParcelableArrayList("BOOKS");
        }
    }

    ToolboxActivity.LibraryLoginListener listener = new ToolboxActivity.LibraryLoginListener() {
        @Override
        public void logined(String usnm, String pwd) {
            key = "";
            if (getUserVisibleHint()) {
                progressBar.setVisibility(View.VISIBLE);
                new LoginTask().execute(usnm, pwd);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_borrow_book, container, false);
        loginButton = (Button)v.findViewById(R.id.refresh);
        renewButton = (Button)v.findViewById(R.id.renew);
        progressBar = (ProgressBar)v.findViewById(R.id.loading);
        recyclerView = (RecyclerView)v.findViewById(R.id.book_list);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BorrowBookRecycleAdapter(getContext(), borrowBooks);
        recyclerView.setAdapter(adapter);

        ToolboxActivity activity = (ToolboxActivity)getActivity();
        activity.addLibraryLoginListener(listener);

        final SharedPreferences preferences =
                getActivity().getSharedPreferences(getString(R.string.library_key),
                        Context.MODE_PRIVATE);

        final String username = preferences.getString("username", "");
        final String password = preferences.getString("password", "");

        if (isShown && key.isEmpty() && preferences.contains("username") && preferences.contains("password")) {
            progressBar.setVisibility(View.VISIBLE);
            new LoginTask().execute(username, password);
        }

        loginButton.setOnClickListener(view -> {
            String usnm = preferences.getString("username", "");
            String pwd = preferences.getString("password", "");
            key = "";
            progressBar.setVisibility(View.VISIBLE);
            new LoginTask().execute(usnm, pwd);
        });

        renewButton.setOnClickListener(view -> {
            if (key.isEmpty()) {
                Snackbar.make(v, "请先登录或等待登录完成！", Snackbar.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            new RenewTask().execute();
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("KEY", key);
        outState.putParcelableArrayList("BOOKS", borrowBooks);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ToolboxActivity activity = (ToolboxActivity) getActivity();
        activity.removeLibraryLoginListener(listener);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isShown = isVisibleToUser;
        if (!this.isAdded()) return;
        if (isVisibleToUser) {
            SharedPreferences preferences =
                    getContext().getSharedPreferences(getString(R.string.library_key),
                            Context.MODE_PRIVATE);

            String username = preferences.getString("username", "");
            String password = preferences.getString("password", "");

            if (key.isEmpty() && preferences.contains("username") && preferences.contains("password")) {
                progressBar.setVisibility(View.VISIBLE);
                new LoginTask().execute(username, password);
            }
        }
    }
}
