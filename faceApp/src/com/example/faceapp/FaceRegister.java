package com.example.faceapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Toast;

public class FaceRegister extends Activity {
	private String name = "";
	private String gender = "male";
	private String race = "Asian";
	private int age = 0;
	private Bitmap bitmap;
	private ImageView imageView;

	private Uri fileUri;
	private static final int IMAGE_SELECTOR = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		// get name
		final EditText textView_name = (EditText) findViewById(R.id.editText_name);

		// get Age
		NumberPicker numberPicker = (NumberPicker) findViewById(R.id.numberpicker);
		numberPicker.setMaxValue(200);
		numberPicker.setMinValue(1);
		numberPicker.setValue(25);
		numberPicker.setWrapSelectorWheel(true);
		numberPicker
				.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

					@Override
					public void onValueChange(NumberPicker picker, int oldVal,
							int newVal) {
						// TODO Auto-generated method stub
						age = newVal;
					}
				});

		// Gender radio button group
		final OnClickListener radioListener_gender = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				RadioButton rb = (RadioButton) v;
				gender = rb.getText().toString();
			}
		};
		final RadioButton rButton_male = (RadioButton) findViewById(R.id.radioButton_male);
		rButton_male.setOnClickListener(radioListener_gender);
		final RadioButton rButton_female = (RadioButton) findViewById(R.id.radioButton_female);
		rButton_female.setOnClickListener(radioListener_gender);

		// Race radio button group
		final OnClickListener radioListener_race = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				RadioButton rb = (RadioButton) v;
				race = rb.getText().toString();
			}
		};
		final RadioButton rButton_asian = (RadioButton) findViewById(R.id.radioButton_asian);
		rButton_asian.setOnClickListener(radioListener_race);
		final RadioButton rButton_black = (RadioButton) findViewById(R.id.radioButton_black);
		rButton_black.setOnClickListener(radioListener_race);
		final RadioButton rButton_white = (RadioButton) findViewById(R.id.radioButton_white);
		rButton_white.setOnClickListener(radioListener_race);

		// Photo
		this.imageView = (ImageView) this.findViewById(R.id.imageView_photo);
		imageView.setBackgroundResource(R.drawable.imageborder);
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT,
						null);
				galleryintent.setType("image/*");

				Intent cameraIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a
																	// file to
																	// save the
																	// image
				Log.i("fileUri", fileUri.toString());
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set
																			// the
																			// image
																			// file
																			// name
				cameraIntent.putExtra("return-data", true);

				Intent chooser = new Intent(Intent.ACTION_CHOOSER);
				chooser.putExtra(Intent.EXTRA_INTENT, galleryintent);
				chooser.putExtra(Intent.EXTRA_TITLE, "Select Source");

				Intent[] intentArray = { cameraIntent };
				chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
				startActivityForResult(chooser, IMAGE_SELECTOR);

			}
		});

		// Button register
		final Button button_reg = (Button) findViewById(R.id.button_register);
		button_reg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				name = textView_name.getText().toString();
				if (name.isEmpty()) {
					Toast.makeText(getApplicationContext(),
							"Please Input a Name", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(),
							name + gender.toString() + age, Toast.LENGTH_LONG)
							.show();
				}
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == IMAGE_SELECTOR && resultCode == Activity.RESULT_OK) {
			if (data == null) {
				try {
					bitmap = BitmapFactory.decodeStream(getContentResolver()
							.openInputStream(fileUri));
					Matrix matrix = new Matrix();
					matrix.postRotate((float) 90.0);
					Bitmap rotaBitmap = Bitmap.createBitmap(bitmap, 0, 0,
							bitmap.getWidth(), bitmap.getHeight(), matrix,
							false);
					Bitmap sizeBitmap = Bitmap.createScaledBitmap(rotaBitmap,
							540, 800, true);

					imageView.setImageBitmap(sizeBitmap);

				} catch (FileNotFoundException e) {

					e.printStackTrace();
				}

			}

			else {
				Uri selectedImageUri = data.getData();
				try {
					bitmap = BitmapFactory.decodeStream(getContentResolver()
							.openInputStream(selectedImageUri));
					Bitmap sizeBitmap = Bitmap.createScaledBitmap(bitmap, 540,
							800, true);
					imageView.setImageBitmap(sizeBitmap);

				} catch (FileNotFoundException e) {

					e.printStackTrace();
				}

			}

			super.onActivityResult(requestCode, resultCode, data);
		}

	}

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
			Log.i("newImg", "True");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

}
