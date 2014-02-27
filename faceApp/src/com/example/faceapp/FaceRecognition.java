package com.example.faceapp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.android.volley.toolbox.ImageLoader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FaceRecognition extends Activity {
	private Uri fileUri;
	private ImageView imageView;
	private Bitmap bitmap;
	private ListView listView1;
	ProgressDialog dialog = null;
	
	/**********  File Path *************/
    final String uploadFilePath = Environment.getExternalStorageDirectory().getPath() + "/Pictures/";
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
		
		 /************* Php script path ****************/
        upLoadServerUri = "http://157.182.38.37/uploadTry.php";
        imageServerUri = "http://157.182.38.37/uploads/4.jpg";
		
		final MatchedFace matchedFace_data[] = new MatchedFace[]
			        {
			            new MatchedFace(R.drawable.face1, "Cloudy"),
			            new MatchedFace(R.drawable.face2, "Showers"),
			            new MatchedFace(R.drawable.face3, "Snow"),
			            new MatchedFace(R.drawable.face4, "Storm"),
			            new MatchedFace(R.drawable.face5, "Sunny"),
			            new MatchedFace(R.drawable.face6, "Lucky")
			        };
			
		
		MatchedFaceAdapter adapter = new MatchedFaceAdapter(this, 
                R.layout.listview_item_row, matchedFace_data);
        
        
        listView1 = (ListView)findViewById(R.id.listView1);
        listView1.setBackgroundResource(R.drawable.imageborder);
         
        View header = (View)getLayoutInflater().inflate(R.layout.listview_header_row, null);
        listView1.addHeaderView(header);
        
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
                
            	String item = matchedFace_data[position-1].title.toString();
                Toast.makeText(getBaseContext(), item, Toast.LENGTH_SHORT).show();
                
            }
        });
		
		
		
		
		
		this.imageView = (ImageView) this.findViewById(R.id.imageView1);
		imageView.setBackgroundResource(R.drawable.imageborder);

		// Detect Button
		Button btnDect = (Button) findViewById(R.id.button_dect);
		btnDect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO: Apply Face Detection
				//Create a new image bitmap and attach a brand new canvas to it
				
				//TODO: check if the Bitmap exists
				if (bitmap==null){
					Context context = getApplicationContext();
					CharSequence text = "Please Add One Photo!";
					int duration = Toast.LENGTH_SHORT;

					Toast toast = Toast.makeText(context, text, duration);
					toast.show();	 
					
					
				}else{
				//Get a detected photo from server
					ImageLoader imageLoader = MyVolley.getImageLoader();
					imageLoader.get(imageServerUri, 
                            ImageLoader.getImageListener(imageView, 
                                                          R.drawable.no_image, 
                                                          R.drawable.error_image));
					
					
					
				//TODO: write function for drawing
				//TODO: face region and facial points
				/*Paint paint = new Paint();
				paint.setColor(Color.GREEN);
				
				Bitmap tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
				Canvas canvas = new Canvas(tempBitmap);
			    canvas.drawBitmap(bitmap, 0, 0, null);
			    canvas.drawCircle(60, 50, 25, paint);
			    ImageView imageView = (ImageView)findViewById(R.id.imageView2);
			  //Attach the canvas to the ImageView
			    imageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));;*/
				}
				
			}
		});

		
		//Recognize Button
		Button btnRecog = (Button) findViewById(R.id.button_recog);
		btnRecog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO: Apply Face Detection
				//Create a new image bitmap and attach a brand new canvas to it
				
				//TODO: check if the Bitmap exists
				if (bitmap==null){
					Context context = getApplicationContext();
					CharSequence text = "Please Detect Face!";
					int duration = Toast.LENGTH_SHORT;

					Toast toast = Toast.makeText(context, text, duration);
					toast.show();	 
					
					
				}else{
					dialog = ProgressDialog.show(FaceRecognition.this, "", "Uploading file...", true);
					
	                new Thread(new Runnable() {
	                        public void run() {
	                             //uploadFile(uploadFilePath + uploadFileName);
	                        	
	                             uploadFile(fileUri);
	                        }
	                      }).start();        
	                }	
				}
		});

				
		
		
		
		//Take photo button
		Button btn = (Button) findViewById(R.id.button_take);
		btn.setOnClickListener(new OnClickListener() {
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
	
	 View insertPhoto(String path){
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
	 public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {
	     Bitmap bm = null;
	     
	     // First decode with inJustDecodeBounds=true to check dimensions
	     final BitmapFactory.Options options = new BitmapFactory.Options();
	     options.inJustDecodeBounds = true;
	     BitmapFactory.decodeFile(path, options);
	     
	     // Calculate inSampleSize
	     options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	     
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
	       inSampleSize = Math.round((float)height / (float)reqHeight);   
	      } else {
	       inSampleSize = Math.round((float)width / (float)reqWidth);   
	      }   
	     }
	     
	     return inSampleSize;   
	    }
	    
	  	  	   
	    //Image loader
	    public int uploadFile(Uri sourceFileUri) {
	          
	    	   	    		    	
	    	 // Wrong: String fileName = sourceFileUri.getPath();
	    	//Wrong: String fileName = sourceFileUri.toString().substring(7);
	    	//Environment.getExternalStorageDirectory()
	    	 String fileName = Environment.getExternalStorageDirectory().getPath()+ "/Pictures/"+sourceFileUri.toString().substring(sourceFileUri.toString().indexOf("IMG"));
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
	          File sourceFile=new File(fileName);
	          			  
	          
	          if (!sourceFile.isFile()) {
	          	               
	               dialog.dismiss(); 
	                
	               //Log.e("uploadFile", "Source File not exist :"
	                                   //+uploadFilePath + "" + uploadFileName);
	               Log.e("uploadFile","Source File not exist :" + fileName);
	                  	               
	                
	               return 0;
	            
	          }
	          else
	          {
	               try { 
	                    
	                     // open a URL connection to the Servlet
	                   FileInputStream fileInputStream = new FileInputStream(sourceFile);
	                   URL url = new URL(upLoadServerUri);
	                    
	                   // Open a HTTP  connection to  the URL
	                   conn = (HttpURLConnection) url.openConnection(); 
	                   conn.setDoInput(true); // Allow Inputs
	                   conn.setDoOutput(true); // Allow Outputs
	                   conn.setUseCaches(false); // Don't use a Cached Copy
	                   conn.setRequestMethod("POST");
	                   conn.setRequestProperty("Connection", "Keep-Alive");
	                   conn.setRequestProperty("ENCTYPE", "multipart/form-data");
	                   conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
	                   conn.setRequestProperty("uploaded_file", fileName); 
	                    
	                   dos = new DataOutputStream(conn.getOutputStream());
	          
	                   dos.writeBytes(twoHyphens + boundary + lineEnd); 
	                   dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ fileName + "\"" + lineEnd);
	                    
	                   dos.writeBytes(lineEnd);
	          
	                   // create a buffer of  maximum size
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
	                    
	                   if(serverResponseCode == 200){
	                        
	                       runOnUiThread(new Runnable() {
	                            public void run() {
	                                 
	                                                               
	                                Toast.makeText(FaceRecognition.this, "File Upload Complete.", 
	                                             Toast.LENGTH_SHORT).show();
	                            }
	                        });                
	                   }    
	                    
	                   //close the streams //
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
	                         
	                          Toast.makeText(FaceRecognition.this, "Got Exception : see logcat ", 
	                                  Toast.LENGTH_SHORT).show();
	                      }
	                  });
	                  Log.e("Upload file to server Exception", "Exception : "
	                                                   + e.getMessage(), e);  
	              }
	              dialog.dismiss();       
	              return serverResponseCode; 
	               
	           } // End else block 
	         } 
	    
}
