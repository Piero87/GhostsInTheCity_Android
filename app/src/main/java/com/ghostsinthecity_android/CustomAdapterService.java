package com.ghostsinthecity_android;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ghostsinthecity_android.R;
import com.ghostsinthecity_android.models.Game;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CustomAdapterService extends ArrayAdapter<Game>{

    public CustomAdapterService(Context context, int textViewResourceId,
                                ArrayList<Game> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.row, null);
        TextView nome = (TextView)convertView.findViewById(R.id.textViewList);
        Game g = getItem(position);

        String[] parts = g.getName().split("__");
        String part1 = parts[0];
        String part2 = parts[1];
        String tmp_date = getDate(Long.parseLong(part2));

        nome.setText(String.format("Mission created by %s at %s", part1, tmp_date));
        nome.setTextColor(Color.WHITE);
        //c1.setTextSize(18);

        return convertView;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ITALIAN);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm", cal).toString();
        return date;
    }

}