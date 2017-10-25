package unal.edu.co.controlcar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import unal.edu.co.controlcar.R;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9823;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private TextView txtAccept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Button btnLogin = (Button) findViewById(R.id.btnLoginGoogle);
        txtAccept = (TextView) findViewById(R.id.txtAccept);

        //Hosting
        txtAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ExplorerActivity.class);
                intent.putExtra("title", getString(R.string.terms));
                intent.putExtra("url", "https://controlcar-33b52.firebaseapp.com/");
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                //Conexión a Google API
                if (mGoogleApiClient == null) {
                    mGoogleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
                            .enableAutoManage(LoginActivity.this,
                                    new GoogleApiClient.OnConnectionFailedListener() {
                                        @Override
                                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                            Snackbar.make(getCurrentFocus(),
                                                    R.string.cannot_connect_api_google,
                                                    Snackbar.LENGTH_LONG).show();
                                        }
                                    })
                            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                            .build();
                }
                signIn();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        //Current user
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, InitTravelActivity.class));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_SIGN_IN:
                //Resultado de la conexión con el API de Google
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                } else {
                    Snackbar.make(txtAccept, R.string.google_auth_fail,
                            Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        //Uso de la autenticación
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mGoogleApiClient.disconnect();
                            finish();
                            startActivity(new Intent(LoginActivity.this, InitTravelActivity.class));
                        } else {
                            Snackbar.make(txtAccept, R.string.firebase_auth_fail,
                                    Snackbar.LENGTH_INDEFINITE).show();
                        }
                    }
                });
    }
}
