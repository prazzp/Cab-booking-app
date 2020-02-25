package cabdriver.max.com.cabdriver;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by root on 1/5/17.
 */

public class MyProfileFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_profile_fragment, container, false);
        TextView tvUserEmailID = (TextView)rootView.findViewById(R.id.tvUserEmail);
        TextView tvUserMobileNumber = (TextView)rootView.findViewById(R.id.tvUserContact);
        TextView tvUserName = (TextView)rootView.findViewById(R.id.tvUserName);
        TextView tvCarReg = (TextView)rootView.findViewById(R.id.tvCarRegNum);
        SharedPreferences mSharedPreferences = MainActivity.aActivity.getSharedPreferences("Cab", Context.MODE_PRIVATE);
        if(mSharedPreferences.contains("email"))
        {
            tvUserEmailID.setText(mSharedPreferences.getString("email",""));
        }
        else
            tvUserEmailID.setText("Mail ID not yet set");
        if(mSharedPreferences.contains("contact"))
        {
            tvUserMobileNumber.setText(String.valueOf(mSharedPreferences.getLong("contact",0)));
        }
        else
            tvUserMobileNumber.setText("Mobile number not yet set");

        if(mSharedPreferences.contains("CarReg"))
        {
            tvCarReg.setText(String.valueOf(mSharedPreferences.getString("CarReg", "")));
        }
        else
            tvCarReg.setText("Car Reg num yet not set");

        if(mSharedPreferences.contains("name"))
        {
            tvUserName.setText(mSharedPreferences.getString("name",""));
        }
        else
            tvUserName.setText("UserName not yet set");
        Button btnLogOut = (Button)rootView.findViewById(R.id.btnLogout);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UserSessionManager(MainActivity.aActivity).logoutUser();
            }
        });
        return rootView;
    }
}
