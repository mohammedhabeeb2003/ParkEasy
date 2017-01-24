package com.pingala.parkeasy;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Habeeb on 1/21/2017.
 */

public class MarkerAsync /*extends AsyncTask<Void, Void, Void>*/
{
   /* FirebaseDatabase database;
    ArrayList<ParkingLots> parkingLotList = new ArrayList<>();



    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }

    @Override
    protected Void doInBackground(Void... params) {

        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReferenceFromUrl("https://parkeasy-37469.firebaseio.com/ParkEasy/Points");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){


                    String lat = data.child("Lat").getValue(String.class);
                    String lon = data.child("Long").getValue(String.class);
                    String name = data.child("Name").getValue(String.class);
                    ParkingLots pl = new ParkingLots();
                    pl.setLat(Double.parseDouble(lat));
                    pl.setLon(Double.parseDouble(lon));
                    pl.setName(name);

                   parkingLotList.add(pl);


                }
                Log.e("List","Size"+parkingLotList.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.e("List","Size"+parkingLotList.size());
        MapsActivity mapsActivity = new MapsActivity(){
            @Override
            public void passMap(GoogleMap mMap) {
                super.passMap(mMap);
                for (int i = 0; i <parkingLotList.size();  i++) {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(parkingLotList.get(i).getLat(),parkingLotList.get(i).getLon())).title(parkingLotList.get(i).getName()));
                }

            }
        };
    }*/
}