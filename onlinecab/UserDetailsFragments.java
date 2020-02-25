package in.co.app.onlinecab;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by root on 30/4/17.
 */

public class UserDetailsFragments extends Fragment {

    private TextView tvUserEmail;
    private TextView tvUserName;
    private TextView tvUserContact;
    Button btnLogout;
    UserSessionManager session;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_profile_fragment, container, false);
        SharedPreferences pref = MainActivity.aActivity.getSharedPreferences("Cab", MODE_PRIVATE);
        tvUserName = (TextView)rootView.findViewById(R.id.tvUserName);
        tvUserName.setText(pref.getString("name", "name"));
        session = new UserSessionManager(MainActivity.aActivity);

        tvUserContact = (TextView)rootView.findViewById(R.id.tvUserContact);
        tvUserContact.setText(String.valueOf(pref.getLong("contact", 996434256)));

        tvUserEmail = (TextView)rootView.findViewById(R.id.tvUserEmail);
        tvUserEmail.setText(pref.getString("email", "ashu@gmail.com"));
        btnLogout = (Button) rootView.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.logoutUser();
            }
        });
        return rootView;
    }
}
