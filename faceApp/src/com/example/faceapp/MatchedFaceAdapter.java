package com.example.faceapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MatchedFaceAdapter extends ArrayAdapter<MatchedFace>{

    Context context; 
    int layoutResourceId;    
    MatchedFace data[] = null;
    
    public MatchedFaceAdapter(Context context, int layoutResourceId, MatchedFace[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MatchedFaceHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new MatchedFaceHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            
            row.setTag(holder);
        }
        else
        {
            holder = (MatchedFaceHolder)row.getTag();
        }
        
        MatchedFace matchedFace = data[position];
        holder.txtTitle.setText(matchedFace.title);
        holder.imgIcon.setImageResource(matchedFace.icon);
        
        return row;
    }
    
    static class MatchedFaceHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
    }
}