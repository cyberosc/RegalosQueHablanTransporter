package com.acktos.regalosquehablan.transporter.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.acktos.regalosquehablan.transporter.controllers.OrdersController;
import com.acktos.regalosquehablan.transporter.models.Delivery;
import com.android.volley.Response;
import com.android.volley.VolleyError;


public class UpdateDeliveryStateService extends IntentService {


    public static Context context;

    public static void startUpdateDeliveryState(Context activity,String orderId) {

        Intent intent = new Intent(activity, UpdateDeliveryStateService.class);
        intent.putExtra(Delivery.KEY_ORDER_ID, orderId);
        activity.startService(intent);
        context=activity;
    }



    public UpdateDeliveryStateService() {
        super("UpdateDeliveryStateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            final String orderId = intent.getStringExtra(Delivery.KEY_ORDER_ID);
            updateDeliveryState(orderId);

        }
    }

    private void updateDeliveryState(String orderId) {

        OrdersController ordersController=new OrdersController(context);
        ordersController.updateOrderState(orderId, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

    }
}
