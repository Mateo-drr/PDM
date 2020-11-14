package com.example.pdmg2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

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

    private ArrayList<User> arr_users;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewPager viewPager = findViewById(R.id.viewPager);
        LogActivity.AuthenticationPagerAdapter pagerAdapter = new LogActivity.AuthenticationPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragmet(new LoginFragment());
        pagerAdapter.addFragmet(new RegisterFragment());
        viewPager.setAdapter(pagerAdapter);

        //read login data
        logemail = findViewById(R.id.txtlogemail);
        logpass = findViewById(R.id.txtlogpass);
        //read register data
        regname = findViewById(R.id.txtregname);
        regemail = findViewById(R.id.txtregemail);
        regpass = findViewById(R.id.txtregpass);
        regpass2 = findViewById(R.id.txtregpass2);

    }

    public void onClickRegister(View view) {
        userregname = regname.getText().toString();
        userregemail = regemail.getText().toString();
        userregpass = regpass.getText().toString();
        userregpass2 = regpass2.getText().toString();

        if(userlogemail.isEmpty() || userlogpass.isEmpty() || userlogemail.equals("-") || userlogpass.equals("-")) {
            //AlertDialog ad = new AlertDialog.Builder(this).create();
            //ad.setMessage(getString(R.string.tst_fillEverything));
            //ad.setTitle(getString(R.string.tst_error));
            //ad.setIcon(R.drawable.actionbar_exc);
            //ad.setButton(Dialog.BUTTON_NEUTRAL, "OK", null,null);
            //ad.show();

            Toast.makeText(this, getString(R.string.tst_fillEverything), Toast.LENGTH_SHORT).show();
        }else {
            if(userregpass.equals(userregpass2)) {
                User ureg = new User(userregemail, userName, userPass);
                arr_users.add(ureg);
                // TODO compare if email already exists
                Toast.makeText(this, getString(R.string.tst_usercreated), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, getString(R.string.tst_diffpass), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onClickLogin(View view) {
        userlogemail = logemail.getText().toString();
        userPass = logpass.getText().toString();

        if(userlogemail.isEmpty() || userlogpass.isEmpty() || userlogemail.equals("-") || userlogpass.equals("-")) {
            //AlertDialog ad = new AlertDialog.Builder(this).create();
            //ad.setMessage(getString(R.string.tst_fillEverything));
            //ad.setTitle(getString(R.string.tst_error));
            //ad.setIcon(R.drawable.actionbar_exc);
            //ad.setButton(Dialog.BUTTON_NEUTRAL, "OK", null,null);
            //ad.show();

            Toast.makeText(this, getString(R.string.tst_fillEverything), Toast.LENGTH_SHORT).show();
        }else {
            //TODO
            User ulog = new User(userlogemail, "", userPass);
            //ulog.getEmail().compareTo();

            //Launch main activity
            Intent i = new Intent (this, MainActivity.class);
            startActivityForResult(i, 1);
        }

    }

    public String getUserlogemail() {
        return userlogemail;
    }

    public void setUserlogemail(String userlogemail) {
        userlogemail = userlogemail;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        userPass = userPass;
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
