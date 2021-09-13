package com.example.pdmg2;

/**
 * Projeto de PDM - Grupo2
 * <p>
 * Realizado por:
 * Gonçalo Lopes 2181775
 * Mateo Rodríguez 2182076
 */

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.pdmg2.login.LogActivity;
import com.example.pdmg2.presets.CustomPresetActivity;
import com.example.pdmg2.presets.PresetsActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
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
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Classe MainActivity
 * Esta é a parte principal da app, funciona com fragmentos, por tanto os onClicks da maioria dos objetos encontram-se aqui.
 * É utilizado um hamburger menu, com 5 diferentes janelas 4 delas mostram o fragmento respectivo, e a ultima é
 * só mostra um alert dialog para confirmar se o utilizador pretende sair da sua conta.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity implements Serializable {
    public static final int GALLERY_REQUEST_CODE_UPDATE = 1;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int PRESET_REQUEST_CODE = 2;
    private AppBarConfiguration mAppBarConfiguration;
    private boolean switch_on = false;
    private boolean clicked = false;
    private ArrayList<Float> array = new ArrayList<>();
    private ArrayList<Float> array2 = new ArrayList<>();
    private ArrayList<Float> array3 = new ArrayList<>();
    private int index = 0;
    private Date now = new Date();
    private Instant Glide;
    private ImageView selectedImage;
    private StorageReference storageReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userID = user.getUid();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
    private Switch swi;

    Handler handler = new Handler();
    Handler handler2 = new Handler();
    Handler handler3 = new Handler();

    private String txtt = "busy";
    private String txth = "busy";
    private String txtl = "busy";
    private boolean ledon = false;

    private boolean ton = false;
    private boolean ron = false;
    private boolean aon = false;
    private boolean man = false;

    private String blehum;
    private String bletemp;
    private String blelum;
    private int last_bletemp;
    private int last_blehum;

    private Runnable runnableCode;
    public BLE ble = new BLE(MainActivity.this);
    private int counter = 0;
    private String bletxdata;
    private Uri uri_img;
    private int upload = 0;
    private int ok_towrite = 0;

    /**
     * Classe onCreate da MainActivity
     *
     * Mostra no ecrã o layout "activity_main".
     * Configura a ActionBar da aplicação e se clicar numa das opções inicia o fragmento correspondente.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_analytics)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        storageReference = FirebaseStorage.getInstance().getReference();

    }

    /**
     * Método onStart da MainActivity
     *
     * Verifica se a ultima vez que o utilizador fez login foi há mais de 24h. Caso seja verdade atrasa atrasa os registos dos logs.
     */
    @Override
    public void onStart() {
        super.onStart();
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if (userProfile != null) {
                    int[] calendar = new int[3];
                    calendar[0] = (Calendar.getInstance().get(Calendar.YEAR));
                    calendar[1] = (Calendar.getInstance().get(Calendar.MONTH));
                    calendar[2] = (Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                    int last_log_day = userProfile.last_log_day;
                    int last_log_month = userProfile.last_log_month;
                    int last_log_year = userProfile.last_log_year;

                    if ((calendar[2] - last_log_day + calendar[1] + 1 - last_log_month + calendar[0] - last_log_year) > 0) {
                        reference.child(userID).child("dia4").child("hum_max").setValue(userProfile.dia3.hum_max);
                        reference.child(userID).child("dia4").child("hum_min").setValue(userProfile.dia3.hum_min);
                        reference.child(userID).child("dia4").child("temp_max").setValue(userProfile.dia3.temp_max);
                        reference.child(userID).child("dia4").child("temp_min").setValue(userProfile.dia3.temp_min);

                        reference.child(userID).child("dia3").child("hum_max").setValue(userProfile.dia2.hum_max);
                        reference.child(userID).child("dia3").child("hum_min").setValue(userProfile.dia2.hum_min);
                        reference.child(userID).child("dia3").child("temp_max").setValue(userProfile.dia2.temp_min);
                        reference.child(userID).child("dia3").child("temp_min").setValue(userProfile.dia2.temp_max);

                        reference.child(userID).child("dia2").child("hum_max").setValue(userProfile.dia1.hum_max);
                        reference.child(userID).child("dia2").child("hum_min").setValue(userProfile.dia1.hum_min);
                        reference.child(userID).child("dia2").child("temp_max").setValue(userProfile.dia1.temp_max);
                        reference.child(userID).child("dia2").child("temp_min").setValue(userProfile.dia1.temp_min);

                        reference.child(userID).child("dia1").child("hum_max").setValue(userProfile.dia0.hum_max);
                        reference.child(userID).child("dia1").child("hum_min").setValue(userProfile.dia0.hum_min);
                        reference.child(userID).child("dia1").child("temp_max").setValue(userProfile.dia0.temp_max);
                        reference.child(userID).child("dia1").child("temp_min").setValue(userProfile.dia0.temp_min);

                        reference.child(userID).child("dia0").child("hum_max").setValue(0);
                        reference.child(userID).child("dia0").child("hum_min").setValue(0);
                        reference.child(userID).child("dia0").child("temp_max").setValue(0);
                        reference.child(userID).child("dia0").child("temp_min").setValue(0);

                        reference.child(userID).child("last_log_day").setValue(calendar[0]);
                        reference.child(userID).child("last_log_month").setValue(calendar[1] + 1);
                        reference.child(userID).child("last_log_year").setValue(calendar[2]);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, getString(R.string.tst_UploadFireError), Toast.LENGTH_SHORT).show();
            }
        });
        Log.d("", "onStartMain" + ble.getDevice());
    }

    /**
     * Método onClicksw
     *
     * Ao ativar o switch o dispositivo começa a enviar mensagens para o microcontrolador retornar o que é pretendido, chamando as funções ble.Write() e ble.Read().
     */
    public void onClicksw(View view) {
        swi = findViewById(R.id.switch1);
        if (!switch_on) {
            switch_on = true;
            //check if connected
            if (ble.getDevice() != null) {

                ble.Write("h");
                // Define the code block to be executed counter starts with 1
                runnableCode = new Runnable() {
                    @Override
                    public void run() {
                        // Do something here on the main thread
                        if (counter == 1) {
                            ble.Read();
                            counter = 2;
                        } else if (counter == 2) {
                            blehum = ble.getS();
                            bleUpdateHumGraph();
                            ble.Write("t");
                            counter = 3;
                        } else if (counter == 3) {
                            ble.Read();
                            counter = 4;
                        } else if (counter == 4) {
                            bletemp = ble.getS();
                            bleUpdateTempGraph();
                            ble.Write("l");
                            counter = 5;
                        } else if (counter == 5) {
                            ble.Read();
                            counter = 6;
                        } else if (counter == 6) {
                            blelum = ble.getS();
                            bleUpdateLumGraph();
                            ble.Write("h");
                            counter = 1;
                        } else if (counter == 0) {
                            counter = 1;
                        }

                        Log.d("Handlers", "Called on main thread");
                        // Repeat this the same runnable code block again another 5 seconds
                        // 'this' is referencing the Runnable object
                        handler.postDelayed(this, 5000);
                    }
                };
                // Start the initial runnable task by posting through the handler
                handler.post(runnableCode);

            } else {
                Toast.makeText(MainActivity.this, getString(R.string.tst_NoDevices), Toast.LENGTH_SHORT).show();
                if (swi.isChecked()) {
                    swi.setChecked(false);
                }
                switch_on = false;
            }
        } else {
            switch_on = false;
            handler.removeCallbacks(runnableCode);
        }
    }

    /**
     * Método onCreateOpitionsMenu
     *
     * Coloca o nome, mail e foto do utilizador no menu lateral.
     * Faz inflate do menu do canto superior direito.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        TextView txtName = findViewById(R.id.txtV_username);
        TextView txtEmail = findViewById(R.id.txtV_usermail);
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
                Toast.makeText(MainActivity.this, getString(R.string.tst_UploadFireError), Toast.LENGTH_SHORT).show();
            }
        });

        StorageReference profileRef = storageReference.child("users/" + user.getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            public void onSuccess(Uri uri) {
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
    }

    /**
     * Método onClickLogOut
     *
     * Cria um alert dialog a perguntar se pretende sair da aplicação, se a resposta for sim sai da aplicação.
     */
    public void onClickLogOut(MenuItem item) {
        //Criar um alert dialog
        AlertDialog ad = new AlertDialog.Builder(this).create();
        ad.setMessage(getString(R.string.alertDMessg_logout));
        ad.setTitle(getString(R.string.alertD_logout));
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

    /**
     * Método nClickEditUser
     *
     * Ao clicar no botão de edição do utilizador, mostra o campo de edição do nome e da iamagem do mesmo.
     */
    public void onClickEditUser(View view) {
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

        StorageReference profileRef = storageReference.child("users/" + user.getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imguser);
            }
        });

    }

    /**
     * Método onClickAcceptChanges
     *
     * Ao clicar no botão para aceitar alterações, este altera na Firebase o registo do nome e/ou chama uma função para alterar a imagem.
     */
    public void onClickAcceptChanges(View view) {
        Button buttonedit = findViewById(R.id.btnedit);
        EditText nametxt = findViewById(R.id.editTextTextPersonName);
        TextView txtName = (TextView) findViewById(R.id.txtV_username);
        ImageView imguser = (ImageView) findViewById(R.id.imgview_user);
        Button buttonphotogal = findViewById(R.id.btn_photogal);
        Button takephoto = findViewById(R.id.btn_takephoto);

        String content = nametxt.getText().toString().trim();
        if (!content.isEmpty()) {
            reference.child(userID).child("name").setValue(content);
            txtName.setText(content);
        }
        if (upload == 1) {
            Log.d("", "UPLOAD");
            uploadImageToFirebase(uri_img);
        }

        buttonedit.setVisibility(View.GONE);
        nametxt.setVisibility(View.GONE);
        buttonphotogal.setVisibility(View.GONE);
        takephoto.setVisibility(View.GONE);
        imguser.setVisibility(View.GONE);
    }

    /**
     * Método OnClickPhotoGallery
     *
     * Este método abre a galeria para escolher uma foto para o utilizador.
     */
    public void onClickPhotoGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.txtSelectPhoto)), GALLERY_REQUEST_CODE_UPDATE);

    }

    /**
     * Método OnClickPhotoCamera
     *
     * Verifica se a aplicação tem as premissões para usar a camera.
     */
    public void onClickPhotoCamera(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            opencamera();
        }
    }

    /**
     * Método onRequestPermissionsResult
     *
     * Verifica se o utilizador deu premissão para usar a camera.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                opencamera();
            } else
                Toast.makeText(MainActivity.this, getString(R.string.tst_CameraPermission), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *Método opencamera
     *
     * Liga a camera para tirar uma foto.
     */
    private void opencamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    /**
     * Método onActivityResult
     *
     * Se o utilizador escolher uma foto da galeria ou tirar uma foto, este método autoriza fazer upload para a Firebase.
     * Se o utilizador alterar os presets da humidade/luminosidade/temperatura, este método envia-os para o microcontrolador via BLE.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        selectedImage = findViewById(R.id.imgview_user);
        upload = 0;
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                selectedImage.setImageBitmap(image);
                uri_img = getImageUri(this, image);
                upload = 1;
                // uploadImageToFirebase(uri_img);
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.tst_NoImage), Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE_UPDATE) {
            if (resultCode == Activity.RESULT_OK) {
                uri_img = data.getData();
                selectedImage.setImageURI(uri_img);
                upload = 1;
                //uploadImageToFirebase(uri_img);
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.tst_NoImage), Toast.LENGTH_SHORT).show();
            }
        }

        boolean aa = resultCode == Activity.RESULT_OK;
        boolean bb = requestCode == PRESET_REQUEST_CODE;
        System.out.println("-------------------------------------------------------------------------------");
        Log.d("", "zero if " + aa + bb);
        if (resultCode == Activity.RESULT_OK && requestCode == PRESET_REQUEST_CODE) {
            Log.d("", "first if");
            if (data.hasExtra("pre")) {

                bletxdata = data.getExtras().getString("pre");
                Log.d("", bletxdata);
                if (ble.getDevice() != null) {
                    ble.Write(bletxdata);
                } else
                    Toast.makeText(MainActivity.this, getString(R.string.tst_NoDevices), Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     * Método getImageUri
     *
     * Transforma formato de imagem de Bitmap para Uri.
     *
     * @param inContext
     * @param inImage - imagem em formato Bitmap
     * @return - imagem em formato Uri
     */
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    /**
     * Método uploadImageToFirebase
     *
     * Faz upload da imagem escolhida pelo utilizador para a Firebase.
     * @param contentUri - imagem em formato Uri
     */
    private void uploadImageToFirebase(Uri contentUri) {
        StorageReference fileRef = storageReference.child("users/" + user.getUid() + "/profile.jpg");
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
                Toast.makeText(MainActivity.this, getString(R.string.tst_UploadFireError), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Método onClickScan
     *
     * Chama a função ble.Scan() para ligar a um dispositivo via BLE.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClickScan(View view) {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView txtconnect = findViewById(R.id.txtv_connect);
        ble.Scan();
        progressBar.setVisibility(View.VISIBLE);
        txtconnect.setText(getString(R.string.connecting));

        runnableCode = new Runnable() {
            int count = 0;

            // Define the code block to be executed
            @Override
            public void run() {
                // Do something here on the main thread
                if (count == 1) {
                    if (ble.getDevice() != null) {
                        progressBar.setVisibility(View.INVISIBLE);
                        txtconnect.setText(getString(R.string.connected));
                        count = 2;
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        txtconnect.setText(getString(R.string.devnotfound));
                    }
                } else if (count == 0) {
                    count = 1;
                }

                Log.d("Handlers", "Called on main thread");
                // Repeat this the same runnable code block again another 2 seconds
                // 'this' is referencing the Runnable object
                if (count != 2) {
                    handler2.postDelayed(this, 2000);
                } else {
                    count = 0;
                    handler2.removeCallbacks(runnableCode);
                }
            }
        };

        handler.post(runnableCode);
    }

    /**
     * Método bleUpdateHumGraph
     *
     * Este método atualiza o gráfico da humidade do fragment_home e altera a variavel hum_max ou hum_min se o valor recebido for maior ou menor que o valor registado na Firebase no dia0.
     */
    private void bleUpdateHumGraph() {
        GraphView graphView = (GraphView) findViewById(R.id.graphid3);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        Calendar calendar = Calendar.getInstance().getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        if (blehum != null && !blehum.equals("busy")) {
            last_blehum = Math.round(Float.parseFloat(blehum));

            array.add((float) last_blehum);
            graphView.addSeries(series);
            if (array.size() > 79) {
                array.remove(0);
                graphView.removeSeries(series);
            }
            for (int i = 0; i < array.size(); i++) {
                series.appendData(new DataPoint(i, array.get(i)), true, 80);
            }
            series.setColor(Color.BLUE);
            graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if (isValueX) {
                        return format.format(calendar.getTime());
                    }
                    return super.formatLabel(value, isValueX);
                }
            });
            reference.child(userID).child("dia0").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User.Dia0 userProfile = snapshot.getValue(User.Dia0.class);
                    if (userProfile != null) {
                        float hum_max = userProfile.hum_max;
                        float hum_min = userProfile.hum_min;
                        if (hum_max < last_blehum || hum_max == 0) {
                            reference.child(userID).child("dia0").child("hum_max").setValue(last_blehum);
                        }
                        if (hum_min > last_blehum || hum_min == 0) {
                            reference.child(userID).child("dia0").child("hum_min").setValue(last_blehum);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, getString(R.string.tst_UploadFireError), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Método bleUpdateTempGraph
     *
     * Este método atualiza o gráfico da temperatura do fragment_home e altera a variavel temp_max ou temp_min se o valor recebido for maior ou menor que o valor registado na Firebase no dia0.
     */
    private void bleUpdateTempGraph() {
        GraphView graphView2 = (GraphView) findViewById(R.id.graphid2);
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>();
        Calendar calendar = Calendar.getInstance().getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Log.d("", "1");
        if (bletemp != null && !bletemp.equals("busy")) {
            last_bletemp = Math.round(Float.parseFloat(bletemp));
            array2.add((float) last_bletemp);
            graphView2.addSeries(series2);
            if (array2.size() > 79) {
                array2.remove(0);
                graphView2.removeSeries(series2);
            }
            for (int i = 0; i < array2.size(); i++) {
                series2.appendData(new DataPoint(i, array2.get(i)), true, 80);
            }
            series2.setColor(Color.RED);
            graphView2.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if (isValueX) {
                        return format.format(calendar.getTime());
                    }
                    return super.formatLabel(value, isValueX);
                }
            });
            reference.child(userID).child("dia0").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User.Dia0 userProfile = snapshot.getValue(User.Dia0.class);
                    Log.d("", "2");
                    if (userProfile != null) {
                        Log.d("", "3");
                        float temp_max = userProfile.temp_max;
                        float temp_min = userProfile.temp_min;
                        if (temp_max < last_bletemp || temp_max == 0) {
                            reference.child(userID).child("dia0").child("temp_max").setValue(last_bletemp);
                            Log.d("", "4");
                        }
                        if (temp_min > last_bletemp || temp_min == 0) {
                            reference.child(userID).child("dia0").child("temp_min").setValue(last_bletemp);
                            Log.d("", "5");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, getString(R.string.tst_UploadFireError), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Método bleUpdateLumGraph
     *
     * Este método atualiza o gráfico da luminosidade do fragment_home.
     */
    private void bleUpdateLumGraph() {
        GraphView graphView3 = (GraphView) findViewById(R.id.graphid);
        LineGraphSeries<DataPoint> series3 = new LineGraphSeries<>();
        Calendar calendar = Calendar.getInstance().getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        if (blelum != null && !blelum.equals("busy")) {

            array3.add(Float.parseFloat(blelum));

            graphView3.addSeries(series3);

            if (array3.size() > 79) {
                array3.remove(0);
                graphView3.removeSeries(series3);
            }

            for (int i = 0; i < array3.size(); i++) {
                series3.appendData(new DataPoint(i, array3.get(i)), true, 80);
            }

            series3.setColor(Color.YELLOW);
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

    /**
     * Método onClickReadTemp
     *
     * Se clicar no botão "Ler Temperatura" no fragment_slideshow, o dispositivo envia um código ao microcontrolador para devolver o valor da temperatura.
     */
    public void onClickReadTemp(View view) {
        if (ble.getDevice() != null && ok_towrite == 0) {
            ok_towrite = 1;
            ble.Write("t");
            TextView txt_temp = findViewById(R.id.txt_read_temp);
            runnableCode = new Runnable() {
                int counter1 = 0;

                // Define the code block to be executed
                @Override
                public void run() {
                    // Do something here on the main thread
                    if (counter1 == 1) {
                        ble.Read();
                        counter1 = 2;
                    } else if (counter1 == 2) {
                        txtt = ble.getS();
                        Log.d("", ble.getS());
                        //ble.Write("t");
                        counter1 = 3;
                    } else if (counter1 == 3) {
                        //txt_temp.setText(txt);
                        handler2.removeCallbacks(runnableCode);
                        counter1 = 0;
                    } else if (counter1 == 0) {
                        counter1 = 1;
                    }

                    Log.d("Handlers", "Called on main thread");
                    // Repeat this the same runnable code block again another 2 seconds
                    // 'this' is referencing the Runnable object
                    if (txtt.equals("busy")) {
                        handler2.postDelayed(this, 2000);
                    } else {
                        txt_temp.setText(txtt + " Cº");
                        txtt = "busy";
                        handler.removeCallbacks(runnableCode);
                        ok_towrite = 0;
                    }
                }
            };
            //handler2.removeCallbacks(runnableCode);

            if (txtt.equals("busy")) {
                handler2.post(runnableCode);
                Log.d("", "post");
            }

        } else {
            if (ok_towrite == 0) {
                Toast.makeText(MainActivity.this, getString(R.string.tst_NoDevices), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Método onClickReadHum
     *
     * Se clicar no botão "Ler Humidade" no fragment_slideshow, o dispositivo envia um código ao microcontrolador para devolver o valor da humidade.
     */
    public void onClickReadHum(View view) {
        if (ble.getDevice() != null && ok_towrite == 0) {
            ok_towrite = 1;
            ble.Write("h");
            TextView txt_hum = findViewById(R.id.txt_read_hum);
            runnableCode = new Runnable() {
                int counter2 = 0;

                // Define the code block to be executed
                @Override
                public void run() {

                    if (counter2 == 1) {
                        ble.Read();
                        counter2 = 2;
                    } else if (counter2 == 2) {
                        txth = ble.getS();
                        Log.d("", ble.getS());
                        //ble.Write("t");
                        counter2 = 3;
                    } else if (counter2 == 3) {
                        //txt_temp.setText(txt);
                        handler2.removeCallbacks(runnableCode);
                        counter2 = 0;
                    } else if (counter2 == 0) {
                        counter2 = 1;
                    }

                    Log.d("Handlers", "Called on main thread");
                    // Repeat this the same runnable code block again another 2 seconds
                    // 'this' is referencing the Runnable object
                    if (txth.equals("busy")) {
                        handler2.postDelayed(this, 2000);
                    } else {
                        txt_hum.setText(txth + " %RH");
                        txth = "busy";
                        handler2.removeCallbacks(runnableCode);
                        ok_towrite = 0;
                    }
                }
            };
            //handler2.removeCallbacks(runnableCode);

            if (txth.equals("busy")) {
                handler2.post(runnableCode);
                Log.d("", "post");
            }

        } else {
            if (ok_towrite == 0) {
                Toast.makeText(MainActivity.this, getString(R.string.tst_NoDevices), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Método onClickReadLum
     *
     * Se clicar no botão "Ler Luminosidade" no fragment_slideshow, o dispositivo envia um código ao microcontrolador para devolver o valor da luminosidade.
     */
    public void onClickReadLum(View view) {
        if (ble.getDevice() != null && ok_towrite == 0) {
            ok_towrite = 1;
            ble.Write("l");
            TextView txt_lum = findViewById(R.id.txt_read_lum);
            runnableCode = new Runnable() {
                int counter3 = 0;

                // Define the code block to be executed
                @Override
                public void run() {
                    // Do something here on the main thread
                    if (counter3 == 1) {
                        ble.Read();
                        counter3 = 2;
                    } else if (counter3 == 2) {
                        txtl = ble.getS();
                        Log.d("", ble.getS());
                        //ble.Write("t");
                        counter3 = 3;
                    } else if (counter3 == 3) {
                        //txt_temp.setText(txt);
                        handler3.removeCallbacks(runnableCode);
                        counter3 = 0;
                    } else if (counter3 == 0) {
                        counter3 = 1;
                    }

                    Log.d("Handlers", "Called on main thread");
                    // Repeat this the same runnable code block again another 2 seconds
                    // 'this' is referencing the Runnable object
                    if (txtl.equals("busy")) {
                        handler3.postDelayed(this, 2000);
                    } else {
                        txt_lum.setText(txtl + " lux");
                        txtl = "busy";
                        handler3.removeCallbacks(runnableCode);
                        ok_towrite = 0;
                    }
                }
            };

            if (txtl.equals("busy")) {
                handler3.post(runnableCode);
                Log.d("", "post");
            }

        } else {
            if (ok_towrite == 0) {
                Toast.makeText(MainActivity.this, getString(R.string.tst_NoDevices), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Método onClickLed
     *
     * Se clicar no botão "LED" no fragment_slideshow, o dispositivo envia um código ao microcontrolador para ligar ou desligar o LED conforme o seu estado.
     */
    public void onClickLed(View view) {
        TextView txtled = findViewById(R.id.txt_led_state);
        if (ble.getDevice() != null && ok_towrite == 0) {
            ok_towrite = 1;
            if (ledon == false) {
                ble.Write("on");
                ledon = true;
                txtled.setText("ON");
                ok_towrite = 0;
            } else {
                ble.Write("off");
                ledon = false;
                txtled.setText("OFF");
                ok_towrite = 0;
            }
        } else {
            if (ok_towrite == 0) {
                Toast.makeText(MainActivity.this, getString(R.string.tst_NoDevices), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Método onClickChangePreset
     *
     * Inicia a atividade "PresetsActivity".
     */
    public void onClickChangePreset(MenuItem item) {
        Intent intent = new Intent(this, PresetsActivity.class);
        intent.putExtra("bletxdata", "");
        startActivityForResult(intent, PRESET_REQUEST_CODE);
    }

    /**
     * Método onClickInfo
     *
     * Mostra um alert dialog com a informação do grupo
     * @param item
     */
    public void onClickInfo(MenuItem item) {
        AlertDialog ad = new AlertDialog.Builder(this).create();
        ad.setMessage(getString(R.string.info_ad));
        ad.setButton(Dialog.BUTTON_NEUTRAL, "Ok", null, null);
        ad.show();
    }

    public void onClickServoOn(View view) {
        TextView txtled2 = findViewById(R.id.txt_led_state2);
        if (man) {
            if (ble.getDevice() != null && ok_towrite == 0) {
                ok_towrite = 1;
                if (ton == false) {
                    ble.Write("ton");
                    ton = true;
                    txtled2.setText("ON");
                    ok_towrite = 0;
                } else {
                    ble.Write("toff");
                    ton = false;
                    txtled2.setText("OFF");
                    ok_towrite = 0;
                }
            } else {
                if (ok_towrite == 0) {
                    Toast.makeText(MainActivity.this, getString(R.string.tst_NoDevices), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.manual) + " OFF", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickResistOn(View view) {
        TextView txtled4 = findViewById(R.id.txt_led_state4);
        if (man) {
            if (ble.getDevice() != null && ok_towrite == 0) {
                ok_towrite = 1;
                if (aon == false) {
                    ble.Write("aon");
                    aon = true;
                    txtled4.setText("ON");
                    ok_towrite = 0;
                } else {
                    ble.Write("aoff");
                    aon = false;
                    txtled4.setText("OFF");
                    ok_towrite = 0;
                }
            } else {
                if (ok_towrite == 0) {
                    Toast.makeText(MainActivity.this, getString(R.string.tst_NoDevices), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.manual) + " OFF", Toast.LENGTH_SHORT).show();
        }
    }
    public void onClickRegaOn(View view) {
        TextView txtled3 = findViewById(R.id.txt_led_state3);
        if (man) {
            if (ble.getDevice() != null && ok_towrite == 0) {
                ok_towrite = 1;
                if (ron == false) {
                    ble.Write("ron");
                    ron = true;
                    txtled3.setText("ON");
                    ok_towrite = 0;
                } else {
                    ble.Write("roff");
                    ron = false;
                    txtled3.setText("OFF");
                    ok_towrite = 0;
                }
            } else {
                if (ok_towrite == 0) {
                    Toast.makeText(MainActivity.this, getString(R.string.tst_NoDevices), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.manual) + " OFF", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickManual(View view) {
        Switch sw = findViewById(R.id.switch2);
        if (ble.getDevice() != null && ok_towrite == 0) {
            ok_towrite = 1;
            if (man == false) {
                ble.Write("m");
                man = true;
                ok_towrite = 0;
                sw.setChecked(true);
            } else {
                ble.Write("m");
                man = false;
                ok_towrite = 0;
                sw.setChecked(false);
            }
        } else {
            sw.setChecked(false);
            if (ok_towrite == 0) {
                Toast.makeText(MainActivity.this, getString(R.string.tst_NoDevices), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
