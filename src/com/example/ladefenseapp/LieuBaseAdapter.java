package com.example.ladefenseapp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class LieuBaseAdapter extends BaseAdapter {

	private List<Lieu> lieux;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;

	private class ViewHolder {
		TextView nom;
		TextView quartierSecteur;
		ImageView image;
	}

	@Override
	public int getCount() {
		return this.lieux.size();
	}

	public LieuBaseAdapter(Context context, List<Lieu> objects) {
		Log.i("TAG", "Lieu base adapter creation");
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		this.inflater = LayoutInflater.from(context);
		Log.i("TAG", "size lieux constructor adapter : " + objects.size());
		this.lieux = objects;
		Log.i("TAG", "Lieu base adapter creation end");
	}

	@Override
	public Lieu getItem(int position) {
		return lieux.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.lieu, null);
			holder.nom = (TextView) convertView.findViewById(R.id.nom);
			holder.quartierSecteur = (TextView) convertView
					.findViewById(R.id.quartier_secteur);
			holder.image = (ImageView) convertView.findViewById(R.id.img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Lieu lieu = lieux.get(position);
		holder.nom.setText(lieu.getNom());
		holder.quartierSecteur.setText(lieu.getQuartier() + " - "
				+ lieu.getSecteur());
		
		
		//Log.i("TAG", "Image existe ? " + testUrl(lieu.getSmallImage()));
		if (testUrl(lieu.getSmallImage())){
			imageLoader.displayImage(lieu.getSmallImage(), holder.image);
		}
		else{
			holder.image.setImageResource(R.drawable.ic_launcher);
		}
		return convertView;
	}

	private boolean testUrl(String url) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url)
					.openConnection();
			conn.connect();
			return conn.getResponseCode() == HttpURLConnection.HTTP_OK;
		} catch (MalformedURLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	public List<Lieu> getLieux() {
		return lieux;
	}

	public void setLieux(List<Lieu> lieux_) {
		lieux = lieux_;
	}

}
