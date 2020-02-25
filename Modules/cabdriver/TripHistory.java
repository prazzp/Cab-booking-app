package cabdriver.max.com.cabdriver;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by root on 1/5/17.
 */

public class TripHistory extends Fragment {

    public static TripHistoryAdapter mTripHistoryAdapter;
    public static RecyclerView rlTripMain;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTripHistoryAdapter = new TripHistoryAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.history_fragment,container,false);
        rlTripMain = (RecyclerView)rootView.findViewById(R.id.rvTripMain);
        rlTripMain.setLayoutManager(new LinearLayoutManager(MainActivity.aActivity));
        rlTripMain.setAdapter(mTripHistoryAdapter);
        getTripHistory();
        return rootView;
    }

    public void getTripHistory()
    {
        new TripAsyncTask().execute();
    }
}
