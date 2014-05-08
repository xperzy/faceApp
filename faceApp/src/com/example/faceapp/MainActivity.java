package com.example.faceapp;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		
		MyVolley.init(MainActivity.this);
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

		
		// SetIP Button
		Button btnSet = (Button) findViewById(R.id.button5);
		btnSet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// get prompts.xml view 
				AlertDialog.Builder inputDialog = new AlertDialog.Builder(MainActivity.this);
				LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
				View layout = inflater.inflate(R.layout.setip,
						(ViewGroup) findViewById(R.id.layout_root));
				inputDialog.setView(layout);
				final EditText userInput = (EditText) layout.findViewById(R.id.editTextDialogUserInput);
				  
				inputDialog.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
								Toast.makeText(getApplicationContext(), userInput.getText(), Toast.LENGTH_SHORT).show();
								((setIP)MainActivity.this.getApplication()).setServerIP(userInput.getText().toString());
								dialog.dismiss();
							}

						});
				inputDialog.create();
				inputDialog.show();
				 

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
