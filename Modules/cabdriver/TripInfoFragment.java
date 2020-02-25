package cabdriver.max.com.cabdriver;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 3/5/17.
 */

public class TripInfoFragment extends Fragment {
    List<TripDetails> alTripDetails;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.trip_info,container,false);
        TextView tvUserName_TF = (TextView)rootView.findViewById(R.id.tvCustomerName);
        tvUserName_TF.setText(getArguments().getString("name"));
        TextView tvFrom_TF = (TextView)rootView.findViewById(R.id.tvFromLOC);
        tvFrom_TF.setText(getArguments().getString("from"));
        TextView tvTo_TF = (TextView)rootView.findViewById(R.id.tvToLOC);
        tvTo_TF.setText(getArguments().getString("to"));
        TextView tvDistance_TF = (TextView)rootView.findViewById(R.id.tvDistance);
        tvDistance_TF.setText(getArguments().getString("distance"));
        TextView tvFare_TF = (TextView)rootView.findViewById(R.id.tvFare);
        tvFare_TF.setText(getArguments().getString("fare"));
        Button btnComplete = (Button)rootView.findViewById(R.id.btnComplete);
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(String.valueOf(getArguments().getLong("number")), null, "Your drive completed. Thanks for using online cab booking service", null, null);
                new TripStatusUpdateAsync("completed",getArguments().getInt("id"),"dont").execute();
                new CarstatusUpdateAsync("true").execute();
            }
        });
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        alTripDetails = new ArrayList<>();
        super.onCreate(savedInstanceState);
    }
}
