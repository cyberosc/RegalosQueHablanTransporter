package com.acktos.regalosquehablan.transporter.controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.acktos.regalosquehablan.transporter.android.AppController;
import com.acktos.regalosquehablan.transporter.android.Encrypt;
import com.acktos.regalosquehablan.transporter.models.BaseModel;
import com.acktos.regalosquehablan.transporter.models.Delivery;
import com.acktos.regalosquehablan.transporter.models.Route;
import com.acktos.regalosquehablan.transporter.models.ServerResponse;
import com.acktos.regalosquehablan.transporter.models.Transporter;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Acktos on 10/15/15.
 */
public class OrdersController extends BaseController{

    private Context context;
    private TransportersController transportersController;
    private Transporter transporter;


    //Volley Request Tags
    private final static String TAG_GET_ORDERS="get_orders";
    private final static String TAG_UPDATE_DELIVERY_STATE="update_delivery_state";

    public OrdersController(Context context){

        this.context=context;
        transportersController=new TransportersController(context);
        transporter=transportersController.getTransporter();
    }


    public void getOrders(
            final Response.Listener<List<Route>> responseListener,
            final Response.ErrorListener errorListener){


        final String transporterId;
        transporterId=transporter.getId();

        Log.i(TAG_DEBUG,"transporter id:"+transporterId);

        StringRequest stringReq = new StringRequest(

                Request.Method.POST,
                API.GET_ORDERS.getUrl(),
                new Response.Listener<String>() {

                    List<Route> routes=null;

                    @Override
                    public void onResponse(String response) {

                        Log.d(TAG_DEBUG, "get orders:"+response);

                        ServerResponse serverResponse=new ServerResponse(response);

                        if(serverResponse.getCode().equals(SUCCESS_CODE)){

                            try {

                                routes=new ArrayList<>();

                                JSONObject routesObject=new JSONObject(response);
                                JSONArray routesJson=routesObject.getJSONArray(KEY_FIELDS);

                                for (int i=0; i<routesJson.length();i++){
                                    JSONObject routeObject=routesJson.getJSONObject(i);

                                    Route routeItem=new Route(routeObject);

                                    routes.add(routeItem);

                                    Log.i(TAG_DEBUG, "route item:" + routeItem.toString());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        responseListener.onResponse(routes);

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d(TAG_DEBUG, "volley error:"+error.getMessage());
                        errorListener.onErrorResponse(error);
                    }
                }){

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put(Delivery.KEY_MESSENGER_ID, transporterId);
                params.put(BaseController.KEY_ENCRYPT, Encrypt.md5(transporterId + BaseController.TOKEN));

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringReq, TAG_GET_ORDERS);

        //(new TestServerTask()).execute();

    }


    public void updateOrderState(
            final String orderId,
            final Response.Listener<String> responseListener,
            final Response.ErrorListener errorListener){

        Log.i(BaseController.TAG_DEBUG,"order.id:"+orderId);
        Log.i(BaseController.TAG_DEBUG,"transporter.id:"+transporter.getId());

        StringRequest stringReq = new StringRequest(

                Request.Method.POST,
                API.UPDATE_ORDER_STATE.getUrl(),
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {

                        Log.d(TAG_DEBUG, "update order state:"+response);

                        ServerResponse serverResponse=new ServerResponse(response);

                        if(serverResponse.getCode().equals(SUCCESS_CODE)){

                            responseListener.onResponse(SUCCESS_CODE);

                        }else{
                            responseListener.onResponse(FAILED_CODE);
                        }

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d(TAG_DEBUG, "volley error update order state:"+error.getMessage());
                        errorListener.onErrorResponse(error);
                    }
                }){

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put(Delivery.KEY_ORDER_ID, orderId);
                params.put(Delivery.KEY_MESSENGER_ID,transporter.getId());
                params.put(Delivery.KEY_RECIPIENT_NAME,"none");
                params.put(Delivery.KEY_STATE_ID,"5");
                params.put(Delivery.KEY_DELIVERY_STATE,"5");
                params.put(Delivery.KEY_DELIVERY_MESSAGE,"none");
                String encrypt=orderId+transporter.getId()+"none"+"5"+"5"+"none";
                params.put(BaseController.KEY_ENCRYPT, Encrypt.md5(encrypt + BaseController.TOKEN));

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringReq, TAG_UPDATE_DELIVERY_STATE);

    }


    public class TestServerTask extends AsyncTask<Void,Void,Void> {


        @Override
        protected Void doInBackground(Void... params) {

            testServer();

            return null;
        }
    }


}
