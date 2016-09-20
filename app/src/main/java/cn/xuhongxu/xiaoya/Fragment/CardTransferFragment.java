package cn.xuhongxu.xiaoya.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import cn.xuhongxu.xiaoya.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CardTransferFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class CardTransferFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public CardTransferFragment() {
        // Required empty public constructor
    }

    Button confirm;
    TextInputEditText money, pwd;
    SharedPreferences preferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        preferences =
                getActivity().getSharedPreferences(getString(R.string.preference_key),
                        Context.MODE_PRIVATE);

        View v = inflater.inflate(R.layout.fragment_card_transfer, container, false);
        confirm = (Button) v.findViewById(R.id.confirm);
        money = (TextInputEditText) v.findViewById(R.id.edit_money);
        pwd = (TextInputEditText) v.findViewById(R.id.edit_query_pwd);

        pwd.setText(preferences.getString("query_pwd", ""));

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("query_pwd", pwd.getText().toString());
                editor.apply();

                mListener.onTransfer(money.getText().toString(), pwd.getText().toString());
            }
        });

        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onTransfer(String money, String pwd);
    }
}
