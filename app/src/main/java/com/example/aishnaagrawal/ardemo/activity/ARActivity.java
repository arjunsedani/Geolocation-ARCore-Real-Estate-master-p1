package com.example.aishnaagrawal.ardemo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Range;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.aishnaagrawal.ardemo.R;
import com.example.aishnaagrawal.ardemo.helper.CameraPermissionHelper;
import com.example.aishnaagrawal.ardemo.model.LocationTime;
/*import com.example.aishnaagrawal.ardemo.model.MarkerInfo;*/
import com.example.aishnaagrawal.ardemo.model.MarkerLocation;
import com.example.aishnaagrawal.ardemo.renderer.BackgroundRenderer;
import com.example.aishnaagrawal.ardemo.renderer.ObjectRenderer;
import com.google.android.gms.maps.model.Marker;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Frame.TrackingState;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;


public class ARActivity extends AppCompatActivity implements GLSurfaceView.Renderer, SensorEventListener, LocationListener {

    private static final String TAG = ARActivity.class.getSimpleName();

    private GLSurfaceView mSurfaceView;
    private Config mDefaultConfig;
    private Session mSession;
    private BackgroundRenderer mBackgroundRenderer = new BackgroundRenderer();
    private GestureDetector mGestureDetector;
    private ObjectRenderer mVirtualObject = new ObjectRenderer();
    private ObjectRenderer mVirtualObject2 = new ObjectRenderer();
    private ObjectRenderer mVirtualObject3 = new ObjectRenderer();

    private final float[] mAnchorMatrix = new float[16];

    // Tap handling and UI.
    private ArrayBlockingQueue<MotionEvent> mQueuedSingleTaps = new ArrayBlockingQueue<>(16);

    //Location-based stuff
    private SensorManager mSensorManager;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 0;//1000 * 60 * 1; // 1 minute
    public static final int REQUEST_LOCATION_PERMISSIONS_CODE = 0;

    private LocationManager mLocationManager;
    public Location mLocation;

    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    boolean locationServiceAvailable;

    /*private static MarkerApi mMarkerApi;*/
    private String mBaseUrl = "http://139.59.30.117:3000/listings/";
    /*private Retrofit mRetrofit;*/

    private List<MarkerInfo> mMarkerList;
    private Frame mFrame;
    private float[] mZeroMatrix = new float[16];

    float[] translation = new float[]{0.0f, -0.8f, -0.8f};
    float[] rotation = new float[]{0.0f, -1.00f, 0.0f, 0.3f};

    Pose mPose = new Pose(translation, rotation);

