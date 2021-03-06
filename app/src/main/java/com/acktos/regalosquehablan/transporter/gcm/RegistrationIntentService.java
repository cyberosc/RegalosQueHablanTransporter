package com.acktos.regalosquehablan.transporter.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.acktos.regalosquehablan.transporter.R;
import com.acktos.regalosquehablan.transporter.controllers.BaseController;
import com.acktos.regalosquehablan.transporter.controllers.TransportersController;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;



/**
 * Created by Acktos on 2/10/16.
 */
public class RegistrationIntentService extends IntentService {

    private TransportersController transportesController;

    public RegistrationIntentService() {
        super("RegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        transportesController=new TransportersController(this);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {

            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);


            Log.i(BaseController.TAG_DEBUG, "GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);

            // Subscribe to topic channels
            subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(BaseController.SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(BaseController.TAG_DEBUG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(BaseController.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(BaseController.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {

        transportesController.registerGCMIdOnBackend(token, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if(result.equals(BaseController.SUCCESS_CODE)){
                    Log.i(BaseController.TAG_DEBUG,"Register id saved successFully");
                }else{
                    Log.i(BaseController.TAG_DEBUG,"Register id couldn't be saved");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {

        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        //for (String topic : TOPICS) {
        pubSub.subscribe(token, "/topics/" + BaseController.TOPIC_TRANSPORTERS, null);
        //}
    }
    // [END subscribe_topics]

}
