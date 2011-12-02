package net.bmaron.openfixmap;

import java.util.Date;

import net.bmaron.openfixmap.ErrorParsers.ErrorPlateform;

import org.osmdroid.util.GeoPoint;

public class ErrorItem {
	private long id;
	private String title;
	private String description;
	private double lat;
	private double lon;
	private boolean is_closed;
	private int error_level;
	private Date date;
	private ErrorPlateform error_plateform; 
	private String link;
	
	
    public ErrorItem(long id, String title, String description, double lat, double lon) {
    	this.id = id;
    	this.title = title;
    	this.description = description;
    	this.lat = lat;
    	this.lon = lon;
    }
    
    public ErrorItem(ErrorPlateform parser) {
    	this.error_plateform = parser;
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

	public ErrorPlateform getPlateform() {
		return error_plateform;
	}

	public void setPlateform(ErrorPlateform error_parser) {
		this.error_plateform = error_parser;
	}

	public boolean getIsClosed() {
		return is_closed;
	}

	public void setIsClosed(boolean is_closed) {
		this.is_closed = is_closed;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
}
