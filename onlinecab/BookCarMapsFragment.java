package in.co.app.onlinecab;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.Route;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by root on 29/4/17.
 */

public class BookCarMapsFragment extends Fragment implements OnMapReadyCallback, DirectionFinderListener, LocationListener {
    MapView mMapView;
    GoogleMap mGoogleMap;
    private Button btnFindPath;
    private EditText etOrigin;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private final static int my_permission_fine_location = 101;
    private List<String> carslocation = new ArrayList<>();
    private Button btnConfirm ;
    View rootView;

    LinearLayout llMicro;
    LinearLayout llMini;
    LinearLayout llPrime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_book_car_maps, container, false);
        btnFindPath = (Button) rootView.findViewById(R.id.btnFindPath);
        llMicro = (LinearLayout) rootView.findViewById(R.id.llMicro);
        btnConfirm = (Button) rootView.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              SharedPreferences pref = MainActivity.aActivity.getSharedPreferences("Cab", MODE_PRIVATE);
                int cars_size = MainActivity.alCarDetail.size();
                int randNum;
                randNum = new Random().nextInt(cars_size);
                MainActivity.carIndex = randNum;
                int nCarId = MainActivity.alCarDetail.get(randNum).nId;
                String sFromLoc = etOrigin.getText().toString();
                String sToLoc = etDestination.getText().toString();
                float fdistance = Float.valueOf(MainActivity.sdistance.split(" ")[0]);
                float fFare = Float.valueOf(((TextView) rootView.findViewById(R.id.tvFare)).getText().toString());

                new CarBookingAsyncTask("booked",pref.getInt("id",0),nCarId,sFromLoc,sToLoc,fdistance,fFare).execute();
            }
        });
        llMicro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnConfirm.setVisibility(View.GONE);
                llMicro.setBackgroundColor(getResources().getColor(R.color.light_blue));
                llMini.setBackgroundColor(getResources().getColor(R.color.white));
                llPrime.setBackgroundColor(getResources().getColor(R.color.white));
                getCars("micro");

            }
        });
        llMini = (LinearLayout) rootView.findViewById(R.id.llMini);
        llMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnConfirm.setVisibility(View.GONE);
                llMini.setBackgroundColor(getResources().getColor(R.color.light_blue));
                llMicro.setBackgroundColor(getResources().getColor(R.color.white));
                llPrime.setBackgroundColor(getResources().getColor(R.color.white));
                getCars("mini");
            }
        });
        llPrime = (LinearLayout) rootView.findViewById(R.id.llPrime);
        llPrime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnConfirm.setVisibility(View.GONE);
                llPrime.setBackgroundColor(getResources().getColor(R.color.light_blue));
                llMini.setBackgroundColor(getResources().getColor(R.color.white));
                llMicro.setBackgroundColor(getResources().getColor(R.color.white));
                getCars("prime");
            }
        });
        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String origin = etOrigin.getText().toString();
                String destination = etDestination.getText().toString();

                if (origin.length() < 1) {
                    etOrigin.setError("Please enter origin address");
                    //Toast.makeText(getActivity(), "Please enter origin address", Toast.LENGTH_SHORT).show();
                } else if (destination.length() < 1) {
                    //Toast.makeText(getActivity(), "Please enter destination address", Toast.LENGTH_SHORT).show();
                    etDestination.setError("Please enter destination address");
                } else {
                    try {
                        btnConfirm.setVisibility(View.VISIBLE);
                        new DirectionFinder(BookCarMapsFragment.this, origin, destination).execute();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        etOrigin = (EditText) rootView.findViewById(R.id.etOrigin);
        etDestination = (EditText) rootView.findViewById(R.id.etDestination);

        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(BookCarMapsFragment.this);
        return rootView;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(MainActivity.aActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("Location permission ->", "Granted");
            mGoogleMap.setMyLocationEnabled(true);
        } else {
            Log.d("Location permission ->", "Denied");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, my_permission_fine_location);
            }
        }
        LocationManager locationManager = (LocationManager) MainActivity.aActivity.getApplicationContext().getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            mGoogleMap.addMarker(new MarkerOptions().position(latLng));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            getCars("micro");
        }
        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);


        if (mGoogleMap != null) {
            try {
                MapsInitializer.initialize(getActivity());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            Toast.makeText(getActivity(), "Some problem occured. Please try again later..", Toast.LENGTH_SHORT).show();
    }

//    @Override
//    public void onLocationChanged(Location location) {
//        double latitude = location.getLatitude();
//        double longitude = location.getLongitude();
//        LatLng latLng = new LatLng(latitude, longitude);
//        mGoogleMap.addMarker(new MarkerOptions().position(latLng));
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//        getCars("micro");
//
//    }

    @Override
    public void onLocationChanged(Location location) {

    }


//    private void showCars() {
//        if(carslocation.size() > 0){
//            for(int i=0; i < carslocation.size(); i++){
//                String[] latlang = carslocation.get(i).split(",");
//                Double lattitude = Double.valueOf(latlang[0]);
//                Double longtude = Double.valueOf(latlang[1]);
//                LatLng latlng = new LatLng(lattitude, longtude);
//                mGoogleMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.mini72)));
////                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
////                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//            }
//        }else{
//            Toast.makeText(MainActivity.aActivity, "cars not available, try other type", Toast.LENGTH_LONG).show();
//        }
//    }

    private void getCars(String Cartype) {
//        new GetCarsAsyncTask().execute();
        new CarsDisplayAsncTask(Cartype, mGoogleMap).execute();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case my_permission_fine_location:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mGoogleMap.setMyLocationEnabled(true);
                    } else {
                        Toast.makeText(getActivity(), "App requires location permission", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }


    @Override
    public void onDirectionFinderStart() {

        progressDialog = ProgressDialog.show(getActivity(), "Please wait.", "Finding the route...!!", true);
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }
        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }
        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }

    }

    private float calculatefare(String distance) {
        Log.d("duration", distance);
        String[] distance_seperated = distance.split(" ");
        float distancekms = Float.parseFloat(distance_seperated[0]);
        Log.d("sdistance Kms ", Float.toString(distancekms));
        Calendar cal = Calendar.getInstance();
        int minute = cal.get(Calendar.MINUTE);
        int hour = cal.get(Calendar.HOUR);
        Log.d("Time", "Hour: " + hour + " Minutes: " + minute);
        float finalfare = 0;
        float fareMultiplier = 0;
        int busyhours = (hour * 60) + minute;
        if (busyhours >= 480 && busyhours <= 539) {
            fareMultiplier = 1.7f;
        } else if (busyhours >= 540 && busyhours <= 599) {
            fareMultiplier = 1.5f;
        } else if (busyhours >= 600 && busyhours <= 659) {
            fareMultiplier = 1.3f;
        }
        finalfare = (MainActivity.fCarPrice * fareMultiplier) * distancekms;
        if (finalfare >= 30) {
            return finalfare;
        } else {
            return 30;
        }
    }


    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();
        float fare = 0;
        String farestr = "";
        for (Route route : routes) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 15));
            ((TextView) rootView.findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) rootView.findViewById(R.id.tvDistance)).setText(route.distance.text);
            MainActivity.sdistance = route.distance.text;

            fare = calculatefare(route.distance.text);
            farestr = String.format("%.2f", fare);
