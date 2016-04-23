package com.acktos.regalosquehablan.transporter.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.acktos.regalosquehablan.transporter.R;
import com.acktos.regalosquehablan.transporter.controllers.BaseController;
import com.acktos.regalosquehablan.transporter.controllers.TransportersController;
import com.acktos.regalosquehablan.transporter.models.Transporter;
import com.acktos.regalosquehablan.transporter.presentation.DeliveryActivity;
import com.firebase.client.Firebase;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class TrackingIntentService extends Service implements
        GoogleApiClient.ConnectionCallbacks,LocationListener {

    //GOOGLE PLAY SERVICES
    private GoogleApiClient mGoogleApiClient;

    //Attributes
    Location mLastLocation;
    Transporter transporter;
    TransportersController transportersController;


    //Fused location
    LocationRequest mLocationRequest;

    //notification ID
    private static final int ONGOING_NOTIFICATION_ID=1;

    //Firebase
    Firebase myFirebaseRef;


    @Override
    public void onCreate() {
        super.onCreate();

        buildGoogleApiClient();
        createLocationRequest();
        setupFirebase();

        transportersController=new TransportersController(this);
        transporter=transportersController.getTransporter();
    }


    /**
     * Starts this service to start tracking process.
     */
    // TODO: Customize helper method
    public static void startTracking(Context context) {
        Intent intent = new Intent(context, TrackingIntentService.class);
        context.startService(intent);
        Firebase.setAndroidContext(context);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {




        // Connect to Google Play Services
        if(mGoogleApiClient!=null){
            mGoogleApiClient.connect();
        }

        Intent notificationIntent = new Intent(this, DeliveryActivity.class);
        //notificationIntent.putExtra(LocationClientUtils.KEY_SERVICE_ID, serviceId);
        //notificationIntent.putExtra(LocationClientUtils.KEY_PICK_UP_ADDRESS, locationClientUtils.getPickUpAddress());
        //notificationIntent.putExtra(LocationClientUtils.KEY_FROM_BACKGROUND, true);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_local_shipping_24dp)
                        .setContentTitle(getText(R.string.notif_title_track))
                        .setContentText(getText(R.string.notif_track_on))
                        .setContentIntent(pendingIntent);

        startForeground(ONGOING_NOTIFICATION_ID, mBuilder.build());

        return (START_NOT_STICKY);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i(BaseController.TAG_DEBUG, "entry on destroy tracking intent service");
        if(mLocationRequest!=null && mGoogleApiClient.isConnected()){
            stopLocationUpdates();
            stopForeground(true);
            stopSelf();
        }

        if(mGoogleApiClient!=null){
            mGoogleApiClient.disconnect();
        }

    }

    //************************
    //*  FUSED LOCATION API  *
    //************************

    /**
     * Setup location request method
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(12000);
        mLocationRequest.setFastestInterval(6000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    protected void startLocationUpdates() {

        Log.i(BaseController.TAG_DEBUG,"entry to setup location request");
        if(mGoogleApiClient.isConnected() && mLocationRequest!=null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Log.i(BaseController.TAG_DEBUG, "start location request successfully started");
        }else{
            Log.i(BaseController.TAG_DEBUG,"start location request could not start");
        }
    }

    protected void stopLocationUpdates() {

        if(mGoogleApiClient.isConnected() && mLocationRequest!=null){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        String mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI(mLastUpdateTime);
        saveLocationToFirebase();
    }

    private void updateUI(String date) {
        Log.i(BaseController.TAG_DEBUG,String.valueOf(mLastLocation.getLatitude()));
        Log.i(BaseController.TAG_DEBUG,String.valueOf(mLastLocation.getLongitude()));
        Log.i(BaseController.TAG_DEBUG,date);
    }



    //********************************
    //GOOGLE PLAY SERVICES CONNECTION
    //********************************

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        Log.i(BaseController.TAG_DEBUG, "connected to google play services from tracking service");

        if(mLocationRequest!=null){
            startLocationUpdates();
        }

    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(BaseController.TAG_DEBUG, "connection suspended from tracking service");
    }


    //**************
    //*  FIREBASE  *
    //**************

    private void setupFirebase(){

        myFirebaseRef = new Firebase("https://coffe-play.firebaseio.com/");
    }

    private void saveLocationToFirebase(){

        Map<String,String> locationMap=new HashMap<>();
        locationMap.put("lat",String.valueOf(mLastLocation.getLatitude()));
        locationMap.put("long", String.valueOf(mLastLocation.getLongitude()));

        Firebase transporterRef=myFirebaseRef.child("transporters").child(transporter.getId());
        transporterRef.setValue(locationMap);
    }

}
