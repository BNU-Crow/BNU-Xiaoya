package com.xuhongxu.xiaoya.View;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.v13.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.OverScroller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.xuhongxu.xiaoya.R;

/**
 * Created by xuhon on 2016/8/11.
 *
 * @author Hongxu Xu
 * @version 0.1
 */
public class TimeTableView extends View {

    public interface TableListener {
        void onRemoveCourse(String name);
    }

    public void addListener(TableListener listener) {
        mListener = listener;
    }

    TableListener mListener = null;

    private List<Rectangle> classes;
    Paint paint, paintDup, paintNew;
    Paint paintCurrent;
    Paint weekPaint, numberPaint;
    TextPaint txtPaint;
    int day, hour, minute;

    final private int w = 100;
    final private int h = 50;

    private int offsetX = 0, offsetY = 0;
    private OverScroller mScroller;

    private ScaleGestureDetector scaleDetector;
    private GestureDetector gestureDetector;
    private float scaleFactor = 1.0f;

    private int []weekNames = {R.string.monday, R.string.tuesday, R.string.wednesday,
            R.string.thursday, R.string.friday, R.string.saturday, R.string.sunday};

    int[] slots = new int[7 * 12];

    private void init() {
        classes = new ArrayList<>();

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#CCFFCC"));
        paint.setStyle(Paint.Style.FILL);

        paintDup = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintDup.setColor(Color.parseColor("#77AA77"));
        paintDup.setStyle(Paint.Style.FILL);

        paintNew = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintNew.setColor(Color.parseColor("#AA8855"));
        paintNew.setStyle(Paint.Style.FILL);

        paintCurrent = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCurrent.setColor(Color.parseColor("#CCCCFF"));
        paintCurrent.setStyle(Paint.Style.FILL);

        txtPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setColor(Color.BLACK);
        txtPaint.setStyle(Paint.Style.FILL);
        txtPaint.setTextSize(px(12));

        weekPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        weekPaint.setColor(Color.parseColor("#3F51B5"));
        weekPaint.setStyle(Paint.Style.FILL);

        numberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        numberPaint.setColor(Color.parseColor("#FFFFFF"));
        numberPaint.setStyle(Paint.Style.FILL);

        Calendar now = Calendar.getInstance();
        day = now.get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.SUNDAY) day = 8;
        day -= 2;

        hour = now.get(Calendar.HOUR_OF_DAY);
        minute = now.get(Calendar.MINUTE);

        mScroller = new OverScroller(getContext());

        scaleDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scaleFactor *= detector.getScaleFactor();
                scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));

                invalidate();

                return true;
            }

        });

        gestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {


            @Override
            public boolean onDown(MotionEvent motionEvent) {
                // Aborts any active scroll animations and invalidates.
                mScroller.forceFinished(true);
                ViewCompat.postInvalidateOnAnimation(TimeTableView.this);
                return true;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                float x = motionEvent.getX() - px(21) * scaleFactor + offsetX;
                float y = motionEvent.getY() - px(31) * scaleFactor + offsetY;
                int week = (int) (x / px(w + 2) / scaleFactor);
                int n = (int) (y / px(h + 2) / scaleFactor);

                String info = "";
                String name = "";

                for (Rectangle c : getClasses()) {
                    if (c.day == week && c.start <= n && c.end >= n) {
                        info += "\n" + c.text + "\n\n";
                        name = c.name;
                    }
                }

                if (name.isEmpty()) return true;

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.class_details);
                builder.setMessage(info);
                final String finalName = name;
                builder.setPositiveButton(R.string.delete_course, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mListener != null) {
                            mListener.onRemoveCourse(finalName);
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                Dialog dialog = builder.create();
                dialog.show();
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float x, float y) {

                mScroller.forceFinished(true);

                int dx = (int) x;
                int dy = (int) y;
                int newX = (offsetX + dx);
                int newY = (offsetY + dy);

                if (newX < 0) {
                    dx -= newX;
                } else if (newX > getMaxX()) {
                    dx -= newX - getMaxX();
                }
                if (newY < 0) {
                    dy -= newY;
                } else if (newY > getMaxY()) {
                    dy -= newY - getMaxY();
                }
                mScroller.startScroll(offsetX, offsetY, dx, dy, 0);
                ViewCompat.postInvalidateOnAnimation(TimeTableView.this);

                return true;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float vx, float vy) {
                fling((int) -vx, (int) -vy);
                return true;
            }

            private void fling(int velocityX, int velocityY) {
                mScroller.forceFinished(true);
                mScroller.fling(
                        offsetX,
                        offsetY,
                        velocityX,
                        velocityY,
                        0, getMaxX(),
                        0, getMaxY());
                ViewCompat.postInvalidateOnAnimation(TimeTableView.this);
            }
        });

    }

    int getMaxX() {
        return (int) Math.max(px(7 * w + 14 + 21) * scaleFactor - getWidth(), 0);
    }
    int getMaxY() {
        return (int) Math.max(px(12 * h + 24 + 31) * scaleFactor - getHeight(), 0);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            offsetX = mScroller.getCurrX();
            offsetY = mScroller.getCurrY();
            postInvalidate();
        }
    }

    private float dp(int px) {
        return ((float) px / getResources().getDisplayMetrics().density);
    }

    private int px(float dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
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

    public List<Rectangle> getClasses() {
        return classes;
    }

    public void setClasses(List<Rectangle> classes) {
        this.classes = classes;
    }

    static public class Rectangle {
        public String text;
        public int day;
        public int start, end;
        public String name, teacher, loc;

        public Rectangle(String text, int day, int start, int end) {
            this.text = text;
            this.day = day;
            this.start = start;
            this.end = end;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.scale(scaleFactor, scaleFactor);

        canvas.save();
        canvas.translate(px(21) - offsetX / scaleFactor, px(31) - offsetY / scaleFactor);

        for (int i = 0; i < 7 * 12; ++i) {
            slots[i] = 0;
        }

        for (Rectangle c : getClasses()) {

            Paint cp;
            if (c.day == day) {
                cp = paintCurrent;
            } else {
                cp = paint;
            }

            txtPaint.setColor(Color.BLACK);

            if (c.name.contains(" (蹭)")) {
                cp = paintNew;
                txtPaint.setColor(Color.WHITE);
            }

            for (int i = c.start; i <= c.end; ++i) {
                int index = c.day * 12 + i;
                if (slots[index] != 0) {
                    cp = paintDup;
                    txtPaint.setColor(Color.WHITE);
                }
                slots[index]++;
            }

            canvas.drawRect(px(c.day * (w + 2) + 1), px(c.start * (h + 2) + 1), px(c.day * (w + 2) + w + 1), px(c.end * (h + 2) + h + 1), cp);
            StaticLayout sl = new StaticLayout(c.text, txtPaint, px(w - 8), Layout.Alignment.ALIGN_CENTER, 1, 1, true);
            canvas.save();
            canvas.translate(px(c.day * (w + 2) + 5), px(c.start * (h + 2) + 5));
            sl.draw(canvas);
            canvas.restore();
        }
        canvas.restore();

        txtPaint.setColor(Color.WHITE);

        canvas.save();
        canvas.translate(px(21) - offsetX / scaleFactor, px(0));
        for (int i = 0; i < 7; ++i) {
            canvas.drawRect(px(i * (w + 2) + 1), px(0), px(i * (w + 2) + w + 1), px(30), weekPaint);
            StaticLayout sl = new StaticLayout(getContext().getString(weekNames[i]), txtPaint, px(w - 8), Layout.Alignment.ALIGN_CENTER, 1, 1, true);
            canvas.save();
            canvas.translate(px(i * (w + 2) + 5), px(4));
            sl.draw(canvas);
            canvas.restore();
        }
        canvas.restore();

        canvas.save();
        txtPaint.setColor(Color.BLACK);
        canvas.drawRect(px(0), px(0), px(20), px((h + 2) * 12 + 31), numberPaint);
        canvas.translate(0, px(31) - offsetY / scaleFactor);
        for (int i = 0; i < 12; ++i) {
            StaticLayout sl = new StaticLayout("" + (i + 1), txtPaint, px(20 - 2), Layout.Alignment.ALIGN_CENTER, 1, 1, true);
            canvas.save();
            canvas.translate(px(1), px(i * (h + 2) + 19));
            sl.draw(canvas);
            canvas.restore();
        }
        canvas.restore();

        canvas.restore();
        canvas.clipRect(0, 0, getWidth(), getHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        scaleDetector.onTouchEvent(e);
        gestureDetector.onTouchEvent(e);
        return true;
    }

}
