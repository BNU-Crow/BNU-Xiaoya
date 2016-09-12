package cn.xuhongxu.xiaoya.Service;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.Context;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.xuhongxu.xiaoya.Helper.TimetableHelper;
import cn.xuhongxu.xiaoya.R;
import cn.xuhongxu.xiaoya.View.TimeTableView;

public class TimetableAppWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new TimetableRemoteViewsFactory(getApplicationContext(), intent);
    }

    class TimetableRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        private List<TimeTableView.Rectangle> mWidgetItems;
        private Context mContext;
        private int mAppWidgetId;
        private TimetableHelper helper;

        TimetableRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            mWidgetItems = new ArrayList<>();
        }

        @Override
        public void onCreate() {
            try {
                helper = new TimetableHelper(mContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDataSetChanged() {
            mWidgetItems.clear();
            List<TimeTableView.Rectangle> temp = helper.parseTable(helper.calcWeek());
            Calendar now = Calendar.getInstance();
            int day = now.get(Calendar.DAY_OF_WEEK);
            if (day == Calendar.SUNDAY) day = 8;
            day -= 2;
            for (TimeTableView.Rectangle c : temp) {
                if (day == c.day) {
                    mWidgetItems.add(c);
                }
            }
            Collections.sort(mWidgetItems, new Comparator<TimeTableView.Rectangle>() {
                @Override
                public int compare(TimeTableView.Rectangle r1, TimeTableView.Rectangle r2) {
                    if (r1.start != r2.start) {
                        return r1.start - r2.start;
                    } else {
                        return r1.end - r2.end;
                    }
                }
            });

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return mWidgetItems.size();
        }

        @Override
        public RemoteViews getViewAt(int i) {
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.table_course_item);
            TimeTableView.Rectangle info = mWidgetItems.get(i);
            rv.setTextViewText(R.id.courseName, info.name + " (" + (info.start + 1) + " - " + (info.end + 1) + ")");
            rv.setTextViewText(R.id.teacherName, info.teacher);
            rv.setTextViewText(R.id.location, info.loc);
            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.table_course_item);
            return rv;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
