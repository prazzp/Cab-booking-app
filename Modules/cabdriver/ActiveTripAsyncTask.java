package cabdriver.max.com.cabdriver;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by root on 3/5/17.
 */

public class ActiveTripAsyncTask extends AsyncTask<Object, Object, String> {
    TripDetails mTripDetails;
    public String jsonData;
    private String sTime;

    List<TripDetails> alTripDetails = new ArrayList<>();


    @Override
    protected String doInBackground(Object... params) {
        try {
            getActiveTrips();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return jsonData;
    }

    private void getActiveTrips() throws JSONException, IOException {
        SharedPreferences mSharedPreferences = MainActivity.aActivity.getSharedPreferences("Cab", Context.MODE_PRIVATE);
        OkHttpClient client = new OkHttpClient();
        Log.d("requesting _>", "done");
        Request request = new Request.Builder()
                .url("http://192.168.1.13:8000/api/trips/caractiveTrips?car=" + String.valueOf(mSharedPreferences.getInt("carID", 0)))
                .build();
        Response response = null;

        try {
            response = client.newCall(request).execute();
            jsonData = response.body().string();
            Log.d("jsonData", jsonData);
        } catch (final IOException e) {
            e.printStackTrace();
            MainActivity.aActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.aActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
//        Toast.makeText(MainActivity.aActivity, s, Toast.LENGTH_LONG).show();
        try {
            JSONArray tripsArray = new JSONArray(s);
            if (tripsArray.length() > 0) {
                for (int i = 0; i < tripsArray.length(); i++) {
                    Log.d("Trip ", String.valueOf(tripsArray.getJSONObject(i)));
                    mTripDetails = new TripDetails();
                    mTripDetails.nId = tripsArray.getJSONObject(i).getInt("id");
                    mTripDetails.nCustId = tripsArray.getJSONObject(i).getJSONObject("customer").getInt("id");
                    mTripDetails.sCustName = tripsArray.getJSONObject(i).getJSONObject("customer").getString("name");
                    mTripDetails.nCustContact = tripsArray.getJSONObject(i).getJSONObject("customer").getLong("contact");
                    mTripDetails.sCustEmail = tripsArray.getJSONObject(i).getJSONObject("customer").getString("email");
                    mTripDetails.fFare = tripsArray.getJSONObject(i).getDouble("fare");
                    mTripDetails.nCarId = tripsArray.getJSONObject(i).getInt("car");
                    mTripDetails.sFromLoc = tripsArray.getJSONObject(i).getString("from_loc");
                    mTripDetails.sToLoc = tripsArray.getJSONObject(i).getString("to_loc");
                    mTripDetails.sStartTime = tripsArray.getJSONObject(i).getString("start_time");
                    sTime = tripsArray.getJSONObject(i).getString("end_time");
                    String[] sTemp = sTime.split("\\.");
                    mTripDetails.sEndTime = sTemp[0].replace("T", " ");
                    mTripDetails.fDistance = tripsArray.getJSONObject(i).getDouble("distance");
                    mTripDetails.sStatus = tripsArray.getJSONObject(i).getString("status");
                    alTripDetails.add(mTripDetails);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (alTripDetails.size() > 0) {
            TripPopup mTripPopup = new TripPopup(MainActivity.aActivity, alTripDetails);
            mTripPopup.setCanceledOnTouchOutside(false);
            DisplayMetrics mDisplayMetrics = new DisplayMetrics();
            MainActivity.aActivity.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
            int height = mDisplayMetrics.heightPixels;
            int width = mDisplayMetrics.widthPixels;
            Toast.makeText(MainActivity.aActivity, "Height" + height, Toast.LENGTH_SHORT).show();
            mTripPopup.getWindow().setLayout(width - 10, height - 10);
            mTripPopup.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
            mTripPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            mTripPopup.show();
        }
    }
}
