package com.pingala.parkeasy;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<ParkingLots> parkingLotList;
    SupportMapFragment mapFragment;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener  mAuthStateListner;
    public static final int RC_SIGN_IN = 1;
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        latitude =  getIntent().getDoubleExtra("calculated_Lat",0.0);
        longitude =  getIntent().getDoubleExtra("calculated_Lon",0.0);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mAuthStateListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user !=null){

                    //user is Sign In
                    new MarkerAsync1().execute();
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    mapFragment.getMapAsync(MapsActivity.this);

                }
                else{
                    //user is Sign Out
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(
                                            AuthUI.EMAIL_PROVIDER,
                                            AuthUI.GOOGLE_PROVIDER)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

    }

    public void onMapSearch(View view) {
        EditText locationSearch = (EditText) findViewById(R.id.editText);
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        double lat = latitude;
        double lon = longitude;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lat, lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,12.f));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMyLocationEnabled(true);

    }

    public class MarkerAsync1 extends AsyncTask<Void, Void, Void>
    {

        FirebaseDatabase database;

        @Override
        protected Void doInBackground(Void... params) {
            parkingLotList = new ArrayList<>();
            database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReferenceFromUrl("https://parkeasy-37469.firebaseio.com/ParkEasy/Points");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot data : dataSnapshot.getChildren()){

                        try {
                            Double lat = data.child("Lat").getValue(Double.class);
                            Double lon = data.child("Long").getValue(Double.class);
                            String name = data.child("Name").getValue(String.class);

                            ParkingLots pl = new ParkingLots();
                            pl.setLat(lat);
                            pl.setLon(lon);
                            pl.setName(name);

                            parkingLotList.add(pl);
                        }
                        catch (Exception e){

                        }
                        Log.e("Parking","Size ="+parkingLotList.size());

                    }

                    for (int i = 0; i <parkingLotList.size();  i++) {
                        try {
                            mMap.addMarker(new MarkerOptions().position(new LatLng(parkingLotList.get(i).getLat(), parkingLotList.get(i).getLon())).title(parkingLotList.get(i).getName()));
                            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                @Override
                                public void onInfoWindowClick(Marker marker) {
                                    String title =    marker.getTitle();

                                    Intent i = new Intent(MapsActivity.this,FloorsActivity.class);
                                    i.putExtra("Title",title);
                                    i.putExtra("FirebaseLink","https://parkeasy-37469.firebaseio.com/ParkEasy/Floors/"+title);
                                    startActivity(i);
                                }
                            });
                        }
                        catch (Exception e){


                        }
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListner);
        getSupportFragmentManager().beginTransaction().detach(mapFragment).commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListner);
        if(mapFragment.isDetached()==true){
           /* Toast.makeText(this, "mapFragmetn Null", Toast.LENGTH_SHORT).show();*/
            /*mAuthStateListner = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if(user !=null){
                        latitude =  getIntent().getDoubleExtra("calculated_Lat",0.0);
                        longitude =  getIntent().getDoubleExtra("calculated_Lon",0.0);
                        //user is Sign In
                        new MarkerAsync1().execute();
                        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

                        mapFragment.getMapAsync(MapsActivity.this);

                    }
                    else{
                        //user is Sign Out
                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setIsSmartLockEnabled(false)
                                        .setProviders(
                                                AuthUI.EMAIL_PROVIDER,
                                                AuthUI.GOOGLE_PROVIDER)
                                        .build(),
                                RC_SIGN_IN);
                    }
                }
            };*/
            startActivity(getIntent());
        }
    }

}

