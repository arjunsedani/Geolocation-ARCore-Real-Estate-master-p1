package com.example.aishnaagrawal.ardemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.example.aishnaagrawal.ardemo.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_about);
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        final double ltt1 = bundle.getDouble("LT");
        final double lnn2 = bundle.getDouble("LN");
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation2);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_favorites:
                                Intent startActivityIntent = new Intent(AboutActivity.this, MyLocation.class);
                                startActivity(startActivityIntent);
                                AboutActivity.this.finish();
                                break;
                            case R.id.action_schedules:
                                Intent startActivityIntent1 = new Intent(AboutActivity.this, ARActivity.class);
                                startActivityIntent1.putExtra("LT", ltt1);
                                startActivityIntent1.putExtra("LN", lnn2);
                                startActivity(startActivityIntent1);
                                AboutActivity.this.finish();
                                break;
                            case R.id.action_music:
                                break;
                        }
                        return false;
                    }
                });
    }
}
