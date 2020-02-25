package in.co.app.onlinecab;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by root on 29/4/17.
 */

public class CarsDisplayAsncTask extends AsyncTask <Object, Object, Void> {
    String type;
    GoogleMap mGoogleMap ;
    private List<String> carslocation = new ArrayList<>();
    CarDetail mCarDetail = new CarDetail();
    CarsDisplayAsncTask(String type, GoogleMap map){
        this.type = type;
        this.mGoogleMap = map;

    }

    @Override
    protected Void doInBackground(Object... params) {
        try{
            fetchCars();
        }catch (JSONException | IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private void fetchCars() throws JSONException, IOException{
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.1.13:8000/api/cars?type="+type)
                .build();
        Response response = null;

        try{
            response = client.newCall(request).execute();
            String jsonData = response.body().string();
            Log.d("jsonData", jsonData);
            JSONArray carsArray = new JSONArray(jsonData);
            Log.d("jsonObj", String.valueOf(carsArray));
            Log.d("jsonObj size ->", String.valueOf(carsArray.length()));
            if(carsArray.length() > 0){
                for(int i=0; i<carsArray.length();i++){
                    MainActivity.fCarPrice = Float.valueOf(carsArray.getJSONObject(i).getString("price"));
                    Log.d("price", String.valueOf(MainActivity.fCarPrice));
                    carslocation.add(carsArray.getJSONObject(i).getString("current_location"));
                    mCarDetail.nId = carsArray.getJSONObject(i).getInt("id");
                    mCarDetail.nUserId = carsArray.getJSONObject(i).getJSONObject("user").getInt("id");
                    mCarDetail.sDriverName = carsArray.getJSONObject(i).getJSONObject("user").getString("name");
                    mCarDetail.nDriverContact = carsArray.getJSONObject(i).getJSONObject("user").getInt("contact");
                    mCarDetail.sDriverEmail = carsArray.getJSONObject(i).getJSONObject("user").getString("email");
                    mCarDetail.sModel = carsArray.getJSONObject(i).getString("model");
                    mCarDetail.sReg_No = carsArray.getJSONObject(i).getString("reg_num");
                    mCarDetail.sCurrentLocation = carsArray.getJSONObject(i).getString("current_location");
                    mCarDetail.sType = carsArray.getJSONObject(i).getString("type");
                    mCarDetail.fPrice = carsArray.getJSONObject(i).getDouble("price");
                    mCarDetail.isAvailable = carsArray.getJSONObject(i).getBoolean("availability");
                    mCarDetail.isCarStatus = carsArray.getJSONObject(i).getBoolean("car_status");
                    Log.d("curret location ->", carsArray.getJSONObject(i).getString("current_location"));
                    MainActivity.alCarDetail.add(mCarDetail);
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
    protected void onPostExecute(Void aVoid) {
        showcars();
        super.onPostExecute(aVoid);
    }

    private void showcars() {
        if(carslocation.size() > 0){
            for(int i=0; i < carslocation.size(); i++){
                String[] latlang = carslocation.get(i).split(",");
                Double lattitude = Double.valueOf(latlang[0]);
                Double longtude = Double.valueOf(latlang[1]);
                LatLng latlng = new LatLng(lattitude, longtude);
                mGoogleMap.clear();
                switch (type){
                    case "mini":
                        mGoogleMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.mini72)));
                        break;
                    case "micro":
                        mGoogleMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.micro72)));
                        break;
                    case "prime":
                        mGoogleMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.prime72)));
                        break;
                }
//                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
//                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }
        }else{
            Toast.makeText(MainActivity.aActivity, "cars not available, try other type", Toast.LENGTH_LONG).show();
        }
    }
}
