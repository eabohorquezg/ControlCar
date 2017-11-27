package unal.edu.co.controlcar.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import unal.edu.co.controlcar.R;
import unal.edu.co.controlcar.models.Travel;

public class InitTravelActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Button btnInitTravel;
    private Button btnLogoutUser;
    private EditText edtPlate;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;
    private boolean flagLocation = false;
    private Double longitude;
    private Double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_travel);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        btnInitTravel = (Button) findViewById(R.id.btnInitTravel);
        btnLogoutUser = (Button) findViewById(R.id.btnLogoutUser);
        edtPlate = (EditText) findViewById(R.id.edtPlate);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        mGoogleApiClient.connect();

        btnLogoutUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                finish();
                startActivity(new Intent(InitTravelActivity.this, LoginActivity.class));
            }
        });

        btnInitTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtPlate.getText().toString().toCharArray().length == 6 ) {
                    boolean result =  checkLocation();
                    // TODO Verify Firebase
                    if (result) {
                        FirebaseDatabase.getInstance().getReference()
                                .child("Cars")
                                .child(edtPlate.getText().toString().toUpperCase()).addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null) {
                                            Travel travel = new Travel();
                                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                            dateFormat.setTimeZone(TimeZone.getTimeZone("America/Bogota"));
                                            Calendar today = Calendar.getInstance();
                                            travel.setInitHour(dateFormat.format(today.getTime()));
                                            travel.setEndTime("");
                                            if(latitude != null && longitude != null){
                                                travel.setInitLatitude(latitude);
                                                travel.setInitLongitude(longitude);
                                                travel.setCurLocation(Double.toString(latitude) + "," + Double.toString(longitude));
                                            }
                                            else{
                                                travel.setInitLatitude(4.7110);
                                                travel.setInitLongitude(-74.0721);
                                                travel.setCurLocation("4.7110,-74.0721");
                                            }
                                            travel.setPlate(edtPlate.getText().toString().toUpperCase());
                                            travel.setDriverName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                            travel.setRequestLocation(1);
                                            String key = FirebaseDatabase.getInstance().getReference().child("Travels").push().getKey();
                                            travel.setId(key);
                                            FirebaseDatabase.getInstance().getReference().child("Travels").child(key).setValue(travel);
                                            Intent intent = new Intent(InitTravelActivity.this, TravelActivity.class);
                                            intent.putExtra("key", key);
                                            if(latitude != null && longitude != null){
                                                intent.putExtra("longitude", String.valueOf(longitude));
                                                intent.putExtra("latitude", String.valueOf(latitude) );
                                            }
                                            else{
                                                intent.putExtra("longitude", "-74.0721");
                                                intent.putExtra("latitude", "4.7110" );
                                            }
                                            finish();
                                            startActivity(intent);
                                            //startActivity(new Intent(InitTravelActivity.this, TravelActivity.class));
                                        } else {
                                            edtPlate.setError("La placa no existe. Lo siento.");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }else{
                        Toast.makeText(getApplicationContext(),"Se debe activar la ubicación para continuar",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    edtPlate.setError(getString(R.string.invalid_plate));
                }

            }
        });
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Su ubicación esta desactivada.\npor favor active su ubicación " +
                        "usa esta app")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        flagLocation =false;
                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude =  mLastLocation.getLongitude();
        } else {
            Toast.makeText(this, "Ubicación no encontrada", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
