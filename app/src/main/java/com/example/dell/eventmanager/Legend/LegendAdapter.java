package com.example.dell.eventmanager.Legend;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.eventmanager.R;

import java.util.List;

public class LegendAdapter extends ArrayAdapter<Legend> {

    Dialog userDialog;
    public LegendAdapter(Context context, int resource, List<Legend> objects,String eventKey) {
        super(context, resource, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {


            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.legend_list_item, parent, false);
        }

        Dialog userDialog;

        TextView legendNameTextView = convertView.findViewById(R.id.legend_name);
        LinearLayout legendTextView = convertView.findViewById(R.id.legend_list_item_details);

//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Legend legend = getItem(position);
//                Toast.makeText(getContext(), legend.getLegendName(), Toast.LENGTH_SHORT).show();
//            }
//        });

        Legend legend = getItem(position);
        legendNameTextView.setText(legend.getLegendName());

        final Drawable mDrawable = ContextCompat.getDrawable(getContext(), R.drawable.legend_inputs);
        switch (legend.getLegendColor()) {
            case "Red":
                mDrawable.setTint(Color.rgb(255, 96, 127));
                break;
            case "Yellow":
                mDrawable.setTint(Color.rgb(255, 187, 0));
                break;
            case "Green":
                mDrawable.setTint(Color.rgb(3, 213, 163));
                break;
            case "Blue":
                mDrawable.setTint(Color.rgb(0, 154, 255));
                break;
            case "Purple":
                mDrawable.setTint(Color.rgb(218, 92, 240));
                break;
            case "Cyan":
                mDrawable.setTint(Color.rgb(103, 225, 247));
                break;
            case "Magenta":
                mDrawable.setTint(Color.rgb(109, 103, 247));
                break;
            case "Bush":
                mDrawable.setTint(Color.rgb(0, 165, 108));
                break;
        }

        legendTextView.setBackground(mDrawable);
        return convertView;
    }
}

