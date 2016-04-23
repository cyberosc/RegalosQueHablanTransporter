package com.acktos.regalosquehablan.transporter.presentation;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.acktos.regalosquehablan.transporter.R;
import com.acktos.regalosquehablan.transporter.controllers.BaseController;

public class TestAPIActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_api);

        (new TestAPI()).execute();
    }



    public class TestAPI extends AsyncTask<Void,Void,Void> {


        @Override
        protected Void doInBackground(Void... params) {

            BaseController.testServer();
            return null;
        }
    }
}
