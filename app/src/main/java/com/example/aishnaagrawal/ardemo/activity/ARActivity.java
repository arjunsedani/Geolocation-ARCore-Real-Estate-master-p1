package com.example.aishnaagrawal.ardemo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
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
import com.example.aishnaagrawal.ardemo.model.MarkerLocation;
import com.example.aishnaagrawal.ardemo.renderer.BackgroundRenderer;
import com.example.aishnaagrawal.ardemo.renderer.ObjectRenderer;
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
    private ObjectRenderer mVirtualObject2 = new ObjectRenderer();
    private ObjectRenderer mVirtualObject3 = new ObjectRenderer();
    private ObjectRenderer mVirtualObject4 = new ObjectRenderer();

    private final float[] mAnchorMatrix = new float[16];

    // Tap handling and UI.
    private ArrayBlockingQueue<MotionEvent> mQueuedSingleTaps = new ArrayBlockingQueue<>(16);

    //Location-based stuff
    private SensorManager mSensorManager;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 0;//1000 * 60 * 1; // 1 minute
    public static final int REQUEST_LOCATION_PERMISSIONS_CODE = 0;

    public Location mLocation;

    boolean isGPSEnabled;
    boolean isNetworkEnabled;


    private List<MarkerInfo> mMarkerList;
    private Frame mFrame;
    private float[] mZeroMatrix = new float[16];

    float[] translation = new float[]{0.0f, -0.8f, -0.8f};
    float[] rotation = new float[]{0.0f, -1.00f, 0.0f, 0.3f};

    Pose mPose = new Pose(translation, rotation);
    //names for validatng names and augmenting different objects
    public String name1, name2, name3, gptext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);


        mSurfaceView = findViewById(R.id.surfaceview);
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

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        final double ltt = bundle.getDouble("LT");
        final double lnn = bundle.getDouble("LN");
        mMarkerList = new ArrayList<>();

        MarkerLocation markerLocation = new MarkerLocation("" + ltt, "" + lnn);
        MarkerInfo marker1 = new MarkerInfo("Jack Baskin Engineering1", "Academic Building", markerLocation);
        mMarkerList.add(marker1);
        name1 = marker1.name;
        MarkerLocation markerLocation1 = new MarkerLocation("" + (ltt + 0.000123), "" + (ltt + 0.000230));
        MarkerInfo marker2 = new MarkerInfo("Jack Baskin Engineering2", "Academic Building", markerLocation1);
        name2 = marker2.name;
        mMarkerList.add(marker2);
        MarkerLocation markerLocation2 = new MarkerLocation("" + (ltt + 0.000243), "" + (ltt + 0.000456));
        MarkerInfo marker3 = new MarkerInfo("Jack Baskin Engineering3", "Academic Building", markerLocation2);
        name3 = marker3.name;
        mMarkerList.add(marker3);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation1);

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
                                Intent startActivityIntent2 = new Intent(ARActivity.this, AboutActivity.class);
                                startActivityIntent2.putExtra("LT", ltt);
                                startActivityIntent2.putExtra("LN", lnn);
                                startActivity(startActivityIntent2);
                                ARActivity.this.finish();
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
            // initLocationService();
            getLocation();
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
                bearing = mLocation.bearingTo(marker.getLocation());
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

    @SuppressLint("MissingPermission")
    public void getLocation() {
        try {
            LocationManager mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

            // Getting GPS status
            assert mLocationManager != null;
            isGPSEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Getting network status
            isNetworkEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isNetworkEnabled) {
                mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.d("Network", "Network");
                mLocation = mLocationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            }
            // If GPS enabled, get latitude/longitude using GPS Services
            if (isGPSEnabled) {
                if (mLocation == null) {
                    mLocationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("GPS Enabled", "GPS Enabled");
                    mLocation = mLocationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
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


            mVirtualObject2.createOnGlThread(/*context=*/this, "sign.obj", "sign4.jpg");
            mVirtualObject2.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);

            mVirtualObject3.createOnGlThread(/*context=*/this, "sign.obj", "sign2.jpg");
            mVirtualObject3.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);

            mVirtualObject4.createOnGlThread(/*context=*/this, "sign.obj", "sign3.jpg");
            mVirtualObject4.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);


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

                mPose = new Pose(translation, rotation);
                mPose.toMatrix(mAnchorMatrix, 0);
                Matrix.multiplyMM(viewmtx, 0, viewmtx, 0, marker.getZeroMatrix(), 0);

                if (Objects.equals(mMarkerList.get(i).name, name1)) {
                    mVirtualObject2.updateModelMatrix(mAnchorMatrix, scaleFactor);
                    mVirtualObject2.draw(viewmtx, projmtx, lightIntensity);
                    gptext = "MANTRISQUARE";

                } else if (Objects.equals(mMarkerList.get(i).name, name2)) {
                    /*mVirtualObject3.updateModelMatrix(mAnchorMatrix, scaleFactor);
                    mVirtualObject3.draw(viewmtx, projmtx, lightIntensity);
                    gptext = "MANTRISQUARE";*/
                } else if (Objects.equals(mMarkerList.get(i).name, name3)) {
                 /*   mVirtualObject4.updateModelMatrix(mAnchorMatrix, scaleFactor);
                    mVirtualObject4.draw(viewmtx, projmtx, lightIntensity);
                    gptext = "MANTRISQUARE";*/
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

    private void launchbrowser() {
        Intent intent = new Intent(this, BrowserActivity.class);
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

    }
}