package com.ghostsinthecity_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import javax.jmdns.ServiceEvent;

public class CustomAdapterService extends ArrayAdapter<ServiceEvent>{

    public CustomAdapterService(Context context, int textViewResourceId,
                                ArrayList<ServiceEvent> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.row, null);
        TextView nome = (TextView)convertView.findViewById(R.id.textViewList);
        ServiceEvent c = getItem(position);
        nome.setText(c.getName());
        return convertView;
    }

}