package com.example.faceapp;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class AboutApp extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aboutapp);
		TextView myTextView = (TextView) findViewById(R.id.aboutapptw);
		myTextView.setText(Html.fromHtml(getString(R.string.aboutDemoFull)));
		myTextView.setMovementMethod(new ScrollingMovementMethod());
		
	}

	

}
