package com.example.faceapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BMICal extends Activity {

	double ft = -1;
	double lb = -1;
	double inch = -1;
	double cm = -1;
	double kg = -1.0;
	double bmi_val = -1.0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bmical);

		// get name
		final EditText tV_ft = (EditText) findViewById(R.id.bmi_ft);
		final EditText tV_in = (EditText) findViewById(R.id.bmi_in);
		final EditText tV_lb = (EditText) findViewById(R.id.bmi_lb);
		final EditText tV_cm = (EditText) findViewById(R.id.bmi_cm);
		final EditText tV_kg = (EditText) findViewById(R.id.bmi_kg);
		final TextView myTextView = (TextView) findViewById(R.id.bmiCat_res);
	

		final Button button_reg = (Button) findViewById(R.id.bmi_cal_btn);
		button_reg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (tV_ft.getText().toString().length()>0
						&& tV_lb.getText().toString().length()>0) {
					ft = Double.parseDouble(tV_ft.getText().toString());
					lb = Double.parseDouble(tV_lb.getText().toString());
					inch = 0;
					if (tV_in.getText().toString().length()>0) {
						inch = Integer.parseInt(tV_in.getText().toString());
					}
				}

				if (tV_cm.getText().toString().length()>0
						&& tV_kg.getText().toString().length()>0) {
					cm = Double.parseDouble(tV_cm.getText().toString());
					kg = Double.parseDouble(tV_kg.getText().toString());
				}
				
				// check measurement
				boolean mes_kg = false;
				boolean mes_lb = false;
				if (cm != -1 && kg != -1) {
					mes_kg = true;
				}
				if (ft != -1 && lb != -1) {
					mes_lb = true;
				}

				//if missing input
				if (!mes_kg && !mes_lb) {
					Toast.makeText(getApplicationContext(), "Wrong Input!!!",
							Toast.LENGTH_SHORT).show();
				} else {

					// compute BMI
					if (mes_kg) {
						// cm & kg
						bmi_val = kg / ((cm / 100) * (cm / 100));
					}
					if (mes_lb){
						// inch & lb
						bmi_val = lb * 703
								/ ((ft * 12 + inch) * (ft * 12 + inch));
					}
				

				// show on textview
				
				myTextView.setText(String.format("%.1f", bmi_val));
				// Show on dialog
				LayoutInflater inflater = BMICal.this
						.getLayoutInflater();
				View ve = inflater.inflate(R.layout.bmi_details,	null);
				TextView bmi_val_view = (TextView) ve
						.findViewById(R.id.textView_bmiVal);

				// Select bmi category
				// double bmi = 33.32;
				bmi_val_view.setText(String.format("%.1f", bmi_val));

				// bmi_val.setText(String.format("%0.1f", bmi));
				TextView bmi_cat0;
				TextView bmi_cat1;
				TextView bmi_cat2 = (TextView) ve
						.findViewById(R.id.textView_bmiCat);
				if (bmi_val < 18.5) {
					bmi_cat0 = (TextView) ve
							.findViewById(R.id.textView_ud);
					bmi_cat1 = (TextView) ve
							.findViewById(R.id.textView_udVal);
					bmi_cat0.setTextColor(getResources()
							.getColor(R.color.Button_Blue));
					bmi_cat1.setTextColor(getResources()
							.getColor(R.color.Button_Blue));
					bmi_cat2.setText("Underweight");
					bmi_cat2.setTextColor(getResources()
							.getColor(R.color.Button_Blue));
					bmi_val_view.setTextColor(getResources()
							.getColor(R.color.Button_Blue));
				}
				if (bmi_val >= 18.5 && bmi_val <= 24.9) {
					bmi_cat0 = (TextView) ve
							.findViewById(R.id.textView_nor);
					bmi_cat1 = (TextView) ve
							.findViewById(R.id.textView_norVal);
					bmi_cat0.setTextColor(getResources()
							.getColor(R.color.YellowGreen));
					bmi_cat1.setTextColor(getResources()
							.getColor(R.color.YellowGreen));
					bmi_cat2.setText("Normal");
					bmi_cat2.setTextColor(getResources()
							.getColor(R.color.YellowGreen));
					bmi_val_view.setTextColor(getResources()
							.getColor(R.color.YellowGreen));
				}
				if (bmi_val >= 25.0 && bmi_val <= 29.9) {
					bmi_cat0 = (TextView) ve
							.findViewById(R.id.textView_over);
					bmi_cat1 = (TextView) ve
							.findViewById(R.id.textView_overVal);
					bmi_cat0.setTextColor(getResources()
							.getColor(R.color.Red));
					bmi_cat1.setTextColor(getResources()
							.getColor(R.color.Red));
					bmi_cat2.setText("Overweight");
					bmi_cat2.setTextColor(getResources()
							.getColor(R.color.Red));
					bmi_val_view.setTextColor(getResources()
							.getColor(R.color.Red));
				}
				if (bmi_val >= 30.0) {
					bmi_cat0 = (TextView) ve
							.findViewById(R.id.textView_obe);
					bmi_cat1 = (TextView) ve
							.findViewById(R.id.textView_obeVal);
					bmi_cat0.setTextColor(getResources()
							.getColor(R.color.Red));
					bmi_cat1.setTextColor(getResources()
							.getColor(R.color.Red));
					bmi_cat2.setText("Obese");
					bmi_cat2.setTextColor(getResources()
							.getColor(R.color.Red));
					bmi_val_view.setTextColor(getResources()
							.getColor(R.color.Red));
				}

				new AlertDialog.Builder(BMICal.this,
						android.R.style.Theme_Holo_Dialog)
						.setTitle("Body Mass Index")
						.setView(ve)
						.setPositiveButton(
								android.R.string.ok,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(
											DialogInterface dialog,
											int which) {
										// TODO Auto-generated
										// method stub
										tV_ft.setText("");
										tV_in.setText("");
										tV_lb.setText("");
										tV_kg.setText("");
										tV_cm.setText("");
										myTextView.setText("N/A");
										
										ft = -1;
										lb = -1;
										inch = -1;
										cm = -1;
										kg = -1.0;
										bmi_val = -1.0;

									}
								}).create().show();
			}
			}
		});
		

		

	}
}
