package com.pingala.parkeasy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pingala.parkeasy.adapters.ListviewAdapter;
import com.pingala.parkeasy.models.GridHeader;
import com.pingala.parkeasy.models.Slot;

import java.util.ArrayList;

public class FloorsActivity extends AppCompatActivity {
    String title;
    String firebaseLink;
    ArrayList<GridHeader> gridHeaders;
    ListView lv_home;
    ArrayList<ArrayList<Slot>> slotList;
    ArrayList<Slot> slots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floors);
        lv_home = (ListView)findViewById(R.id.lv_home);

        title = getIntent().getStringExtra("Title");
        this.getSupportActionBar().setTitle(title);
        firebaseLink = getIntent().getStringExtra("FirebaseLink");
        Log.e("FirebaseLink",""+firebaseLink);
        new AsyncTaskFloors().execute();
    }
    class AsyncTaskFloors extends AsyncTask<Void,Void,Void> {

        ProgressDialog pd;
        FirebaseDatabase database;
        DatabaseReference myRef;
        DatabaseReference myRefChild;
        ListviewAdapter lv;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(FloorsActivity.this);
            pd.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            database = FirebaseDatabase.getInstance();
            myRef = database.getReferenceFromUrl(firebaseLink);
            myRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                     gridHeaders = new ArrayList<GridHeader>();
                     slotList = new ArrayList<ArrayList<Slot>>();
                     slots = new ArrayList<Slot>();
                    for(DataSnapshot data : dataSnapshot.getChildren()){
                        String floorName =  data.child("FloorName").getValue(String.class);
                        DataSnapshot datachild = data.child("Slots");
                        for(DataSnapshot datachild1 : datachild.getChildren()){
                            try {
                                Boolean booked = datachild1.child("Booked").getValue(Boolean.class);
                                String slotName = datachild1.child("SlotName").getValue(String.class);
                                Slot sl = new Slot();
                                sl.setBooked(booked);
                                sl.setSlotName(slotName);

                                slots.add(sl);
                            }
                            catch (Exception e){

                            }

                        }

                        GridHeader gh = new GridHeader();
                        gh.setFloors(floorName);

                        gridHeaders.add(gh);
                        slotList.add(slots);

                    }

                    lv = new ListviewAdapter(getApplicationContext(),R.layout.grid_row_list,gridHeaders,slotList);
                    lv_home.setAdapter(lv);
                    pd.dismiss();
                    Log.e("gridHeader",""+gridHeaders.size());

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
           lv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
               @Override
               public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                     Intent i = new Intent(FloorsActivity.this,ParkingArea.class);
                     i.putExtra("ParkingArea",firebaseLink+"/"+position+"/"+"Slots");
                     i.putExtra("FloorNo",gridHeaders.get(position).getFloors());
                     startActivity(i);
               }
           });

        }
    }

}
