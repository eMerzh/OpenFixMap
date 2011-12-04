package net.bmaron.openfixmap;

import java.util.Date;

import net.bmaron.openfixmap.ErrorParsers.ErrorPlatform;

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
	private ErrorPlatform error_platform; 
	private String link;
	
	
    public ErrorItem(long id, String title, String description, double lat, double lon) {
    	this.id = id;
    	this.title = title;
    	this.description = description;
    	this.lat = lat;
    	this.lon = lon;
    }
    
    public ErrorItem(ErrorPlatform parser) {
    	this.error_platform = parser;
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

	public String getTitleOr(String def){
		if(title==null)
			return def;
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

	public ErrorPlatform getPlatform() {
		return error_platform;
	}

	public void setPlatform(ErrorPlatform error_parser) {
		this.error_platform = error_parser;
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

	public void setPoint(GeoPoint p) {
		setLat(p.getLatitudeE6()/ 1E6);
		setLon(p.getLongitudeE6()/ 1E6);
	}

	public void save() {
		if(error_platform != null && error_platform.canAdd()) {
			error_platform.createBug(this);
		}
	}
	
}
