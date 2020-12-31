package com.example.pdmg2;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public static final int GALLERY_REQUEST_CODE_UPDATE = 1;
    public static final int CAMERA_REQUEST_CODE = 102;
    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;
    private SensorManager mSensorManager;
    private Sensor mSensorTemperature;
    private Sensor mSensorLight;
    private Sensor mSensorHum;
    private boolean switch_on = false;
    private boolean clicked=false;
    private ArrayList<Float> array = new ArrayList<>();
    private ArrayList<Float> array2 = new ArrayList<>();
    private ArrayList<Float> array3 = new ArrayList<>();
    private int index=0;
    private Date now = new Date();
    Handler handler = new Handler();
    //Handler handler2 = new Handler();
    private String blehum;
    private String bletemp;
    private Runnable runnableCode;
    //private Runnable runnableCode2;
    private int counter = 1;


    BLE ble = new BLE(MainActivity.this);
    private Instant Glide;
    private ImageView selectedImage;
    private StorageReference storageReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userID= user.getUid();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
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
        if (!switch_on) {
            switch_on = true;
            //check if connected
            if (ble.getDevice() != null){

                ble.Write("h");
                // Define the code block to be executed
                runnableCode = new Runnable() {
                    @Override
                    public void run() {
                        // Do something here on the main thread
                        if(counter == 1){
                            ble.Read();
                            counter = 2;
                        }else if(counter == 2){
                            blehum = ble.getS();
                            bleUpdateHumGraph();
                            ble.Write("t");
                            counter = 3;
                        }else if(counter == 3){
                            ble.Read();
                            counter = 4;
                        }else if(counter == 4){
                            bletemp = ble.getS();
                            bleUpdateTempGraph();
                            ble.Write("h");
                            counter = 1;
                        }

                        Log.d("Handlers", "Called on main thread");
                        // Repeat this the same runnable code block again another 2 seconds
                        // 'this' is referencing the Runnable object
                        handler.postDelayed(this, 5000);
                    }
                };
                // Start the initial runnable task by posting through the handler
                handler.post(runnableCode);

                /*
                ble.Write("t");
                // Define the code block to be executed
                runnableCode2 = new Runnable() {
                    @Override
                    public void run() {
                        // Do something here on the main thread
                        ble.Read();
                        bletemp = ble.getS();
                        bleUpdateTempGraph();
                        Log.d("Handlers", "Called on main thread");
                        // Repeat this the same runnable code block again another 2 seconds
                        // 'this' is referencing the Runnable object
                        handler2.postDelayed(this, 5000);
                    }
                };
                // Start the initial runnable task by posting through the handler
                handler2.post(runnableCode2);
                 */

            }
        }else {
            switch_on = false;
            handler.removeCallbacks(runnableCode);
            //handler2.removeCallbacks(runnableCode2);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Calendar calendar = Calendar.getInstance().getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        GraphView graphView = (GraphView) findViewById(R.id.graphid);
        //GraphView graphView2 = (GraphView) findViewById(R.id.graphid2);
        //GraphView graphView3 = (GraphView) findViewById(R.id.graphid3);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        //LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>();
        //LineGraphSeries<DataPoint> series3 = new LineGraphSeries<>();

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
/*
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

 */
/*
        if (sensorType == Sensor.TYPE_RELATIVE_HUMIDITY && switch_on){
            // Write a message to the database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Humidade");
            myRef.setValue("H" + event.values[0]);
            //myRef.setValue()

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

 */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        TextView txtName =findViewById(R.id.txtV_username);
        TextView txtEmail =findViewById(R.id.txtV_usermail);
        ImageView imgUser = findViewById(R.id.imgV_user);
        // Name, email address, and profile photo Url
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if (userProfile != null) {
                    String name = userProfile.name;
                    String email = userProfile.email;
                    txtName.setText(name);
                    txtEmail.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,"Something wrong happened!", Toast.LENGTH_SHORT).show();
            }
        });
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
            TextView txtName =findViewById(R.id.txtV_username);
            TextView txtEmail =findViewById(R.id.txtV_usermail);
            txtName.setText(name);
            txtEmail.setText(email);

        StorageReference profileRef = storageReference.child("users/"+user.getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            public void onSuccess(Uri uri){
                Picasso.get().load(uri).into(imgUser);
            }
        });

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClickScan(View view) {
        ble.Scan();
    }

    public void onClickRead(View view) {
        //TextView rd = findViewById(R.id.txt_write);
        //if (!rd.getText().toString().isEmpty()) {
            ble.Read();
        //}
    }

    public void onClickWrite(View view) {
        ble.Write(null); // autosend -> t ou h para temperatura ou humidade
    }

    public void onClickEditUser(View view){
        Button buttonedit = findViewById(R.id.btnedit);
        Button buttonphotogal = findViewById(R.id.btn_photogal);
        Button takephoto = findViewById(R.id.btn_takephoto);
        ImageView imguser = (ImageView) findViewById(R.id.imgview_user);
        EditText nametxt = findViewById(R.id.editTextTextPersonName);

        buttonedit.setVisibility(View.VISIBLE);
        nametxt.setVisibility(View.VISIBLE);
        buttonphotogal.setVisibility(View.VISIBLE);
        takephoto.setVisibility(View.VISIBLE);
        imguser.setVisibility(View.VISIBLE);
    }

    public void onClickAcceptChanges(View view){
        Button buttonedit = findViewById(R.id.btnedit);
        EditText nametxt = findViewById(R.id.editTextTextPersonName);
        TextView txtName = (TextView)findViewById(R.id.txtV_username);
        ImageView imguser = (ImageView) findViewById(R.id.imgview_user);
        Button buttonphotogal = findViewById(R.id.btn_photogal);
        Button takephoto = findViewById(R.id.btn_takephoto);

        String content = nametxt.getText().toString().trim();
        if(!content.isEmpty()){
            reference.child(userID).child("name").setValue(content);
            txtName.setText(content);
        }

        buttonedit.setVisibility(View.GONE);
        nametxt.setVisibility(View.GONE);
        buttonphotogal.setVisibility(View.GONE);
        takephoto.setVisibility(View.GONE);
        imguser.setVisibility(View.GONE);
    }
    public void buttonOnClickPhotoGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.txtSelectPhoto)), GALLERY_REQUEST_CODE_UPDATE);

    }

    public void buttonOnClickPhotoCamera(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        selectedImage = findViewById(R.id.imgview_user);

        if (requestCode == CAMERA_REQUEST_CODE){
            Bitmap image = (Bitmap) data.getExtras().get("data");
            selectedImage.setImageBitmap(image);
            Uri uri_img = getImageUri(this,image);
            uploadImageToFirebase(uri_img);
        }else{
            Uri contentUri = data.getData();
            String timeStamp = new SimpleDateFormat("HHmmss_ddMMyyyy").format(new Date());
            String iamgeFileName = "JPEG_" + timeStamp +"."+getFileExt(contentUri);
            selectedImage.setImageURI(contentUri);
            uploadImageToFirebase(contentUri);
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void uploadImageToFirebase (Uri contentUri) {
        StorageReference fileRef = storageReference.child("users/"+user.getUid()+"/profile.jpg");
        fileRef.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ImageView imguser = (ImageView) findViewById(R.id.imgV_user);
                        Picasso.get().load(uri).into(imguser);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"Fail to Upload.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }


    private void bleUpdateHumGraph(){
        GraphView graphView3 = (GraphView) findViewById(R.id.graphid3);
        LineGraphSeries<DataPoint> series3 = new LineGraphSeries<>();
        Calendar calendar = Calendar.getInstance().getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        if(blehum != null && !blehum.equals("busy")) {
            // Write a message to the database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Humidade");
            myRef.setValue("H" + Float.parseFloat(blehum));
            //myRef.setValue()

            array3.add(Float.parseFloat(blehum));
            graphView3.addSeries(series3);
            //series.setTitle("Luminosidade");
            //graphView.getLegendRenderer().setVisible(true);
            //graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);

            if (array3.size() > 79) {
                array3.remove(0);
                graphView3.removeSeries(series3);
            }

            for (int i = 0; i < array3.size(); i++) {
                series3.appendData(new DataPoint(i, array3.get(i)), true, 80);
            }

            series3.setColor(Color.BLUE);
            graphView3.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if (isValueX) {
                        return format.format(calendar.getTime());
                    }
                    return super.formatLabel(value, isValueX);
                }
            });
        }
    }

    private void bleUpdateTempGraph(){
        GraphView graphView2 = (GraphView) findViewById(R.id.graphid2);
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>();
        Calendar calendar = Calendar.getInstance().getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        if(bletemp != null && !bletemp.equals("busy")) {
            // Write a message to the database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Temperature");
            myRef.setValue("T" + Float.parseFloat(bletemp));

            array2.add(Float.parseFloat(bletemp));

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
