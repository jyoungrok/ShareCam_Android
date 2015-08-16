package com.claude.sharecam.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.claude.sharecam.R;

import java.util.ArrayList;

/**
 * Created by Claude on 15. 8. 5..
 */
public class CountrySpinnerAdapter extends ArrayAdapter<Country> {

    private Context context;
    private ArrayList<Country> itemList;
    private LayoutInflater layoutInflater;
    private int textViewResourceId;
    public CountrySpinnerAdapter(Context context, int textViewResourceId, ArrayList<Country> itemList) {

        super(context, textViewResourceId,itemList);
        this.context=context;
        this.itemList=itemList;
        layoutInflater=LayoutInflater.from(context);
        this.textViewResourceId=textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        return getCustomView(position,convertView,parent);
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        return getCustomView(position,convertView,parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent)
    {
        View root=layoutInflater.inflate(textViewResourceId,parent,false);

        ((TextView)root.findViewById(R.id.countryNameTxt)).setText(itemList.get(position).mCountryName);
        ((TextView)root.findViewById(R.id.phoneCodeTxt)).setText(itemList.get(position).mDialPrefix);


        return root;
    }

}