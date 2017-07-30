package com.xuhongxu.xiaoya.Service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.widget.Toast
import com.xuhongxu.Assist.ExamScore
import com.xuhongxu.Assist.SchoolworkAssist
import com.xuhongxu.xiaoya.Activity.LoginActivity
import com.xuhongxu.xiaoya.R
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashSet

class QueryScoreService : Service() {

    private var timer: Timer? = null
    private val QUERY_INTERVAL: Long = 15 * 60 * 1000

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        if (timer != null) {
            timer!!.cancel()
        }
        timer = Timer()
        timer!!.scheduleAtFixedRate(QueryScoreTask(applicationContext), 0, QUERY_INTERVAL)
    }

    class QueryScoreTask(val mycontext: Context) : TimerTask() {

        val OLD_MAJOR = "OLD_MAJOR"
        val OLD_MINOR = "OLD_MINOR"

        private var oldMajorScores: Set<String>
        private var oldMinorScores: Set<String>

        val preferences = mycontext.getSharedPreferences(mycontext.getString(R.string.preference_key),
                Context.MODE_PRIVATE)

        val username = preferences.getString("username", "")
        val password = preferences.getString("password", "")

        init {
            oldMajorScores = preferences.getStringSet(OLD_MAJOR, HashSet<String>())
            oldMinorScores = preferences.getStringSet(OLD_MINOR, HashSet<String>())
        }

        override fun run() {

            if (username!!.isEmpty() && password!!.isEmpty()) {
                return
            }

            val assist = SchoolworkAssist(username, password, mycontext)

            var tip: String

            assist.login()

            val majorScores: HashSet<String> = HashSet()

            assist.getExamScores(true).forEach { score ->
                majorScores.add(score.courseName + ": " + score.score)
            }

            val minorScores: HashSet<String> = HashSet()

            assist.getExamScores(false).forEach { score ->
                minorScores.add(score.courseName + ": " + score.score)
            }

            if (minorScores.count() > oldMinorScores.count()
                    || majorScores.count() > oldMajorScores.count()) {

                tip = "主修：" + majorScores.minus(oldMajorScores).joinToString() + " || "
                tip += "辅修：" + minorScores.minus(oldMinorScores).joinToString()

                val notificationManager = mycontext.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
                val intent = Intent(mycontext, LoginActivity::class.java)

                val notification = NotificationCompat.Builder(mycontext)
                        .setContentTitle("成绩更新")
                        .setContentText(tip)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(PendingIntent.getActivity(mycontext, 0, intent, 0))
                        .setChannelId("DEFAULT")
                        .build()
                notificationManager.notify(0, notification)
            }

            preferences.edit()
                    .putStringSet(OLD_MINOR, minorScores)
                    .putStringSet(OLD_MAJOR, majorScores)
                    .apply()

        }

    }
}
