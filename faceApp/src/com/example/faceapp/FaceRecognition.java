package com.example.faceapp;

import java.io.DataInputStream;
import android.content.DialogInterface;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.JsonObjectRequest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class FaceRecognition extends Activity {

	private final static String TAG = "FaceRecognition";

	private Uri fileUri;
	private ImageView imageView;
	private ImageView imageView2;
	private ImageView imageView3;
	private TextView textView_Res;
	private TextView textView_gender;
	private TextView textView_race;
	private TextView textView_age;
	private TextView textView_bmi;

	private Bitmap bitmap;
	private ListView listView1;
	boolean upload_success = false;
	ProgressDialog dialog = null;
	private ProgressBar mProgressBar;
	private int mDelay = 500;
	private boolean FACE_UPLOADED = false;
	private boolean FACE_DETECTED = false;
	private String jsonReply = "";
	private JSONObject detectJSONObj;

	/********** Show Image List parameters *************/
	private static final int RESULTS_PAGE_SIZE = 10;
	// private static final String SERVER_IP_ADDRESS = "http://157.182.38.37/";
	
	//2014/05 nexus7
	//private static String SERVER_IP_ADDRESS = "http://192.168.43.222/php/";
	//try set ip
	SharedPreferences sp;
	private String SERVER_IP_ADDRESS = "";
	
	
	private String imageServerUri_load;
	private String imageServerUri_getD;
	private String imageServerUri_getCrop;
	private String imageServerUri_getMesh;
	private String imageServerUri_getPoints;
	private String imageServerUri_getBMI;

	private ListView mLvPicasa;
	private boolean mHasData = false;
	private boolean mInError = false;
	private ArrayList<MatchedFace> mEntries = new ArrayList<MatchedFace>();
	private MatchedFaceAdapter2 mAdapter;

	/********** File Path *************/
	final String uploadFilePath = Environment.getExternalStorageDirectory()
			.getPath() + "/Pictures/";
	final String uploadFileName = "face1.png";
	String upLoadServerUri = null;
	String imageServerUri = null;

	private static final int IMAGE_SELECTOR = 1;
	protected static final int CAMERA_REQUEST = 2;
	protected static final int GALLERY_PICTURE = 3;
	int serverResponseCode = 0;
	LinearLayout myGallery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recog);
		
		sp = getSharedPreferences("preferences",Context.MODE_PRIVATE);
		SERVER_IP_ADDRESS = "http://"+sp.getString("serverIP","defName") +"/php/";
		
		imageServerUri_load = SERVER_IP_ADDRESS
				+ "load_list.php"; // "http://157.182.38.37/welcome.php";
		imageServerUri_getD = SERVER_IP_ADDRESS
				+ "getDetected.php"; // "http://157.182.38.37/getDetected.php";
		imageServerUri_getCrop = SERVER_IP_ADDRESS
				+ "getCrop.php";// "http://157.182.38.37/getCrop.php";
		imageServerUri_getMesh = SERVER_IP_ADDRESS
				+ "getMesh.php";// "http://157.182.38.37/getMesh.php";
		imageServerUri_getPoints = SERVER_IP_ADDRESS
				+ "getPoints.php";
		imageServerUri_getBMI = SERVER_IP_ADDRESS
				+ "computeBMI.php";
		upLoadServerUri = SERVER_IP_ADDRESS 
				+ "upload_img_final.php";

		// SERVER_IP_ADDRESS =
		// "http://"+((setIP)this.getApplication()).getServerIP()+"/php/";
		// Toast.makeText(getApplicationContext(), "Server IP: "+
		// SERVER_IP_ADDRESS, Toast.LENGTH_LONG).show();

		/************* Php script path ****************/
		// upLoadServerUri = "http://157.182.38.24/php/upload_img.php";

		// 3.20 test
		//upLoadServerUri = "http://192.168.43.222/php/upload_img_try.php";

		// imageServerUri = "http://157.182.38.37/uploads/4.jpg";

		/*
		 * final MatchedFace matchedFace_data[] = new MatchedFace[] { new
		 * MatchedFace(R.drawable.face1, "Cloudy"), new
		 * MatchedFace(R.drawable.face2, "Showers"), new
		 * MatchedFace(R.drawable.face3, "Snow"), new
		 * MatchedFace(R.drawable.face4, "Storm"), new
		 * MatchedFace(R.drawable.face5, "Sunny"), new
		 * MatchedFace(R.drawable.face6, "Lucky") };
		 * 
		 * MatchedFaceAdapter adapter = new MatchedFaceAdapter(this,
		 * R.layout.listview_item_row, matchedFace_data);
		 * 
		 * listView1 = (ListView) findViewById(R.id.listView1);
		 * listView1.setBackgroundResource(R.drawable.imageborder);
		 * 
		 * View header = (View) getLayoutInflater().inflate(
		 * R.layout.listview_header_row, null); listView1.addHeaderView(header);
		 * 
		 * listView1.setAdapter(adapter); listView1.setOnItemClickListener(new
		 * OnItemClickListener() {
		 * 
		 * @Override public void onItemClick(AdapterView<?> parent, View view,
		 * int position, long id) {
		 * 
		 * String item = matchedFace_data[position - 1].title.toString();
		 * Toast.makeText(getBaseContext(), item, Toast.LENGTH_SHORT) .show();
		 * 
		 * } });
		 */

		this.imageView = (ImageView) this.findViewById(R.id.imageView1);
		imageView.setBackgroundResource(R.drawable.imageborder);
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadPhoto(imageView, v.getWidth(), v.getHeight());
			}
		});
		this.imageView2 = (ImageView) findViewById(R.id.imageView2);
		imageView2.setBackgroundResource(R.drawable.imageborder);
		imageView2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadPhoto(imageView2, v.getWidth(), v.getHeight());
			}
		});

		imageView3 = (ImageView) findViewById(R.id.imageView_3);
		imageView3.setBackgroundResource(R.drawable.imageborder);
		imageView3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadPhoto(imageView3, v.getWidth(), v.getHeight());
			}
		});

		textView_gender = (TextView) findViewById(R.id.textView_gender_val);
		textView_race = (TextView) findViewById(R.id.textView_race_val);
		textView_age = (TextView) findViewById(R.id.textView_age_val);
		textView_bmi = (TextView) findViewById(R.id.textView_bmi_val);

		textView_Res = (TextView) findViewById(R.id.textView_Res);
		textView_Res.setMovementMethod(new ScrollingMovementMethod());

		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

		loadPage();

		// this.imageView3 = (ImageView) this.findViewById(R.id.imageView3);
		// imageView3.setBackgroundResource(R.drawable.imageborder);

		// Image List View

		mLvPicasa = (ListView) findViewById(R.id.listView1);
		// mLvPicasa.setBackgroundColor(getResources().getColor(R.color.Background_Gray));
		mLvPicasa.setBackgroundResource(R.drawable.imageborder);
		View header = (View) getLayoutInflater().inflate(
				R.layout.listview_header_row, null);

		mLvPicasa.addHeaderView(header);
		mAdapter = new MatchedFaceAdapter2(this,
				R.layout.listview_item_row_volley, mEntries,
				MyVolley.getImageLoader());
		mLvPicasa.setAdapter(mAdapter);
		mLvPicasa.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MatchedFace mf = (MatchedFace) parent.getAdapter().getItem(
						position);
				String item = mf.getTitle();
				// Toast.makeText(getBaseContext(), item,
				// Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(FaceRecognition.this,
						FaceDetail.class);
				intent.putExtra("face_title", mf.getTitle());
				intent.putExtra("face_uri", mf.getThumbnailUrl());
				startActivity(intent);
			}
		});

		// Add popup menu
		Button button_popup = (Button) findViewById(R.id.button_popup);
		button_popup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
				popupMenu.getMenuInflater().inflate(R.menu.popupmenu,
						popupMenu.getMenu());

				// popupMenu.getMenu().getItem(1).setEnabled(false); //set click
				// to menu items

				popupMenu
						.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
							@Override
							public boolean onMenuItemClick(MenuItem item) {
								// TODO Auto-generated method stub
								// Toast.makeText(getApplicationContext(),
								// item.toString(), Toast.LENGTH_SHORT).show();

								switch (item.getItemId()) {
								case R.id.menu1:
									// Toast.makeText(getApplicationContext(),
									// "1", Toast.LENGTH_SHORT).show();
									takePhoto();
									break;
								case R.id.menu2:
									// Toast.makeText(getApplicationContext(),
									// "2", Toast.LENGTH_SHORT).show();
									detectFace();
									break;
								case R.id.menu3:
									// Toast.makeText(getApplicationContext(),
									// "3", Toast.LENGTH_SHORT).show();
									recognizeFace();
									break;
								case R.id.menu4:
									// Toast.makeText(getApplicationContext(),
									// "4", Toast.LENGTH_SHORT).show();
																	
									computeBMI();
									break;
								}

								return true;
							}
						});

				popupMenu.show();
			}

		});

		/*
		 * // Detect Button Button btnDect = (Button)
		 * findViewById(R.id.button_dect); btnDect.setOnClickListener(new
		 * OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO: Apply Face Detection
		 * // Create a new image bitmap and attach a brand new canvas to it
		 * 
		 * // TODO: check if the Bitmap exists if (bitmap == null) { Context
		 * context = getApplicationContext(); CharSequence text =
		 * "Please Add One Photo!"; int duration = Toast.LENGTH_SHORT;
		 * 
		 * Toast toast = Toast.makeText(context, text, duration); toast.show();
		 * 
		 * } else {
		 * 
		 * dialog = ProgressDialog.show(FaceRecognition.this, "",
		 * "Uploading file...", true);
		 * 
		 * 
		 * final CountDownLatch latch = new CountDownLatch(1); //wait for the
		 * thread finished new Thread(new Runnable() { public void run() { //
		 * uploadFile(fileUri); // used for upload face // image
		 * uploadFile(fileUri.toString().substring(
		 * fileUri.toString().indexOf("IMG"))); latch.countDown(); } }).start();
		 * 
		 * 
		 * try { latch.await(); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 * 
		 * 
		 * 
		 * //Get Detected Points RequestQueue queue =
		 * MyVolley.getRequestQueue();
		 * 
		 * JsonObjectRequest myReq = new JsonObjectRequest(Method.GET,
		 * imageServerUri_getPoints+"?name="+fileUri.toString().substring(
		 * fileUri.toString().indexOf("IMG")), null,
		 * createMyReqSuccessListener2(), createMyReqErrorListener2());
		 * 
		 * queue.add(myReq);
		 * 
		 * 
		 * 
		 * 
		 * 
		 * // Get a detected photo from server //
		 * MyVolley.init(FaceRecognition.this);
		 * 
		 * ImageLoader imageLoader = MyVolley.getImageLoader();
		 * 
		 * imageLoader.get(imageServerUri_getD + "?name=" + fileUri.toString(),
		 * new ImageListener(){
		 * 
		 * @Override public void onErrorResponse(VolleyError error) { //if
		 * (errorImageResId != 0) { //
		 * imageView.setImageResource(errorImageResId); //}
		 * Toast.makeText(FaceRecognition.this, "No Face Detected!!!",
		 * Toast.LENGTH_LONG).show(); }
		 * 
		 * @Override public void onResponse(ImageContainer response, boolean
		 * isImmediate) { if (response.getBitmap() != null) {
		 * imageView.setImageBitmap(response.getBitmap()); } else if
		 * (R.drawable.blank_photo != 0) {
		 * imageView.setImageResource(R.drawable.blank_photo); } } } );
		 * 
		 * 
		 * 
		 * 
		 * //Draw cropped Face image imageLoader.get(imageServerUri_getCrop +
		 * "?name=" + fileUri.toString(), ImageLoader
		 * .getImageListener(imageView2, R.drawable.no_image,
		 * R.drawable.blank_photo)); //Draw meshed Face image
		 * imageLoader.get(imageServerUri_getCrop + "?name=" +
		 * fileUri.toString(), ImageLoader .getImageListener(imageView3,
		 * R.drawable.no_image, R.drawable.blank_photo)); }
		 * 
		 * } });
		 * 
		 * // Recognize Button Button btnRecog = (Button)
		 * findViewById(R.id.button_recog); btnRecog.setOnClickListener(new
		 * OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO: Apply Face Detection
		 * // Create a new image bitmap and attach a brand new canvas to it
		 * 
		 * // TODO: check if the Bitmap exists if (bitmap == null) { Context
		 * context = getApplicationContext(); CharSequence text =
		 * "Please Detect Face!"; int duration = Toast.LENGTH_SHORT;
		 * 
		 * Toast toast = Toast.makeText(context, text, duration); toast.show();
		 * 
		 * } else { // Show Matched Faces from Server on the Image List if
		 * (!mHasData && !mInError) { loadPage(); } } } });
		 * 
		 * // Take photo button Button btn = (Button)
		 * findViewById(R.id.button_take); btn.setOnClickListener(new
		 * OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { Intent galleryintent = new
		 * Intent(Intent.ACTION_GET_CONTENT, null);
		 * galleryintent.setType("image/*");
		 * 
		 * // This can work as well // Intent galleryintent = new
		 * Intent(Intent.ACTION_PICK, //
		 * android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		 * 
		 * Intent cameraIntent = new Intent(
		 * android.provider.MediaStore.ACTION_IMAGE_CAPTURE); fileUri =
		 * getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a // file to //
		 * save the // image Log.i("fileUri", fileUri.toString());
		 * cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set //
		 * the // image // file // name cameraIntent.putExtra("return-data",
		 * true);
		 * 
		 * Intent chooser = new Intent(Intent.ACTION_CHOOSER);
		 * chooser.putExtra(Intent.EXTRA_INTENT, galleryintent);
		 * chooser.putExtra(Intent.EXTRA_TITLE, "Select Source");
		 * 
		 * Intent[] intentArray = { cameraIntent };
		 * chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
		 * startActivityForResult(chooser, IMAGE_SELECTOR);
		 * 
		 * } });
		 */
	}

	// Detect face: response to the button in popupmenu
	private void detectFace() {
		if (bitmap == null) { // Check if there is an image
			Context context = getApplicationContext();
			CharSequence text = "Please Take a Photo";
			int duration = Toast.LENGTH_SHORT;
			Toast.makeText(context, text, duration).show();
		} else {

			new UploadImageTask().execute();
		}
	}

	// RecognizeFace: response to the button in popupmenu
	private void recognizeFace() {
		if (bitmap == null) {
			Context context = getApplicationContext();
			CharSequence text = "Please Detect Face!";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();

		} else {
			// Show Matched Faces from Server on the Image List
			if (!mHasData && !mInError) {
				loadPage();
			}
		}
	}

	// Take Photo: response to the button in popupmenu
	private void takePhoto() {

		imageView3.setImageResource(R.drawable.face_a);
		imageView2.setImageResource(R.drawable.facial_b);

		Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT, null);
		galleryintent.setType("image/*");

		// This can work as well
		// Intent galleryintent = new Intent(Intent.ACTION_PICK,
		// android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

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

	// Comput BMI: response to the button in popupmenu
	private void computeBMI() {
			
		Toast.makeText(getApplicationContext(), "Computing BMI...",
				Toast.LENGTH_SHORT).show();

		String uri = imageServerUri_getBMI
				+ "?name="
				+ fileUri.toString().substring(
						fileUri.toString().indexOf("IMG"));
		Log.i("bmi uri:", uri);
		// Get BMI result
		RequestQueue queue = MyVolley.getRequestQueue();

		JsonObjectRequest myReq = new JsonObjectRequest(Method.GET, uri, null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						try {
							
							double bmi = Double.parseDouble(response.getString("bmi").toString());
							textView_bmi.setText(Double.toString(bmi));
														
							LayoutInflater inflater = FaceRecognition.this.getLayoutInflater();
							View v = inflater.inflate(R.layout.bmi_details, null);
							TextView bmi_val = (TextView)v.findViewById(R.id.textView_bmiVal);
							
							//Select bmi category
							//double bmi = 33.32;
							bmi_val.setText(Double.toString(bmi));
							TextView bmi_cat0;
							TextView bmi_cat1;
							TextView bmi_cat2 = (TextView)v.findViewById(R.id.textView_bmiCat);
							if (bmi<18.5){
								bmi_cat0 = (TextView)v.findViewById(R.id.textView_ud);
								bmi_cat1 = (TextView)v.findViewById(R.id.textView_udVal);
								bmi_cat0.setTextColor(getResources().getColor(R.color.YellowGreen));
								bmi_cat1.setTextColor(getResources().getColor(R.color.YellowGreen));
								bmi_cat2.setText("Underweight");
							}
							if (bmi>=18.5 && bmi<=24.9){
								bmi_cat0 = (TextView)v.findViewById(R.id.textView_nor);
								bmi_cat1 = (TextView)v.findViewById(R.id.textView_norVal);
								bmi_cat0.setTextColor(getResources().getColor(R.color.YellowGreen));
								bmi_cat1.setTextColor(getResources().getColor(R.color.YellowGreen));
								bmi_cat2.setText("Normal");
							}
							if (bmi>=25.0 && bmi<=29.9){
								bmi_cat0 = (TextView)v.findViewById(R.id.textView_over);
								bmi_cat1 = (TextView)v.findViewById(R.id.textView_overVal);
								bmi_cat0.setTextColor(getResources().getColor(R.color.YellowGreen));
								bmi_cat1.setTextColor(getResources().getColor(R.color.YellowGreen));
								bmi_cat2.setText("Overweight");
							}
							if (bmi>=30.0){
								bmi_cat0 = (TextView)v.findViewById(R.id.textView_obe);
								bmi_cat1 = (TextView)v.findViewById(R.id.textView_obeVal);
								bmi_cat0.setTextColor(getResources().getColor(R.color.YellowGreen));
								bmi_cat1.setTextColor(getResources().getColor(R.color.YellowGreen));
								bmi_cat2.setText("Obese");
							}
							
							
							new AlertDialog.Builder(FaceRecognition.this,android.R.style.Theme_Holo_Dialog).setTitle("Body Mass Index").setView(v).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									
								}
							}).create().show();
							
							
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.i("bmierror", error.toString());
						textView_bmi.setText("N/A");
					}
				});
		queue.add(myReq);
	}

	class UploadImageTask extends AsyncTask<Integer, Integer, Bitmap> {

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			mProgressBar.setVisibility(ProgressBar.INVISIBLE);

			// Draw points and images

			// Draw Rectangle and display on image view 3
			int x = 0;
			int y = 0;
			int w = 0;
			int h = 0;
			
			
			
				try {
					JSONArray rect_arr = (JSONArray) detectJSONObj
							.get("rectangle");
					
					JSONObject rect = rect_arr.getJSONObject(0);
					if (rect_arr.isNull(0)){
						//Toast.makeText(getApplicationContext(), "No Face Detected!!! Please Select Another Image.", Toast.LENGTH_LONG).show();
	new AlertDialog.Builder(FaceRecognition.this).setTitle("No Face Detected").setMessage("No Face Detected!!! Please Select Another Image.").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
							}
						}).setIcon(android.R.drawable.ic_dialog_alert).create().show();
						
					}else{
						
					
					x = rect.getInt("x");
					y = rect.getInt("y");
					w = rect.getInt("w");
					h = rect.getInt("h");
					Bitmap cropBitmap = Bitmap.createBitmap(bitmap, x, y, w, h);
					Bitmap sizeBitmap = Bitmap
							.createScaledBitmap(cropBitmap,
									imageView3.getWidth(),
									imageView3.getHeight(), true);
					imageView3.setImageBitmap(sizeBitmap);
					}
				} catch (JSONException e) {
					Log.e(TAG, "json draw rectangle error.");
				}

				// Draw Points and display on main image view
				try {
					JSONArray rect_arr = (JSONArray) detectJSONObj
							.get("points");
					
					if (rect_arr.isNull(0)){
						//Toast.makeText(getApplicationContext(), "No Face Detected!!! Please Select Another Image.", Toast.LENGTH_LONG).show();
						
						new AlertDialog.Builder(FaceRecognition.this).setTitle("No Face Detected").setMessage("No Face Detected!!! Please Select Another Image.").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
							}
						}).setIcon(android.R.drawable.ic_dialog_alert).create().show();
						
					}else{
					
					
					Bitmap pointBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,
							true);
					Canvas canvas = new Canvas(pointBitmap);

					Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(),
							bitmap.getHeight(), Bitmap.Config.ARGB_8888);
					Canvas canvas2 = new Canvas(bmp);
					canvas2.drawColor(Color.DKGRAY);
					Paint paint = new Paint();
					paint.setColor(Color.GREEN);
					paint.setTextSize(18);
					paint.setStyle(Paint.Style.FILL);
					paint.setStrokeWidth(8);
					for (int i = 0; i < rect_arr.length(); i++) {
						JSONArray point = rect_arr.getJSONArray(i);
						canvas.drawPoint(point.getInt(1), point.getInt(0),
								paint);
						canvas2.drawPoint(point.getInt(1), point.getInt(0),
								paint);
						canvas2.drawText(Integer.toString(i), point.getInt(1),
								point.getInt(0), paint);
					}

					imageView.setImageBitmap(pointBitmap);

					Bitmap cropBitmap = Bitmap.createBitmap(bmp, x, y, w, h);
					Bitmap sizeBitmap = Bitmap
							.createScaledBitmap(cropBitmap,
									imageView2.getWidth(),
									imageView2.getHeight(), true);
					imageView2.setImageBitmap(sizeBitmap);
					}
				} catch (JSONException e) {
					Log.e(TAG, "json draw points error.");
				}
			
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			mProgressBar.setVisibility(ProgressBar.VISIBLE);

			dialog = ProgressDialog.show(FaceRecognition.this, "",
					"Uploading file...", true);

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			mProgressBar.setProgress(values[0]);
		}

		@Override
		protected Bitmap doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			uploadFile(fileUri.toString().substring(
					fileUri.toString().indexOf("IMG")));

			/*
			 * for (int i=1;i<11;i++){ sleep(); publishProgress(i*10); }
			 */

			return null;
		}

		private void sleep() {
			try {
				Thread.sleep(mDelay);
			} catch (InterruptedException e) {
				Log.e(TAG, e.toString());
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == IMAGE_SELECTOR && resultCode == Activity.RESULT_OK) {
			if (data == null) {
				try {
					bitmap = BitmapFactory.decodeStream(getContentResolver()
							.openInputStream(fileUri));
					Matrix matrix = new Matrix();
					// matrix.postRotate((float) 90.0);
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

	View insertPhoto(String path) {
		Bitmap bm = decodeSampledBitmapFromUri(path, 220, 220);

		LinearLayout layout = new LinearLayout(getApplicationContext());
		layout.setLayoutParams(new LayoutParams(250, 250));
		layout.setGravity(Gravity.CENTER);

		ImageView imageView = new ImageView(getApplicationContext());
		imageView.setLayoutParams(new LayoutParams(220, 220));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setImageBitmap(bm);

		layout.addView(imageView);
		return layout;
	}

	public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth,
			int reqHeight) {
		Bitmap bm = null;

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		bm = BitmapFactory.decodeFile(path, options);

		return bm;
	}

	public int calculateInSampleSize(

	BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}

		return inSampleSize;
	}

	// upload image to server (use cache to create a File from bitmap and
	// upload)
	public int uploadFile(String fileName) {

		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		File sourceFile = new File(this.getCacheDir(), fileName);
		try {
			sourceFile.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (bitmap == null) {

			dialog.dismiss();
			Log.e("uploadFile", "Source File not exist");
			return 0;

		} else {
			try {
				// Convert bitmap to byte array
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bitmap.compress(CompressFormat.JPEG, 0, bos);
				byte[] bitmapdata = bos.toByteArray();

				// write the bytes in file
				FileOutputStream fos = new FileOutputStream(sourceFile);
				fos.write(bitmapdata);

				// open a URL connection to the Servlet
				FileInputStream fileInputStream = new FileInputStream(
						sourceFile);
				URL url = new URL(upLoadServerUri);

				// Open a HTTP connection to the URL
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true); // Allow Inputs
				conn.setDoOutput(true); // Allow Outputs
				conn.setUseCaches(false); // Don't use a Cached Copy
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("ENCTYPE", "multipart/form-data");
				conn.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);
				conn.setRequestProperty("uploaded_file", fileName);

				dos = new DataOutputStream(conn.getOutputStream());

				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
						+ fileName + "\"" + lineEnd);

				dos.writeBytes(lineEnd);

				// create a buffer of maximum size
				bytesAvailable = fileInputStream.available();

				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				// read file and write it into form...
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {

					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				}

				// send multipart form data necesssary after file data...
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				// Responses from the server (code and message)
				serverResponseCode = conn.getResponseCode();
				String serverResponseMessage = conn.getResponseMessage();

				Log.i("uploadFile", "HTTP Response is : "
						+ serverResponseMessage + ": " + serverResponseCode);

				if (serverResponseCode == 200) {
					// Get Response
					InputStream is = conn.getInputStream();
					BufferedReader rd = new BufferedReader(
							new InputStreamReader(is));
					String line;
					StringBuffer response = new StringBuffer();
					while ((line = rd.readLine()) != null) {
						response.append(line);
					}
					jsonReply = response.toString();
					rd.close();

					try {
						detectJSONObj = new JSONObject(jsonReply);
						Log.i(TAG, detectJSONObj.toString());
					} catch (Throwable t) {
						Log.e(TAG, "Could NOT parch JSON: \"" + jsonReply
								+ "\"");
					}

					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(FaceRecognition.this,
									"File Upload Complete.", Toast.LENGTH_SHORT)
									.show();
							// Toast.makeText(getApplicationContext(),
							// jsonReply, Toast.LENGTH_LONG).show();
						}
					});

				}

				// close the streams //
				fileInputStream.close();
				dos.flush();
				dos.close();

				// close the streams //
				bos.flush();
				bos.close();
				fos.flush();
				fos.close();

			} catch (MalformedURLException ex) {

				dialog.dismiss();
				ex.printStackTrace();

				Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
			} catch (Exception e) {

				dialog.dismiss();
				e.printStackTrace();

				runOnUiThread(new Runnable() {
					public void run() {

						Toast.makeText(FaceRecognition.this,
								"Got Exception : see logcat ",
								Toast.LENGTH_SHORT).show();
					}
				});
				Log.e("Upload file to server Exception",
						"Exception : " + e.getMessage(), e);
			}
			dialog.dismiss();
			return serverResponseCode;

		} // End else block
	}

	// (No longer Used) uoload image to server using file uri, cannot work from
	// gallery selected file
	public int uploadFile(Uri sourceFileUri) {

		// Wrong: String fileName = sourceFileUri.getPath();
		// Wrong: String fileName = sourceFileUri.toString().substring(7);
		// Environment.getExternalStorageDirectory()
		String fileName = Environment.getExternalStorageDirectory().getPath()
				+ "/Pictures/"
				+ sourceFileUri.toString().substring(
						sourceFileUri.toString().indexOf("IMG"));
		Log.i("filename", fileName);
		Log.i("uri_tostring", sourceFileUri.toString());

		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		File sourceFile = new File(fileName);

		if (!sourceFile.isFile()) {

			dialog.dismiss();

			// Log.e("uploadFile", "Source File not exist :"
			// +uploadFilePath + "" + uploadFileName);
			Log.e("uploadFile", "Source File not exist :" + fileName);

			return 0;

		} else {
			try {

				// open a URL connection to the Servlet
				FileInputStream fileInputStream = new FileInputStream(
						sourceFile);
				URL url = new URL(upLoadServerUri);

				// Open a HTTP connection to the URL
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true); // Allow Inputs
				conn.setDoOutput(true); // Allow Outputs
				conn.setUseCaches(false); // Don't use a Cached Copy
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("ENCTYPE", "multipart/form-data");
				conn.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);
				conn.setRequestProperty("uploaded_file", fileName);

				dos = new DataOutputStream(conn.getOutputStream());

				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
						+ fileName + "\"" + lineEnd);

				dos.writeBytes(lineEnd);

				// create a buffer of maximum size
				bytesAvailable = fileInputStream.available();

				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				// read file and write it into form...
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {

					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				}

				// send multipart form data necesssary after file data...
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				// Responses from the server (code and message)
				serverResponseCode = conn.getResponseCode();
				String serverResponseMessage = conn.getResponseMessage();

				Log.i("uploadFile", "HTTP Response is : "
						+ serverResponseMessage + ": " + serverResponseCode);

				if (serverResponseCode == 200) {

					runOnUiThread(new Runnable() {
						public void run() {

							Toast.makeText(FaceRecognition.this,
									"File Upload Complete.", Toast.LENGTH_SHORT)
									.show();
						}
					});

					upload_success = true;
				}

				// close the streams //
				fileInputStream.close();
				dos.flush();
				dos.close();

			} catch (MalformedURLException ex) {

				dialog.dismiss();
				ex.printStackTrace();

				Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
			} catch (Exception e) {

				dialog.dismiss();
				e.printStackTrace();

				runOnUiThread(new Runnable() {
					public void run() {

						Toast.makeText(FaceRecognition.this,
								"Got Exception : see logcat ",
								Toast.LENGTH_SHORT).show();
					}
				});
				Log.e("Upload file to server Exception",
						"Exception : " + e.getMessage(), e);
			}
			dialog.dismiss();
			return serverResponseCode;

		} // End else block
	}

	// Image View Loader
	private void loadPage() {
		RequestQueue queue = MyVolley.getRequestQueue();

		int startIndex = 1 + mEntries.size();
		/*
		 * JsonObjectRequest myReq = new JsonObjectRequest(Method.GET,
		 * "https://picasaweb.google.com/data/feed/api/all?q=kitten&max-results="
		 * + RESULTS_PAGE_SIZE + "&thumbsize=160&alt=json" + "&start-index=" +
		 * startIndex, null, createMyReqSuccessListener(),
		 * createMyReqErrorListener());
		 */
		JsonObjectRequest myReq = new JsonObjectRequest(Method.GET,
				imageServerUri_load, null, createMyReqSuccessListener(),
				createMyReqErrorListener());

		queue.add(myReq);
	}

	private Response.Listener<JSONObject> createMyReqSuccessListener() {
		return new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject feed = response.getJSONObject("feed");
					// Log.i("feed",feed.toString());
					JSONArray entries = feed.getJSONArray("entry");
					// Log.i("entries",entries.toString());
					JSONObject entry;
					// for (int i = 0; i < entries.length(); i++) {
					for (int i = 0; i < RESULTS_PAGE_SIZE; i++) {
						entry = entries.getJSONObject(i);
						Log.i("entry", entry.toString());
						String url = null;

						JSONObject media = entry.getJSONObject("media$group");
						// Log.i("media",media.toString());
						if (media != null && media.has("media$thumbnail")) {
							Log.i("media", media.toString());
							JSONArray thumbs = media
									.getJSONArray("media$thumbnail");
							Log.i("thumbs", thumbs.toString());
							if (thumbs != null && thumbs.length() > 0) {
								url = thumbs.getJSONObject(0).getString("url");
							}
						}
						Log.i("url", url);
						mEntries.add(new MatchedFace(entry.getJSONObject(
								"title").getString("$t"), url));
					}
					mAdapter.notifyDataSetChanged();
				} catch (JSONException e) {
					showErrorDialog();
				}
			}
		};
	}

	private Response.ErrorListener createMyReqErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.i("error", error.toString());
				showErrorDialog();
			}
		};
	}

	private void showErrorDialog() {
		mInError = true;

		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setMessage("Error occured");
		b.show();
	}

	// load photo when click the small face image view(big, crop and meshed)
	private void loadPhoto(ImageView imageView, int width, int height) {

		ImageView tempImageView = imageView;

		AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);

		View layout = inflater.inflate(R.layout.custom_fullimage_dialog,
				(ViewGroup) findViewById(R.id.layout_root));
		ImageView image = (ImageView) layout.findViewById(R.id.fullimage);
		image.setImageDrawable(tempImageView.getDrawable());
		imageDialog.setView(layout);
		imageDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				});

		imageDialog.create();
		imageDialog.show();
	}

	private Response.Listener<JSONObject> createMyReqSuccessListener2() {
		return new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					textView_Res.setText(response.getString("size")
							+ response.getString("points"));
				} catch (JSONException e) {
					textView_Res.setText("Parse error");
				}
			}
		};
	}

	private Response.ErrorListener createMyReqErrorListener2() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				textView_Res.setText(error.getMessage());
				Log.i("json error", error.getMessage());
			}
		};
	}
}
