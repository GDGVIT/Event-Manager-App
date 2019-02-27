package com.example.dell.eventmanager.Legend;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dell.eventmanager.R;

import java.util.List;

public class LegendAdapter extends ArrayAdapter<Legend> {
    public LegendAdapter(Context context, int resource, List<Legend> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {


            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.legend_list_item, parent, false);
        }

        TextView legendNameTextView = convertView.findViewById(R.id.legend_name);
        LinearLayout legendTextView = convertView.findViewById(R.id.legend_list_item_details);

        Legend legend = getItem(position);
        legendNameTextView.setText(legend.getLegendName());

        final Drawable mDrawable = ContextCompat.getDrawable(getContext(), R.drawable.legend_inputs);
        switch (legend.getLegendColor()) {
            case "Bisque":
                mDrawable.setTint(Color.rgb(255, 228, 196));
                break;
            case "DeepSkyBlue":
                mDrawable.setTint(Color.rgb(0, 191, 255));
                break;
            case "Lime":
                mDrawable.setTint(Color.rgb(188, 246, 12));
                break;
            case "Yellow":
                mDrawable.setTint(Color.rgb(255, 225, 25));
                break;
            case "Coral":
                mDrawable.setTint(Color.rgb(255, 127, 80));
                break;
            case "Purple":
                mDrawable.setTint(Color.rgb(149, 125, 173));
                break;
            case "Pink":
                mDrawable.setTint(Color.rgb(255, 105, 180));
                break;
            case "Red":
                mDrawable.setTint(Color.rgb(255, 105, 97));
                break;
        }

        legendTextView.setBackground(mDrawable
               // ContextCompat.getDrawable(getContext(), R.drawable.legend_inputs)
                );

        //mDrawable.setTint(Color.rgb(255,228,196));

        Drawable bookmarkDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_bookmark_red_24dp);
        //bookmarkDrawable.setTint(Color.rgb(255,228,196));

//        if (legend.getLegendColor().contentEquals("Bisque")) {
//            mDrawable.setTint(Color.rgb(255, 228, 196));
//        }
//        if (legend.getLegendColor().contentEquals("DeepSkyBlue")) {
//            mDrawable.setTint(Color.rgb(0, 191, 255));
//        }
//        if (legend.getLegendColor().contentEquals("Lime")) {
//            mDrawable.setTint(Color.rgb(188, 246, 12));
//        }
//        if (legend.getLegendColor().contentEquals("Red")) {
//            mDrawable.setTint(Color.rgb(255, 105, 97));
//        }
//        if (legend.getLegendColor().contentEquals("Yellow")) {
//            mDrawable.setTint(Color.rgb(255, 225, 25));
//        }
//        if (legend.getLegendColor().contentEquals("Coral")) {
//            mDrawable.setTint(Color.rgb(255, 127, 80));
//        }
//        if (legend.getLegendColor().contentEquals("Purple")) {
//            mDrawable.setTint(Color.rgb(149, 125, 173));
//        }
//        if (legend.getLegendColor().contentEquals("Pink")) {
//            mDrawable.setTint(Color.rgb(255, 105, 180));
//        }



        // R.color.colorBisque
        //legendTextView.getChildAt(position).setBackgroundColor(Color.GREEN);
//        legendTextView.getChildAt(position).setBackgroundResource(R.color.colorDeepSkyBlue);
//        legendTextView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.legend_inputs));
//        Drawable mDrawable = ContextCompat.getDrawable(getContext(),R.drawable.legend_inputs);
//        mDrawable.setTint(Color.rgb(255,228,196));
        return convertView;
    }
}







/*

public View getView(int position, View convertView, ViewGroup parent)
        {
        if (convertView == null)
        {
        convertView = new TextView(ListHighlightTestActivity.this);
        convertView.setPadding(10, 10, 10, 10);
        ((TextView)convertView).setTextColor(Color.WHITE);
        }

        convertView.setBackgroundColor((position == curSelected) ?
        Color.argb(0x80, 0x20, 0xa0, 0x40) : Color.argb(0, 0, 0, 0));
        ((TextView)convertView).setText((String)getItem(position));

        return convertView;
        }

public long getItemId(int position)
        {
        return position;
        }

public Object getItem(int position)
        {
        return "item " + position;
        }

public int getCount()
        {
        return 20;
        }
        }*/