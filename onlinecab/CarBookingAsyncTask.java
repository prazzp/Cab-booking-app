package in.co.app.onlinecab;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by root on 29/4/17.
 */

public class CarBookingAsyncTask  extends AsyncTask <Object, Object,String>{
    String status;
    int nid;
    int nCarid;
    String sFromLoc;
    String sToLoc;
    float fdistance;
    ProgressDialog mProgressDialog;
    String sResult = null;
    float fFare;
    public CarBookingAsyncTask(String booked, int id, int nCarId, String sFromLoc, String sToLoc, float fdistance, float fFare) {
        this.status = booked;
        this.nid = id;
        this.nCarid = nCarId;
        this.sFromLoc = sFromLoc;
        this.sToLoc = sToLoc;
        this.fdistance = fdistance;
        this.fFare = fFare;
        mProgressDialog = new ProgressDialog(MainActivity.aActivity);
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setTitle("Booking your cab");
        mProgressDialog.setMessage("Please wait");
        mProgressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Object... params) {

        try {
            bookCar();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sResult;
    }

    private String bookCar() throws JSONException, IOException{

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("status", status)
                .add("customer", String.valueOf(nid))
                .add("car", String.valueOf(nCarid))
                .add("from_loc", sFromLoc)
                .add("to_loc", sToLoc)
                .add("distance", String.valueOf(fdistance))
                .add("fare", String.valueOf(fFare))
                .build();
        Request request = new Request.Builder()
                .url("http://192.168.1.13:8000/api/trip/create")
                .post(body)
                .build();
        Response response = null;

        try{
            response = client.newCall(request).execute();
            Log.d("Response Code", String.valueOf(response.code()));
            String jsonData = response.body().string();
            sResult = jsonData;
            Log.d("jsonData", jsonData);

        }catch (final IOException e){
            e.printStackTrace();
            MainActivity.aActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.aActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return  "";

        }

        return sResult;
    }

    @Override
    protected void onPostExecute(String s) {
        mProgressDialog.dismiss();
        /*
        {"id":3,"status":"booked","customer":3,"car":1,"from_loc":"bvb hubli","to_loc":"shirur park","distance":1.3,"fare":30.0}
         */
        Bundle mBundle = new Bundle();
        try {
            JSONObject mJsonObject = new JSONObject(s);
            mBundle.putString("from_loc",mJsonObject.getString("from_loc"));
            mBundle.putString("to_loc",mJsonObject.getString("to_loc"));
            mBundle.putFloat("distance",Float.valueOf(mJsonObject.getString("distance")));
            mBundle.putFloat("fare",Float.valueOf(mJsonObject.getString("fare")));
            mBundle.putInt("car",mJsonObject.getInt("car"));
            FragmentManager mFragmentManager = MainActivity.aActivity.getFragmentManager();
            FragmentTransaction mFragmentTransaction =mFragmentManager.beginTransaction();
            TripDetailsFragment mTripDetailsFragment = new TripDetailsFragment();
            mTripDetailsFragment.setArguments(mBundle);
            mFragmentTransaction.replace(R.id.flMain,mTripDetailsFragment,mTripDetailsFragment.toString()).
                    addToBackStack(mTripDetailsFragment.toString()).commit();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onPostExecute(s);
    }
}
