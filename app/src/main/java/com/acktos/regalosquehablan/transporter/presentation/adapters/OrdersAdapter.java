package com.acktos.regalosquehablan.transporter.presentation.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.acktos.regalosquehablan.transporter.R;
import com.acktos.regalosquehablan.transporter.android.DateTimeUtils;
import com.acktos.regalosquehablan.transporter.models.Order;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Acktos on 10/22/15.
 */
public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private List<Order> orders;
    private static RoutesAdapter.OnRecyclerViewClickListener onRecyclerViewClickListener;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView deliveryState;
        public TextView orderId;
        public TextView deliveryDate;
        public ImageView thumbOrder;
        public CardView orderCard;
        public Button btnInfo;
        public Button btnStart;


        public ViewHolder(View itemView) {

            super(itemView);
            orderCard=(CardView)itemView.findViewById(R.id.card_order);
            orderId=(TextView)itemView.findViewById(R.id.txt_order_id_card);
            deliveryState=(TextView)itemView.findViewById(R.id.txt_state_card);
            deliveryDate=(TextView)itemView.findViewById(R.id.txt_order_hour_card);
            thumbOrder=(ImageView)itemView.findViewById(R.id.thumb_order_card);
            btnInfo=(Button )itemView.findViewById(R.id.btn_info_card);
            btnStart=(Button )itemView.findViewById(R.id.btn_delivery_card);

            btnInfo.setOnClickListener(this);
            btnStart.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onRecyclerViewClickListener.onRecyclerViewClick(view, this.getLayoutPosition());

        }
    }

    public OrdersAdapter(Context context,List<Order> orders,RoutesAdapter.OnRecyclerViewClickListener onRecyclerViewClick){

        this.context=context;
        this.orders=orders;
        this.onRecyclerViewClickListener=onRecyclerViewClick;
    }


    @Override
    public int getItemCount() {
        return orders.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        viewHolder.orderId.setText(orders.get(i).getReference());
        viewHolder.deliveryState.setText(orders.get(i).getDeliveryState());
        viewHolder.deliveryDate.setText(DateTimeUtils.toFriendlyDate(orders.get(i).getDeliveryDate()));

        Picasso.with(context)
                .load(orders.get(i).getProductImage())
                .placeholder(R.drawable.desayuno)
                .into(viewHolder.thumbOrder);

    }


}
