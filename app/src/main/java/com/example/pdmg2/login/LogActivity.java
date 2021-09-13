package com.example.pdmg2.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.pdmg2.MainActivity;
import com.example.pdmg2.R;
import com.example.pdmg2.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Activity para os fragmentos login e register
 */
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

    private FirebaseAuth mAuth;
    private ViewPager viewPager;
    private AuthenticationPagerAdapter pagerAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        viewPager = findViewById(R.id.viewPager);

        pagerAdapter = new AuthenticationPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragmet(new LoginFragment());
        pagerAdapter.addFragmet(new RegisterFragment());
        viewPager.setAdapter(pagerAdapter);
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    /**
     * onClick para registar um usuario, verifica se os campos forma preenchidos corretamente
     * No final volta ao fragmento login.
     * @param view
     */
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
            regemail.setError(getString(R.string.reg_email));
            regemail.requestFocus();
        } else if (userregpass.isEmpty()) {
            regpass.setError(getString(R.string.reg_pass));
            regpass.requestFocus();
        } else if (userregpass.length() < 6) {
            regpass.setError(getString(R.string.reg_pass_inv));
            regpass.requestFocus();
        } else if (userregpass2.isEmpty()) {
            regpass2.setError(getString(R.string.reg_conf_pass));
            regpass2.requestFocus();
        } else if (userregemail.isEmpty() && userregpass.isEmpty() && userregpass2.isEmpty()) {
            Toast.makeText(this, getString(R.string.Fill_all), Toast.LENGTH_SHORT).show();
        } else if (!userregpass.equals(userregpass2)) {
            Toast.makeText(this, getString(R.string.tst_diffpass), Toast.LENGTH_SHORT).show();
        } else {
            mAuth.createUserWithEmailAndPassword(userregemail, userregpass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                User.Dia4 dia4 = new User.Dia4(0, 0, 0, 0);
                                User.Dia3 dia3 = new User.Dia3(0, 0, 0, 0);
                                User.Dia2 dia2 = new User.Dia2(0, 0, 0, 0);
                                User.Dia1 dia1 = new User.Dia1(0, 0, 0, 0);
                                User.Dia0 dia0 = new User.Dia0(0, 0, 0, 0);
                                int[] calendar = new int[3];
                                calendar[0]=(Calendar.getInstance().get(Calendar.YEAR));
                                calendar[1]=(Calendar.getInstance().get(Calendar.MONTH));
                                calendar[2]=(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                                User user = new User(userregname,userregemail, calendar[2], calendar[1]+1, calendar[0], dia4, dia3, dia2, dia1, dia0);

                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(LogActivity.this, getString(R.string.tst_RegisterSuc), Toast.LENGTH_SHORT).show();
                                            viewPager.setAdapter(pagerAdapter);
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
    }

    /**
     * onClick para entrar a conta, verifica com a firebase os dados e inicia a MainActivity.
     * @param view
     */
    public void onClickLogin(View view) {

        logemail = findViewById(R.id.txtlogemail);
        logpass = findViewById(R.id.txtlogpass);
        //read login data
        userlogemail = logemail.getText().toString().trim();
        userlogpass = logpass.getText().toString().trim();

        if (userlogemail.isEmpty() || userlogpass.isEmpty() || userlogemail.equals("-") || userlogpass.equals("-")) {
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
                                Toast.makeText(LogActivity.this, getString(R.string.aut_fail), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

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
