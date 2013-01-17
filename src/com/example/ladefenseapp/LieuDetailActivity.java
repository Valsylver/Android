package com.example.ladefenseapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class LieuDetailActivity extends Activity {
	
	static Lieu lastLieu;
	static boolean goToMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lieu_detail);

		final Lieu lieu = (Lieu) getIntent().getSerializableExtra("lieu");
		lastLieu = lieu;

		TextView nom = (TextView) findViewById(R.id.nom);
		TextView quartier = (TextView) findViewById(R.id.quartier);
		TextView secteur = (TextView) findViewById(R.id.secteur);
		TextView description = (TextView) findViewById(R.id.description);
		ImageView image = (ImageView) findViewById(R.id.img);
		final Button preferenceButton = (Button) findViewById(R.id.lieu_detail_favoris);
		Button yAllerButton = (Button) findViewById(R.id.lieu_detail_yaller);
		Button carteButton = (Button) findViewById(R.id.lieu_detail_carte);

		nom.setText(lieu.getNom());
		quartier.setText(lieu.getQuartier());
		secteur.setText(lieu.getSecteur());
		description.setText(Html.fromHtml(lieu.getInformations()));
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration
				.createDefault(getApplicationContext()));
		imageLoader.displayImage(lieu.getImage(), image);

		final boolean alreadyPrefered = isPlaceAlreadyPrefered(lieu);

		yAllerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				Location location = locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);

				if (location != null) {
					String url = getIteneraireUrl(location.getLatitude(),
							location.getLongitude(), lieu.getLat(),
							lieu.getLon());
					Log.i("TAG", "Url for maps : " + url);
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri
							.parse(url));
					startActivity(intent);
				} else {
					Log.i("TAG", "Location null");
					Toast.makeText(getApplicationContext(), "Impossible de vous localiser", Toast.LENGTH_LONG).show();
				}
			}

		});
		
		carteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("TAG", "LieuDetailActivity, onClick carte button");
				goToMap = true;
		        finish();
			}

		});

		if (alreadyPrefered) {
			preferenceButton.setText("Supprimer des favoris");
		}

		preferenceButton.setOnClickListener(new OnClickListener() {
			// TODO Auto-generated method stub

			@Override
			public void onClick(View v) {

				Log.i("TAG", "onClick");

				if (!alreadyPrefered) {

					Log.i("TAG", "alreadyPrefered");

					SharedPreferences preferences = PreferenceManager
							.getDefaultSharedPreferences(getApplicationContext());
					Editor editor = preferences.edit();
					String storedPreferences = preferences.getString(
							"preferedPlaces", null);
					JSONObject jsonObject;

					try {
						if (storedPreferences != null) {
							Log.i("TAG", "OK not null");
							jsonObject = new JSONObject(storedPreferences);
							JSONArray jsonArray = jsonObject
									.getJSONArray("preferedPlaces");
							jsonArray.put(lieu.toJSONObject());
							editor.putString("preferedPlaces",
									jsonObject.toString());
							editor.commit();
							Log.i("TAG",
									"LieuDetailActivity : jsonObject.getString() : "
											+ jsonObject.toString());
						} else {
							Log.i("TAG", "OK null");
							jsonObject = new JSONObject();
							JSONArray jsonArray = new JSONArray();
							jsonArray.put(lieu.toJSONObject());
							jsonObject.put("preferedPlaces", jsonArray);
							editor.putString("preferedPlaces",
									jsonObject.toString());
							editor.commit();
							Log.i("TAG",
									"LieuDetailActivity : jsonObject.getString() : "
											+ jsonObject.toString());
						}
					} catch (JSONException e) {
						Log.i("TAG", "LieuDetailActivity : JSONException");
						e.printStackTrace();
					}

					preferenceButton.setText("Supprimer des favoris");

					Toast.makeText(getApplicationContext(),
							"Ce lieu a été ajouté aux favoris",
							Toast.LENGTH_LONG).show();
				} else {
					Log.i("TAG", "alreadyPrefered");

					deletePlace(lieu);

					preferenceButton.setText("Ajouter aux favoris");

					Toast.makeText(getApplicationContext(),
							"Ce lieu a bien été supprimé des favoris",
							Toast.LENGTH_LONG).show();
				}
			}

		});

	}

	public String getIteneraireUrl(double fromLat, double fromLon,
			double toLat, double toLon) {
		StringBuffer urlString = new StringBuffer();
		urlString.append("http://maps.google.com/maps?f=d&hl=en");
		urlString.append("&saddr=");
		urlString.append(Double.toString(fromLat));
		urlString.append(",");
		urlString.append(Double.toString(fromLon));
		urlString.append("&daddr=");
		urlString.append(Double.toString(toLat));
		urlString.append(",");
		urlString.append(Double.toString(toLon));
		urlString.append("&ie=UTF8&0&om=0&output=kml");
		return urlString.toString();
	}

	private boolean isPlaceAlreadyPrefered(Lieu lieu) {
		boolean alreadyPrefered = false;
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String storedPreferences = preferences
				.getString("preferedPlaces", null);
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		if (storedPreferences != null) {
			try {
				jsonObject = new JSONObject(storedPreferences);
				jsonArray = jsonObject.getJSONArray("preferedPlaces");

				boolean cond = true;
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject object = jsonArray.getJSONObject(i);
					String nom = object.getString("nom");
					String secteur = object.getString("secteur");
					String quartier = object.getString("quartier");

					cond = cond && nom.equals(lieu.getNom());
					cond = cond && secteur.equals(lieu.getSecteur());
					cond = cond && quartier.equals(lieu.getQuartier());

					if (cond) {
						alreadyPrefered = true;
						break;
					}

					cond = true;
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.i("TAG",
						"LieuDetailActivity : JSONException caught while retrieving prefered places");
				e.printStackTrace();
			}
			Log.i("TAG", "LieuDetailActivity : Lieu already in prefered ? "
					+ alreadyPrefered);
			return alreadyPrefered;
		} else {
			return false;
		}
	}

	private void deletePlace(Lieu lieu) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		Editor editor = preferences.edit();
		String storedPreferences = preferences
				.getString("preferedPlaces", null);
		JSONObject jsonObject;
		JSONArray jsonArray;
		JSONObject newJsonObject = new JSONObject();

		try {
			jsonObject = new JSONObject(storedPreferences);
			jsonArray = jsonObject.getJSONArray("preferedPlaces");
			JSONArray newJsonArray = new JSONArray();

			boolean cond = true;

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				String nom = object.getString("nom");
				String secteur = object.getString("secteur");
				String quartier = object.getString("quartier");

				cond = cond && nom.equals(lieu.getNom());
				cond = cond && secteur.equals(lieu.getSecteur());
				cond = cond && quartier.equals(lieu.getQuartier());

				if (!cond) {
					newJsonArray.put(object);
				}

				cond = true;
			}

			newJsonObject.put("preferedPlaces", newJsonArray);

			editor.putString("preferedPlaces", newJsonObject.toString());
			editor.commit();
		} catch (JSONException e) {
			Log.i("TAG", "LieuDetailActivity : JSONException");
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_lieu_detail, menu);
		return true;
	}

}
