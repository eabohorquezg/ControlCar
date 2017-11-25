package unal.edu.co.controlcar.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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

public class InitTravelActivity extends AppCompatActivity {

    private Button btnInitTravel;
    private Button btnLogoutUser;
    private EditText edtPlate;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;
    private boolean flagLocation = false;

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
                                            travel.setInitLatitude(0);
                                            travel.setInitLongitude(0);
                                            travel.setPlate(edtPlate.getText().toString().toUpperCase());
                                            travel.setDriverName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                            String key = FirebaseDatabase.getInstance().getReference().child("Travels").push().getKey();
                                            travel.setId(key);
                                            FirebaseDatabase.getInstance().getReference().child("Travels").child(key).setValue(travel);
                                            Intent intent = new Intent(InitTravelActivity.this, TravelActivity.class);
                                            intent.putExtra("key", key);
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
}
