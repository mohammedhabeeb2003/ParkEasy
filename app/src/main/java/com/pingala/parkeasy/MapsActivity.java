package com.pingala.parkeasy;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final int RC_SIGN_IN = 1;
    ArrayList<ParkingLots> parkingLotList;
    SupportMapFragment mapFragment;
    double latitude;
    double longitude;
    //Get GoogleApi
    private GoogleMap mMap;
    //Get Firebase API
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //Get latitude and longitude value from splash screen
        latitude = getIntent().getDoubleExtra("calculated_Lat", 0.0);
        longitude = getIntent().getDoubleExtra("calculated_Lon", 0.0);
        //instancetitate
        mFirebaseAuth = FirebaseAuth.getInstance();
        //Map Fragment
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //Make Authentiction Listner
        mAuthStateListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    //user is Sign In
                    new MarkerAsync1().execute();
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    mapFragment.getMapAsync(MapsActivity.this);

                } else {
                    //user is Sign Out
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(
                                            //Providing Email and Gmail Auth
                                            AuthUI.EMAIL_PROVIDER,
                                            AuthUI.GOOGLE_PROVIDER)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

    }

    //Searching Location
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

    //Animating to the present location
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        double lat = latitude;
        double lon = longitude;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lat, lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12.f));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMyLocationEnabled(true);

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
        if (mapFragment.isDetached() == true) {
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

    // Background task to get data from firebase database
    public class MarkerAsync1 extends AsyncTask<Void, Void, Void> {
        FirebaseDatabase database;

        @Override
        protected Void doInBackground(Void... params) {
            parkingLotList = new ArrayList<>();
            database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReferenceFromUrl("https://parkeasy-37469.firebaseio.com/ParkEasy/Points");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        try {
                            Double lat = data.child("Lat").getValue(Double.class);
                            Double lon = data.child("Long").getValue(Double.class);
                            String name = data.child("Name").getValue(String.class);

                            ParkingLots pl = new ParkingLots();
                            pl.setLat(lat);
                            pl.setLon(lon);
                            pl.setName(name);

                            parkingLotList.add(pl);
                        } catch (Exception e) {

                        }
                        Log.e("Parking", "Size =" + parkingLotList.size());

                    }

                    for (int i = 0; i < parkingLotList.size(); i++) {
                        try {
                            //Adding markers from the database to inside Map
                            mMap.addMarker(new MarkerOptions().position(new LatLng(parkingLotList.get(i).getLat(), parkingLotList.get(i).getLon())).title(parkingLotList.get(i).getName()));
                            // Making Pointer on Click
                            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                @Override
                                public void onInfoWindowClick(Marker marker) {
                                    String title = marker.getTitle();
                                    //Opening Floors activity with the title and link
                                    Intent i = new Intent(MapsActivity.this, FloorsActivity.class);
                                    i.putExtra("Title", title);
                                    i.putExtra("FirebaseLink", "https://parkeasy-37469.firebaseio.com/ParkEasy/Floors/" + title);
                                    startActivity(i);
                                }
                            });
                        } catch (Exception e) {


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

}

