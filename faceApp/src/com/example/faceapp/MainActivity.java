package com.example.faceapp;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

		// About Button
		Button btnAge = (Button) findViewById(R.id.button6);
		btnAge.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						AboutApp.class);
				startActivity(intent);
			}
		});
		
		
		// BMI Calculator Button
		Button btnBMI = (Button) findViewById(R.id.button7);
		btnBMI.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						BMICal.class);
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
				final AutoCompleteTextView userInput = (AutoCompleteTextView) layout.findViewById(R.id.editTextDialogUserInput);
				userInput.setDropDownBackgroundResource(R.color.Black);
				
				SharedPreferences sp = getSharedPreferences("preferences",Context.MODE_PRIVATE);		
				String serverIP = sp.getString("serverIP", "Input Server IP...");
				ArrayList<String> ips=new ArrayList<String>();
				ips.add(serverIP);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_dropdown_item_1line,ips);
				
				userInput.setAdapter(adapter);
				
								 
				
				inputDialog.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
																
								SharedPreferences sp = getSharedPreferences("preferences",Context.MODE_PRIVATE);
								Editor editor = sp.edit();
								editor.putString("serverIP", userInput.getText().toString());
								if (editor.commit()){
									Toast.makeText(getApplicationContext(), userInput.getText().toString()+"  Set saved.", Toast.LENGTH_SHORT).show();
								}else{
									Toast.makeText(getApplicationContext(), userInput.getText().toString()+"  Set Falied.", Toast.LENGTH_SHORT).show();
								}
																
								dialog.dismiss();
							}

						});
				inputDialog.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
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
