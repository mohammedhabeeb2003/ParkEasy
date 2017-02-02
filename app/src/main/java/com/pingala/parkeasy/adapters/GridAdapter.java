package com.pingala.parkeasy.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pingala.parkeasy.ClientBooking;
import com.pingala.parkeasy.R;
import com.pingala.parkeasy.models.CalculateTime;
import com.pingala.parkeasy.models.Slot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Habeeb on 1/24/2017.
 */


public class GridAdapter extends ArrayAdapter {

    ArrayList<Slot> mslots;
    Context context;
    int resource;
    LayoutInflater layoutInflater;
    ViewHolder holder;
    String url;
    TinyDB tydb;
    ArrayList<String> adminList;
    FirebaseDatabase database = FirebaseDatabase.getInstance();


    public GridAdapter(Context context, int resource, ArrayList<Slot> mslots, String url) {
        super(context, resource, mslots);
        this.context = context;
        this.resource = resource;
        this.mslots = mslots;
        this.url = url;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View v = convertView;
        final DatabaseReference myRef = database.getReferenceFromUrl(url);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();


        tydb = new TinyDB(context);
        adminList = tydb.getListString("AdminsList");
        Log.e("Uid", ":" + uid);
        holder = new ViewHolder();

        if (v == null) {
            v = layoutInflater.inflate(resource, null);
            holder.tv_slot = (TextView) v.findViewById(R.id.slot_name);
            holder.tg_booking = (ToggleButton) v.findViewById(R.id.tg_booking);
            holder.btn_book = (Button) v.findViewById(R.id.btn_book);
            //Checking Slot is Available or Occupied
            //If Slot is Available  go inside this function
            if (mslots.get(position).isBooked()) {
                holder.btn_book.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Checking the user is Admin
                        if (adminList.contains(uid)) {
                            EditText et_carno = new EditText(context);
                            et_carno.setHint("Car No");
                            String title = tydb.getString("title");
                            String title_main = tydb.getString("TitleMain");
                            String slot_no = mslots.get(position).getSlotName();
                            AlertDialog.Builder ad = new AlertDialog.Builder(context);
                            ad.setTitle(title_main);
                            ad.setMessage(title + " " + slot_no);
                            ad.setView(et_carno);
                            //I
                            ad.setPositiveButton("Occupied", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    myRef.child(String.valueOf(position)).child("Booked").setValue(false);
                                    mslots.clear();
                                    Log.e("firebaseUrl", "" + url);
                                    Log.e("PositionSlot", "" + position);

                                }
                            });
                            ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            ad.create();
                            ad.show();


                        } else {
                            // If it is not admin take to this activity
                            String title = tydb.getString("title");
                            String title_main = tydb.getString("TitleMain");
                            String slot_no = mslots.get(position).getSlotName();
                            Intent i = new Intent(context, ClientBooking.class);
                            i.putExtra("title_main", title_main);
                            i.putExtra("firebaseLink",url+"/"+position);
                            i.putExtra("floor_no", title);
                            i.putExtra("slot_no", slot_no);
                            context.startActivity(i);


                        }
                    }
                });

            } else {
                //If Slot is Occupied go inside this function
                holder.btn_book.setAlpha(.5f);
                if (adminList.contains(uid)) {
                    holder.btn_book.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            String title = tydb.getString("title");
                            String title_main = tydb.getString("TitleMain");
                            String slot_no = mslots.get(position).getSlotName();
                            AlertDialog.Builder ad = new AlertDialog.Builder(context);
                            ad.setTitle(title_main);
                            ad.setMessage(title + " " + slot_no);
                            ad.setPositiveButton("Available", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    myRef.child(String.valueOf(position)).child("Booked").setValue(true);
                                    mslots.clear();
                                    Log.e("firebaseUrl", "" + url);
                                    Log.e("PositionSlot", "" + position);

                                }
                            });
                            ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            ad.create();
                            ad.show();
                        }


                    });

                }
                else{
                    holder.btn_book.setVisibility(View.INVISIBLE);

                }
            }

            v.setTag(holder);
        }
        holder = (ViewHolder) v.getTag();
        try {
            holder.tv_slot.setText(mslots.get(position).getSlotName());

                holder.tg_booking.setChecked(mslots.get(position).isBooked());
            if (adminList.contains(uid)) {
                if(!mslots.get(position).getTimestamp().isEmpty()){
                    holder.tg_booking.setBackgroundColor(Color.parseColor("#f57c00"));
                    holder.tg_booking.setChecked(false);
                    holder.tg_booking.setTextOff("Reserved");
                    holder.tg_booking.setTextOn("Reserved");
                    holder.btn_book.setVisibility(View.VISIBLE);
                    holder.btn_book.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClientBooking cb = new ClientBooking();
                            String current_time = cb.getcurrentTimeStamp();
                            CalculateTime diff = new CalculateTime();
                            String time =  diff.timeDifference(mslots.get(position).getTimestamp(),current_time);
                            AlertDialog.Builder ad = new AlertDialog.Builder(context);
                            ad.setTitle("Reserved");
                            ad.setMessage("Booked :"+time+"Ago");
                            ad.setNegativeButton("Available", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                AlertDialog.Builder ad_available = new AlertDialog.Builder(context);
                                    ad_available.setTitle("Available");
                                    ad_available.setTitle("Make This Slot Available");
                                    ad_available.setNegativeButton("Save", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            myRef.child(String.valueOf(position)).child("TimeStamp").setValue("");

                                        }
                                    });
                                    ad_available.create();
                                    ad_available.show();
                                }
                            });
                            ad.setNeutralButton("Occupied", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final EditText et_carNo = new EditText(context);
                                    et_carNo.setHint("CarNo");
                                    AlertDialog.Builder ad_occupied = new AlertDialog.Builder(context);
                                    ad_occupied.setTitle("Available");
                                    ad_occupied.setView(et_carNo);
                                    ad_occupied.setNegativeButton("Save", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            et_carNo.setText(mslots.get(position).getCarNo());
                                            String car_No = et_carNo.getText().toString().trim();
                                                myRef.child(String.valueOf(position)).child("TimeStamp").setValue("");
                                                myRef.child(String.valueOf(position)).child("CarNo").setValue(car_No);
                                                myRef.child(String.valueOf(position)).child("Booked").setValue(false);


                                        }
                                    });
                                    ad_occupied.create();
                                    ad_occupied.show();
                                }
                            });
                            ad.create();
                            ad.show();

                        }
                    });
                }
            }else{

                if(!mslots.get(position).getTimestamp().isEmpty()){
                    holder.tg_booking.setBackgroundColor(Color.parseColor("#f57c00"));
                    holder.tg_booking.setChecked(false);
                    holder.tg_booking.setTextOff("Reserved");
                    holder.tg_booking.setTextOn("Reserved");
                    holder.btn_book.setVisibility(View.INVISIBLE);
                }
            }



        } catch (Exception e) {


        }
        return v;
    }

    static class ViewHolder {
        ToggleButton tg_booking;
        TextView tv_slot;
        Button btn_book;

    }
    public void updateReceiptsList(ArrayList<Slot> newlist) {
           mslots.clear();
           this.addAll(newlist);

    }

}
