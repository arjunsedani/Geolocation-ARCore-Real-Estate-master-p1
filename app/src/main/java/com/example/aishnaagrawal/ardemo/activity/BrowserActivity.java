package com.example.aishnaagrawal.ardemo.activity;

/**
 * Created by sedani.ab on 1/31/2018.
 */

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;



import com.example.aishnaagrawal.ardemo.R;


/**
 * Created by sedani.ab on 1/17/2018.
 */

public class BrowserActivity extends AppCompatActivity {

    private String str1,str2,str3,str4,str5;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageView GoToNewActivity;


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        WebView htmlWebView = (WebView)findViewById(R.id.webView);
        htmlWebView.setWebViewClient(new CustomWebViewClient());
        //arjun
        WebSettings webSetting = htmlWebView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDisplayZoomControls(true);

        htmlWebView.loadUrl("file:///android_asset/sample.html");

        /*GoToNewActivity = (ImageView)findViewById(R.id.btnOk);



        GoToNewActivity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                // Intent code for open new activity through intent.
                startActivity(new Intent(BrowserActivity.this, ARActivity.class));
               *//* Intent intent = new Intent(BrowserActivity.this, SelectAppActivity.class);
                startActivity(intent);*//*
               *//* overridePendingTransition(R.anim.slide_down_info,R.anim.no_change);*//*

            }
        });*/
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }



    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);
            return true;
        }
    }

    public void onBackPressed() {
        finish();
        startActivity(new Intent(BrowserActivity.this, ARActivity.class));
    }


}