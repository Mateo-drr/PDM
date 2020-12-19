package com.example.pdmg2;

import android.animation.FloatArrayEvaluator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private AppBarConfiguration mAppBarConfiguration;
    private SensorManager mSensorManager;
    private Sensor mSensorTemperature;
    private Sensor mSensorLight;
    private Sensor mSensorHum;
    private boolean switch_on = false;
    private ArrayList<Float> array = new ArrayList<>();
    private ArrayList<Float> array2 = new ArrayList<>();
    private ArrayList<Float> array3 = new ArrayList<>();
    private int index=0;
    private Date now = new Date();

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
        mSensorHum = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mSensorLight != null) {
            mSensorManager.registerListener(this, mSensorLight, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorTemperature != null) {
            mSensorManager.registerListener(this, mSensorTemperature, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorLight != null) {
            mSensorManager.registerListener(this, mSensorHum, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void onClicksw(View view) {
        if (!switch_on)
            switch_on = true;
        else
            switch_on = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Calendar calendar = Calendar.getInstance().getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        GraphView graphView = (GraphView) findViewById(R.id.graphid);
        GraphView graphView2 = (GraphView) findViewById(R.id.graphid2);
        GraphView graphView3 = (GraphView) findViewById(R.id.graphid3);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>();
        LineGraphSeries<DataPoint> series3 = new LineGraphSeries<>();

        int sensorType = event.sensor.getType();

        if (sensorType ==Sensor.TYPE_LIGHT && switch_on){
            // Write a message to the database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Luminosidade");
            myRef.setValue("L" + event.values[0]);

            array.add(event.values[0]);

            graphView.addSeries(series);
            //series.setTitle("Luminosidade");
            //graphView.getLegendRenderer().setVisible(true);
            //graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);

            if (array.size() > 79) {
                array.remove(0);
                graphView.removeSeries(series);
            }

            for(int i=0; i< array.size(); i++){
                series.appendData(new DataPoint(i, array.get(i)), true, 80);
            }

            series.setColor(Color.YELLOW);
            graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if(isValueX){
                        return format.format(calendar.getTime());
                    }
                    return super.formatLabel(value, isValueX);
                }
            });
        }

        if (sensorType ==Sensor.TYPE_AMBIENT_TEMPERATURE && switch_on){
            // Write a message to the database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Temperature");
            myRef.setValue("T" + event.values[0]);

            array2.add(event.values[0]);

            graphView2.addSeries(series2);
            //series.setTitle("Luminosidade");
            //graphView.getLegendRenderer().setVisible(true);
            //graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);

            if (array2.size() > 79) {
                array2.remove(0);
                graphView2.removeSeries(series2);
            }

            for(int i=0; i< array2.size(); i++){
                series2.appendData(new DataPoint(i, array2.get(i)), true, 80);
            }

            series2.setColor(Color.RED);
            graphView2.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if(isValueX){
                        return format.format(calendar.getTime());
                    }
                    return super.formatLabel(value, isValueX);
                }
            });
        }
        if (sensorType ==Sensor.TYPE_RELATIVE_HUMIDITY && switch_on){
            // Write a message to the database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Humidade");
            myRef.setValue("H" + event.values[0]);

            array3.add(event.values[0]);

            graphView3.addSeries(series3);
            //series.setTitle("Luminosidade");
            //graphView.getLegendRenderer().setVisible(true);
            //graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);

            if (array3.size() > 79) {
                array3.remove(0);
                graphView3.removeSeries(series3);
            }

            for(int i=0; i< array3.size(); i++){
                series3.appendData(new DataPoint(i, array3.get(i)), true, 80);
            }

            series3.setColor(Color.BLUE);
            graphView3.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if(isValueX){
                        return format.format(calendar.getTime());
                    }
                    return super.formatLabel(value, isValueX);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            //Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            //boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            //String uid = user.getUid();
            TextView txtName =findViewById(R.id.txtV_usermail);
            TextView txtEmail =findViewById(R.id.txtV_usermail);
            txtName.setText(name);
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
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
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
        ad.setButton(Dialog.BUTTON_NEGATIVE, "CANCEL", null,null);
        ad.show();
    }

        /*
    public void start (SensorEvent event){
        Timer t = new Timer();
        Calendar calendar = Calendar.getInstance().getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        TextView txtemp =findViewById(R.id.txt_showtemp);
        int sensorType = event.sensor.getType();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                if (sensorType ==Sensor.TYPE_LIGHT && switch_on){
                    txtemp.setText("L" + event.values[0]);
                    // Write a message to the database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Temperature");
                    myRef.setValue("T" + event.values[0]);

                    GraphView graphView = (GraphView) findViewById(R.id.graphid);
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>();

                    array.add(event.values[0]);

                    if (array.size() > 49)
                        array.remove(0);

                    for(int i=0; i< array.size(); i++){
                        series.appendData(new DataPoint(Double.parseDouble(format.format(calendar.getTime())), array.get(i)), true, 50);
                    }
                    graphView.addSeries(series);
                    graphView.getViewport().setYAxisBoundsManual(true);
                    graphView.getViewport().setMinY(0);
                    graphView.getViewport().setMaxY(1024);
                    graphView.getViewport().setScrollable(true);
                }
            }
        };

        t.schedule(tt,0,1000);
    }
    */
}
