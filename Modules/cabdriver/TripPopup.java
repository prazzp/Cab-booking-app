package cabdriver.max.com.cabdriver;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by root on 3/5/17.
 */

public class TripPopup extends Dialog{
    List<TripDetails> alTripDetails;
    String sTripStatus;
    public TripPopup(@NonNull Context context,List<TripDetails> alTripDetails) {
        super(context);
        this.alTripDetails = alTripDetails;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.trip_popup);
        TextView tvUserName_TP = (TextView)findViewById(R.id.tvCustomerName);
        tvUserName_TP.setText(alTripDetails.get(0).sCustName);
        TextView tvFrom_TP = (TextView)findViewById(R.id.tvFromLOC);
        tvFrom_TP.setText(alTripDetails.get(0).sFromLoc);
        TextView tvTo_TP = (TextView)findViewById(R.id.tvToLOC);
        tvTo_TP.setText(alTripDetails.get(0).sToLoc);
        Button btnAccept_TP = (Button)findViewById(R.id.btnAccept);
        Button btnCancel_TP = (Button)findViewById(R.id.btnCancel);
        btnAccept_TP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(String.valueOf(alTripDetails.get(0).nCustContact), null, "Your Trip confirmed, Your cab is on way.", null, null);
                sTripStatus = "ongoing";
                new TripStatusUpdateAsync(sTripStatus, alTripDetails.get(0).nId,alTripDetails).execute();
                new CarstatusUpdateAsync("false").execute();
            }
        });

        btnCancel_TP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(String.valueOf(alTripDetails.get(0).nCustContact), null, "Driver Cancelled your Trip, Sorry for the inconvenience, Please book a new cab", null, null);
                sTripStatus = "cancelled";
                new TripStatusUpdateAsync(sTripStatus, alTripDetails.get(0).nId).execute();
            }
        });
    }
}
