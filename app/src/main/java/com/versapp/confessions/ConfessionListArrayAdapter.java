package com.versapp.confessions;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.versapp.R;

import java.util.ArrayList;

/**
 * Created by william on 20/09/14.
 */
public class ConfessionListArrayAdapter extends ArrayAdapter<Confession> {

    private Activity activity;
    private ArrayList<Confession> confessions;

    public ConfessionListArrayAdapter(Activity activity, ArrayList<Confession> confessions) {
        super(activity, R.layout.confession_list_item, confessions);
        this.activity = activity;
        this.confessions = confessions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        final Confession confession = confessions.get(position);

        if (convertView == null) {

            convertView = (View) ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                    R.layout.confession_list_item, parent, false);

            holder = new ViewHolder();

            holder.confessionSizer = (RelativeLayout) convertView.findViewById(R.id.big_confession_sizer);
            holder.backgroundImage = (ImageView) convertView.findViewById(R.id.big_confession_background_image);
            holder.body = (TextView) convertView.findViewById(R.id.big_confession_body);
            // holder.shareBtn = (ImageButton)
            // convertView.findViewById(R.id.big_confession_share_btn);
            holder.degreeIcon = (ImageView) convertView.findViewById(R.id.big_confession_degree_indicator_ic);
            holder.favoriteCount = (TextView) convertView.findViewById(R.id.big_confession_favorite_count);
            holder.degreeText = (TextView) convertView.findViewById(R.id.big_confession_degree_indicator_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.favoriteCount.setText(String.valueOf(confession.getNumFavorites()));
        holder.degreeText.setText(confession.getDegree());

        // Makes layout squared.
        int width = activity.getWindowManager().getDefaultDisplay().getWidth(); // deprecated
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
        holder.confessionSizer.setLayoutParams(params);

        if (confession.getBody().length() > 140) {
            holder.body.setTextSize(25);
        } else {
            holder.body.setTextSize(30);
        }
        holder.body.setText(confession.getBody());

        holder.backgroundImage.setImageBitmap(null);
        holder.backgroundImage.setBackgroundColor(activity.getResources().getColor(android.R.color.white));

        if (confessions.get(position).getImageUrl().charAt(0) == '#') {
            holder.backgroundImage.setBackgroundColor(Color.parseColor(confessions.get(position).getImageUrl()));
        } else {


        }

        updateDegreeIndicatorIcon(position, holder.degreeIcon);

        return convertView;
    }

    private class ViewHolder {
        ImageView backgroundImage;
        TextView body;
        RelativeLayout confessionSizer;
        ImageView degreeIcon;
        TextView favoriteCount;
        TextView degreeText;
    }

    private void updateDegreeIndicatorIcon(int position, ImageView iconView) {
    }

    @Override
    public int getCount() {
        return confessions.size();
    }
}