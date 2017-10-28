package unal.edu.co.controlcar.activities;

/**
 * Created by Edwin on 11/10/2017.
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

import unal.edu.co.controlcar.R;

public class TravelActivity extends AppCompatActivity implements LocationListener, View.OnClickListener, OnMapReadyCallback {

    //variables for accelerometer
    private TextView textX, textY, textZ;
    private SensorManager sensorManager;
    private Sensor sensor;

    //variables for velocimeter
    private LocationManager locationManager;
    private TextView speedTextView;
    private Button btnfinishTravel;
    private TextView longitudeValue;
    private TextView latitudeValue;
    private float currentSpeed = 0.0f;

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);

        speedTextView = (TextView) findViewById(R.id.speedTextView);
        btnfinishTravel = (Button) findViewById(R.id.btnfinishTravel);
        longitudeValue = (TextView) findViewById(R.id.longitudeValue);
        latitudeValue = (TextView) findViewById(R.id.latitudeValue);

        btnfinishTravel.setOnClickListener(this);

        //setup GPS location service
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //setup accelerometer sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        textX = (TextView) findViewById(R.id.textX);
        textY = (TextView) findViewById(R.id.textY);
        textZ = (TextView) findViewById(R.id.textZ);

        //turn on speedometer using GPS
        turnOnGps();


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(accelListener);
        turnOffGps();
    }

    public void onResume() {
        super.onResume();
        sensorManager.registerListener(accelListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(accelListener);
    }

    SensorEventListener accelListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int acc) {
        }

        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            textX.setText("X : " + x);
            textY.setText("Y : " + y);
            textZ.setText("Z : " + z);
        }
    };

    //Velocimeter
    private void turnOnGps() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, this);
        }
        /*if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        }*/
    }

    private void turnOffGps() {
        longitudeValue.setText(getResources().getString(R.string.unknownLongLat));
        latitudeValue.setText(getResources().getString(R.string.unknownLongLat));
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        latitudeValue.setText(String.valueOf(lat));
        longitudeValue.setText(String.valueOf(lng));
        currentSpeed = location.getSpeed() * 3.6f;//in kmh
        speedTextView.setText(new DecimalFormat("#.##").format(currentSpeed));

        LatLng current = new LatLng(lat, lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 17));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
        turnOnGps();
    }

    @Override
    public void onProviderDisabled(String provider) {
        turnOffGps();
    }

    @Override
    public void onClick(View v) {
        turnOffGps();
        finish();
        startActivity(new Intent(TravelActivity.this, InitTravelActivity.class));
        /*
        if (v.getId() == R.id.toggleButton) {
            vibrate();
            if (toggleButton.isChecked()) {
                turnOnGps();
                onResume();
            } else {
                turnOffGps();
                onStop();
            }
        }
        */
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //cargar a una posicion por defecto, si el gps esta desactivado.
        mMap = googleMap;
        LatLng bogota = new LatLng(4.7110, -74.0721);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bogota, 5));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }
}