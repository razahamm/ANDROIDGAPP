package com.example.androidapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.location.Address;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    AddressReceiver Receiver;
    TextView infoText;
    EditText addressEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        infoText = (TextView) findViewById(R.id.infoText);
        addressEdit = (EditText) findViewById(R.id.Address);
        Receiver = new AddressReceiver(null);
    }

    public void ButtonClicked(View view) {
        Intent intent = new Intent(this, GeocodeService.class);
        intent.putExtra(Constants.RECEIVER, Receiver);
        if(addressEdit.getText().length() == 0) {
            Toast.makeText(this, "Enter an address", Toast.LENGTH_LONG).show();
            return;
        }
        intent.putExtra(Constants.LOCATION_NAME, addressEdit.getText().toString());

        infoText.setVisibility(View.INVISIBLE);
        startService(intent);
    }

    class AddressReceiver extends ResultReceiver {
        public AddressReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {
            if (resultCode == 0) {
                final Address address = resultData.getParcelable(Constants.RESULT_ADDRESS);
                String tdisplay = "Latitude: " + address.getLatitude() + "\n" +
                        "Longitude: " + address.getLongitude() + "\n" +
                        "Address: " + resultData.getString(Constants.RESULT_DATA_KEY) + "\n" +
                        "County: ";
                if(address.getSubAdminArea() == null) tdisplay += "No County Listed";
                else tdisplay += address.getSubAdminArea();
                final String display = tdisplay;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        infoText.setVisibility(View.VISIBLE);
                        infoText.setText(display);
                    }
                });
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("Latitude",address.getLatitude());
                intent.putExtra("Longitude",address.getLongitude());
                intent.putExtra("Address",resultData.getString(Constants.RESULT_DATA_KEY));
                startActivity(intent);
            }
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        infoText.setVisibility(View.VISIBLE);
                        infoText.setText(resultData.getString(Constants.RESULT_DATA_KEY));
                    }
                });
            }

        }
    }
}
