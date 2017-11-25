package unal.edu.co.controlcar.activities;

/**
 * Created by Edwin on 11/10/2017.
 */

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import unal.edu.co.controlcar.models.Alert;
import unal.edu.co.controlcar.R;
import unal.edu.co.controlcar.View.Speedometer;
import unal.edu.co.controlcar.models.Travel;

import static java.lang.Math.abs;

public class TravelActivity extends AppCompatActivity implements LocationListener, View.OnClickListener, OnMapReadyCallback{

    //variables for accelerometer
    private TextView textX, textY, textZ;
    private SensorManager sensorManager;
    private Sensor sensor;

    //variables for velocimeter
    private LocationManager locationManager;
    private Speedometer speedometer;
    private Button btnfinishTravel;
    private TextView longitudeValue;
    private TextView latitudeValue;
    private float currentSpeed = 0.0f;

    //variables for ubication
    private static DecimalFormat df2 = new DecimalFormat(".#######");
    private double longitude;
    private double latitude;

    AlertDialog.Builder alert_dialog;
    private Dialog tmpdialog;

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);

        alert_dialog = new AlertDialog.Builder(this);

        speedometer = (Speedometer) findViewById(R.id.Speedometer);
        speedometer.onSpeedChanged(currentSpeed);

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

        longitude = Double.parseDouble(getIntent().getExtras().getString("longitude"));
        latitude = Double.parseDouble(getIntent().getExtras().getString("latitude"));
        longitudeValue.setText(getIntent().getExtras().getString("longitude"));
        latitudeValue.setText(getIntent().getExtras().getString("latitude"));

        //turn on speedometer using GPS
        //checkLocation();
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

    public void showAlertDialog(String description, String date) {
        if (tmpdialog != null) tmpdialog.dismiss();
        alert_dialog.setTitle("Conduce con cuidado!");
        alert_dialog.setIcon(R.drawable.warning);
        alert_dialog.setMessage("Se registro un " + description + "\n" + date);
        alert_dialog.setCancelable(false);
        alert_dialog.setPositiveButton("Volver al viaje", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        tmpdialog = alert_dialog.show();
    }

    SensorEventListener accelListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int acc) {
        }

        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            textX.setText("X : " + String.format("%.1f", x));
            textY.setText("Y : " + String.format("%.1f", y));
            textZ.setText("Z : " + String.format("%.1f", z));
            if ((x >= 0.5 && x <= 1) || (x >= 1.5 && x <= 2) || (x >= 2.5 && x <= 3) ||
                    (x >= -1 && x <= -0.5) || (x >= -2 && x <= -1.5) || (x >= -3 && x <= -2.5) ||
                    speedometer.getCurrentSpeed() > 80) {
                String description = "";
                if (speedometer.getCurrentSpeed() > 80) {
                    description = "Exceso de velocidad";
                } else {
                    description = "Movimiento brusco del vehiculo";
                }
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                dateFormat.setTimeZone(TimeZone.getTimeZone("America/Bogota"));
                Calendar today = Calendar.getInstance();
                double velocity = speedometer.getCurrentSpeed();
                Alert alert = new Alert(dateFormat.format(today.getTime()), description, velocity, latitude, longitude, abs((int) x));
                FirebaseDatabase.getInstance().getReference().child("Travels").
                        child(getIntent().getExtras().getString("key")).child("Alerts").push().setValue(alert);
                showAlertDialog(description, alert.getInitHour());
            }
        }
    };


    //Velocimeter
    private void turnOnGps() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        }
    }

    private void turnOffGps() {
        longitudeValue.setText(getResources().getString(R.string.unknownLongLat));
        latitudeValue.setText(getResources().getString(R.string.unknownLongLat));
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        latitudeValue.setText(df2.format(latitude));
        longitudeValue.setText(df2.format(longitude));
        currentSpeed = location.getSpeed() * 3.6f;
        speedometer.onSpeedChanged(currentSpeed);

        LatLng current = new LatLng(latitude, longitude);
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
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/Bogota"));
        Calendar today = Calendar.getInstance();
        FirebaseDatabase.getInstance().getReference().child("Travels").
                child(getIntent().getExtras().getString("key")).child("endTime").setValue(dateFormat.format(today.getTime()));
        turnOffGps();
        finish();
        startActivity(new Intent(TravelActivity.this, InitTravelActivity.class));
    }

    private Location loc;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //cargar a una posicion por defecto, si el gps esta desactivado.
        mMap = googleMap;
        LatLng bogota = new LatLng(4.7110, -74.0721);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bogota, 5));
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

}