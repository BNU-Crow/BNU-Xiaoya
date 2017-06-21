package cn.xuhongxu.LibrarySeat;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by xuhon on 5/17/2017.
 */

public class SeatClient {
    private static final String URL_LOGIN = "http://219.224.23.151/rest/auth?username=%s&password=%s";
    private static final String URL_HISTORY = "http://219.224.23.151/rest/v2/history/%s/%s?token=%s";
    private static final String URL_BUILDING = "http://219.224.23.151/rest/v2/free/filters?token=%s";
    private static final String URL_ROOM = "http://219.224.23.151/rest/v2/room/stats2/%s?token=%s";
    private static final String URL_CURRENT_RESERVATION = "http://219.224.23.151/rest/v2/user/reservations/?token=%s";
    private static final String URL_RESERVATION = "http://219.224.23.151/rest/view/%s?token=%s";
    private static final String URL_CANCEL_RESERVATION = "http://219.224.23.151/rest/v2/cancel/%s?token=%s";
    private static final String URL_LAYOUT = "http://219.224.23.151/rest/v2/room/layoutByDate/%s/%s?token=%s";
    private static final String URL_START_TIME = "http://219.224.23.151/rest/v2/startTimesForSeat/%s/%s?token=%s";
    private static final String URL_END_TIME = "http://219.224.23.151/rest/v2/endTimesForSeat/%s/%s/%s?token=%s";
    private static final String URL_ORDER = "http://219.224.23.151/rest/v2/freeBook";
    private static final String URL_CHECKIN = "http://219.224.23.151/rest/v2/checkIn?token=%s";
    private static final String URL_LEAVE = "http://219.224.23.151/rest/v2/leave?token=%s";
    private static final String URL_STOP = "http://219.224.23.151/rest/v2/stop?token=%s";

    private String username, password;

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

    private String token = "";

    public SeatClient() {
    }

    public SeatClient(String username, String password) {
        setUsername(username);
        setPassword(password);
    }

