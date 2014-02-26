package com.example.faceapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Register Button
		final Button btnReg = (Button) findViewById(R.id.button1);
		btnReg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						FaceRegister.class);
				startActivity(intent);
			}
		});

		// Recognize Button
		Button btnRecog = (Button) findViewById(R.id.button2);
		btnRecog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						FaceRecognition.class);
				startActivity(intent);
			}
		});

		// Age Button
		Button btnAge = (Button) findViewById(R.id.button3);
		btnAge.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						AgeEstimation.class);
				startActivity(intent);
			}
		});
				
		

		// Exit Button
		Button btnExit = (Button) findViewById(R.id.button4);
		btnExit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				System.exit(0);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
