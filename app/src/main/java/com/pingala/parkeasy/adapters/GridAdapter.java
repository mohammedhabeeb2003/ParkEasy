package com.pingala.parkeasy.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.pingala.parkeasy.R;
import com.pingala.parkeasy.models.Slot;

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


    public GridAdapter(Context context, int resource, ArrayList<Slot> mslots) {
        super(context, resource, mslots);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        holder = new ViewHolder();

        if(v==null){

            holder.tv_slot =(TextView)v.findViewById(R.id.tv_slot);
            holder.tg_booking = (ToggleButton)v.findViewById(R.id.tg_booking);
            holder.tg_booking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });




            v.setTag(holder);
        }
        holder = (ViewHolder)v.getTag();

        holder.tv_slot.setText(mslots.get(position).getSlotName());
        holder.tg_booking.setChecked(mslots.get(position).isBooked());

        return v;
    }

    static class ViewHolder{

        ToggleButton tg_booking;
        TextView tv_slot;


    }

}
