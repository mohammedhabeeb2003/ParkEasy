package com.pingala.parkeasy.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.pingala.parkeasy.R;
import com.pingala.parkeasy.models.GridHeader;
import com.pingala.parkeasy.models.Slot;

import java.util.ArrayList;

/**
 * Created by Habeeb on 1/24/2017.
 */

public class ListviewAdapter extends ArrayAdapter {

    ArrayList<GridHeader> gridHeaders;
    LayoutInflater layoutInflater;
    Context context;
    ViewHolder holder;
    int resource;
    ArrayList<ArrayList<Slot>> slotList;
    public ListviewAdapter(Context context, int resource, ArrayList<GridHeader> gridHeaders,ArrayList<ArrayList<Slot>> slotList) {
        super(context, resource, gridHeaders);
        this.context = context;
        this.resource = resource;
        this.gridHeaders = gridHeaders;
        this.slotList = slotList;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        holder = new ViewHolder();
        if(v==null){
            v = layoutInflater.inflate(resource,null);
            holder.tv_header = (TextView)v.findViewById(R.id.floors);
            holder.gv_item = (GridView)v.findViewById(R.id.gv_item);

            v.setTag(holder);
        }
        else
        holder =(ViewHolder)v.getTag();

        holder.tv_header.setText("Floor"+" "+gridHeaders.get(position).getFloors());
/*
        GridAdapter ga = new GridAdapter(context,slotList.get(position))
*/

        return v;
    }

    static class ViewHolder{

        TextView tv_header;
        GridView gv_item;
    }
}
