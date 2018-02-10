package com.example.aishnaagrawal.ardemo.activity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


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
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.aishnaagrawal.ardemo.R.drawable.ic_add_location_black_24dp;


public class MyLocation extends AppCompatActivity implements com.example.aishnaagrawal.ardemo.activity.LocationProvider.LocationCallback, GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback{


    private static final LatLng RAJKOT = new LatLng(22.3039, 70.8022);
    private static final LatLng BANGALORE = new LatLng(12.9716, 77.5946);
    private static LatLng srcltglng;
    private static LatLng destltglng;
    public  double latt,lngg;


    /*public static final String TAG = MapsActivity.class.getSimpleName();*/

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private com.example.aishnaagrawal.ardemo.activity.LocationProvider mLocationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_location_demo);
        //DRAW PATH
        /*String url = getDirectionsUrl(RAJKOT,BANGALORE);
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);*/

        //setUpMapIfNeeded();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

        mLocationProvider = new com.example.aishnaagrawal.ardemo.activity.LocationProvider(this, this);

        /*initNavigationDrawer();*/
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_favorites:
                                Intent startActivityIntent = new Intent(MyLocation.this, ARActivity.class);
                                startActivityIntent.putExtra("LT", latt);
                                startActivityIntent.putExtra("LN", lngg);
                                startActivity(startActivityIntent);
                                MyLocation.this.finish();
                                break;
                            case R.id.action_schedules:
                                Intent startActivityIntent1 = new Intent(MyLocation.this, ARActivity.class);
                                startActivity(startActivityIntent1);
                                MyLocation.this.finish();
                                break;
                            case R.id.action_music:
                                Intent startActivityIntent2 = new Intent(MyLocation.this, ARActivity.class);
                                startActivity(startActivityIntent2);
                                MyLocation.this.finish();
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

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
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

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    public void handleNewLocation(Location location) {
        //BitmapDescriptor icon1 = BitmapDescriptorFactory.fromResource(R.drawable.arjun);
        UiSettings settings = mMap.getUiSettings();
        /*Log.d(TAG, location.toString());*/

        double currentLatitude = location.getLatitude();
        latt=currentLatitude;
        double currentLongitude = location.getLongitude();
        lngg=currentLongitude;
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        srcltglng = latLng;
        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.arjun))
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng)
                .title("Snowqualmie Falls")
                .snippet("Snoqualmie Falls is located 25 miles east of Seattle.")
                .icon(BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_BLUE));

        InfoWindowData info = new InfoWindowData();
        info.setImage("snowqualmie");
        info.setHotel("Hotel : excellent hotels available");
        info.setFood("Food : all types of restaurants available");
        info.setTransport("Reach the site by bus, car and train.");

        CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(this);
        mMap.setInfoWindowAdapter(customInfoWindow);

        Marker m = mMap.addMarker(markerOptions);
        m.setTag(info);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        mMap.setTrafficEnabled(true);
        settings.setZoomControlsEnabled(true);
        settings.setCompassEnabled(true);
        mMap.setOnInfoWindowClickListener(this);
        /*m.showInfoWindow();*/
     /*   MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!")
                .snippet(String.valueOf(latLng))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        //.icon(vectorToBitmap(R.drawable.arjun, Color.parseColor("#A4C639")));

        mMap.addMarker(options);
        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,5));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        mMap.setTrafficEnabled(true);
        settings.setZoomControlsEnabled(true);
        settings.setCompassEnabled(true);
        mMap.setOnInfoWindowClickListener(this);*/
    }
//1111
    @Override
    public void onInfoWindowClick(Marker marker) {
      /*  Intent startActivityIntent = new Intent(MyLocation.this, BrowserActivity.class);
        startActivity(startActivityIntent);*/
       /* Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();*/
        /*infoWindowClick();*/
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

  /*  public void onMapSearchmylocation(View view) {
        UiSettings settings = mMap.getUiSettings();
        EditText locationSearch = (EditText) findViewById(R.id.editText);
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            destltglng = latLng;
            String url = getDirectionsUrl(srcltglng, destltglng);
            ReadTask downloadTask = new ReadTask();
            downloadTask.execute(url);
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title("I am here!")
                    .snippet(String.valueOf(latLng))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            //.icon(vectorToBitmap(R.drawable.arjun, Color.parseColor("#A4C639")));

            mMap.addMarker(options);
            // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,5));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            mMap.setTrafficEnabled(true);
            settings.setZoomControlsEnabled(true);
            settings.setCompassEnabled(true);
            mMap.setOnInfoWindowClickListener(this);
        }
    }*/

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        // Output format
        String output = "json";
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(5);
                polyLineOptions.color(Color.BLUE);
            }

            mMap.addPolyline(polyLineOptions);
        }
    }

   /* public void initNavigationDrawer() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view1);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.nav_first_fragment:
                        Intent startActivityIntent = new Intent(MyLocation.this, ARActivity.class);
                        startActivityIntent.putExtra("LT", latt);
                        startActivityIntent.putExtra("LN", lngg);
                        startActivity(startActivityIntent);
                        MyLocation.this.finish();
                        break;
                    case R.id.nav_second_fragment:
                        //same logic
                        break;
                    case R.id.nav_third_fragment:
                        //same logic
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }*/

