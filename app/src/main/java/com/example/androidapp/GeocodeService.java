package com.example.androidapp;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeocodeService extends IntentService {
    protected ResultReceiver rr;
    public GeocodeService() {
        super("GeocodeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String message = "";
        List<Address> addresses = null;

        String name = intent.getStringExtra(Constants.LOCATION_NAME);
        try {
            addresses = geocoder.getFromLocationName(name, 1);
        } catch (IOException e) {
            message = "Error";
        }

        rr = intent.getParcelableExtra(Constants.RECEIVER);
        if (addresses == null || addresses.size()  == 0) {
            if (message.isEmpty()) {
                message = "Address not found";
            }
            SendToReceiver(1, message, null);
        } else {
            String address = addresses.get(0).getAddressLine(0).toString();
             for(int i = 1; i < addresses.get(0).getMaxAddressLineIndex(); i++) {
               address += ", " + addresses.get(0).getAddressLine(i).toString();
             }
            SendToReceiver(0, address, addresses.get(0));
        }
    }

    private void SendToReceiver(int resultCode, String message, Address address) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.RESULT_ADDRESS, address);
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        rr.send(resultCode, bundle);
    }

}
