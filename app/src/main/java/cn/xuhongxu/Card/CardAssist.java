package cn.xuhongxu.Card;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xuhon on 2016/9/19.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class CardAssist {

    private String username;
    private String password;

    private int timeout = 30000;

    private Map<String, String> cookies;

    public static final String BASE_URL = "http://card.bnu.edu.cn/";
    public static final String CODE_URL =
            "http://card.bnu.edu.cn/Account/GetCheckCodeImg/Flag=";
    public static final String LOGIN_URL = "http://card.bnu.edu.cn/Account/MiniCheckIn";
    public static final String BASIC_INFO_URL =
            "http://card.bnu.edu.cn/CardManage/CardInfo/BasicInfo?_=";
    public static final String TRANSFER_URL =
            "http://card.bnu.edu.cn/CardManage/CardInfo/TransferAccount";
    public static final String KEYPAD_URL =
            "http://card.bnu.edu.cn/Account/GetNumKeyPadImg";

    public CardAssist(String username, String password) {
        cookies = new HashMap<>();
        this.setUsername(username);
        this.setPassword(password);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public Drawable fetchCode() throws IOException {

        Connection.Response res = Jsoup.connect(BASE_URL)
                .header("Host", "card.bnu.edu.cn")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2859.0 Safari/537.36")
                .timeout(getTimeout())
                .method(Connection.Method.GET)
                .execute();

        setCookies(res.cookies());


        URLConnection conn = new URL(CODE_URL + Math.random()).openConnection();
        conn.setDoOutput(true);
        for (Map.Entry<String, String> cookie : getCookies().entrySet()) {
            conn.setRequestProperty("Cookie", cookie.getKey() + "=" + cookie.getValue());
        }
        return Drawable.createFromStream(conn.getInputStream(), "code");
    }

    public void login(String code) throws Exception {

         Connection.Response res = Jsoup.connect(LOGIN_URL)
                .timeout(getTimeout())
                .cookies(getCookies())
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Referer", "http://card.bnu.edu.cn")
                .header("Origin", "http://card.bnu.edu.cn")
                // .header("X-Requested-With", "XMLHttpRequest")
                .header("Host", "card.bnu.edu.cn")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2859.0 Safari/537.36")
                .data("signtype", "UIAS")
                .data("username", getUsername())
                .data("password", getPassword())
                .data("checkcode", code)
                .data("isUsedKeyPad", "false")
                .method(Connection.Method.POST)
                .execute();

        setCookies(res.cookies());

        if (!res.body().contains("success")) {
            throw new Exception(res.body());
        }
    }

    public CardInfo getInfo() throws IOException {

        CardInfo info = new CardInfo();

        Document doc = Jsoup.connect(BASIC_INFO_URL + Math.random())
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Referer", "http://card.bnu.edu.cn")
                .header("Host", "card.bnu.edu.cn")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2859.0 Safari/537.36")
                .timeout(getTimeout())
                .cookies(cookies)
                .get();

        if (doc.outerHtml().contains("登录")) {
            throw new IOException("登录状态过期");
        }

        Elements infoP = doc.select(".userInfoR p em");

        info.setName(infoP.get(0).text());
        info.setId(infoP.get(1).text());
        info.setCardId(infoP.get(2).text());
        info.setBalance(infoP.get(3).text());
        info.setTransition(infoP.get(4).text());
        info.setStatus(infoP.get(5).text());
        info.setFrozen(infoP.get(6).text());

        return info;

    }

    public String transfer(String password, String code, String money) throws IOException, JSONException {

        Connection.Response res = Jsoup.connect(TRANSFER_URL)
                .ignoreContentType(true)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Referer", "http://card.bnu.edu.cn")
                .header("Host", "card.bnu.edu.cn")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2859.0 Safari/537.36")
                .timeout(getTimeout())
                .cookies(cookies)
                .data("password", password)
                .data("checkcode", code)
                .data("amt", money)
                .data("fcard", "bcard")
                .data("tocard", "card")
                .data("bankno", "")
                .data("bankpwd", "")
                .method(Connection.Method.POST)
                .execute();

        if (res.body().contains("登录")) {
            throw new IOException("登录状态过期");
        }

        JSONObject object = new JSONObject(res.body());
        return object.getString("msg");
    }

    public Drawable fetchKeypad() throws IOException {

        URLConnection conn = new URL(KEYPAD_URL).openConnection();
        conn.setDoOutput(true);
        for (Map.Entry<String, String> cookie : getCookies().entrySet()) {
            conn.setRequestProperty("Cookie", cookie.getKey() + "=" + cookie.getValue());
        }
        return Drawable.createFromStream(conn.getInputStream(), "keypad");
    }
}
