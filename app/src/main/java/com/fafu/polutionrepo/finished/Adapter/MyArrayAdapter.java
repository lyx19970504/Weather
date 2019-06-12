package com.fafu.polutionrepo.finished.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class MyArrayAdapter extends ArrayAdapter  {


    public MyArrayAdapter( Context context, int resource,  List objects) {
        super(context, resource, objects);
    }



    @Override
    public View getView(int position,  View convertView, ViewGroup parent) {
        String string = (String) getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_expandable_list_item_1, parent,false);
        }
        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(string);
        textView.setTextColor(Color.WHITE);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        return super.getView(position, convertView, parent);
    }

}
