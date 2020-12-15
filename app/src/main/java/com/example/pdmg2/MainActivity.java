package com.example.pdmg2;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private AppBarConfiguration mAppBarConfiguration;

    private SensorManager mSensorManager;
    private Sensor mSensorTemperature;
    private Sensor mSensorLight;
    private boolean switch_on = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorTemperature = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mSensorLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE not supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        //((ScrollView) findViewById(R.id.scrollView)).addView(listView);

    }

    private DataPoint[] getDataPoint(float event) {
        DataPoint[] dp= new DataPoint[]{
                new DataPoint(new Date().getTime(), event)
        };
        return dp;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            TextView txtName = findViewById(R.id.txtV_username);
            TextView txtEmail = findViewById(R.id.txtV_usermail);
            String name = user.getDisplayName();
            txtName.setText(name);
            String email = user.getEmail();
            txtEmail.setText(email);
        }

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration);
               // || super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mSensorTemperature != null) {
            mSensorManager.registerListener(this, mSensorTemperature, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        TextView txtemp = findViewById(R.id.txt_showtemp);
        int sensorType = event.sensor.getType();
        if (sensorType == Sensor.TYPE_AMBIENT_TEMPERATURE && switch_on) {
            txtemp.setText("T" + event.values[0]);
            // Write a message to the database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Temperature");
            GraphView graphView = (GraphView) findViewById(R.id.graphid);
            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
            array[a]=event.values[0];
            a++;
            for(int i=0; i< array.length; i++){
                series.appendData(new DataPoint(i,array[i]), true, 500);
            }
            graphView.addSeries(series);
            myRef.setValue("T" + event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onClicksw(View view) {
        if (!switch_on)
            switch_on = true;
        else
            switch_on = false;
    }

    /*
    Metodo onClick para opcao logout
     */
    public void onClickLogOut(MenuItem item) {
        //Criar um alert dialog
        AlertDialog ad = new AlertDialog.Builder(this).create();
        ad.setMessage(getString(R.string.alertDMessg_logout));
        ad.setTitle(getString(R.string.alertD_logout));
        //ad.setIcon(R.drawable.actionbar_exc);
        //onClick handler para a opcao OK, intent da logactivity
        ad.setButton(Dialog.BUTTON_POSITIVE, "OK", null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, LogActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        //Ao selecionar Cancel, nao faz nada
        ad.setButton(Dialog.BUTTON_NEGATIVE, "CANCEL", null, null);
        ad.show();
    }

    public void onClickBLEStart(View view) {
        /*
        //Peripherals -> small and battery powered
        //Centrals -> phones

        //GAP -> used by peripherals
        //      handles connection requests
        //      advertisement data

        //GATT -> used for coms
        //          services stored in gatt
        //          charact. stored in services
        //          read, write, notify
        //          one connection at a time

        //UUID ->
        */

    }

}