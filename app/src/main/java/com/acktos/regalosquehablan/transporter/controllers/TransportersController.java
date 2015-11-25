package com.acktos.regalosquehablan.transporter.controllers;

import android.content.Context;
import android.util.Log;

import com.acktos.regalosquehablan.transporter.android.AppController;
import com.acktos.regalosquehablan.transporter.android.Encrypt;
import com.acktos.regalosquehablan.transporter.android.InternalStorage;
import com.acktos.regalosquehablan.transporter.models.BaseModel;
import com.acktos.regalosquehablan.transporter.models.Delivery;
import com.acktos.regalosquehablan.transporter.models.ServerResponse;
import com.acktos.regalosquehablan.transporter.models.Transporter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Acktos on 10/15/15.
 */
public class TransportersController extends BaseController {

    private Context context;
    private InternalStorage internalStorage;

    //Volley Request Tags
    private final static String TAG_TRANSPORTER_LOGIN = "transporter_login";

    public TransportersController(Context context) {
        this.context = context;
        internalStorage = new InternalStorage(this.context);

    }

    public void transporterLogin(
            final String email,
            final String pswrd,
            final Response.Listener<String> responseListener,
            final Response.ErrorListener errorListener) {


        StringRequest jsonObjReq = new StringRequest(

                Request.Method.POST,
                API.TRANSPORTER_LOGIN.getUrl(),
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        Log.d(TAG_DEBUG, response.toString());

                        ServerResponse serverResponse = new ServerResponse(response);

                        if (serverResponse.getCode().equals(SUCCESS_CODE)) {

                            try {
                                JSONObject jsonResponse=new JSONObject(response);
                                Transporter transporter = new Transporter(jsonResponse.getJSONObject(BaseModel.KEY_TRANSPORTER));
                                saveDriverProfile(transporter);
                                responseListener.onResponse(SUCCESS_CODE);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            responseListener.onResponse(FAILED_CODE);
                        }

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d(TAG_DEBUG, "volley error:" + error.getMessage());
                        errorListener.onErrorResponse(error);
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Transporter.KEY_CC, email);
                params.put(Transporter.KEY_PASSWORD, pswrd);
                params.put(BaseController.KEY_ENCRYPT, Encrypt.md5(email + pswrd + BaseController.TOKEN));

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, TAG_TRANSPORTER_LOGIN);
    }

    /**
     * save profile into internal storage
     */
    private void saveDriverProfile(Transporter transporter) {

        internalStorage.saveFile(FILE_TRANSPORTER_PROFILE, transporter.toString());

    }

    /**
     * Get user profile from internal storage
     *
     * @return User
     */
    public Transporter getTransporter() {

        Transporter transporter = null;

        if (internalStorage.isFileExists(FILE_TRANSPORTER_PROFILE)) {

            try {
                String profileString = internalStorage.readFile(FILE_TRANSPORTER_PROFILE);

                Log.i(TAG_DEBUG, "profile: " + profileString);
                JSONObject jsonObject = new JSONObject(profileString);

                transporter = new Transporter(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return transporter;
    }

}