//            Log.d("sdistance ", route.sdistance.text);
//            Toast.makeText(this,Float.toString(fare),Toast.LENGTH_SHORT);
            ((TextView) rootView.findViewById(R.id.tvFare)).setText(farestr);

            originMarkers.add(mGoogleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));


            destinationMarkers.add(mGoogleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polygonOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++) {
                polygonOptions.add(route.points.get(i));

                polylinePaths.add(mGoogleMap.addPolyline(polygonOptions));
            }
        }
    }

//    class GetCarsAsyncTask extends AsyncTask<Object, Object, Void> {
//
//        @Override
//        protected Void doInBackground(Object... params) {
//            try{
//                fetchCars();
//            }catch (JSONException | IOException e){
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        private void fetchCars() throws JSONException, IOException{
//            OkHttpClient client = new OkHttpClient();
//            Request request = new Request.Builder()
//                    .url("http://192.168.1.13:8000/api/cars")
//                    .build();
//            Response response = null;
//
//            try{
//                response = client.newCall(request).execute();
//                String jsonData = response.body().string();
//                Log.d("jsonData", jsonData);
//                JSONArray carsArray = new JSONArray(jsonData);
//                Log.d("jsonObj", String.valueOf(carsArray));
//                Log.d("jsonObj size ->", String.valueOf(carsArray.length()));
//                if(carsArray.length() > 0){
//                    for(int i=0; i<carsArray.length();i++){
//                        carslocation.add(carsArray.getJSONObject(i).getString("current_location"));
//                        Log.d("curret location ->", carsArray.getJSONObject(i).getString("current_location"));
//                    }
//                }
//            }catch (final IOException e){
//                e.printStackTrace();
//                MainActivity.aActivity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(MainActivity.aActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            showCars();
//        }
//    }
}
