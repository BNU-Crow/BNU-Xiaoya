package cn.xuhongxu.xiaoya.View;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by xuhon on 2016/8/11.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class TimeTableView extends View {
    public List<Rectangle> classes;
    Paint paint;
    Paint paintCurrent;
    TextPaint txtPaint;
    int day, hour, minute;

    private void init() {
        classes = new ArrayList<>();

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#CCFFCC"));
        paint.setStyle(Paint.Style.FILL);

        paintCurrent = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCurrent.setColor(Color.parseColor("#CCCCFF"));
        paintCurrent.setStyle(Paint.Style.FILL);

        txtPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setColor(Color.BLACK);
        txtPaint.setStyle(Paint.Style.FILL);
        txtPaint.setTextSize(px(12));

        Calendar now = Calendar.getInstance();
        day = now.get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.SUNDAY) day = 8;
        day -= 2;

        hour = now.get(Calendar.HOUR_OF_DAY);
        minute = now.get(Calendar.MINUTE);
    }

    private int px(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = px(714);
        int h = px(624);
        setMeasuredDimension(w, h);
    }

    public TimeTableView(Context context) {
        super(context);
        init();
    }

    public TimeTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimeTableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TimeTableView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    static public class Rectangle {
        public String name;
        public int day;
        public int start, end;
        public Rectangle(String name, int day, int start, int end) {
            this.name = name;
            this.day = day;
            this.start = start;
            this.end = end;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Rectangle c : classes) {
            Paint cp;
            if (c.day == day) {
                cp = paintCurrent;
            } else {
                cp = paint;
            }
            canvas.drawRect(px(c.day * 102 + 1), px(c.start * 52 + 1), px(c.day * 102 + 101), px(c.end * 52 + 51), cp);
            StaticLayout sl = new StaticLayout(c.name, txtPaint, px(92), Layout.Alignment.ALIGN_CENTER, 1, 1, true);
            canvas.save();
            canvas.translate(px(c.day * 102 + 5), px(c.start * 52 + 5));
            sl.draw(canvas);
            canvas.restore();
        }
    }

}
