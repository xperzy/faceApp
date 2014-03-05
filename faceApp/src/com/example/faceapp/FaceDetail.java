package com.example.faceapp;

import com.android.volley.toolbox.ImageLoader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class FaceDetail extends Activity {
	ImageView imageView;
	TextView textView;
	String imageServerUri_getDetail = "http://157.182.38.37/getDetail.php";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facedetail);
		
		Intent intent = getIntent();
		String face_title = intent.getStringExtra("face_title");
		String face_uri = intent.getStringExtra("face_uri");
		Log.i("face_uri",face_uri);
		this.imageView = (ImageView) this.findViewById(R.id.imageView_faceD);
		imageView.setBackgroundResource(R.drawable.imageborder);
		
		this.textView = (TextView) this.findViewById(R.id.textView_name);
		textView.setText("Name: "+face_title);
		
		ImageLoader imageLoader = MyVolley.getImageLoader();
		imageLoader.get(imageServerUri_getDetail + "?link="
				+ face_uri.toString(), ImageLoader
				.getImageListener(imageView,
						R.drawable.no_image,
						R.drawable.error_image));
		
	}		


}
