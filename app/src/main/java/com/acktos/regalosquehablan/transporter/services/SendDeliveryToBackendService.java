package com.acktos.regalosquehablan.transporter.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.acktos.regalosquehablan.transporter.android.AndroidMultiPartEntity;
import com.acktos.regalosquehablan.transporter.android.Encrypt;
import com.acktos.regalosquehablan.transporter.controllers.BaseController;
import com.acktos.regalosquehablan.transporter.models.Delivery;
import com.acktos.regalosquehablan.transporter.models.ServerResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class SendDeliveryToBackendService extends IntentService {


    private Delivery delivery;


    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     */
    public static void startSendDelivery(Context context, String deliveryObject) {
        Intent intent = new Intent(context, SendDeliveryToBackendService.class);
        intent.putExtra(Delivery.KEY_DELIVERY, deliveryObject);
        context.startService(intent);
    }


    public SendDeliveryToBackendService() {
        super("SendDeliveryToBackendService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            final String deliveryObject = intent.getStringExtra(Delivery.KEY_DELIVERY);

            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(deliveryObject);
                delivery=new Delivery(jsonObject);
               sendDeliveryToServer();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Sends delivery update info to backend
     */

    @SuppressWarnings("deprecation")
    private void sendDeliveryToServer() {

        Long totalSize=0l;
        String responseString;

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(BaseController.API.UPDATE_ORDER_STATE.getUrl());

        try {
            AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                    new AndroidMultiPartEntity.ProgressListener() {

                        @Override
                        public void transferred(long num) {
                           // publishProgress((int) ((num / (float) totalSize) * 100));
                        }
                    });

            String encrypt="";

            if(delivery.getFilePathPhoto()!=null){
                File filePhoto = new File(delivery.getFilePathPhoto());
                entity.addPart(Delivery.KEY_RECIPIENT_IMAGE, new FileBody(filePhoto));
            }

            if(delivery.getFilePathSignature()!=null){
                File fileSignature = new File(delivery.getFilePathSignature());
                entity.addPart(Delivery.KEY_SIGNATURE_IMAGE, new FileBody(fileSignature));
            }


            entity.addPart(Delivery.KEY_ORDER_ID, new StringBody(delivery.getOrderId()));
            encrypt+=delivery.getOrderId();


            entity.addPart(Delivery.KEY_MESSENGER_ID, new StringBody(delivery.getTransporterId()));
            encrypt+=delivery.getTransporterId();


            if(delivery.getDeliveryNameWhoReceive()!=null){
                entity.addPart(Delivery.KEY_RECIPIENT_NAME, new StringBody(delivery.getDeliveryNameWhoReceive()));
                encrypt+=delivery.getDeliveryNameWhoReceive();
            }else{
                entity.addPart(Delivery.KEY_RECIPIENT_NAME, new StringBody(""));
            }

            entity.addPart(Delivery.KEY_STATE_ID, new StringBody("6"));
            encrypt+="6";

            if(delivery.getDeliveryStateId()!=null){
                entity.addPart(Delivery.KEY_DELIVERY_STATE, new StringBody(delivery.getDeliveryStateId()));
                encrypt+=delivery.getDeliveryStateId();
            } else{
                entity.addPart(Delivery.KEY_DELIVERY_STATE, new StringBody(""));
            }

            if(delivery.getDeliveryMessage()!=null){
                entity.addPart(Delivery.KEY_DELIVERY_MESSAGE, new StringBody(delivery.getDeliveryMessage()));
                encrypt+=delivery.getDeliveryMessage();
            }else{
                entity.addPart(Delivery.KEY_DELIVERY_MESSAGE, new StringBody(""));
            }


            entity.addPart(BaseController.KEY_ENCRYPT, new StringBody(Encrypt.md5(encrypt+BaseController.TOKEN)));

            //totalSize = entity.getContentLength();
            httppost.setEntity(entity);

            // Making server call
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity r_entity = response.getEntity();

            String responseObject=EntityUtils.toString(r_entity);

            Log.i(BaseController.TAG_DEBUG,"responseObject:"+responseObject);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {

                ServerResponse serverResponse=new ServerResponse(responseObject);
                if(serverResponse.getCode().equals(BaseController.SUCCESS_CODE)){

                    Log.i(BaseController.TAG_DEBUG,"server response to send delivery:"+responseObject);
                    sendBroadcastServerResponse(BaseController.SUCCESS_CODE);
                }else{
                    sendBroadcastServerResponse(BaseController.FAILED_CODE);
                }


            } else {
                Log.i(BaseController.TAG_DEBUG,"Error occurred! Http Status Code: " + statusCode);
                sendBroadcastServerResponse(BaseController.ERROR_CODE);
            }

        } catch (ClientProtocolException e) {
            Log.e(BaseController.TAG_DEBUG,"Client protocol exception");
        } catch (IOException e) {
            Log.e(BaseController.TAG_DEBUG,"IO exception in httpPost");
        }


    }


    private void  sendBroadcastServerResponse(String code){

        Intent responseIntent = new Intent();
        responseIntent.setAction(BaseController.BROADCAST_RESPONSE_DELIVERY);
        responseIntent.putExtra(ServerResponse.KEY_RESPONSE_CODE, code);
        LocalBroadcastManager.getInstance(this).sendBroadcast(responseIntent);

        Log.i(BaseController.TAG_DEBUG,"send broadcast sendDelivery response:"+code);

    }


}
