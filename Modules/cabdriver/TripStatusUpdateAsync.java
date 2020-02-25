package cabdriver.max.com.cabdriver;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by root on 3/5/17.
 */

public class TripStatusUpdateAsync extends AsyncTask<Object, Object, Void> {
    String sTripStatus;
    int nTripId;
    String sDisplay = "";
    ProgressDialog mProgressDialog;
    List<TripDetails> alTrpDetails;

    public TripStatusUpdateAsync(String sTripStatus, int nTripId, List<TripDetails> alTrpDetails) {
        this.sTripStatus = sTripStatus;
        this.nTripId = nTripId;
        this.alTrpDetails = alTrpDetails;
    }

    public TripStatusUpdateAsync(String sTripStatus, int nTripId, String sDisplay) {
        this.sTripStatus = sTripStatus;
        this.nTripId = nTripId;
        this.sDisplay = sDisplay;
    }

    public TripStatusUpdateAsync(String sTripStatus, int nTripId) {
        this.sTripStatus = sTripStatus;
        this.nTripId = nTripId;
    }

    @Override
    protected Void doInBackground(Object... params) {
        try {
            updateTripStatus();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(MainActivity.aActivity);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setTitle("updating trip");
        mProgressDialog.setMessage("Please wait");
        mProgressDialog.show();
        super.onPreExecute();
    }

    private void updateTripStatus() throws JSONException, IOException {
        SharedPreferences mSharedPreferences = MainActivity.aActivity.getSharedPreferences("Cab", Context.MODE_PRIVATE);
        OkHttpClient client = new OkHttpClient();
        Log.d("requesting _>", "done");
        RequestBody body = new FormBody.Builder()
                .add("status", sTripStatus)
                .build();
        Request request = new Request.Builder()
                .url("http://192.168.1.13:8000/api/trip/" + nTripId + "/update")
                .put(body)
                .build();
        Response response = null;

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response.code() == 200) {
            MainActivity.aActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.aActivity, "Trip " + sTripStatus, Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            MainActivity.aActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.aActivity, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mProgressDialog.cancel();
        if (!sDisplay.equals("dont")) {
            MainActivity.flMain.setVisibility(View.VISIBLE);
            FragmentManager mFragmentManager = MainActivity.aActivity.getFragmentManager();
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
            Bundle mBundle = new Bundle();
            mBundle.putString("name", alTrpDetails.get(0).sCustName);
            mBundle.putString("from", alTrpDetails.get(0).sFromLoc);
            mBundle.putString("to", alTrpDetails.get(0).sToLoc);
            mBundle.putString("distance", String.valueOf(alTrpDetails.get(0).fDistance));
            mBundle.putString("fare", String.valueOf(alTrpDetails.get(0).fFare));
            mBundle.putInt("id", alTrpDetails.get(0).nId);
            mBundle.putLong("number",alTrpDetails.get(0).nCustContact);
            TripInfoFragment mTripInfoFragment = new TripInfoFragment();
            mTripInfoFragment.setArguments(mBundle);
            mFragmentTransaction.replace(R.id.flMain, mTripInfoFragment, mTripInfoFragment.toString()).addToBackStack(mTripInfoFragment.toString()).commit();
        }
        else
        {
            MainActivity.aActivity.finish();
            Intent mIntent = new Intent(MainActivity.aActivity,MainActivity.class);
            MainActivity.aActivity.startActivity(mIntent);
        }

    }
}
