package com.example.faceapp;

import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MatchedFaceAdapter2 extends ArrayAdapter<MatchedFace> {
    private ImageLoader mImageLoader;
    Context context;
	int layoutResourceId;
	List<MatchedFace> data=null;
	
    public MatchedFaceAdapter2(Context context, 
                              int textViewResourceId, 
                              List<MatchedFace> objects,
                              ImageLoader imageLoader
                              ) {
        super(context, textViewResourceId, objects);
        mImageLoader = imageLoader;
        this.layoutResourceId = textViewResourceId;
		this.context = context;
		this.data = objects;
    }

    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	View row = convertView;
		MatchedFaceHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new MatchedFaceHolder(row);
			row.setTag(holder);
		}else {
			holder = (MatchedFaceHolder) row.getTag();
		}
			
        
        
        MatchedFace entry = getItem(position);
        if (entry.getThumbnailUrl() != null) {
            holder.image.setImageUrl(entry.getThumbnailUrl(), mImageLoader);
        } else {
            holder.image.setImageResource(R.drawable.no_image);
        }
        
        holder.title.setText(entry.getTitle());
        
        return row;
    }
    
    
    private class MatchedFaceHolder {
        NetworkImageView image;
        TextView title; 
        
        public MatchedFaceHolder(View v) {
            image = (NetworkImageView) v.findViewById(R.id.iv_thumb);
            title = (TextView) v.findViewById(R.id.tv_title);
            
            v.setTag(this);
        }
    }
}