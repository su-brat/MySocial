package com.example.mysocial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firebaseAuth = FirebaseAuth.getInstance();

        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.nav_view);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        HomeFragment fragment1 = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.container, fragment1, "");
        ft1.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    HomeFragment fragment1 = new HomeFragment();
                    FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                    ft1.replace(R.id.container, fragment1, "");
                    ft1.commit();
                    return true;
                case R.id.nav_profile:
                    ProfileFragment fragment2 = new ProfileFragment();
                    FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                    ft2.replace(R.id.container, fragment2, "");
                    ft2.commit();
                    return true;
                case R.id.nav_chat:
                    ChatListFragment fragment3 = new ChatListFragment();
                    FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                    ft3.replace(R.id.container, fragment3, "");
                    ft3.commit();
                    return true;
                case R.id.nav_users:
                    UsersFragment fragment4 = new UsersFragment();
                    FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                    ft4.replace(R.id.container, fragment4, "");
                    ft4.commit();
                    return true;
            }

            return false;
        }
    };

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null) {
            //Toast.makeText(this, ""+user.getEmail(), Toast.LENGTH_SHORT).show();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("SignInTimestamp","online");
            FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).updateChildren(hashMap);
        }
        else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void finish() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null) {
            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
            String dateTime = DateFormat.format("dd-MM-yy  hh:mm aa", calendar).toString();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("SignInTimestamp",dateTime);
            FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).updateChildren(hashMap);
        }
        super.finish();
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

}
