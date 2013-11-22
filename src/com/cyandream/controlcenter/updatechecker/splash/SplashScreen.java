package com.cyandream.controlcenter.updatechecker.splash;

import com.cyandream.controlcenter.lib.JsonParser;

import com.cyandream.controlcenter.R;
import com.cyandream.controlcenter.updatechecker.UpdateChecker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashScreen extends Activity {
	SharedPreferences preferences;
	DefaultHttpClient httpclient = new DefaultHttpClient();
    String version, size, installupdate, filename, upgradefrom, otasize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
	    preferences = PreferenceManager.getDefaultSharedPreferences(this);
		new PrefetchData().execute();
		// Some code copyright by androidhive.info

	}
	private class PrefetchData extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// before making http calls
			Log.e("JSON", "Pre execute");
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			Boolean nightly = preferences.getBoolean("nightly", false);
			
			if(!nightly) {
				String usenightly = "-release";
			/*
			 * Will make http call here This call will download required data
			 * before launching the app 
			 * example: 
			 * 1. Downloading and storing SQLite 
			 * 2. Downloading images 
			 * 3. Parsing the xml / json 
			 * 4. Sending device information to server 
			 * 5. etc.,
			 */
			JsonParser jsonParser = new JsonParser();
			String json = jsonParser
					.getJSONFromUrl("http://yanniks.de/roms/cd-" + android.os.Build.PRODUCT + usenightly + ".json" );
			if (json != null) {
				try {
					JSONObject jObj = new JSONObject(json)
							.getJSONObject("rominfo");
					version = jObj.getString("version");
					size = jObj.getString("size");
					otasize = jObj.getString("otasize");
					installupdate = jObj.getString("installupdate");
					filename = jObj.getString("filename");
					upgradefrom = jObj.getString("upgradefrom");

					Log.e("Current version: ", version + ", " + size + ", install update: " + installupdate);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			return null;
			} else {
				String usenightly = "";
				/*
				 * Will make http call here This call will download required data
				 * before launching the app 
				 * example: 
				 * 1. Downloading and storing SQLite 
				 * 2. Downloading images 
				 * 3. Parsing the xml / json 
				 * 4. Sending device information to server 
				 * 5. etc.,
				 */
				JsonParser jsonParser = new JsonParser();
				String json = jsonParser
						.getJSONFromUrl("http://yanniks.de/roms/cd-" + android.os.Build.PRODUCT + usenightly + ".json" );
				if (json != null) {
					try {
						JSONObject jObj = new JSONObject(json)
								.getJSONObject("rominfo");
						version = jObj.getString("version");
						size = jObj.getString("size");
						otasize = jObj.getString("otasize");
						installupdate = jObj.getString("installupdate");
						filename = jObj.getString("filename");
						upgradefrom = jObj.getString("upgradefrom");

						Log.e("Current version: ", version + ", " + size + ", install update: " + installupdate);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				return null;
			}
		}


		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Intent i = new Intent(SplashScreen.this, UpdateChecker.class);
			i.putExtra("currentversion", version);
			i.putExtra("size", size);
			i.putExtra("otasize", otasize);
			i.putExtra("installupdate", installupdate);
			i.putExtra("filename", filename);
			i.putExtra("upgradefrom", upgradefrom);
			startActivity(i);
			finish();
		}

	}

}
