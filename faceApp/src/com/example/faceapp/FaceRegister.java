package com.example.faceapp;

import java.util.ArrayList;

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
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class FaceRegister extends Activity {
	private static final int RESULTS_PAGE_SIZE = 10;
	String imageServerUri = "http://157.182.38.37/welcome.php";
	private ListView mLvPicasa;
	private boolean mHasData = false;
	private boolean mInError = false;
	private ArrayList<MatchedFace> mEntries = new ArrayList<MatchedFace>();
	private MatchedFaceAdapter2 mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		mLvPicasa = (ListView) findViewById(R.id.lv_picasa);
		mAdapter = new MatchedFaceAdapter2(this,
				R.layout.listview_item_row_volley, mEntries,
				MyVolley.getImageLoader());
		mLvPicasa.setAdapter(mAdapter);

	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!mHasData && !mInError) {
			loadPage();
		}
	}

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
				imageServerUri, null, createMyReqSuccessListener(),
				createMyReqErrorListener());

		queue.add(myReq);
	}

	private Response.Listener<JSONObject> createMyReqSuccessListener() {
		return new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject feed = response.getJSONObject("feed");
					//Log.i("feed",feed.toString());
					JSONArray entries = feed.getJSONArray("entry");
					//Log.i("entries",entries.toString());
					JSONObject entry;
					//for (int i = 0; i < entries.length(); i++) {
					for (int i = 0; i < RESULTS_PAGE_SIZE; i++) {
						entry = entries.getJSONObject(i);
						Log.i("entry",entry.toString());
						String url = null;

						JSONObject media = entry.getJSONObject("media$group");
						//Log.i("media",media.toString());
						if (media != null && media.has("media$thumbnail")) {
							Log.i("media",media.toString());
							JSONArray thumbs = media
											.getJSONArray("media$thumbnail");
							Log.i("thumbs",thumbs.toString());
							if (thumbs != null && thumbs.length() > 0) {
								url = thumbs.getJSONObject(0).getString("url");
							}
						}
						Log.i("url",url);
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
				Log.i("error",error.toString());
				showErrorDialog();
			}
		};
	}

	private void showErrorDialog() {
		mInError = true;

		AlertDialog.Builder b = new AlertDialog.Builder(FaceRegister.this);
		b.setMessage("Error occured");
		b.show();
	}

}
