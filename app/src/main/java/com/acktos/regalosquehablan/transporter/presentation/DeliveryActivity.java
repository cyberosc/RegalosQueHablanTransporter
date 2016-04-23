package com.acktos.regalosquehablan.transporter.presentation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.acktos.regalosquehablan.transporter.R;
import com.acktos.regalosquehablan.transporter.android.DrawingView;
import com.acktos.regalosquehablan.transporter.controllers.BaseController;
import com.acktos.regalosquehablan.transporter.controllers.TransportersController;
import com.acktos.regalosquehablan.transporter.models.Delivery;
import com.acktos.regalosquehablan.transporter.models.Order;
import com.acktos.regalosquehablan.transporter.models.ServerResponse;
import com.acktos.regalosquehablan.transporter.models.Transporter;
import com.acktos.regalosquehablan.transporter.services.SendDeliveryToBackendService;
import com.acktos.regalosquehablan.transporter.services.TrackingIntentService;
import com.acktos.regalosquehablan.transporter.services.UpdateDeliveryStateService;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.fasterxml.jackson.databind.deser.Deserializers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DeliveryActivity extends AppCompatActivity{

    FloatingActionButton fab;
    Toolbar toolbar;

    //Constants
    public static final int REQUEST_TAKE_PHOTO = 100;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private static final String ALBUM_NAME = "GiftTestimony";
    private static final String CAMERA_DIR = "/dcim/";

    //Attributes
    private String temporalPhotoPath;
    private String finalPhotoPath;
    private String currentSignatureFile;
    private Boolean isSendDeliveryAttempt=false;
    private String personReceivingValue=null;
    private String deliveryMessageValue=null;
    private int deliveryStatusValue=0;
    private Order order;
    private Transporter transporter;


    //UI references
    ImageView imgGiftTestimony;
    Spinner spinnerDeliveryStates;
    DrawingView drawingView;
    RelativeLayout contentFingerPaint;
    Button btnClearFingerPaint;
    MaterialDialog deliveryDialog;
    MaterialDialog confirmSendDeliveryDialog;
    MaterialDialog progressDialog;
    EditText txtPersonReceiving;
    EditText txtDeliveryMessage;
    CardView cardDeliveryDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_delivery);

        //Initialize UI
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imgGiftTestimony=(ImageView) findViewById(R.id.img_gift_testimony);
        contentFingerPaint=(RelativeLayout) findViewById(R.id.content_finger_paint);
        btnClearFingerPaint=(Button) findViewById(R.id.btn_clear_finger_paint);
        cardDeliveryDetails=(CardView) findViewById(R.id.card_delivery_details);

        //Initialize Dialogs
        cardDeliveryDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(deliveryDialog!=null){
                    if(!deliveryDialog.isShowing()){
                       deliveryDialog.show();
                    }
                }else{
                    launchDeliveryDialog();
                }
            }
        });

        //Initialize transporter
        TransportersController transportersController=new TransportersController(this);
        transporter=transportersController.getTransporter();

        //get Order from intent
        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            try {

                String orderObject=extras.getString(Order.KEY_ORDER);
                JSONObject jsonOrder=new JSONObject(orderObject);
                order=new Order(jsonOrder);
                Log.i(BaseController.TAG_DEBUG,"order:"+order.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }



        // setup widgets
        setSupportActionBar(toolbar);
        setupFingerPaintPad();

        // set clicks listeners
        imgGiftTestimony.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent(REQUEST_TAKE_PHOTO);
            }
        });

        // hide keyboard until user touch some EditText
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Update order state to ON-ROAD
        UpdateDeliveryStateService.startUpdateDeliveryState(this,order.getId());


        //Start Tracking
        TrackingIntentService.startTracking(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mMessageReceiver, new IntentFilter(BaseController.BROADCAST_RESPONSE_DELIVERY));
        Log.i(BaseController.TAG_DEBUG, "registerBroadcast delivery activity");
    }

    @Override
    protected void onStop(){

        super.onStop();
        Log.i(BaseController.TAG_DEBUG, "entry to onStrop");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        Log.i(BaseController.TAG_DEBUG, "unregister broadcast delivery activity in OnDestroy");
    }

    private void  setupDeliveryStatesSpinner(Spinner spinnerDeliveryStates){

       if(spinnerDeliveryStates!=null){
           ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                   R.array.delivery_states, R.layout.custom_spinner_item);
           adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
           spinnerDeliveryStates.setAdapter(adapter);
       }

    }

    private void setupFingerPaintPad(){

        drawingView = new DrawingView(this);
        drawingView.setMinimumWidth(300);
        drawingView.setMinimumHeight(190);
        contentFingerPaint.addView(
                drawingView,
                0,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        btnClearFingerPaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.clear();
            }
        });


    }

    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        File photoFile;

        try {
            photoFile = setUpPhotoFile();
            temporalPhotoPath = photoFile.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
            photoFile= null;
            temporalPhotoPath = null;
        }


        if (photoFile != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        temporalPhotoPath = f.getAbsolutePath();

        return f;
    }

    private File setUpSignatureFile() throws IOException{

        File f = createImageFile();
        currentSignatureFile = f.getAbsolutePath();
        return f;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = getAlbumStorageDir(ALBUM_NAME);

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.i(BaseController.TAG_DEBUG, "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    public File getAlbumStorageDir(String albumName) {
        return new File (
                Environment.getExternalStorageDirectory()
                        + CAMERA_DIR
                        + albumName
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(BaseController.TAG_DEBUG,"entry to onActivityResult");

        Log.i(BaseController.TAG_DEBUG,"temporalPhotoPath:"+finalPhotoPath);

        if(requestCode==REQUEST_TAKE_PHOTO){

            Log.i(BaseController.TAG_DEBUG, "recognize request tag");

            if (resultCode == RESULT_OK) {

                Log.i(BaseController.TAG_DEBUG,"result code is ok");

                if (temporalPhotoPath != null) {

                    Log.i(BaseController.TAG_DEBUG,"current path is not null");

                    finalPhotoPath=temporalPhotoPath;
                    placePhotoIntoView();
                    galleryAddPic();
                    //temporalPhotoPath = null;
                }
            }

        }

    }

    private void placePhotoIntoView(){


		/* Get the size of the ImageView */
        int targetW = imgGiftTestimony.getWidth();
        int targetH = imgGiftTestimony.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(temporalPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(temporalPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */
        imgGiftTestimony.setImageBitmap(bitmap);

    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(temporalPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_delivery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_delivery) {
            Log.i(BaseController.TAG_DEBUG, "Entry to send delivery info.");
            launchConfirmSendDeliveryDialog();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void attemptSendDeliveryToServer(){

        if(!isSendDeliveryAttempt){

            //There is a send information attempt
            isSendDeliveryAttempt=true;

            //Show a progress waiting server response
            launchProgressDialog();

            generateSignatureBitmap();

            //Stop tracking service
            stopService(new Intent(DeliveryActivity.this,TrackingIntentService.class));

            Delivery delivery=new Delivery();
            delivery.setDeliveryNameWhoReceive(personReceivingValue);
            delivery.setDeliveryMessage(deliveryMessageValue);
            delivery.setDeliveryStateId(Integer.toString(deliveryStatusValue));
            delivery.setOrderId(order.getId());
            delivery.setTransporterId(transporter.getId());
            delivery.setFilePathPhoto(finalPhotoPath);
            delivery.setFilePathSignature(currentSignatureFile);

            SendDeliveryToBackendService.startSendDelivery(this, delivery.toString());

        }
    }

    private void generateSignatureBitmap(){

        File signatureFile;

        try{
            signatureFile=setUpSignatureFile();


        }catch (IOException e){
            e.printStackTrace();
            signatureFile=null;
            currentSignatureFile=null;
        }

        if(signatureFile!=null){

            drawingView.setDrawingCacheEnabled(true);
            Bitmap signatureMap=drawingView.getDrawingCache();
            try {
                FileOutputStream out = new FileOutputStream(signatureFile);
                signatureMap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void launchDeliveryDialog(){

        deliveryDialog=new MaterialDialog.Builder(this)
                .title(R.string.delivery_details)
                .customView(R.layout.delivery_dialog, true)
                .positiveText(R.string.accept)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        View customView = dialog.getCustomView();
                        if (customView != null) {
                            txtPersonReceiving = (EditText) customView.findViewById(R.id.txt_person_receiving);
                            txtDeliveryMessage = (EditText) customView.findViewById(R.id.txt_delivery_message);

                            personReceivingValue=txtPersonReceiving.getText().toString();
                            deliveryMessageValue=txtDeliveryMessage.getText().toString();
                            deliveryStatusValue=spinnerDeliveryStates.getSelectedItemPosition()+7;

                            Log.i(BaseController.TAG_DEBUG,"delivery message:"+deliveryMessageValue);

                        }

                    }
                })
                .show();

        View contentView=deliveryDialog.getCustomView();
        if(contentView!=null){
            spinnerDeliveryStates=(Spinner)contentView.findViewById(R.id.spinner_delivery_states);
            setupDeliveryStatesSpinner(spinnerDeliveryStates);
        }

    }


    private void launchConfirmSendDeliveryDialog(){

        confirmSendDeliveryDialog=new MaterialDialog.Builder(this)
                .title(R.string.send_confirmation)
                .content(R.string.send_delivery_confirmation)
                .positiveText(R.string.accept)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        attemptSendDeliveryToServer();
                    }
                })
                .negativeText(R.string.cancel)
                .show();
    }

    private void launchProgressDialog(){

        progressDialog = new MaterialDialog.Builder(this)
                .title(R.string.send_info)
                .content(R.string.plase_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .cancelable(false)
                .autoDismiss(false)
                .show();
    }

    private void launchErrorDialog(String code){

        String message;

        if(code.equals(BaseController.ERROR_CODE)){
            message=getString(R.string.msg_service_unavailable);
        }else{
            message=getString(R.string.msg_network_error);
        }

        MaterialDialog errorDialog = new MaterialDialog.Builder(this)
                .title(R.string.error)
                .content(message)
                .autoDismiss(false)
                .positiveText(R.string.accept)
                .show();
    }

    /**
     * Handler for received Intents for the response delivery status
     */

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String action = intent.getAction();
            Log.i(BaseController.TAG_DEBUG, "Action received:" + action);

            isSendDeliveryAttempt=false;
            if(progressDialog!=null){
                progressDialog.dismiss();
            }

            String code=intent.getStringExtra(ServerResponse.KEY_RESPONSE_CODE);

            if(code.equals(BaseController.SUCCESS_CODE)){

                //stopService(new Intent(DeliveryActivity.this,TrackingIntentService.class)); // comment for re-location
                Intent i=new Intent(DeliveryActivity.this,NavigationActivity.class);
                startActivity(i);
                finish();

            }else{
                launchErrorDialog(code);
                stopService(new Intent(DeliveryActivity.this, TrackingIntentService.class));
            }

        }
    };

}
