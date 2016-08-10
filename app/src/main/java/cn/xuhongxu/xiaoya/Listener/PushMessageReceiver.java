package cn.xuhongxu.xiaoya.Listener;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;
import cn.xuhongxu.xiaoya.R;

/**
 * Created by xuhon on 2016/8/10.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class PushMessageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
            Log.d("bmob", "客户端收到推送内容：" + intent.getStringExtra("msg"));
            JSONObject jsonObject;
            String msg = "收到一条推送，内容获取失败！";
            try {
                jsonObject = new JSONObject(intent.getStringExtra("msg"));
                msg = jsonObject.getString("alert");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_school_black)
                    .setContentTitle(context.getString(R.string.push_title))
                    .setContentText(msg);

            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, builder.build());
        }
    }
}
