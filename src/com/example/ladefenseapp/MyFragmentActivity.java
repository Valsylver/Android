package com.example.ladefenseapp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.MyLocationOverlay;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MyFragmentActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	static Context context;

	static Lieu currentLieu;

	private static MapView mapView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		Log.i("TAG", "onCreate TabActivity");
		// StrictMode.setVmPolicy(new
		// StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_fragment);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);

		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		Editor editor = preferences.edit();
		editor.remove("preferedPlaces");
		editor.commit();
	}

	@Override
	public void onRestart() {
		super.onRestart();
		boolean state = LieuDetailActivity.goToMap;
		if (state) {
			Lieu lastLieu = LieuDetailActivity.lastLieu;
			MapController mapController = mapView.getController();
			mapController.setCenter(new GeoPoint(
					(int) (lastLieu.getLat() * 1e6),
					(int) (lastLieu.getLon() * 1e6)));
			mapController.setZoom(21);
			mViewPager.setCurrentItem(1);
			LieuDetailActivity.goToMap = false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_my_fragment, menu);
		return true;
	}

	public MapView getMapView() {
		if (mapView == null) {
			mapView = new MapView(this,
					"0M4ksh2slqYBQJpFFdgkfGqrofBOel8TibMX3GA");
			mapView.setEnabled(true);
			mapView.setClickable(true);
			mapView.setBuiltInZoomControls(true);
			MapController mapController = mapView.getController();
			mapController.setCenter(new GeoPoint(48891076, 2241712));
			mapController.setZoom(15);

			MyLocationOverlay location = new MyLocationOverlay(
					getApplicationContext(), mapView);
			mapView.getOverlays().add(location);
			location.enableMyLocation();
		}
		return mapView;
	}

	/**
	 * public void onTabSelected(ActionBar.Tab tab, FragmentTransaction
	 * fragmentTransaction) { // When the given tab is selected, switch to the
	 * corresponding page in // the ViewPager. Log.i("TAG", "On Tab Selected");
	 * mViewPager.setCurrentItem(tab.getPosition()); }
	 **/

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			if (position == 0) {
				Fragment fragment = new LieuFragment();
				return fragment;
			}
			if (position == 1) {
				Fragment fragment = new CustomMapFragment();
				return fragment;
			}
			if (position == 2) {
				Fragment fragment = new DummySectionFragment();
				Bundle args = new Bundle();
				args.putInt(DummySectionFragment.ARG_SECTION_NUMBER,
						position + 1);
				fragment.setArguments(args);
				return fragment;
			}
			return new DummySectionFragment();
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase();
			case 1:
				return getString(R.string.title_section2).toUpperCase();
			case 2:
				return getString(R.string.title_section3).toUpperCase();
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// Create a new TextView and set its text to the fragment's section
			// number argument value.
			Log.i("TAG", "TabActivity : On Create View DummySection");
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(getActivity()
							.getApplicationContext());
			String storedPreferences = preferences.getString("preferedPlaces",
					"");
			Log.i("TAG", "TabActivity : storedPreferences : "
					+ storedPreferences);
			JSONObject jsonObject = new JSONObject();
			JSONArray jsonArray = new JSONArray();

			ListView listView = new ListView(getActivity());
			try {
				jsonObject = new JSONObject(storedPreferences);
				jsonArray = jsonObject.getJSONArray("preferedPlaces");

				List<Lieu> preferedPlaces = new ArrayList<Lieu>();
				Lieu lieu;
				List<Integer> categorieId;

				Log.i("TAG",
						"TabActivity : Number of prefered places stored : "
								+ jsonArray.length());

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject object = jsonArray.getJSONObject(i);
					lieu = new Lieu();
					lieu.setImage(object.getString("image"));
					lieu.setSmallImage(object.getString("small_image"));
					lieu.setNom(object.getString("nom"));
					lieu.setLat(Float.valueOf((float) object.getDouble("lat")));
					lieu.setLon(Float.valueOf((float) object.getDouble("lon")));
					lieu.setSecteur(object.getString("secteur"));
					lieu.setQuartier(object.getString("quartier"));
					lieu.setInformations(object.getString("informations"));
					String cat = object.getString("categorieId");

					categorieId = new ArrayList<Integer>();
					for (int index = 0; i < cat.length(); i++) {
						String c = String.valueOf(cat.charAt(index));
						categorieId.add(Integer.valueOf(c));
					}

					preferedPlaces.add(lieu);
				}

				final LieuBaseAdapter adapter = new LieuBaseAdapter(
						getActivity().getBaseContext(), preferedPlaces);

				listView.setAdapter(adapter);

				OnItemClickListener listener = new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapterView,
							View arg1, int position, long arg3) {
						Intent monIntent = new Intent(adapterView.getContext(),
								LieuDetailActivity.class);
						monIntent.putExtra("lieu",
								adapter.getLieux().get(position));
						startActivity(monIntent);
					}
				};

				listView.setOnItemClickListener(listener);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.i("TAG",
						"TabActivity : JSONException caught while retrieving prefered places");
				e.printStackTrace();
			}

			return listView;
		}
	}

	public static class LieuFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public LieuFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			Context context = getActivity().getBaseContext();

			ListView listView = new ListView(getActivity());
			Log.i("TAG", "TabActivity : On Create View LieuFragment");

			final ArrayList<Lieu> allLieux = (ArrayList<Lieu>) getActivity()
					.getIntent().getSerializableExtra("lieux");

			Log.i("TAG",
					"TabActivity : Success Retrievement of the List of Lieu, size : "
							+ allLieux.size());

			Log.i("TAG",
					"TabActivity : Creation of the sublists for categories");

			final ArrayList<Lieu> allLieuxCat1 = new ArrayList<Lieu>();
			final ArrayList<Lieu> allLieuxCat2 = new ArrayList<Lieu>();
			final ArrayList<Lieu> allLieuxCat3 = new ArrayList<Lieu>();
			final ArrayList<Lieu> allLieuxCat4 = new ArrayList<Lieu>();

			List<Integer> categorieId;
			for (Lieu lieu : allLieux) {
				categorieId = lieu.getCategorieId();
				if (categorieId.contains(1)) {
					allLieuxCat1.add(lieu);
				}
				if (categorieId.contains(2)) {
					allLieuxCat2.add(lieu);
				}
				if (categorieId.contains(3)) {
					allLieuxCat3.add(lieu);
				}
				if (categorieId.contains(4)) {
					allLieuxCat4.add(lieu);
				}
			}

			Log.i("TAG",
					"TabActivity : Category 1, size " + allLieuxCat1.size());
			Log.i("TAG",
					"TabActivity : Category 2, size " + allLieuxCat2.size());
			Log.i("TAG",
					"TabActivity : Category 3, size " + allLieuxCat3.size());
			Log.i("TAG",
					"TabActivity : Category 4, size " + allLieuxCat4.size());

			final LieuBaseAdapter adapter = new LieuBaseAdapter(getActivity()
					.getBaseContext(), allLieux);

			listView.setAdapter(adapter);

			OnItemClickListener listener = new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View arg1,
						int position, long arg3) {
					Intent monIntent = new Intent(adapterView.getContext(),
							LieuDetailActivity.class);
					monIntent
							.putExtra("lieu", adapter.getLieux().get(position));
					startActivity(monIntent);
				}
			};

			listView.setOnItemClickListener(listener);

			final Spinner spinner = new Spinner(context);
			String[] spinnerItems = { "Toutes", "Batiments", "Résidences",
					"Hotels", "Services" };
			ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
					context, R.layout.spinner_item, spinnerItems);
			spinner.setAdapter(spinnerAdapter);
			LinearLayout.LayoutParams pSpinner = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			pSpinner.weight = 30;
			pSpinner.width = 0;

			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					Log.i("TAG", "Spinner : on item selected, position : "
							+ position);
					switch (position) {
					case 0:
						Log.i("TAG", "Switch case 0");
						adapter.setLieux(allLieux);
						break;
					case 1:
						Log.i("TAG", "Switch case 1");
						adapter.setLieux(allLieuxCat1);
						break;
					case 2:
						Log.i("TAG", "Switch case 2");
						adapter.setLieux(allLieuxCat2);
						break;
					case 3:
						Log.i("TAG", "Switch case 3");
						adapter.setLieux(allLieuxCat3);
						break;
					case 4:
						Log.i("TAG", "Switch case 4");
						adapter.setLieux(allLieuxCat4);
						break;
					}
					adapter.notifyDataSetChanged();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}

			});

			EditText editText = new EditText(context);
			editText.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.defacto_tab_recherche_w, 0, 0, 0);
			editText.setCompoundDrawablePadding(15);
			editText.setTextColor(Color.BLACK);
			editText.setHint("Recherche");
			LinearLayout.LayoutParams pEditText = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			pEditText.weight = 70;
			pEditText.width = 0;

			editText.addTextChangedListener(new TextWatcher() {

				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					List<Lieu> lieux = new ArrayList<Lieu>();
					List<Lieu> lieuxToDisplay = new ArrayList<Lieu>();
					switch (spinner.getSelectedItemPosition()) {
					case 0:
						lieux = allLieux;
						break;
					case 1:
						lieux = allLieuxCat1;
						break;
					case 2:
						lieux = allLieuxCat2;
						break;
					case 3:
						lieux = allLieuxCat3;
						break;
					case 4:
						lieux = allLieuxCat3;
						break;
					}

					boolean condition;
					for (Lieu lieu : lieux) {
						condition = lieu.getNom().contains(s);
						condition = condition || lieu.getQuartier().contains(s);
						condition = condition || lieu.getSecteur().contains(s);
						if (condition) {
							lieuxToDisplay.add(lieu);
						}
					}
					adapter.setLieux(lieuxToDisplay);
					adapter.notifyDataSetChanged();
				}
			});

			LinearLayout mainLayout = new LinearLayout(context);
			mainLayout.setOrientation(LinearLayout.VERTICAL);

			LinearLayout hLayout = new LinearLayout(context);
			hLayout.setOrientation(LinearLayout.HORIZONTAL);
			hLayout.setWeightSum(100);

			hLayout.addView(editText, pEditText);
			hLayout.addView(spinner, pSpinner);

			LinearLayout.LayoutParams pHLayout = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			mainLayout.addView(hLayout, pHLayout);
			mainLayout.addView(listView);

			Log.i("TAG", "End On Create View LieuFragment");

			return mainLayout;
		}
	}

	public static class CustomMapFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public CustomMapFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			Log.i("TAG", "On Create View Map Fragment");

			mapView = ((MyFragmentActivity) getActivity()).getMapView();

			final ArrayList<Lieu> allLieux = (ArrayList<Lieu>) getActivity()
					.getIntent().getSerializableExtra("lieux");

			GeoPoint point;

			final ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.init(ImageLoaderConfiguration
					.createDefault(getActivity()));

			for (final Lieu lieu : allLieux) {
				ImageView imageView = new ImageView(getActivity()
						.getApplicationContext());
				List<Integer> categories = lieu.getCategorieId();
				if (categories.contains(1)){
					imageView.setImageResource(R.drawable.buildings);
				}
				else{
					if (categories.contains(2)){
						imageView.setImageResource(R.drawable.home);
					}
					else{
						if (categories.contains(3)){
							imageView.setImageResource(R.drawable.hotel);
						}
						else{
							imageView.setImageResource(R.drawable.service);
						}
					}
				}
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						final Dialog dialog = new Dialog(getActivity());
						dialog.setContentView(R.layout.map_lieu_detail);
						dialog.setTitle(lieu.getNom());

						ImageView image = (ImageView) dialog
								.findViewById(R.id.map_details_img);
						TextView secteur = (TextView) dialog
								.findViewById(R.id.map_details_secteur);
						TextView quartier = (TextView) dialog
								.findViewById(R.id.map_details_quartier);
						Button closeButton = (Button) dialog
								.findViewById(R.id.map_details_close);
						Button favorisButton = (Button) dialog
								.findViewById(R.id.map_details_favoris);
						Button yAllerButton = (Button) dialog
								.findViewById(R.id.map_details_yaller);
						Button detailsButton = (Button) dialog
								.findViewById(R.id.map_details_details);

						secteur.setText(lieu.getSecteur());
						quartier.setText(lieu.getQuartier());
						imageLoader.displayImage(lieu.getSmallImage(), image);
						Log.i("TAG",
								"Lieu : " + lieu.getNom() + ", "
										+ lieu.getImage());

						closeButton.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
							}
						});

						detailsButton.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(getActivity()
										.getApplicationContext(),
										LieuDetailActivity.class);
								intent.putExtra("lieu", lieu);
								startActivity(intent);
							}
						});

						final boolean alreadyPrefered = isPlaceAlreadyPrefered(lieu);
						favorisButton.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (!alreadyPrefered) {

									Log.i("TAG", "alreadyPrefered");

									SharedPreferences preferences = PreferenceManager
											.getDefaultSharedPreferences(getActivity()
													.getApplicationContext());
									Editor editor = preferences.edit();
									String storedPreferences = preferences
											.getString("preferedPlaces", null);
									JSONObject jsonObject;

									try {
										if (storedPreferences != null) {
											Log.i("TAG", "OK not null");
											jsonObject = new JSONObject(
													storedPreferences);
											JSONArray jsonArray = jsonObject
													.getJSONArray("preferedPlaces");
											jsonArray.put(lieu.toJSONObject());
											editor.putString("preferedPlaces",
													jsonObject.toString());
											editor.commit();
										} else {
											Log.i("TAG", "OK null");
											jsonObject = new JSONObject();
											JSONArray jsonArray = new JSONArray();
											jsonArray.put(lieu.toJSONObject());
											jsonObject.put("preferedPlaces",
													jsonArray);
											editor.putString("preferedPlaces",
													jsonObject.toString());
											editor.commit();
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}

									Toast.makeText(
											getActivity()
													.getApplicationContext(),
											"Ce lieu a été ajouté aux favoris",
											Toast.LENGTH_LONG).show();
								} else {
									Log.i("TAG", "alreadyPrefered");
									Log.i("TAG",
											"LieuDetailActivity : JSONException");
									deletePlace(lieu);

									Toast.makeText(
											getActivity()
													.getApplicationContext(),
											"Ce lieu a bien été supprimé des favoris",
											Toast.LENGTH_LONG).show();
								}

							}
						});

						yAllerButton.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								LocationManager locationManager = (LocationManager) getActivity()
										.getSystemService(
												Context.LOCATION_SERVICE);
								Location location = locationManager
										.getLastKnownLocation(LocationManager.GPS_PROVIDER);

								if (location != null) {
									String url = getIteneraireUrl(
											location.getLatitude(),
											location.getLongitude(),
											lieu.getLat(), lieu.getLon());
									Log.i("TAG", "Url for maps : " + url);
									Intent intent = new Intent(
											Intent.ACTION_VIEW, Uri.parse(url));
									startActivity(intent);
								} else {
									Log.i("TAG", "Location null");
									Toast.makeText(
											getActivity()
													.getApplicationContext(),
											"Impossible de vous localiser",
											Toast.LENGTH_LONG).show();
								}
							}
						});

						dialog.show();
					}
				});
				point = new GeoPoint((int) (lieu.getLat() * 1e6),
						(int) (lieu.getLon() * 1e6));
				LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT, point, LayoutParams.TOP_LEFT);
				mapView.addView(imageView, lp);
			}

			Log.i("TAG", "End On Create View Map Fragment");

			mapView.setSatellite(true);

			return mapView;

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
					.getDefaultSharedPreferences(getActivity()
							.getApplicationContext());
			String storedPreferences = preferences.getString("preferedPlaces",
					null);
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
					.getDefaultSharedPreferences(getActivity()
							.getApplicationContext());
			Editor editor = preferences.edit();
			String storedPreferences = preferences.getString("preferedPlaces",
					null);
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
	}

	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		Log.i("TAG", "On Tab Selected");
		mViewPager.setCurrentItem(tab.getPosition());

	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {

	}

}
