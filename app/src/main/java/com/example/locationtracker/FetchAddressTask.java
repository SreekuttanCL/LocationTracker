package com.example.locationtracker;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddressTask extends AsyncTask<Location, Void, String> {

    private final String TAG = FetchAddressTask.class.getSimpleName();
    private Context mContext;
    private OnTaskCompleted mListener;

    interface OnTaskCompleted{
        void onTaskCompleted(String result);

    }

    FetchAddressTask(Context applicationContext, OnTaskCompleted listner) {
        mContext = applicationContext;
        mListener = listner;
    }

    @Override
    protected void onPostExecute(String address) {

        mListener.onTaskCompleted(address);
        super.onPostExecute(address);
    }

    @Override
    protected String doInBackground(Location... locations) {

        //set up our geoCoder object
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

        Location location = locations[0];

        List<Address> addresses = null;
        String resultMessage = "";

        try {

            addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            //in our example we only want 1 address

        }
        catch (IOException e) {

            //catch any network or inout output errors
            resultMessage = mContext.getString(R.string.service_not_available);
            Log.d(TAG , resultMessage, e);

        }
        catch (IllegalArgumentException illegalArgumentException) {

            resultMessage = mContext.getString(R.string.invalid_lat_long_used);
            Log.e(TAG, resultMessage + "," + "Lat:" + location.getLatitude() + "Long:" + location.getLongitude(), illegalArgumentException );

        }

        if (addresses == null || addresses.size() == 0) {

            if (resultMessage.isEmpty()) {

                resultMessage = mContext.getString(R.string.no_address_found);
                Log.e(TAG, resultMessage);

            }
        }
        else {

            //if an address is found through the geocoder read it to the resultMessage variable
            Address address = addresses.get(0);
            ArrayList<String> addressParts = new ArrayList<>();

            //get the address lines using getAddressLine method
            //loop through and join them

            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressParts.add(address.getAddressLine(i));
            }

            resultMessage = TextUtils.join("\n", addressParts);

        }

        return resultMessage;
    }
}
