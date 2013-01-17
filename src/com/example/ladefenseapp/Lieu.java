package com.example.ladefenseapp;

import java.io.Serializable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("serial")
public class Lieu implements Serializable{

	private String nom;

	private double lat;

	private double lon;

	private String image;

	private String smallImage;

	private String secteur;

	private String quartier;

	private String informations;
	
	private List<Integer> categorieId;


	public List<Integer> getCategorieId() {
		return categorieId;
	}

	public void setCategorieId(List<Integer> categorieId) {
		this.categorieId = categorieId;
	}

	public String getInformations() {
		return informations;
	}

	public void setInformations(String informations) {
		this.informations = informations;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getSmallImage() {
		return smallImage;
	}

	public void setSmallImage(String smallImage) {
		this.smallImage = smallImage;
	}

	public String getSecteur() {
		return secteur;
	}

	public void setSecteur(String secteur) {
		this.secteur = secteur;
	}

	public String getQuartier() {
		return quartier;
	}

	public void setQuartier(String quartier) {
		this.quartier = quartier;
	}
	
	private String categorieIdToString(){
		String cat = "";
		for (Integer index:categorieId){
			cat += index;
		}
		return cat;
	}
	
	public JSONObject toJSONObject() throws JSONException{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("nom", nom);
		jsonObject.put("lat", lat);
		jsonObject.put("lon", lon);
		jsonObject.put("quartier", quartier);
		jsonObject.put("secteur", secteur);
		jsonObject.put("informations", informations);
		jsonObject.put("categorieId", categorieIdToString());
		jsonObject.put("image", image);
		jsonObject.put("small_image", smallImage);
		return jsonObject;
	}

}
