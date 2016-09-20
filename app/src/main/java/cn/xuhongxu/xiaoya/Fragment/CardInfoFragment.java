package cn.xuhongxu.xiaoya.Fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import cn.xuhongxu.Card.CardInfo;
import cn.xuhongxu.xiaoya.Activity.CardActivity;
import cn.xuhongxu.xiaoya.R;
import cn.xuhongxu.xiaoya.YaApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardInfoFragment extends Fragment {

    YaApplication app;
    TextView nameView, idView, cardIdView, balanceView,
            transitionView, statusView, frozenView;

    public CardInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        app = (YaApplication) getActivity().getApplication();
        View v = inflater.inflate(R.layout.fragment_card_info, container, false);
        nameView = (TextView) v.findViewById(R.id.name);
        idView = (TextView) v.findViewById(R.id.id);
        cardIdView = (TextView) v.findViewById(R.id.card_id);
        balanceView = (TextView) v.findViewById(R.id.balance);
        transitionView = (TextView) v.findViewById(R.id.transition);
        statusView = (TextView) v.findViewById(R.id.status);
        frozenView = (TextView) v.findViewById(R.id.frozen);

        new InfoTask().execute();

        return v;
    }

    private class InfoTask extends AsyncTask<Void, Void, CardInfo> {

        @Override
        protected CardInfo doInBackground(Void... params) {
            try {
                return app.getCardAssist().getInfo();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(CardInfo result) {
            super.onPostExecute(result);
            if (result == null) {
                Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
            } else {
                nameView.setText(result.getName());
                idView.setText(result.getId());
                cardIdView.setText(result.getCardId());
                balanceView.setText(result.getBalance());
                transitionView.setText(result.getTransition());
                statusView.setText(result.getStatus());
                frozenView.setText(result.getFrozen());
            }
        }
    }

}