    private double ltt, lnn;
    //names for validatng names and augmenting different objects
    public  String name1,name2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);


        mSurfaceView = (GLSurfaceView) findViewById(R.id.surfaceview);
        mSession = new Session(/*context=*/this);
        mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);

        // Create default config, check is supported, create session from that config.
        mDefaultConfig = Config.createDefaultConfig();
        if (!mSession.isSupported(mDefaultConfig)) {
            Toast.makeText(this, "This device does not support AR", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Matrix.setIdentityM(mZeroMatrix, 0);
        mPose.toMatrix(mAnchorMatrix, 0);

        // Set up tap listener.
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                onSingleTap(e);
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });

        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });

        // Set up renderer.
        mSurfaceView.setPreserveEGLContextOnPause(true);
        mSurfaceView.setEGLContextClientVersion(2);
        mSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Alpha used for plane blending.
        mSurfaceView.setRenderer(this);
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        /*mRetrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mMarkerApi = mRetrofit.create(MarkerApi.class);*/
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        ltt = bundle.getDouble("LT");
        lnn = bundle.getDouble("LN");
        mMarkerList = new ArrayList<>();
           /* MarkerLocation markerLocation = new MarkerLocation("" + 12.913714, "" + 77.500570);*/
       /* MarkerLocation markerLocation = new MarkerLocation("" + ltt, "" + lnn);
        MarkerInfo marker1 = new MarkerInfo("Jack Baskin Engineering1", "Academic Building", markerLocation);
        mMarkerList.add(marker1);
        markerLocation = new MarkerLocation("" + (ltt+0.01), "" + (lnn+0.1));
        marker1 = new MarkerInfo("Jack Baskin Engineering2", "Academic Building", markerLocation);
        mMarkerList.add(marker1);*/
        MarkerLocation markerLocation = new MarkerLocation("" + ltt, "" + lnn);
        MarkerInfo marker1 = new MarkerInfo("Jack Baskin Engineering1", "Academic Building", markerLocation);
        mMarkerList.add(marker1);
        name1 = marker1.name;
        MarkerLocation markerLocation1 = new MarkerLocation("" + (ltt+0.01), "" + (lnn+0.1));
        MarkerInfo marker2 = new MarkerInfo("Jack Baskin Engineering2", "Academic Building", markerLocation1);
        name2 = marker2.name;
        mMarkerList.add(marker2);
       /* MarkerLocation markerLocation = new MarkerLocation("" + 12.9197749, "" + 77.4995699);
        MarkerInfo marker1 = new MarkerInfo("Jack Baskin Engineering1", "Academic Building", markerLocation);
        mMarkerList.add(marker1);
        markerLocation = new MarkerLocation("" + 12.9198010, "" + 77.4995682);
        marker1 = new MarkerInfo("Jack Baskin Engineering2", "Academic Building", markerLocation);
        mMarkerList.add(marker1);*/
        //arjun
         /*   MarkerLocation markerLocation1 = new MarkerLocation("" + 12.913714, "" + 77.520570);*/
        /*MarkerLocation markerLocation1 = new MarkerLocation("" + ltt, "" + (lnn + 0.02000));
        MarkerInfo marker2 = new MarkerInfo("Jack Baskin Engineering2", "Academic Building1", markerLocation1);
        mMarkerList.add(marker2);*/
        /*initNavigationDrawer1();*/

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation1);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_favorites:
                                Intent startActivityIntent = new Intent(ARActivity.this, MyLocation.class);
                                startActivity(startActivityIntent);
                                ARActivity.this.finish();
                                break;
                            case R.id.action_schedules:
                                break;
                            case R.id.action_music:
                              /*  Intent startActivityIntent2 = new Intent(MyLocation.this, ARActivity.class);
                                startActivity(startActivityIntent2);
                                MyLocation.this.finish();*/
                                break;
                        }
                        return false;
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestLocationPermission();
        registerSensors();
        requestCameraPermission();
    }


    public void requestLocationPermission() {
        if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSIONS_CODE);
        } else {
            initLocationService();
        }
    }

    public void requestCameraPermission() {
        if (CameraPermissionHelper.hasCameraPermission(this)) {
            mSession.resume(mDefaultConfig);
            mSurfaceView.onResume();
        } else {
            CameraPermissionHelper.requestCameraPermission(this);
        }
    }

    private void registerSensors() {
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onPause() {
        super.onPause();

        mSurfaceView.onPause();
        mSession.pause();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

            float azimuth, pitch, bearing;
            Range<Float> azimuthRange, pitchRange;

            float[] rotationMatrixFromVector = new float[16];
            float[] updatedRotationMatrix = new float[16];
            float[] orientationValues = new float[3];

            SensorManager.getRotationMatrixFromVector(rotationMatrixFromVector, sensorEvent.values);
            SensorManager
                    .remapCoordinateSystem(rotationMatrixFromVector,
                            SensorManager.AXIS_X, SensorManager.AXIS_Y,
                            updatedRotationMatrix);
            SensorManager.getOrientation(updatedRotationMatrix, orientationValues);

            if (mMarkerList.isEmpty()) {
                return;
            }

            for (int i = 0; i < mMarkerList.size(); i++) {

                MarkerInfo marker = mMarkerList.get(i);

                try {
                    bearing = mLocation.bearingTo(marker.getLocation());
                } catch (NullPointerException exception) {
                    bearing = 100;
                }
                azimuth = (float) Math.toDegrees(orientationValues[0]);
                pitch = (float) Math.toDegrees(orientationValues[1]);

                azimuthRange = new Range<>(bearing - 10, bearing + 10);
                pitchRange = new Range<>(-90.0f, -45.0f);

                if (azimuthRange.contains(azimuth) && pitchRange.contains(pitch)) {
                    marker.setInRange(true);
                } else {
                    marker.setInRange(false);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //do nothing
    }

    private void initLocationService() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            this.mLocationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);

            // Get GPS and network status
            this.isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            this.isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isNetworkEnabled && !isGPSEnabled) {
                // cannot get location
                this.locationServiceAvailable = false;
            }

            this.locationServiceAvailable = true;

            if (isNetworkEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                if (mLocationManager != null) {
                    mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }

            if (isGPSEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                if (mLocationManager != null) {
                    mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());

        }
    }

    @Override
    public void onLocationChanged(Location location) {

        mLocation = location;
        MarkerInfo marker;

        for (int i = 0; i < mMarkerList.size(); i++) {
            marker = mMarkerList.get(i);
            marker.setDistance(location.distanceTo(marker.getLocation()));
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(this,
                    "Camera permission is needed to run this application", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Standard Android full-screen functionality.
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void onSingleTap(MotionEvent e) {
        // Queue tap if there is space. Tap is lost if queue is full.
        mQueuedSingleTaps.offer(e);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        // Create the texture and pass it to ARCore session to be filled during update().
        mBackgroundRenderer.createOnGlThread(/*context=*/this);
        mSession.setCameraTextureName(mBackgroundRenderer.getTextureId());

        // Prepare the other rendering objects.

        try {
            mVirtualObject.createOnGlThread(/*context=*/this, "Castle/Castle OBJ.obj", "Castle/Castle Exterior Texture.jpg");
            mVirtualObject.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);

            mVirtualObject2.createOnGlThread(/*context=*/this, "sign.obj", "mchenry.jpg");
            mVirtualObject2.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);

            mVirtualObject3.createOnGlThread(/*context=*/this, "sign.obj", "jb1.jpg");
            mVirtualObject3.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);


        } catch (IOException e) {
            Log.e(TAG, "Failed to read obj file");
        }

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        // Notify ARCore session that the view size changed so that the perspective matrix and
        // the video background can be properly adjusted.
        mSession.setDisplayGeometry(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear screen to notify driver it should not load any pixels from previous frame.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        try {
            // Obtain the current frame from ARSession. When the configuration is set to
            // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
            // camera framerate.
            Frame frame = mSession.update();
            mFrame = frame;

            MotionEvent tap = mQueuedSingleTaps.poll();

            // Draw background.
            mBackgroundRenderer.draw(frame);

            // If not tracking, don't draw 3d objects.
            if (frame.getTrackingState() == TrackingState.NOT_TRACKING) {
                return;
            }

            // Get projection matrix.
            float[] projmtx = new float[16];
            mSession.getProjectionMatrix(projmtx, 0, 0.1f, 100.0f);

            // Get camera matrix and draw.
            float[] viewmtx = new float[16];
            frame.getViewMatrix(viewmtx, 0);

            // Compute lighting from average intensity of the image.
            final float lightIntensity = frame.getLightEstimate().getPixelIntensity();

            float scaleFactor = 0.5f;

            MarkerInfo marker;

            if (mMarkerList.isEmpty()) {
                return;
            }

            for (int i = 0; i < mMarkerList.size(); i++) {

                marker = mMarkerList.get(i);

                if (marker.getInRange()) {
                    if (marker.getZeroMatrix() == null) {
                        marker.setZeroMatrix(getCalibrationMatrix());
                    }
                }

                if (marker.getZeroMatrix() == null) {
                    break;
                }

                Matrix.multiplyMM(viewmtx, 0, viewmtx, 0, marker.getZeroMatrix(), 0);

                if (Objects.equals(mMarkerList.get(i).name, name1))
                {
                    mVirtualObject.updateModelMatrix(mAnchorMatrix, 0.009f);
                    mVirtualObject2.updateModelMatrix(mAnchorMatrix, scaleFactor);
                    mVirtualObject.draw(viewmtx, projmtx, lightIntensity);
                    mVirtualObject2.draw(viewmtx, projmtx, lightIntensity);
            }
            else
                {
                    mVirtualObject3.updateModelMatrix(mAnchorMatrix, scaleFactor);
                    mVirtualObject3.draw(viewmtx, projmtx, lightIntensity);
                }

                if (tap != null) {
                    //showToast(marker.getCategory());
                    launchbrowser();
                }
            }

        } catch (Throwable t) {
            // Avoid crashing the application due to unhandled exceptions.
            Log.e(TAG, "Exception on the OpenGL thread", t);
        }

    }

    public float[] getCalibrationMatrix() {
        float[] t = new float[3];
        float[] m = new float[16];

        mFrame.getPose().getTranslation(t, 0);
        float[] z = mFrame.getPose().getZAxis();
        Vector3f zAxis = new Vector3f(z[0], z[1], z[2]);
        zAxis.y = 0;
        zAxis.normalize();

        double rotate = Math.atan2(zAxis.x, zAxis.z);

        Matrix.setIdentityM(m, 0);
        Matrix.translateM(m, 0, t[0], t[1], t[2]);
        Matrix.rotateM(m, 0, (float) Math.toDegrees(rotate), 0, 1, 0);
        return m;
    }

    private void showToast(final String category) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), category, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void launchbrowser() {

        Intent intent = new Intent(this, BrowserActivity.class);
        // intent.putExtra("aasa",gptext);
        String gptext="mysore";
        intent.putExtra("GP", gptext);
        startActivity(intent);
    }

    public class MarkerInfo {

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("time")
        @Expose
        private LocationTime time;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("category")
        @Expose
        private String category;
        @SerializedName("location")
        @Expose
        private MarkerLocation markerLocation;

        //Additional variables
        private float distance;
        private boolean inRange;
        private float[] zeroMatrix;
        private ObjectRenderer virtualObject;

        private MarkerInfo(String name, String category, MarkerLocation markerLocation) {
            this.name = name;
            this.category = category;
            this.markerLocation = markerLocation;
        }

        public float getDistance() {
            return distance;
        }

        void setDistance(Float distance) {
            this.distance = distance;
        }

        boolean getInRange() {
            return inRange;
        }

        void setInRange(Boolean inRange) {
            this.inRange = inRange;
        }

        public String getName() {
            return name;
        }

        public LocationTime getTime() {
            return time;
        }

        public String getCategory() {
            return category;
        }

        Location getLocation() {
            Location location = new Location(name);
            location.setLatitude(markerLocation.getLat());
            location.setLongitude(markerLocation.getLng());
            return location;
        }

        float[] getZeroMatrix() {
            return zeroMatrix;
        }

        void setZeroMatrix(float[] zeroMatrix) {
            this.zeroMatrix = zeroMatrix;
        }

        public ObjectRenderer getVirtualObject() {
            return virtualObject;
        }

        public void setVirtualObject(ObjectRenderer virtualObject) {
            this.virtualObject = virtualObject;
        }

    }
  /*  public void initNavigationDrawer1() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.nav_first_fragment:
                        Intent startActivityIntent = new Intent(ARActivity.this, MyLocation.class);
                        startActivity(startActivityIntent);
                        ARActivity.this.finish();
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


}