package com.acktos.regalosquehablan.transporter.presentation.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.acktos.regalosquehablan.transporter.R;
import com.acktos.regalosquehablan.transporter.android.DateTimeUtils;
import com.acktos.regalosquehablan.transporter.models.Route;

import java.util.List;

/**
 * Created by Acktos on 10/20/15.
 */
public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.ViewHolder> {

    private List<Route> routes;
    private static OnRecyclerViewClickListener onRecyclerViewClickListener;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tag;
        public TextView date;
        public TextView ordersQty;
        public TextView startTime;
        public TextView endTime;
        public CardView routeCard;


        public ViewHolder(View itemView) {

            super(itemView);
            routeCard=(CardView)itemView.findViewById(R.id.card_view_route);
            tag=(TextView)itemView.findViewById(R.id.txt_tag_routes);
            date=(TextView)itemView.findViewById(R.id.txt_date_routes);
            ordersQty=(TextView)itemView.findViewById(R.id.txt_qty_order_routes);
            startTime=(TextView)itemView.findViewById(R.id.txt_start_hour_routes);
            endTime=(TextView)itemView.findViewById(R.id.txt_end_hour_routes);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onRecyclerViewClickListener.onRecyclerViewClick(view, this.getLayoutPosition());
        }
    }

    public RoutesAdapter(Context context,List<Route> sessions,OnRecyclerViewClickListener onRecyclerViewClick){

        this.context=context;
        this.routes=sessions;
        this.onRecyclerViewClickListener=onRecyclerViewClick;
    }


    @Override
    public int getItemCount() {
        return routes.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.route_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        String tag=(context.getString(R.string.tag)+" "+routes.get(i).getTag()).toUpperCase();
        String orderQty=context.getString(R.string.orders)+" "+routes.get(i).getOrdersQty();
        viewHolder.tag.setText(tag);
        viewHolder.ordersQty.setText(orderQty);
        viewHolder.date.setText(DateTimeUtils.toSimpleDate(routes.get(i).getDate()+" 00:00:00"));
        viewHolder.startTime.setText(routes.get(i).getStartTime());
        viewHolder.endTime.setText(routes.get(i).getEndTime());
    }


    public interface OnRecyclerViewClickListener
    {

        public void onRecyclerViewClick(View v, int position);
    }
}
