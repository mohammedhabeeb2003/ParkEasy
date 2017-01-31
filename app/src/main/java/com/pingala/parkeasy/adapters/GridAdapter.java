package com.pingala.parkeasy.adapters;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pingala.parkeasy.R;
import com.pingala.parkeasy.models.Slot;

import java.lang.reflect.Array;
import java.util.ArrayList;

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



    public GridAdapter(Context context, int resource, ArrayList<Slot> mslots, String url) {
        super(context, resource, mslots);
        this.context = context;
        this.resource = resource;
        this.mslots = mslots;
        this.url = url;


        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final  String uid = user.getUid();
        holder = new ViewHolder();

        if(v==null){
            v = layoutInflater.inflate(resource,null);
            holder.tv_slot =(TextView)v.findViewById(R.id.slot_name);
            holder.tg_booking = (ToggleButton)v.findViewById(R.id.tg_booking);
            holder.btn_book =(Button)v.findViewById(R.id.btn_book);

            if(mslots.get(position).isBooked()){

              holder.btn_book.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                     tydb = new TinyDB(context);
                   adminList =   tydb.getListString("AdminsList");
                      Log.e("AdminLists",""+adminList.size());
                      Log.e("Uid",":"+uid);
                      if(adminList.contains(uid)){
                          Toast.makeText(context, "This is Admin", Toast.LENGTH_SHORT).show();
                      }
                      else{
                          Toast.makeText(context, "This is Normal User", Toast.LENGTH_SHORT).show();

                      }
                  }
              });

            }

            else{
                holder.btn_book.setAlpha(.5f);
                holder.btn_book.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(adminList.contains(uid)){
                            Toast.makeText(context, "This is Admin", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

            v.setTag(holder);
        }
        holder = (ViewHolder)v.getTag();
try {
    holder.tv_slot.setText(mslots.get(position).getSlotName());
    holder.tg_booking.setChecked(mslots.get(position).isBooked());

}
catch (Exception e){


}
        return v;
    }

    static class ViewHolder{

        ToggleButton tg_booking;
        TextView tv_slot;
        Button btn_book;


    }

}
