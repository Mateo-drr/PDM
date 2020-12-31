package com.example.pdmg2;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class LogActivity extends AppCompatActivity {

    private EditText logemail;
    private EditText logpass;
    private String userlogemail;
    private String userlogpass;

    private EditText regemail;
    private EditText regpass;
    private EditText regpass2;
    private EditText regname;

    private String userregname;
    private String userregemail;
    private String userregpass;
    private String userregpass2;

    private String userName;
    private String userEmail;
    private String userPass;
    private FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewPager viewPager = findViewById(R.id.viewPager);

        LogActivity.AuthenticationPagerAdapter pagerAdapter = new LogActivity.AuthenticationPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragmet(new LoginFragment());
        pagerAdapter.addFragmet(new RegisterFragment());
        viewPager.setAdapter(pagerAdapter);
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    public void OnClickRegister(View view) {
        //read register data
        regname = findViewById(R.id.txtregname);
        regemail = findViewById(R.id.txtregemail);
        regpass = findViewById(R.id.txtregpass);
        regpass2 = findViewById(R.id.txtregpass2);

        userregname = regname.getText().toString();
        userregemail = regemail.getText().toString();
        userregpass = regpass.getText().toString();
        userregpass2 = regpass2.getText().toString();

        if (userregemail.isEmpty()) {
            regemail.setError("Please enter email id");
            regemail.requestFocus();
        } else if (userregpass.isEmpty()) {
            regpass.setError("Please enter your password");
            regpass.requestFocus();
        } else if (userregpass.length() < 6) {
            regpass.setError("The given password is invalid. Password should be at least 6 characters!");
            regpass.requestFocus();
        } else if (userregpass2.isEmpty()) {
            regpass2.setError("Please confirm your password");
            regpass2.requestFocus();
        } else if (userregemail.isEmpty() && userregpass.isEmpty() && userregpass2.isEmpty()) {
            Toast.makeText(this, "Fields Are Empty!", Toast.LENGTH_SHORT).show();
        } else if (!userregpass.equals(userregpass2)) {
            Toast.makeText(this, "Passwords dont match!", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.createUserWithEmailAndPassword(userregemail, userregpass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                User user = new User(userregname,userregemail);

                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(LogActivity.this, getString(R.string.tst_RegisterSuc), Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(LogActivity.this, getString(R.string.tst_RegisterFail), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LogActivity.this, getString(R.string.tst_RegisterFail), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        Intent intent = new Intent(LogActivity.this, LogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        //else{
        //Toast.makeText(this, getString(R.string.tst_diffpass), Toast.LENGTH_SHORT).show();
    }

    public void onClickLogin(View view) {

        logemail = findViewById(R.id.txtlogemail);
        logpass = findViewById(R.id.txtlogpass);
        //read login data
        userlogemail = logemail.getText().toString().trim();
        userlogpass = logpass.getText().toString().trim();

        if (userlogemail.isEmpty() || userlogpass.isEmpty() || userlogemail.equals("-") || userlogpass.equals("-")) {
            //AlertDialog ad = new AlertDialog.Builder(this).create();
            //ad.setMessage(getString(R.string.tst_fillEverything));
            //ad.setTitle(getString(R.string.tst_error));
            //ad.setIcon(R.drawable.actionbar_exc);
            //ad.setButton(Dialog.BUTTON_NEUTRAL, "OK", null,null);
            //ad.show();
            Toast.makeText(this, getString(R.string.tst_fillEverything), Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(userlogemail, userlogpass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LogActivity.this, getString(R.string.tst_log_ok), Toast.LENGTH_SHORT).show();

                                //Launch main activity and remove login activity
                                Intent intent = new Intent(LogActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LogActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    public void onClickLogFing(View view) {

    }

    public void onClickRegFing(View view) {
    }

    class AuthenticationPagerAdapter extends FragmentPagerAdapter {
        private final ArrayList<Fragment> fragmentList = new ArrayList<>();

        public AuthenticationPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return fragmentList.get(i);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        void addFragmet(Fragment fragment) {
            fragmentList.add(fragment);
        }
    }


}
