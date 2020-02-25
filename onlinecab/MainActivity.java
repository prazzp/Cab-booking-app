package in.co.app.onlinecab;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    UserSessionManager session;
    Button tour;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mActionBarDrawerToggle;
    FrameLayout flMain;

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    public static Activity aActivity;
    public static Float fCarPrice;
    public static List<CarDetail> alCarDetail;
    public static String sdistance;
    public static int carIndex;

    public static List<TripDetails> alTrips ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        aActivity = this;
        alTrips = new ArrayList<>();
        flMain = (FrameLayout)findViewById(R.id.flMain);
        session = new UserSessionManager(getApplicationContext());
        alCarDetail = new ArrayList<>();


        mDrawerLayout = (DrawerLayout) findViewById(R.id.dlMain);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView nvMain = (NavigationView)findViewById(R.id.nbMain);
        nvMain.setNavigationItemSelectedListener(this);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickM(View v) {

        switch (v.getId()) {

            case R.id.browse:
                flMain.setVisibility(View.VISIBLE);
                mFragmentManager = getFragmentManager();
                mFragmentTransaction = mFragmentManager.beginTransaction();
                mFragmentTransaction.replace(R.id.flMain,new BookCarMapsFragment(),"BookCar").addToBackStack("BookCar").commit();
                /*Intent i1 = new Intent(this, BookCarMapsActivity.class);
                startActivity(i1);*/
                break;
        }
    }

    @Override
    public void onBackPressed() {
        flMain.setVisibility(View.GONE);
        super.onBackPressed();


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        flMain.setVisibility(View.VISIBLE);
        mFragmentManager = getFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        switch (item.getItemId())
        {
            case  R.id.my_trips :
                mFragmentTransaction.replace(R.id.flMain,new TripHistory(),"Trip").addToBackStack("Trip").commit();
                break;
            case R.id.bookCab:
                mFragmentTransaction.replace(R.id.flMain, new BookCarMapsFragment(),"BOOKCAR").addToBackStack("BOOKCAR").commit();
                break;
            case R.id.logout:
                session.logoutUser();
                finish();
                break;
            case R.id.about:
                mFragmentTransaction.replace(R.id.flMain, new AboutUsFragment(), "About").addToBackStack("About").commit();
                break;
            case R.id.my_profile:
                mFragmentTransaction.replace(R.id.flMain, new UserDetailsFragments(), "UserDetails").addToBackStack("UserDetails").commit();
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