//filter marker
List<Marker> busesList = new ArrayList<>();
    List<Marker> trainsList = new ArrayList<>();
public void addMarkers2Map (){

    // Markers locations
    LatLng sydney = new LatLng(-34, 151);
    LatLng katoomba = new LatLng(-33.717901, 150.312149);
    LatLng portland = new LatLng(-38.311725, 141.585761);
    LatLng adelaide = new LatLng(-34.928401, 138.605669);
    LatLng perth = new LatLng(-31.951340, 115.857019);
    LatLng campbell = new LatLng(-34.072022, 150.806118);
    LatLng albany = new LatLng(-34.977138, 117.884153);
//temp
    MarkerOptions markerOptions = new MarkerOptions();
    markerOptions.position(sydney)
            .title("Snowqualmie Falls")
            .snippet("Snoqualmie Falls is located 25 miles east of Seattle.")
            .icon(BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_BLUE));

    InfoWindowData info = new InfoWindowData();
    info.setImage("snowqualmie");
    info.setHotel("Hotel : excellent hotels available");
    info.setFood("Food : all types of restaurants available");
    info.setTransport("Reach the site by bus, car and train.");

    CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(this);
    mMap.setInfoWindowAdapter(customInfoWindow);

    Marker m1 = mMap.addMarker(markerOptions);
    busesList.add(m1);
    m1.setTag(info);
    mMap.setOnInfoWindowClickListener(this);

   // busesList.add(mMap.addMarker(new MarkerOptions().position(campbell).title("Marker in Campbell").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_add_location_black_24dp)).snippet("an hour interval for Campbell")));

    busesList.add(mMap.addMarker(new MarkerOptions().position(albany).title("Marker in Albany").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_add_location_black_24dp)).snippet("an hour interval for Albany")));
    trainsList.add(mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_add_location_black_24dp)).snippet("an hour interval for Sydney")));
    trainsList.add(mMap.addMarker(new MarkerOptions().position(katoomba).title("Marker in Katoomba").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_add_location_black_24dp)).snippet("an hour interval for Katoomba")));
    trainsList.add(mMap.addMarker(new MarkerOptions().position(portland).title("Marker in Portland").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_add_location_black_24dp)).snippet("an hour interval for Portland")));
    trainsList.add(mMap.addMarker(new MarkerOptions().position(adelaide).title("Marker in Adelaide").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_add_location_black_24dp)).snippet("an hour interval for Adelaide")));
    trainsList.add(mMap.addMarker(new MarkerOptions().position(perth).title("Marker in Perth").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_add_location_black_24dp)).snippet("an hour interval for Perth")));
}

    AlertDialog dialog,dialog1;
    CheckBox buses, trains;

    public void filterTheMarkers(View view) {

        if (dialog == null) {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View checkBoxView = inflater.inflate(R.layout.marker_selection, null);

            builder.setView(checkBoxView);
            buses = (CheckBox) checkBoxView.findViewById(R.id.checkBox1);
            trains = (CheckBox) checkBoxView.findViewById(R.id.checkBox2);
            Button okButton = (Button) checkBoxView.findViewById(R.id.okButton);
            Button cancelButton = (Button) checkBoxView.findViewById(R.id.cancelButton);
            dialog = builder.create();
        }
        dialog.show();


    }





    public void displaySelectedMarkers(View view) {

        dialog.dismiss();
        Log.i("TAG", "Trains Status " + trains.isChecked() + " Bus Status  " + buses.isChecked());
        //according these check boxes status execute your code to show/hide markers
        if (trains.isChecked() && buses.isChecked()) {
            //show all markers
            for (Marker marker : trainsList){
                marker.setVisible(true);
            }
            for (Marker marker : busesList){
                marker.setVisible(true);
            }
        } else if (trains.isChecked() && !buses.isChecked()) {
            //show trains and hide buses markers
            //if (view.getId() == R.id.checkBox1){
            for (Marker marker : trainsList){
                marker.setVisible(true);
            }
            for (Marker marker : busesList){
                marker.setVisible(false);
            }
            //}
        } else if (!trains.isChecked() && buses.isChecked()) {
            //hide trains and show buses markers
            //if (view.getId() == R.id.checkBox2){
            for (Marker marker : busesList){
                marker.setVisible(true);
            }
            for (Marker marker : trainsList){
                marker.setVisible(false);
            }
            //}
        }

        else {
            for (Marker marker : busesList){
                marker.setVisible(false);
            }
            for (Marker marker : trainsList){
                marker.setVisible(false);
            }
        }
    }

    public void doNothing(View view) {
        dialog.dismiss();

    }
    public void doNothing1(View view) {
        dialog1.dismiss();

    }


    public void openVirtual() {

        Intent startActivityIntent = new Intent(MyLocation.this, BrowserActivity.class);
        startActivity(startActivityIntent);

    }

}

