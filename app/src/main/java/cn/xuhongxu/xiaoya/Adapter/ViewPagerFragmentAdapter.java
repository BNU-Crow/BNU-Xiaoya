package cn.xuhongxu.xiaoya.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuhongxu on 16/6/12.
 *
 * @author Hongxu Xu
 * @version 0.1
 */

public class ViewPagerFragmentAdapter extends FragmentStatePagerAdapter {

    private List<Class> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    public ViewPagerFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public void add(Class fm, String title) {
        fragments.add(fm);
        titles.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fm = null;
        try {
            fm = (Fragment) fragments.get(position).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fm;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
