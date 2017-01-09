package com.metalarm.adapters;

/**
 * Created by qtm-purvesh on 21/3/16.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.metalarm.R;
import com.metalarm.model.detailModel;

import java.util.ArrayList;

public class DetailAdapterDialog extends BaseAdapter {

    ArrayList<detailModel> mArrayList = new ArrayList<>();
    Context context;
    private static LayoutInflater inflater = null;

    public DetailAdapterDialog(Activity detailsActivity, ArrayList<detailModel> mArrayList) {

        this.mArrayList = mArrayList;
        context = detailsActivity;

    }


    @Override
    public int getCount() {
        return mArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mArrayList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.cust_listview_dialog, null);
        }

        ImageView iv = (ImageView) convertView.findViewById(R.id.ivCustImage);
        TextView tvName = (TextView) convertView.findViewById(R.id.tvCustName);
        TextView tvDesc = (TextView) convertView.findViewById(R.id.tvCustDetail);
//        Button btn = (Button) convertView.findViewById(R.id.btnCustbtn);

        Glide.with(context).load(mArrayList.get(position).drw).into(iv);
        tvName.setText(mArrayList.get(position).name);
        tvDesc.setText(mArrayList.get(position).desc);


        return convertView;
    }
}
