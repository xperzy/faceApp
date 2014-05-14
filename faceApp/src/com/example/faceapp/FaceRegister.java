package com.example.faceapp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Toast;

public class FaceRegister extends Activity {
	private String name = "";
	private String gender = "male";
	private String race = "Asian";
	private int age = 25;
	private Bitmap bitmap;
	private ImageView imageView;

	private Uri fileUri;
	private static final String SERVER_IP_ADDRESS = "http://192.168.43.222/php/";
	private static final String uoploadUri = SERVER_IP_ADDRESS
			+ "face_register.php";
	private static final int IMAGE_SELECTOR = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		// get name
		final EditText textView_name = (EditText) findViewById(R.id.editText_name);

		// get Age
		final NumberPicker numberPicker = (NumberPicker) findViewById(R.id.numberpicker);
		
		numberPicker.setBackgroundResource(R.color.White);
		numberPicker.setAlpha((float) 0.8);
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
					
					uploadEntry();
				}
			}
		});
		
		
		
		// Button reset
		final Button button_reset = (Button) findViewById(R.id.button_reset);
		button_reset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				textView_name.setText("");
				
				numberPicker.setValue(25);
				
				rButton_asian.setChecked(true);
				rButton_black.setChecked(false);
				rButton_white.setChecked(false);
				
				rButton_male.setChecked(true);
				rButton_female.setChecked(false);
				
				imageView.setImageResource(R.drawable.blank_photo);
				
				
				bitmap=null;
			}
		});

	}
	
	
	//upload infomation to server
	private void uploadEntry(){
		//Create the jsonobject
		HashMap<String,String> params = new HashMap<String,String>();
		
		params.put("name", name);
		params.put("gender", gender);
		params.put("race", race);
		params.put("age", Integer.toString(age));
		
		//Converting Image(bitmap) to String
		String encodedImage="";
		if (bitmap!=null){
			encodedImage = getStringFromBitmap(bitmap);
		}
		//put image
		params.put("image",encodedImage);
		//Create json object
		JSONObject jsonObj = new JSONObject(params);
		//Log.i("jsonobj",jsonObj.toString());
		Log.i("name",name);
		Log.i("gender", gender);
		Log.i("race", race);
		Log.i("age", Integer.toString(age));
		
		
		//Use Volley to upload
		RequestQueue queue = MyVolley.getRequestQueue();
		JsonObjectRequest myReq = new JsonObjectRequest(Method.POST, uoploadUri, jsonObj,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						try {
							Log.i("regiser Response: ", response.toString());
							Toast.makeText(getApplicationContext(), "Face Registrition "+response.getString("res").toString(), Toast.LENGTH_LONG).show();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.i("face register error",error.toString());
					}
				});
		queue.add(myReq);
		
	}
	
	//Converting bitmap to json string
	private String getStringFromBitmap(Bitmap bitmapPicture){
		final int COMPRESSION_QUILITY=100;
		String encodedImage="";
		ByteArrayOutputStream byteArrayBitmapStream= new ByteArrayOutputStream();
		bitmapPicture.compress(Bitmap.CompressFormat.JPEG,COMPRESSION_QUILITY, byteArrayBitmapStream);
		byte[] b = byteArrayBitmapStream.toByteArray();
		encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
		return encodedImage;
	}
	
	//Convertint json string to bitmap
	private Bitmap getBitmapFromString(String jsonString){
		byte[] decodedString = Base64.decode(jsonString, Base64.DEFAULT);
		Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);		
		return decodedByte;
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == IMAGE_SELECTOR && resultCode == Activity.RESULT_OK) {
			if (data == null) {
				try {
					bitmap = BitmapFactory.decodeStream(getContentResolver()
							.openInputStream(fileUri));
					Matrix matrix = new Matrix();
					matrix.postRotate((float) 0.0);
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
