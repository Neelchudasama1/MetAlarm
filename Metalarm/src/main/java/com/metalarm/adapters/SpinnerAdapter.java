package com.metalarm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.metalarm.R;

/**
 * Created by qtm-purvesh on 16/5/16.
 */
    public class SpinnerAdapter extends ArrayAdapter<String>{
    Context context;
    String mArrayData[];

        public SpinnerAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
            mArrayData = objects;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row=inflater.inflate(R.layout.cust_spinner, parent, false);
            TextView label=(TextView)row.findViewById(R.id.txt);
            label.setText(mArrayData[position]);

            return row;
        }
    }

