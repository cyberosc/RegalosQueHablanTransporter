package com.acktos.regalosquehablan.transporter.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Acktos on 10/15/15.
 */
public class Order extends BaseModel {

    private String id;
    private String deliveryDate;
    private String deliveryState;
    private String deliveryMessage;
    private String address;
    private String customerName;
    private String customerPhone;
    private String productImage;
    private String state;
    private String reference;


    public static final String KEY_ORDER="order";
    public static final String KEY_DELIVERY_DATE="deliveryDate";
    public static final String KEY_DELIVERY_STATE="deliveryState";
    public static final String KEY_DELIVERY_MESSAGE="delivery_message";
    public static final String KEY_ADDRESS="address";
    public static final String KEY_CUSTOMER_NAME="customerName";
    public static final String KEY_CUSTOMER_PHONE="customerPhone";
    public static final String KEY_PRODUCT_IMAGE="productImage";
    public static final String KEY_STATE="state";
    public static final String KEY_REFERENCE="reference";

    public Order(){}

    public Order(JSONObject jsonObject){

        try{
            if(jsonObject.has(KEY_ID)) {
                setId(jsonObject.getString(KEY_ID));
            }
            if(jsonObject.has(KEY_DELIVERY_DATE)) {
                setDeliveryDate(jsonObject.getString(KEY_DELIVERY_DATE));
            }
            if(jsonObject.has(KEY_DELIVERY_STATE)) {
                setDeliveryState(jsonObject.getString(KEY_DELIVERY_STATE));
            }
            if(jsonObject.has(KEY_DELIVERY_MESSAGE)) {
                setDeliveryMessage(jsonObject.getString(KEY_DELIVERY_MESSAGE));
            }
            if(jsonObject.has(KEY_ADDRESS)) {
                setAddress(jsonObject.getString(KEY_ADDRESS));
            }
            if(jsonObject.has(KEY_CUSTOMER_NAME)) {
                setCustomerName(jsonObject.getString(KEY_CUSTOMER_NAME));
            }
            if(jsonObject.has(KEY_CUSTOMER_PHONE)) {
                setCustomerPhone(jsonObject.getString(KEY_CUSTOMER_PHONE));
            }
            if(jsonObject.has(KEY_PRODUCT_IMAGE)) {
                setProductImage(jsonObject.getString(KEY_PRODUCT_IMAGE));
            }
            if(jsonObject.has(KEY_STATE)) {
                setState(jsonObject.getString(KEY_STATE));
            }
            if(jsonObject.has(KEY_REFERENCE)) {
                setReference(jsonObject.getString(KEY_REFERENCE));
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public String toString(){

        JSONObject jsonObject=new JSONObject();

        try{
            if(getId()!=null){
                jsonObject.put(KEY_ID,getId());
            }
            if(getDeliveryDate()!=null){
                jsonObject.put(KEY_DELIVERY_DATE,getDeliveryDate());
            }
            if(getDeliveryMessage()!=null){
                jsonObject.put(KEY_DELIVERY_MESSAGE,getDeliveryMessage());
            }
            if(getDeliveryState()!=null){
                jsonObject.put(KEY_DELIVERY_STATE,getDeliveryState());
            }
            if(getAddress()!=null){
                jsonObject.put(KEY_ADDRESS,getAddress());
            }
            if(getCustomerName()!=null){
                jsonObject.put(KEY_CUSTOMER_NAME,getCustomerName());
            }
            if(getCustomerPhone()!=null){
                jsonObject.put(KEY_CUSTOMER_PHONE,getCustomerPhone());
            }
            if(getProductImage()!=null){
                jsonObject.put(KEY_PRODUCT_IMAGE,getProductImage());
            }
            if(getState()!=null){
                jsonObject.put(KEY_STATE,getState());
            }
            if(getReference()!=null){
                jsonObject.put(KEY_REFERENCE, getReference());
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

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryState() {
        return deliveryState;
    }

    public void setDeliveryState(String deliveryState) {
        this.deliveryState = deliveryState;
    }

    public String getDeliveryMessage() {
        return deliveryMessage;
    }

    public void setDeliveryMessage(String deliveryMessage) {
        this.deliveryMessage = deliveryMessage;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
