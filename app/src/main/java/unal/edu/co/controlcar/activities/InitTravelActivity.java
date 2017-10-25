package unal.edu.co.controlcar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import unal.edu.co.controlcar.R;

public class InitTravelActivity extends AppCompatActivity {

    private Button btnInitTravel;
    private EditText edtPlate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_travel);

        btnInitTravel = (Button) findViewById(R.id.btnInitTravel);
        edtPlate = (EditText) findViewById(R.id.edtPlate);

        btnInitTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPlate.getText().toString().toCharArray().length == 6) {
                    // TODO Verify Firebase
                    finish();
                    startActivity(new Intent(InitTravelActivity.this, TravelActivity.class));
                } else {
                    edtPlate.setError(getString(R.string.invalid_plate));
                }
            }
        });
    }
}
