package in.co.app.onlinecab;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by root on 30/4/17.
 */

public class TripDetailsFragment extends Fragment {
    /**
     * sDriverName
     * nDriverContact
     * sDriverEmail
     * sReg_No
     * from_loc
     * to_loc
     * distance
     * fare
     */
    private TextView tvDriverName;
    private TextView tvRegNo;
    private TextView tvFromLocation;
    private TextView tvToLocation;
    private TextView tvDriverEmail;

    private TextView tvDriverMobile;

    private TextView tvFare;
    private TextView tvDistance;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.trip_details_fragment,container,false);
        tvDriverName = (TextView)rootView.findViewById(R.id.tvDriverName);
        tvDriverName.setText(MainActivity.alCarDetail.get(MainActivity.carIndex).sDriverName);
        tvRegNo = (TextView)rootView.findViewById(R.id.tvCarRegisterNumber);
        tvRegNo.setText(MainActivity.alCarDetail.get(MainActivity.carIndex).sReg_No);
        tvFromLocation = (TextView)rootView.findViewById(R.id.tvFromLocation);
        tvFromLocation.setText(getArguments().getString("from_loc"));
        tvToLocation = (TextView)rootView.findViewById(R.id.tvToLocation);
        tvToLocation.setText(getArguments().getString("to_loc"));
        tvDriverEmail = (TextView)rootView.findViewById(R.id.tvDriverEmail);
        tvDriverEmail.setText(MainActivity.alCarDetail.get(MainActivity.carIndex).sDriverEmail);
        tvDriverMobile = (TextView)rootView.findViewById(R.id.tvDriverContact);
        tvDriverMobile.setText(String.valueOf(MainActivity.alCarDetail.get(MainActivity.carIndex).nDriverContact));
        tvFare = (TextView)rootView.findViewById(R.id.tvFare);
        tvFare.setText(String.valueOf(getArguments().getFloat("fare")+ " RS"));
        tvDistance = (TextView)rootView.findViewById(R.id.tvDistance);
        tvDistance.setText(String.valueOf(getArguments().getFloat("distance")+ " KM"));
        return rootView;
    }
}
