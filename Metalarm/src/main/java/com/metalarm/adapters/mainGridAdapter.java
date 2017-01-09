package com.metalarm.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.metalarm.R;
import com.metalarm.model.mainGridModel;

import java.util.ArrayList;

/**
 * Created by qtm-purvesh on 16/5/16.
 */
public class mainGridAdapter extends BaseAdapter {

    Context context;
    ArrayList<mainGridModel> ar1;
    String getString;
    private static LayoutInflater inflater = null;

    public mainGridAdapter(Activity MainActivity, ArrayList<mainGridModel> ar) {
        // TODO Auto-generated constructor stub
        context = MainActivity;
        ar1 = ar;


    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return ar1.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return ar1.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        String direction;
        if (convertView == null) {

            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.cust_main_grid, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.txt);
        tv.setText(ar1.get(position).names);

        TextView txtDirection = (TextView) convertView.findViewById(R.id.txtDirection);
        if (ar1.get(position).directoin.equals("1")) {
            direction = "from downtown";
        } else {
            direction = "to downtown";
        }
        txtDirection.setText(direction);


        return convertView;
    }


}
