package net.bmaron.openfixmap;

import org.osmdroid.util.GeoPoint;

public class ErrorItem {
	private long id;
	private String title;
	private String description;
	private double lat;
	private double lon;
	
    public ErrorItem(long id, String title, String description, double lat, double lon) {
    	this.id = id;
    	this.title = title;
    	this.description = description;
    	this.lat = lat;
    	this.lon = lon;
    }
    
    public ErrorItem() {
    	
    }
    
    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
	
	public GeoPoint getPoint() {
		return new GeoPoint(this.lat,this.lon);
	}
	
	/*
	public setPoint(Geopoint p){
		
	}*/
}
