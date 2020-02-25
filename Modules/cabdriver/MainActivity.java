package cabdriver.max.com.cabdriver;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener {

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mActionBarDrawerToggle;
    UserSessionManager session;
    public static Activity aActivity;
    public static FrameLayout flMain;
    Button btnStartService;
    Button btnStopService;
    private double Longt;
    private double Lat;
    private final static int my_permission_fine_location = 101;
    public static List<TripDetails> alTrips;
    public static boolean isRunHandler = false;

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    public static String serviceStatus;
    Runnable runable;
    public Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler();
        aActivity = this;
        btnStartService = (Button) findViewById(R.id.btnStartService);
        btnStopService = (Button) findViewById(R.id.btnStopService);
        alTrips = new ArrayList<>();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dlMain);
        flMain = (FrameLayout) findViewById(R.id.flMain);
        session = new UserSessionManager(getApplicationContext());
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.Open, R.string.Close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView nvMain = (NavigationView) findViewById(R.id.nbMain);
        nvMain.setNavigationItemSelectedListener(this);
        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceStatus = "true";
                Toast.makeText(MainActivity.aActivity, "Service Started", Toast.LENGTH_SHORT).show();
                updateCarStatus();
            }
        });
        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceStatus = "false";
                updateCarStatus();
                Toast.makeText(MainActivity.aActivity, "Service Stopped", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateCarStatus() {
        find_Location(MainActivity.aActivity);
    }

    @Override
    public void onLocationChanged(Location location) {
        /*Lat = location.getLatitude();
        Longt = location.getLongitude();*/

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

    public class StatusUpdateAsync extends AsyncTask<Object, Object, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            try {
                updateStatus();
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void updateStatus() throws JSONException, IOException {
            SharedPreferences mSharedPreferences = MainActivity.aActivity.getSharedPreferences("Cab", Context.MODE_PRIVATE);
            OkHttpClient client = new OkHttpClient();
            Log.d("requesting _>", "done");
            String currentLocation = String.valueOf(Lat) + "," + String.valueOf(Longt);
            RequestBody body = new FormBody.Builder()
                    .add("availability", serviceStatus)
                    .add("current_location", currentLocation)
                    .build();
            Request request = new Request.Builder()
                    .url("http://192.168.1.13:8000/api/cars/" + String.valueOf(mSharedPreferences.getInt("carID", 0)) + "/update")
                    .put(body)
                    .build();
            Response response = null;

            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response.code() == 200) {
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    runable = this;
                    new ActiveTripAsyncTask().execute();
                    Log.d("Running", "Handler for 3 seconds");
                    mHandler.postDelayed(runable, 30000);
                }
            },30000);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        flMain.setVisibility(View.VISIBLE);
        mFragmentManager = getFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        switch (item.getItemId()) {
            case R.id.my_trips:
                mFragmentTransaction.replace(R.id.flMain, new TripHistory(), "Trip").addToBackStack("Trip").commit();
                break;
//            case R.id.bookCab:
//                mFragmentTransaction.replace(R.id.flMain, new BookCarMapsFragment(),"BOOKCAR").addToBackStack("BOOKCAR").commit();
//                break;
            case R.id.logout:
                session.logoutUser();
                finish();
                break;
            case R.id.about:
                mFragmentTransaction.replace(R.id.flMain, new AboutUsFragment(), "About").addToBackStack("About").commit();
                break;
            case R.id.my_profile:
                mFragmentTransaction.replace(R.id.flMain, new MyProfileFragment(), "UserDetails").addToBackStack("UserDetails").commit();
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        flMain.setVisibility(View.GONE);
        super.onBackPressed();
    }

    public void find_Location(Context con) {
        String location_context = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) con.getSystemService(location_context);

        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(provider, 1000, 0, new LocationListener() {
                    public void onLocationChanged(Location location) {
                    }

                    public void onProviderDisabled(String provider) {
                    }

                    public void onProviderEnabled(String provider) {
                    }

                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }
                });
                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    Lat = location.getLatitude();
                    Longt = location.getLongitude();
                    //  String temp_c = SendToUrl(addr);
                }

            }
            else
            {
                Log.d("Location permission ->", "Denied");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, my_permission_fine_location);
                }
            }
        }
        new StatusUpdateAsync().execute();
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(runable);
        super.onDestroy();
    }
}
