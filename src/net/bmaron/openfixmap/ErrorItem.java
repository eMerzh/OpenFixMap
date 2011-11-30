package net.bmaron.openfixmap;

import java.util.Date;

import org.osmdroid.util.GeoPoint;

public class ErrorItem {
	private long id;
	private String title;
	private String description;
	private double lat;
	private double lon;
	private boolean is_closed;
	private int error_level;
	private String error_parser; 
	
	
    public ErrorItem(long id, String title, String description, double lat, double lon) {
    	this.id = id;
    	this.title = title;
    	this.description = description;
    	this.lat = lat;
    	this.lon = lon;
    }
    
    public ErrorItem(String parser) {
    	this.error_parser = parser;
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

	public int getErrorLevel() {
		return error_level;
	}

	public void setErrorLevel(int error_level) {
		this.error_level = error_level;
	}

	public String getErrorParser() {
		return error_parser;
	}

	public void setErrorParser(String error_parser) {
		this.error_parser = error_parser;
	}

	public boolean getIsClosed() {
		return is_closed;
	}

	public void setIsClosed(boolean is_closed) {
		this.is_closed = is_closed;
	}
	
	public Date getDate() {
		return new Date();
	}
	
}
