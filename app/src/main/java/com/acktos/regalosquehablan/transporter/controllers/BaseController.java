package com.acktos.regalosquehablan.transporter.controllers;

import android.util.Log;

import com.acktos.regalosquehablan.transporter.android.AndroidMultiPartEntity;
import com.acktos.regalosquehablan.transporter.android.Encrypt;
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

import java.io.File;
import java.io.IOException;

/**
 * Created by Acktos on 10/15/15.
 */
public class BaseController {

    public static final String TAG_DEBUG="regalosquehablan_debug";
    public static final String TOKEN="197846643661b7b74eb7db9036c614e6";
    public static final String KEY_ENCRYPT="encrypt";
    public static final String KEY_FIELDS="fields";
    public static final String KEY_USER_AGENT="user_agent";


    // Response codes
    public static final String SUCCESS_CODE="200";
    public static final String FAILED_CODE="402";
    public static final String ERROR_CODE="400";


    //Internal storage files
    public static final String  FILE_TRANSPORTER_PROFILE="transporter_profile.json";

    // Api endpoints
    public enum API{

        TRANSPORTER_LOGIN("http://www.central.acktos.com.co/login_messenger/"),
        GET_ORDERS("http://www.central.acktos.com.co/get_messenger_orders/"),
        GET_ORDERS_TEST("http://www.grafikaenlinea.com/oscar.php"),
        UPDATE_ORDER_STATE("http://www.central.acktos.com.co/update_order_state/"),
        REGISTER_GCM_ID("http://www.central.acktos.com.co/update_messenger_mobile_id/");


        private final String url;

        API (String uri){
            url=uri;
        }

        public String getUrl(){
            return url;
        }
    }

    //BROADCAST
    public static final String BROADCAST_RESPONSE_DELIVERY="com.acktos.regalosquehablan.transporter";

    //GCM
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public final static String TOPIC_TRANSPORTERS="transporters";




    public static void testServer() {

        String responseString;

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://192.34.58.49/assign_service/");

        try {
            AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                    new AndroidMultiPartEntity.ProgressListener() {

                        @Override
                        public void transferred(long num) {
                            // publishProgress((int) ((num / (float) totalSize) * 100));
                        }
                    });

            String encrypt="";


            //entity.addPart(Delivery.KEY_RECIPIENT_IMAGE, new FileBody(filePhoto));

            //entity.addPart(Delivery.KEY_SIGNATURE_IMAGE, new FileBody(fileSignature));



            entity.addPart("id", new StringBody("1"));
            entity.addPart("car", new StringBody("1"));
            entity.addPart("service", new StringBody("1"));


            encrypt+="1"+"2"+"3";


            entity.addPart(BaseController.KEY_ENCRYPT, new StringBody(Encrypt.md5(encrypt + "6f0e37c25064fa258b428ebb6cf79553")));

            //totalSize = entity.getContentLength();
            httppost.setEntity(entity);

            // Making server call
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity r_entity = response.getEntity();

            String responseObject= EntityUtils.toString(r_entity);

            Log.i(BaseController.TAG_DEBUG, "Response test:" + responseObject);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {

                Log.i(BaseController.TAG_DEBUG, "Server is ready!");

            } else {
                Log.i(BaseController.TAG_DEBUG, "Error occurred! Http Status Code: " + statusCode);
            }

        } catch (ClientProtocolException e) {
            Log.e(BaseController.TAG_DEBUG,"Client protocol exception");
        } catch (IOException e) {
            Log.e(BaseController.TAG_DEBUG,"IO exception in httpPost");
        }


    }


}
