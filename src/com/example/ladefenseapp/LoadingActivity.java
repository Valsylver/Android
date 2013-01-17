package com.example.ladefenseapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class LoadingActivity extends Activity {

	private ProgressBar progressBar;

	private TextView textView;

	static ArrayList<Lieu> allLieu;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("TAG", "------------------------ Restart");
		//LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//LocationProvider locationProvider = locationManager.getProvider(LocationManager.NETWORK_PROVIDER);
		// Or, use GPS location data:
		// LocationProvider locationProvider = LocationManager.GPS_PROVIDER;

		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		textView = (TextView) findViewById(R.id.textView1);
		textView.setText(R.string.application_name);
		ReadJson readJson = new ReadJson();
		readJson.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_loading, menu);
		return true;
	}

	private class ReadJson extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... values) {
			Log.i("TAG", "LoadingActivity : doInBackground");
			try {
				String readTwitterFeed = readTwitterFeed();
				Log.i("TAG", "read twitter feed");
				JSONObject theObject = new JSONObject(readTwitterFeed);
				JSONArray jsonArray = theObject.getJSONArray("results");
				Log.i("TAG", "Number of entries " + jsonArray.length());
				progressBar.setProgress(0);
				progressBar.setMax(jsonArray.length());
				ArrayList<Lieu> lieux = new ArrayList<Lieu>();
				Lieu lieu;
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					lieu = new Lieu();
					lieu.setImage(jsonObject.getString("image"));
					lieu.setSmallImage(jsonObject.getString("small_image"));
					lieu.setNom(jsonObject.getString("nom"));
					lieu.setLat(Float.valueOf(jsonObject.getString("lat")));
					lieu.setLon(Float.valueOf(jsonObject.getString("lon")));
					lieu.setSecteur(jsonObject.getString("secteur"));
					lieu.setQuartier(jsonObject.getString("quartier"));
					String informations = jsonObject.getString("informations");
					lieu.setInformations(informations.replaceAll("</br>",
							"<br>"));
					Log.i("TAG", lieu.getNom());
					String categorieIdJson = jsonObject.getString("categorie_id");
					List<Integer> categorieId = new ArrayList<Integer>();
					if (categorieIdJson.length() == 1) {
						categorieId.add(Integer.valueOf(categorieIdJson));
					} else {
						String[] categorieIdList = categorieIdJson.split(" et ");
						for (String currentId : categorieIdList) {
							categorieId.add(Integer.valueOf(currentId));
						}
					}
					lieu.setCategorieId(categorieId);
					Log.i("TAG", "" + lieu.getLat());
					lieux.add(lieu);
					publishProgress(i + 1);
				}
				LoadingActivity.allLieu = lieux;
				Log.i("TAG", "LoadingActivity : End Read Json");
				
				
				Collections.sort(lieux, new Comparator<Lieu>(){

					@Override
					public int compare(Lieu lhs, Lieu rhs) {
						return lhs.getNom().compareTo(rhs.getNom());
					}
					
				});
				
				LoadingActivity.allLieu = lieux;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			progressBar.setProgress(values[0]);
		}

		private String readTwitterFeed() throws Exception {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet("http://cci.corellis.eu/pois.php");
			try {
				HttpResponse response = httpClient.execute(httpGet);
				if (response != null) {
					String line = "";
					InputStream inputStream = response.getEntity().getContent();
					line = convertStreamToString(inputStream);
					return line;
				}
			} catch (ClientProtocolException e) {
				Log.i("TAG", "client protocol excpetion");
			} catch (IOException e) {
				Log.i("TAG", "ioexception");
			} catch (Exception e) {
				Log.i("TAG", "other exception : " + e.getClass());
			}
			return "";
		}

		private String convertStreamToString(InputStream is) {
			String line = "";
			StringBuilder total = new StringBuilder();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			try {
				while ((line = rd.readLine()) != null) {
					total.append(line);
				}
			} catch (Exception e) {
				Log.i("TAG", "Stream Exception");
			}
			return total.toString();
		}

		@Override
		protected void onPostExecute(Void result) {
			Intent monIntent = new Intent(getApplicationContext(),
					MyFragmentActivity.class);
			monIntent.putExtra("lieux", LoadingActivity.allLieu);
			startActivity(monIntent);
		}

	}

}

