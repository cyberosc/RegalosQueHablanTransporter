package com.acktos.regalosquehablan.transporter.presentation;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acktos.regalosquehablan.transporter.R;
import com.acktos.regalosquehablan.transporter.controllers.BaseController;
import com.acktos.regalosquehablan.transporter.controllers.TransportersController;
import com.acktos.regalosquehablan.transporter.gcm.RegistrationIntentService;
import com.acktos.regalosquehablan.transporter.models.Order;
import com.acktos.regalosquehablan.transporter.models.Route;
import com.acktos.regalosquehablan.transporter.models.Transporter;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class NavigationActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        RoutesFragment.OnRouteSelectedListener,
        OrdersFragment.OnOrderSelectedListener {

    // UI References
    private Toolbar toolbar;
    private TextView navTransporterName;
    private TextView navTransporterCc;
    private NavigationView navView;


    //Components
    Transporter transporter;
    TransportersController transportersController;

    //Constants
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //View navView=LayoutInflater.from(this).inflate(R.layout.nav_header_navigation,null);
        navTransporterName=(TextView) findViewById(R.id.nav_transporter_name);
        navTransporterCc=(TextView) findViewById(R.id.nav_transporter_cc);


        // Initialize and set listeners for Floating Action Button
        //setupFAB();

        transportersController=new TransportersController(this);
        transporter=transportersController.getTransporter();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();



        navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        RoutesFragment routesFragment=RoutesFragment.newInstance("hello","hello");
        setMainFragment(routesFragment);

        setupHeaderView();


        // Register APP to GCM
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }else{
            Toast.makeText(this,"Este Dispositivo no soporta servicio de notificaciones",Toast.LENGTH_LONG).show();
        }


    }

    /*private void setupFAB(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }*/

    private void setupHeaderView(){

        View headerView = LayoutInflater.from(this).inflate(R.layout.nav_header_navigation, null);
        TextView transporterName=(TextView) headerView.findViewById(R.id.nav_transporter_name);
        TextView transporterCc=(TextView) headerView.findViewById(R.id.nav_transporter_cc);

        if(transporter!=null){
            transporterName.setText(transporter.getName());
            transporterCc.setText(getString(R.string.prompt_cc)+": "+transporter.getCc());
        }

        navView.addHeaderView(headerView);

    }

    public void setToolBarTitle(String title){

        if(toolbar!=null){
            toolbar.setTitle(title);
        }

    }

    //****************
    //Setup fragments
    //****************

    private void setMainFragment(Fragment launchFragment){

        FragmentTransaction ft=getFragmentManager().beginTransaction();
        if(launchFragment!=null){
            ft.replace(R.id.main_fragment,launchFragment);
            ft.commit();
        }
    }

    private void launchOrdersFragment(Route route){

        OrdersFragment ordersFragment=OrdersFragment.newInstance(route.toString());
        setMainFragment(ordersFragment);
        setToolBarTitle(getString(R.string.route)+" "+route.getTag());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout_action) {

            MaterialDialog confirmLogoutDialog=new MaterialDialog.Builder(this)
                    .title(R.string.logout)
                    .content(R.string.msg_confirm_logout)
                    .positiveText(R.string.accept)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            Log.i(BaseController.TAG_DEBUG, "entry to logout");
                            deleteFile(BaseController.FILE_TRANSPORTER_PROFILE);
                            Intent i=new Intent(NavigationActivity.this,LoginActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();

                        }
                    })
                    .show();

        } else if (id == R.id.nav_report_action) {

        } else if (id == R.id.nav_route_action) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * Interface for manage route selected events
     * @param route
     */
    @Override
    public void onRouteSelected(Route route) {
        launchOrdersFragment(route);
    }

    /**
     * Interface for manage order selected events
     * @param order
     */
    @Override
    public void onOrderSelected(Order order) {

        // Launch deliveryActivity
        Intent i=new Intent(this,DeliveryActivity.class);
        i.putExtra(Order.KEY_ORDER,order.toString());
        startActivity(i);

    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(BaseController.TAG_DEBUG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
