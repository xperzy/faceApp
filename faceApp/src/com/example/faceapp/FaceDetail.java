package com.example.faceapp;

import com.android.volley.toolbox.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

public class FaceDetail extends Activity {
	ImageView imageView;
	TextView textView_name;
	TextView textView_age;
	String imageServerUri_getDetail;
	SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facedetail);
		
		sp = getSharedPreferences("preferences",Context.MODE_PRIVATE);
		String SERVER_IP_ADDRESS = "http://"+sp.getString("serverIP","defName") +"/php/";
		imageServerUri_getDetail =  SERVER_IP_ADDRESS+"getDetail.php";
		
		
		Intent intent = getIntent();
		String face_title = intent.getStringExtra("face_title");
		String face_uri = intent.getStringExtra("face_uri");
		Log.i("face_uri",face_uri);
		this.imageView = (ImageView) this.findViewById(R.id.imageView_photo_d);
		imageView.setBackgroundResource(R.drawable.imageborder);
		
		
		int age = 25;
		String desc = "N/A";
		String gender = "Male";
		String race = "White";
		
		
		//Show name
		textView_name = (TextView) this.findViewById(R.id.textView_name_val_d);
		textView_name.setText(face_title);
		//Show age
		textView_age = (TextView) this.findViewById(R.id.textView_age_val_d);
		textView_age.setText(Integer.toString(age));
		//show description
		textView_age = (TextView) this.findViewById(R.id.textView_description_val);
		textView_age.setText(desc);
		
		final RadioButton rButton_male = (RadioButton) findViewById(R.id.radioButton_male_d);
		final RadioButton rButton_female = (RadioButton) findViewById(R.id.radioButton_female_d);
				
		final RadioButton rButton_asian = (RadioButton) findViewById(R.id.radioButton_asian_d);
		final RadioButton rButton_black = (RadioButton) findViewById(R.id.radioButton_black_d);
		final RadioButton rButton_white = (RadioButton) findViewById(R.id.radioButton_white_d);
		
		if (gender=="Male"){
			rButton_male.setChecked(true);
			rButton_female.setChecked(false);
			rButton_female.setClickable(false);
			rButton_male.setTextColor(getResources().getColor(R.color.GreenYellow));
		}else{
			if (gender=="Female"){
				rButton_female.setChecked(true);
				rButton_male.setChecked(false);
				rButton_male.setClickable(false);
				rButton_female.setTextColor(getResources().getColor(R.color.GreenYellow));
			}
		}
		
		
		if (race=="Asian"){
			rButton_asian.setChecked(true);
			rButton_black.setChecked(false);
			rButton_black.setClickable(false);
			rButton_white.setChecked(false);
			rButton_white.setClickable(false);
			rButton_asian.setTextColor(getResources().getColor(R.color.GreenYellow));
		}
		
		if (race=="White"){
			rButton_white.setChecked(true);
			rButton_black.setChecked(false);
			rButton_black.setClickable(false);
			rButton_asian.setChecked(false);
			rButton_asian.setClickable(false);
			rButton_white.setTextColor(getResources().getColor(R.color.GreenYellow));
		}
		
		if (race=="Black"){
			rButton_black.setChecked(true);
			rButton_asian.setChecked(false);
			rButton_asian.setClickable(false);
			rButton_white.setChecked(false);
			rButton_white.setClickable(false);
			rButton_black.setTextColor(getResources().getColor(R.color.GreenYellow));
		}
			

		ImageLoader imageLoader = MyVolley.getImageLoader();
		imageLoader.get(imageServerUri_getDetail + "?link="
				+ face_uri.toString(), ImageLoader
				.getImageListener(imageView,
						R.drawable.no_image,
						R.drawable.error_image));
		
		
		
	}		


}
