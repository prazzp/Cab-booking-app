package in.co.app.onlinecab;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by root on 2/5/17.
 */

public class TripAsyncTask extends AsyncTask<Object, Object, Void> {
    TripDetails mTripDetails;
    private String sTime;
    @Override
    protected Void doInBackground(Object... params) {
        try{
            getTrips();
        }catch (JSONException | IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private void getTrips() throws JSONException, IOException {
        SharedPreferences mSharedPreferences = MainActivity.aActivity.getSharedPreferences("Cab", Context.MODE_PRIVATE);
        OkHttpClient client = new OkHttpClient();
        Log.d("requesting _>", "done");
        Request request = new Request.Builder()
                .url("http://192.168.1.13:8000/api/trips/usertrips?user=" + String.valueOf(mSharedPreferences.getInt("id", 0)))
                .build();
        Response response = null;

        try {
            response = client.newCall(request).execute();
            String jsonData = response.body().string();
            Log.d("jsonData", jsonData);
            JSONArray tripsArray = new JSONArray(jsonData);
            Log.d("jsonObj", String.valueOf(tripsArray));
            Log.d("jsonObj size ->", String.valueOf(tripsArray.length()));
            if(tripsArray.length() > 0){
                for(int i=0; i<tripsArray.length();i++){
                    Log.d("Trip ", String.valueOf(tripsArray.getJSONObject(i)));
                    mTripDetails = new TripDetails();
                    mTripDetails.nId = tripsArray.getJSONObject(i).getInt("id");
                    mTripDetails.nCustId = tripsArray.getJSONObject(i).getJSONObject("customer").getInt("id");
                    mTripDetails.sCustName = tripsArray.getJSONObject(i).getJSONObject("customer").getString("name");
                    mTripDetails.nCustContact = tripsArray.getJSONObject(i).getJSONObject("customer").getInt("contact");
                    mTripDetails.sCustEmail = tripsArray.getJSONObject(i).getJSONObject("customer").getString("email");
                    mTripDetails.fFare = tripsArray.getJSONObject(i).getDouble("fare");
                    mTripDetails.nCarId= tripsArray.getJSONObject(i).getInt("car");
                    mTripDetails.sFromLoc= tripsArray.getJSONObject(i).getString("from_loc");
                    mTripDetails.sToLoc= tripsArray.getJSONObject(i).getString("to_loc");
                    mTripDetails.sStartTime= tripsArray.getJSONObject(i).getString("start_time");
                    sTime= tripsArray.getJSONObject(i).getString("end_time");
                    String[] sTemp = sTime.split("\\.");
                    mTripDetails.sEndTime = sTemp[0].replace("T"," ");
                    mTripDetails.fDistance = tripsArray.getJSONObject(i).getDouble("distance");
                    mTripDetails.sStatus = tripsArray.getJSONObject(i).getString("status");
                    MainActivity.alTrips.add(mTripDetails);
                }
            }
        }catch (final IOException e){
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
    protected void onPostExecute (Void aVoid) {
        TripHistory.mTripHistoryAdapter.notifyDataSetChanged();
        super.onPostExecute(aVoid);
    }
}

