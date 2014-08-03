package com.mathilde.customcam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mathilde.customcam.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mathilde on 02/08/14.
 */
public class FilterAdapter extends BaseAdapter{
    public static final String TAG = "FilterAdapter";

    private LayoutInflater mInflater;
    private HashMap<Integer, String> mList;
    private Context mContext;

    public FilterAdapter(Context context, HashMap<Integer, String> list){
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mList = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;

        if (convertView == null) {
            // Inflate the view since it does not exist
            convertView = mInflater.inflate(R.layout.item_filter, parent, false);

            // Create and save off the holder in the tag so we get quick access to inner fields
            // This must be done for performance reasons
            holder = new Holder();
            holder.textView = (TextView) convertView.findViewById(R.id.text_filter);
            holder.imageView = (ImageView) convertView.findViewById(R.id.image_filter);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        // Populate the text
        holder.textView.setText(getItem(position).toString());
        holder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.small_circle_red));
        // Set the color
        return convertView;
    }

    /** View holder for the views we need access to */
    private static class Holder {
        public TextView textView;
        public ImageView imageView;
    }

}
