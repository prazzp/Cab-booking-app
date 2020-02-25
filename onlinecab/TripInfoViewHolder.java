package in.co.app.onlinecab;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by root on 2/5/17.
 */

public class TripInfoViewHolder extends RecyclerView.ViewHolder{
    public TextView tvStatus;
    public TextView tvUserName;
    public TextView tvFromLOC;
    public TextView tvToLOC;
    public TextView tvEndDate;
    public TextView tvFare;
    public TripInfoViewHolder(View itemView) {
        super(itemView);
        tvStatus = (TextView)itemView.findViewById(R.id.tvStatus);
        tvUserName = (TextView)itemView.findViewById(R.id.tvUserName);
        tvFromLOC = (TextView)itemView.findViewById(R.id.tvFrom);
        tvToLOC = (TextView)itemView.findViewById(R.id.tvTolocation);
        tvEndDate = (TextView)itemView.findViewById(R.id.tvEndTime);
        tvFare = (TextView)itemView.findViewById(R.id.tvFare);
    }
}
