package cabdriver.max.com.cabdriver;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by root on 1/5/17.
 */

public class TripHistoryAdapter extends RecyclerView.Adapter<TripInfoViewHolder>{
    @Override
    public TripInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_each_layout,parent,false);
        TripInfoViewHolder mTripInfoViewHolder = new TripInfoViewHolder(itemView);
        return mTripInfoViewHolder;
    }

    @Override
    public void onBindViewHolder(TripInfoViewHolder holder, int position) {
        holder.tvStatus.setText(MainActivity.alTrips.get(position).sStatus);
        holder.tvUserName.setText(MainActivity.alTrips.get(position).sCustName);
        holder.tvFromLOC.setText(MainActivity.alTrips.get(position).sFromLoc);
        holder.tvToLOC.setText(MainActivity.alTrips.get(position).sToLoc);
        holder.tvFare.setText(String.valueOf(MainActivity.alTrips.get(position).fFare));
        holder.tvEndDate.setText(MainActivity.alTrips.get(position).sEndTime);
    }

    @Override
    public int getItemCount() {
        return MainActivity.alTrips.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
