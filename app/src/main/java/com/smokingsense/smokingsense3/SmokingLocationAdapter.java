package com.smokingsense.smokingsense3;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by appaaaa on 2017-02-08.
 */

public class SmokingLocationAdapter extends ArrayAdapter<SmokingLocation> {
    public SmokingLocationAdapter(Context context, int resource, List<SmokingLocation> objects){
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.list_item1, parent, false);
        }
        TextView text = (TextView)convertView.findViewById(R.id.list_item_cafe);

        SmokingLocation location = getItem(position);

        text.setText(location.getTitle());

        return convertView;
    }
}

