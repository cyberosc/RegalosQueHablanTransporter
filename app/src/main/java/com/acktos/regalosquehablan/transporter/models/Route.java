package com.acktos.regalosquehablan.transporter.models;

import android.util.Log;

import com.acktos.regalosquehablan.transporter.android.DateTimeUtils;
import com.acktos.regalosquehablan.transporter.controllers.BaseController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Acktos on 10/15/15.
 */
public class Route extends BaseModel {

    private String id;
    private String tag;
    private String date;
    private String ordersQty;
    private String startTime;
    private String endTime;
    private List<Order> orders;


    public static final String KEY_TAG="tag";
    public static final String KEY_DATE="date";
    public static final String KEY_ORDERS_QTY="count";
    public static final String KEY_START_TIME="begin_time";
    public static final String KEY_END_TIME="end_time";
    public static final String KEY_ORDERS="orders";
    public static final String KEY_MODEL="route";


    public Route(){}

    public Route(JSONObject jsonObject){

        //Initialize orders list
        orders=new ArrayList<>();

        try{
            if(jsonObject.has(KEY_ID)) {
                setId(jsonObject.getString(KEY_ID));
            }
            if(jsonObject.has(KEY_TAG)) {
                setTag(jsonObject.getString(KEY_TAG));
            }
            if(jsonObject.has(KEY_DATE)) {
                setDate(jsonObject.getString(KEY_DATE));
            }
            if(jsonObject.has(KEY_ORDERS_QTY)) {
                setOrdersQty(jsonObject.getString(KEY_ORDERS_QTY));
            }
            if(jsonObject.has(KEY_START_TIME)) {
                setStartTime(jsonObject.getString(KEY_START_TIME));
            }
            if(jsonObject.has(KEY_END_TIME)) {
                setEndTime(jsonObject.getString(KEY_END_TIME));
            }

            if(jsonObject.has(KEY_ORDERS)) {

                JSONArray jsonArray=new JSONArray(jsonObject.getString(KEY_ORDERS));

                for(int i=0; i<jsonArray.length();i++){

                    JSONObject jsonOrder= jsonArray.getJSONObject(i);
                    Order order=new Order(jsonOrder);
                    setOrder(order);
                }

            }


        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public String toString(){

        JSONObject jsonObject=new JSONObject();

        try{
            if (getId()!=null){
                jsonObject.put(KEY_ID, getId());
            }
            if(getTag()!=null){
                jsonObject.put(KEY_TAG, getTag());
            }
            if(getDate()!=null){
                jsonObject.put(KEY_DATE, getDate());
            }
            if(getOrdersQty()!=null){
                jsonObject.put(KEY_ORDERS_QTY, getOrdersQty());
            }
            if(getStartTime()!=null){
                jsonObject.put(KEY_START_TIME, getStartTime());
            }
            if(getEndTime()!=null){
                jsonObject.put(KEY_END_TIME, getEndTime());
            }
            if(getOrders()!=null){
                jsonObject.put(KEY_ORDERS, getOrders());
            }



        }catch (JSONException e){
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrdersQty() {
        return ordersQty;
    }

    public void setOrdersQty(String ordersQty) {
        this.ordersQty = ordersQty;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startDate) {
        this.startTime = startDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endDate) {
        this.endTime = endDate;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public void setOrder(Order order){

        if(orders!=null){
            orders.add(order);
        }
    }


}