    public String login() {
        try {
            Connection.Response res = Jsoup.connect(String.format(URL_LOGIN, username, password))
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute();

            JSONObject object = new JSONObject(res.body());

            if (object.getString("status").equals("success")) {
                setToken(object.getJSONObject("data").getString("token"));
                return null;
            } else {
                return object.getString("message");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
    }

    public List<ReservationHistory> getReservationHistory(int start, int end) {
        List<ReservationHistory> histories = new ArrayList<>();
        try {
            Connection.Response res = Jsoup.connect(String.format(URL_HISTORY, start, end, getToken()))
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute();

            JSONObject object = new JSONObject(res.body());

            if (object.getString("status").equals("success")) {
                JSONObject data = object.getJSONObject("data");
                JSONArray arr = data.getJSONArray("reservations");
                for (int i = 0; i < arr.length(); ++i) {
                    JSONObject history = arr.getJSONObject(i);
                    histories.add(new ReservationHistory(
                            history.getString("id"),
                            history.getString("date"),
                            history.getString("begin"),
                            history.getString("end"),
                            history.getString("loc"),
                            history.getString("stat")
                    ));
                }
            } else {
                histories.add(new ReservationHistory("", "", "", "", object.getString("message"), ""));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return histories;
    }

    public Reservation getCurrentReservation() {
        try {
            Connection.Response res = Jsoup.connect(String.format(URL_CURRENT_RESERVATION, getToken()))
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute();

            JSONObject object = new JSONObject(res.body());

            if (object.getString("status").equals("success")) {
                if (object.getString("data").equals("null")) {
                    return new Reservation(0, "无预约", "无", 0, "无",
                            "无", "无", "无", false, "无", "无");
                }
                JSONArray arr = object.getJSONArray("data");
                if (arr.length() > 0) {
                    JSONObject o = arr.getJSONObject(0);
                    return new Reservation(
                            o.getInt("id"),
                            o.getString("receipt"),
                            o.getString("onDate"),
                            o.getInt("seatId"),
                            o.getString("status"),
                            o.getString("location"),
                            o.getString("begin"),
                            o.getString("end"),
                            o.getBoolean("userEnded"),
                            o.getString("message"),
                            "" //o.getString("checkedIn")
                    );
                } else {
                    return new Reservation(0, "无预约", "无", 0, "无",
                            "无", "无", "无", false, "无", "无");
                }
            } else {
                return new Reservation(0, "", "", 0, object.getString("message"),
                        "", "", "", false, "", "");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new Reservation(0, "", "", 0, e.getLocalizedMessage(),
                    "", "", "", false, "", "");
        }
    }
    public String cancelReservation() {
            Reservation reservation = getCurrentReservation();
            return cancelReservation("" + reservation.id);
    }

    public String cancelReservation(String id) {
        try {

            Connection.Response res = Jsoup.connect(String.format(URL_CANCEL_RESERVATION, id, getToken()))
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute();

            JSONObject object = new JSONObject(res.body());

            if (object.getString("status").equals("success")) {
                return object.getString("data");
            } else {
                return object.getString("message");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
    }

    public String checkIn() {
        try {

            Reservation reservation = getCurrentReservation();

            Connection.Response res = Jsoup.connect(String.format(URL_CHECKIN, getToken()))
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute();

            JSONObject object = new JSONObject(res.body());

            if (object.getString("status").equals("success")) {
                return object.getString("data");
            } else {
                return object.getString("message");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
    }

    public String leave() {
        try {

            Reservation reservation = getCurrentReservation();

            Connection.Response res = Jsoup.connect(String.format(URL_LEAVE, getToken()))
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute();

            JSONObject object = new JSONObject(res.body());

            if (object.getString("status").equals("success")) {
                return object.getString("data");
            } else {
                return object.getString("message");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
    }

    public String stop() {
        try {

            Reservation reservation = getCurrentReservation();

            Connection.Response res = Jsoup.connect(String.format(URL_STOP, getToken()))
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute();

            JSONObject object = new JSONObject(res.body());

            if (object.getString("status").equals("success")) {
                return object.getString("data");
            } else {
                return object.getString("message");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
    }

    public List<Building> getBuildings() {
        List<Building> buildings = new ArrayList<>();
        try {
            Connection.Response res = Jsoup.connect(String.format(URL_BUILDING, getToken()))
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute();

            JSONObject object = new JSONObject(res.body());

            if (object.getString("status").equals("success")) {
                JSONObject data = object.getJSONObject("data");
                JSONArray arr = data.getJSONArray("buildings");
                for (int i = 0; i < arr.length(); ++i) {
                    JSONArray o = arr.getJSONArray(i);
                    buildings.add(new Building(
                            o.getInt(0),
                            o.getString(1),
                            o.getInt(2)
                    ));
                }
            } else {
                buildings.add(new Building(0, "加载失败", 0));
            }

        } catch (Exception e) {
            e.printStackTrace();
            buildings.add(new Building(0, e.getLocalizedMessage(), 0));
        }
        return buildings;
    }

    public List<Room> getRooms(int buildingId) {
        List<Room> rooms = new ArrayList<>();
        try {
            Connection.Response res = Jsoup.connect(String.format(URL_ROOM, buildingId, getToken()))
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute();

            JSONObject object = new JSONObject(res.body());

            if (object.getString("status").equals("success")) {
                JSONArray arr = object.getJSONArray("data");
                for (int i = 0; i < arr.length(); ++i) {
                    JSONObject o = arr.getJSONObject(i);
                    rooms.add(new Room(
                            o.getInt("roomId"),
                            o.getString("room"),
                            o.getString("floor"),
                            o.getString("reserved"),
                            o.getString("inUse"),
                            o.getString("away"),
                            o.getString("totalSeats"),
                            o.getString("free")
                    ));
                }
            } else {
                rooms.add(new Room(0, "加载失败", "", "", "", "", "", ""));
            }

        } catch (Exception e) {
            e.printStackTrace();
            rooms.add(new Room(0, e.getLocalizedMessage(), "", "", "", "", "", ""));
        }
        return rooms;
    }

    public SeatLayout getSeatLayout(int roomId, String date) {
        SeatLayout layout;
        try {
            Connection.Response res = Jsoup.connect(String.format(URL_LAYOUT, roomId, date, getToken()))
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute();

            JSONObject object = new JSONObject(res.body());

            if (object.getString("status").equals("success")) {
                JSONObject data = object.getJSONObject("data");
                layout = new SeatLayout(data.getInt("id"),
                        data.getString("name"),
                        data.getInt("cols"),
                        data.getInt("rows"));

                JSONObject arr = data.getJSONObject("layout");
                for (Iterator<String> it = arr.keys(); it.hasNext(); ) {
                    String key = it.next();
                    JSONObject o = arr.getJSONObject(key);
                    layout.layout.add(new SeatLayoutItem(
                            o.has("id") ? o.getInt("id") : -1,
                            o.has("name") ? o.getString("name") : "",
                            o.has("type") ? o.getString("type") : "",
                            o.has("status") ? o.getString("status") : "",
                            o.has("power") && o.getBoolean("power")
                    ));
                }
            } else {
                layout = new SeatLayout(0, object.getString("message"), 0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            layout = new SeatLayout(0, e.getLocalizedMessage(), 0, 0);
        }
        return layout;
    }

    public List<SeatTime> getStartTimes(int seatId, String date) {
        List<SeatTime> times = new ArrayList<>();
        try {
            Connection.Response res = Jsoup.connect(String.format(URL_START_TIME, seatId, date, getToken()))
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute();

            JSONObject object = new JSONObject(res.body());

            if (object.getString("status").equals("success")) {
                JSONObject data = object.getJSONObject("data");
                JSONArray arr = data.getJSONArray("startTimes");
                for (int i = 0; i < arr.length(); ++i) {
                    JSONObject o = arr.getJSONObject(i);
                    times.add(new SeatTime(
                            o.getString("value"),
                            o.getString("id")
                    ));
                }
            } else {
                times.add(new SeatTime("加载失败 " + object.getString("message"), "0"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            times.add(new SeatTime(e.getLocalizedMessage(), "0"));
        }
        return times;
    }

    public List<SeatTime> getEndTimes(int seatId, String date, String startTimeId) {
        List<SeatTime> times = new ArrayList<>();
        try {
            Connection.Response res = Jsoup.connect(String.format(URL_END_TIME, seatId, date, startTimeId, getToken()))
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute();

            JSONObject object = new JSONObject(res.body());

            if (object.getString("status").equals("success")) {
                JSONObject data = object.getJSONObject("data");
                JSONArray arr = data.getJSONArray("endTimes");
                for (int i = 0; i < arr.length(); ++i) {
                    JSONObject o = arr.getJSONObject(i);
                    times.add(new SeatTime(
                            o.getString("value"),
                            o.getString("id")
                    ));
                }
            } else {
                times.add(new SeatTime("加载失败 " + object.getString("message"), "0"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            times.add(new SeatTime(e.getLocalizedMessage(), "0"));
        }
        return times;
    }

    public Reservation orderSeat(int seatId, String date, String startTimeId, String endTimeId) {
        if (startTimeId.equals("now")) startTimeId = "-1";
        try {
            Connection.Response res = Jsoup.connect(URL_ORDER)
                    .data("token", getToken())
                    .data("startTime", startTimeId)
                    .data("endTime", endTimeId)
                    .data("seat", "" + seatId)
                    .data("date", date)
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .execute();

            JSONObject object = new JSONObject(res.body());

            if (object.getString("status").equals("success")) {
                JSONObject o = object.getJSONObject("data");
                return new Reservation(
                        o.getInt("id"),
                        o.getString("receipt"),
                        o.getString("onDate"),
                        0,//o.getInt("seatId"),
                        "",//o.getString("status"),
                        o.getString("location"),
                        o.getString("begin"),
                        o.getString("end"),
                        false,//o.getBoolean("userEnded"),
                        "",//o.getString("message"),
                        o.getString("checkedIn")
                );
            } else {
                return new Reservation(0, "失败", "", 0, object.getString("message"),
                        "失败", "失败", "失败", false, "", "");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new Reservation(0, "", "", 0, e.getLocalizedMessage(),
                    "", "", "", false, "", "");
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
