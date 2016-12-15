package cn.xuhongxu.xiaoya.Activity;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import cn.xuhongxu.xiaoya.R;

public class PreferencesActivity extends AppCompatPreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new MyPreferencesFragment()).commit();
    }

    public static class MyPreferencesFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
        }
    }
}
