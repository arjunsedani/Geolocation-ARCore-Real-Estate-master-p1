package com.example.aishnaagrawal.ardemo.activity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.aishnaagrawal.ardemo.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class MyLocation extends AppCompatActivity implements com.example.aishnaagrawal.ardemo.activity.LocationProvider.LocationCallback, GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {


    public double latt, lngg;
    public String gptext;


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private com.example.aishnaagrawal.ardemo.activity.LocationProvider mLocationProvider;

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
        setContentView(R.layout.my_location_demo);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

        mLocationProvider = new com.example.aishnaagrawal.ardemo.activity.LocationProvider(this, this);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_favorites:
                                break;
                            case R.id.action_schedules:
                                Intent startActivityIntent1 = new Intent(MyLocation.this, ARActivity.class);
                                startActivityIntent1.putExtra("LT", latt);
                                startActivityIntent1.putExtra("LN", lngg);
                                startActivity(startActivityIntent1);
                                MyLocation.this.finish();
                                break;
                            case R.id.action_music:
                                break;
                        }
                        return false;
                    }
                });


    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mLocationProvider.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationProvider.disconnect();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            // mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap;
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    public void handleNewLocation(Location location) {
        UiSettings settings = mMap.getUiSettings();
        double currentLatitude = location.getLatitude();
        latt = currentLatitude;
        double currentLongitude = location.getLongitude();
        lngg = currentLongitude;
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng)
                .title("Snowqualmie Falls")
                .snippet("Snoqualmie Falls is located 25 miles east of Seattle.")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        mMap.setTrafficEnabled(true);
        settings.setZoomControlsEnabled(true);
        settings.setCompassEnabled(true);
        mMap.setOnInfoWindowClickListener(this);

    }

    //1111
    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.v("sdsds", marker.getTitle());
        gptext = marker.getTitle();
        openVirtual();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //to add list of markers
        addMarkers2Map();
        mMap.setMyLocationEnabled(true);
    }


    //filter marker
    List<Marker> applist = new ArrayList<>();
    List<Marker> officelist = new ArrayList<>();
    List<Marker> houselist = new ArrayList<>();

    public void addMarkers2Map() {

        // Markers locations
        LatLng hsr = new LatLng(12.927618, 77.643575);
        LatLng mysore = new LatLng(12.920059, 77.499575);
        LatLng electroniccity = new LatLng(12.98, 77.499575);

//temp

        MarkerOptions markerhsr = new MarkerOptions();
        markerhsr.position(hsr)
                .title("MANTRISQUARE")
                .snippet("Snoqualmie Falls is located 25 miles east of Seattle.")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.apartment));


        CustomInfoWindowGoogleMap customInfoWindowhsr = new CustomInfoWindowGoogleMap(this);
        InfoWindowData infohsr = new InfoWindowData();
        infohsr.setImage("home1");
        infohsr.setHotel("Appartment");
        infohsr.setFood("3bhk flat");
        infohsr.setTransport("PRIZE:1.5crore");
        mMap.setInfoWindowAdapter(customInfoWindowhsr);
        Marker m1 = mMap.addMarker(markerhsr);
        applist.add(m1);
        m1.setTag(infohsr);
        //mysore
        MarkerOptions markermysore = new MarkerOptions();
        markermysore.position(mysore)
                .title("EVRY INDIA PVT LTD")
                .snippet("OFFICE SPACE AVAILABLE IN GLOBLE VILLAGE")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.office_block));


        CustomInfoWindowGoogleMap customInfoWindowmysore = new CustomInfoWindowGoogleMap(this);
        InfoWindowData infomysore = new InfoWindowData();
        infomysore.setImage("myoffice");
        infomysore.setHotel("Office space with capacity of 635 workstation");
        infomysore.setFood("available for 5 years of lease");
        infomysore.setTransport("contact:Viraj at 7777777777");
        mMap.setInfoWindowAdapter(customInfoWindowmysore);
        Marker m2 = mMap.addMarker(markermysore);
        officelist.add(m2);
        m2.setTag(infomysore);

        //electronic city
        MarkerOptions markerelectronic = new MarkerOptions();
        markerelectronic.position(electroniccity)
                .title("HOUSE 303/F1")
                .snippet("VILLA AT THE PRIME LOCATION")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.houseicon));


        CustomInfoWindowGoogleMap customInfoWindowelectronic = new CustomInfoWindowGoogleMap(this);
        InfoWindowData infoelectronic = new InfoWindowData();
        infoelectronic.setImage("mantrivillas");
        infoelectronic.setHotel("4BHK VILLA WITH BASEMENT PARKING");
        infoelectronic.setFood("available for rent and sale");
        infoelectronic.setTransport("contact:Viraj at 7777777777");
        mMap.setInfoWindowAdapter(customInfoWindowelectronic);
        Marker m3 = mMap.addMarker(markerelectronic);
        houselist.add(m3);
        m3.setTag(infoelectronic);

        mMap.setOnInfoWindowClickListener(this);
    }


    AlertDialog dialog;
    CheckBox office, appartment, house;

    public void filterTheMarkers(View view) {
        View decorView1 = getWindow().getDecorView();
        decorView1.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        if (dialog == null) {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View checkBoxView = inflater.inflate(R.layout.marker_selection, null);

            builder.setView(checkBoxView);
            appartment = (CheckBox) checkBoxView.findViewById(R.id.checkBox1);
            office = (CheckBox) checkBoxView.findViewById(R.id.checkBox2);
            house = (CheckBox) checkBoxView.findViewById(R.id.checkBox3);
            Button okButton = (Button) checkBoxView.findViewById(R.id.okButton);
            Button cancelButton = (Button) checkBoxView.findViewById(R.id.cancelButton);
            dialog = builder.create();
        }
        dialog.show();


    }


    public void displaySelectedMarkers(View view) {
        View decorView2 = getWindow().getDecorView();
        decorView2.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        dialog.dismiss();
        View decorView3 = getWindow().getDecorView();
        // Log.i("TAG", "Trains Status " + trains.isChecked() + " Bus Status  " + buses.isChecked());
        //according these check boxes status execute your code to show/hide markers
        if (office.isChecked() && appartment.isChecked() && house.isChecked()) {
            //show all markers
            for (Marker marker : officelist) {
                marker.setVisible(true);
            }
            for (Marker marker : applist) {
                marker.setVisible(true);
            }
            for (Marker marker : houselist) {
                marker.setVisible(true);
            }
        } else if (office.isChecked() && !appartment.isChecked() && !house.isChecked()) {
            //show trains and hide buses markers
            //if (view.getId() == R.id.checkBox1){
            for (Marker marker : officelist) {
                marker.setVisible(true);
            }
            for (Marker marker : applist) {
                marker.setVisible(false);
            }
            for (Marker marker : houselist) {
                marker.setVisible(false);
            }
            //}
        } else if (!office.isChecked() && appartment.isChecked() && !house.isChecked()) {
            //hide trains and show buses markers
            //if (view.getId() == R.id.checkBox2){
            for (Marker marker : applist) {
                marker.setVisible(true);
            }
            for (Marker marker : officelist) {
                marker.setVisible(false);
            }
            for (Marker marker : houselist) {
                marker.setVisible(false);
            }
            //}
        } else if (!office.isChecked() && !appartment.isChecked() && house.isChecked()) {
            //hide trains and show buses markers
            //if (view.getId() == R.id.checkBox2){
            for (Marker marker : applist) {
                marker.setVisible(false);
            }
            for (Marker marker : officelist) {
                marker.setVisible(false);
            }
            for (Marker marker : houselist) {
                marker.setVisible(true);
            }
            //}
        } else if (office.isChecked() && appartment.isChecked() && !house.isChecked()) {
            //hide trains and show buses markers
            //if (view.getId() == R.id.checkBox2){
            for (Marker marker : applist) {
                marker.setVisible(true);
            }
            for (Marker marker : officelist) {
                marker.setVisible(true);
            }
            for (Marker marker : houselist) {
                marker.setVisible(false);
            }
            //}
        } else if (office.isChecked() && !appartment.isChecked() && house.isChecked()) {
            //hide trains and show buses markers
            //if (view.getId() == R.id.checkBox2){
            for (Marker marker : applist) {
                marker.setVisible(false);
            }
            for (Marker marker : officelist) {
                marker.setVisible(true);
            }
            for (Marker marker : houselist) {
                marker.setVisible(true);
            }
            //}
        } else if (!office.isChecked() && appartment.isChecked() && house.isChecked()) {
            //hide trains and show buses markers
            //if (view.getId() == R.id.checkBox2){
            for (Marker marker : applist) {
                marker.setVisible(true);
            }
            for (Marker marker : officelist) {
                marker.setVisible(false);
            }
            for (Marker marker : houselist) {
                marker.setVisible(true);
            }
            //}
        } else {
            for (Marker marker : applist) {
                marker.setVisible(false);
            }
            for (Marker marker : officelist) {
                marker.setVisible(false);
            }
            for (Marker marker : houselist) {
                marker.setVisible(false);
            }
        }
    }

    public void doNothing(View view) {
        View decorView2 = getWindow().getDecorView();
        decorView2.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        dialog.dismiss();

    }


    public void openVirtual() {
      /*  String gptext = markerin;*/
        Intent startActivityIntent = new Intent(MyLocation.this, BrowserActivity.class);
        startActivityIntent.putExtra("GP", gptext);
        startActivity(startActivityIntent);
    }
}

