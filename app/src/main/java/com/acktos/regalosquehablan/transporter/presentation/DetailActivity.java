package com.acktos.regalosquehablan.transporter.presentation;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.acktos.regalosquehablan.transporter.R;
import com.acktos.regalosquehablan.transporter.android.DateTimeUtils;
import com.acktos.regalosquehablan.transporter.controllers.BaseController;
import com.acktos.regalosquehablan.transporter.models.Delivery;
import com.acktos.regalosquehablan.transporter.models.Order;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {


    //Attributes

    private Order order;

    //UI References

    private TextView txtOrderId;
    private TextView txtDeliveryDate;
    private TextView txtDeliveryAddress;
    private TextView txtCustomerPhone;
    private TextView txtDeliveryState;
    private TextView txtCustomerName;
    private ImageView imgGiftDetail;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Maria Carolina");
        setSupportActionBar(toolbar);


        txtOrderId=(TextView) findViewById(R.id.detail_txt_order_id);
        txtDeliveryDate=(TextView) findViewById(R.id.detail_txt_delivery_date);
        txtDeliveryAddress=(TextView) findViewById(R.id.detail_txt_delivery_address);
        txtCustomerPhone=(TextView) findViewById(R.id.detail_txt_customer_phone);
        txtDeliveryState=(TextView) findViewById(R.id.detail_txt_delivery_state);
        txtCustomerName=(TextView) findViewById(R.id.detail_txt_customer_name);
        imgGiftDetail=(ImageView) findViewById(R.id.img_gift_detail);




        Bundle extras=getIntent().getExtras();

        if(extras!=null){

            String orderObject=extras.getString(Order.KEY_ORDER);

            try {
                JSONObject jsonObject=new JSONObject(orderObject);
                order=new Order(jsonObject);
                fillOrderData(order);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(order!=null){
                    MaterialDialog materialDialog=new MaterialDialog.Builder(DetailActivity.this)
                            .title(R.string.confirm_start_delivery)
                            .content(R.string.msg_confirm_start_delivery)
                            .positiveText(R.string.accept)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                    Log.i(BaseController.TAG_DEBUG, "entry to start delivery in detail activity");
                                    deliveryStart(order);
                                }
                            })
                            .show();
                }
            }
        });
    }


    private void fillOrderData(Order order){

        if(order!=null){

            txtOrderId.setText(order.getReference());
            //txtDeliveryDate.setText(DateTimeUtils.toFriendlyDate(order.getDeliveryDate()));
            txtDeliveryDate.setText(order.getDeliveryDate());
            txtDeliveryAddress.setText(order.getAddress());
            txtCustomerPhone.setText(order.getCustomerPhone());
            txtDeliveryState.setText(order.getDeliveryState());
            txtCustomerName.setText(order.getCustomerName());
            toolbar.setTitle(order.getCustomerName());

            Picasso.with(this)
                    .load(order.getProductImage())
                    .placeholder(R.drawable.desayuno)
                    .into(imgGiftDetail);

        }
    }

    private void deliveryStart(Order order){
        Intent i=new Intent(this,DeliveryActivity.class);
        i.putExtra(Order.KEY_ORDER,order.toString());
        startActivity(i);
    }
}
