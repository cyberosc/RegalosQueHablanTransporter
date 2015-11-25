package com.acktos.regalosquehablan.transporter.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Acktos on 11/9/15.
 */
public class Delivery {

    private String orderId;
    private String transporterId;
    private String filePathSignature;
    private String filePathPhoto;
    private String deliveryStateId;
    private String deliveryMessage;
    private String deliveryNameWhoReceive;

    public static final String KEY_DELIVERY="delivery";
    public static final String KEY_ORDER_ID="order_id";
    public static final String KEY_TRANSPORTER_ID="transporter_id";
    public static final String KEY_FILE_PATH_SIGNATURE="file_path_signature";
    public static final String KEY_FILE_PATH_PHOTO="file_path_photo";
    public static final String KEY_DELIVERY_STATE="delivery_state";
    public static final String KEY_DELIVERY_MESSAGE="delivery_message";
    public static final String KEY_DELIVERY_NAME_WHO_RECEIVE="delivery_name_who_receive";


    // Delivery API Keys
    public static final String KEY_MESSENGER_ID="messenger_id";
    public static final String KEY_RECIPIENT_NAME="recipient_name";
    public static final String KEY_STATE_ID="state_id";
    public static final String KEY_OBSERVATIONS="observations";
    public static final String KEY_SIGNATURE_IMAGE="signatureImage";
    public static final String KEY_RECIPIENT_IMAGE="recipientPerson";


    public Delivery(){}

    public Delivery(JSONObject jsonObject){

        try{
            if(jsonObject.has(KEY_ORDER_ID)) {
                setOrderId(jsonObject.getString(KEY_ORDER_ID));
            }
            if(jsonObject.has(KEY_TRANSPORTER_ID)) {
                setTransporterId(jsonObject.getString(KEY_TRANSPORTER_ID));
            }
            if(jsonObject.has(KEY_FILE_PATH_SIGNATURE)) {
                setFilePathSignature(jsonObject.getString(KEY_FILE_PATH_SIGNATURE));
            }
            if(jsonObject.has(KEY_FILE_PATH_PHOTO)) {
                setFilePathPhoto(jsonObject.getString(KEY_FILE_PATH_PHOTO));
            }
            if(jsonObject.has(KEY_DELIVERY_STATE)) {
                setDeliveryStateId(jsonObject.getString(KEY_DELIVERY_STATE));
            }
            if(jsonObject.has(KEY_DELIVERY_MESSAGE)) {
                setDeliveryMessage(jsonObject.getString(KEY_DELIVERY_MESSAGE));
            }
            if(jsonObject.has(KEY_DELIVERY_NAME_WHO_RECEIVE)) {
                setDeliveryNameWhoReceive(jsonObject.getString(KEY_DELIVERY_NAME_WHO_RECEIVE));
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public String toString(){

        JSONObject jsonObject=new JSONObject();

        try{
            if(getOrderId()!=null){
                jsonObject.put(KEY_ORDER_ID,getOrderId());
            }
            if(getTransporterId()!=null){
                jsonObject.put(KEY_TRANSPORTER_ID,getTransporterId());
            }
            if(getFilePathSignature()!=null){
                jsonObject.put(KEY_FILE_PATH_SIGNATURE, getFilePathSignature());
            }
            if(getFilePathPhoto()!=null){
                jsonObject.put(KEY_FILE_PATH_PHOTO, getFilePathPhoto());
            }
            if(getDeliveryStateId()!=null){
                jsonObject.put(KEY_DELIVERY_STATE, getDeliveryStateId());
            }
            if(getDeliveryMessage()!=null){
                jsonObject.put(KEY_DELIVERY_MESSAGE, getDeliveryMessage());
            }
            if(getDeliveryNameWhoReceive()!=null){
                jsonObject.put(KEY_DELIVERY_NAME_WHO_RECEIVE, getDeliveryNameWhoReceive());
            }


        }catch (JSONException e){
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTransporterId() {
        return transporterId;
    }

    public void setTransporterId(String transporterId) {
        this.transporterId = transporterId;
    }

    public String getFilePathSignature() {
        return filePathSignature;
    }

    public void setFilePathSignature(String filePathSignature) {
        this.filePathSignature = filePathSignature;
    }

    public String getFilePathPhoto() {
        return filePathPhoto;
    }

    public void setFilePathPhoto(String filePathPhoto) {
        this.filePathPhoto = filePathPhoto;
    }

    public String getDeliveryStateId() {
        return deliveryStateId;
    }

    public void setDeliveryStateId(String deliveryStateId) {
        this.deliveryStateId = deliveryStateId;
    }

    public String getDeliveryMessage() {
        return deliveryMessage;
    }

    public void setDeliveryMessage(String deliveryMessage) {
        this.deliveryMessage = deliveryMessage;
    }

    public String getDeliveryNameWhoReceive() {
        return deliveryNameWhoReceive;
    }

    public void setDeliveryNameWhoReceive(String deliveryNameWhoReceive) {
        this.deliveryNameWhoReceive = deliveryNameWhoReceive;
    }
}
