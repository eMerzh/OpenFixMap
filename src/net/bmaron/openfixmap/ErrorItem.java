package net.bmaron.openfixmap;

import java.util.Date;

import net.bmaron.openfixmap.ErrorParsers.ErrorPlatform;

import org.osmdroid.util.GeoPoint;

public class ErrorItem {
	final public static int ER_CLEAN = 0;
	final public static int ER_DIRTY = 1;
	
	final public static int ST_OPEN = 0;
	final public static int ST_INVALID = 1;
	final public static int ST_CLOSE = 2;
	
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

	private int err_status = 0; //  ST_CLOSED, ST_OPEN , ST_INVALID
	private int saved_status = 0; //  CLEAN,  DIRTY

	
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
		setSavedStatus(ER_DIRTY);
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
		setSavedStatus(ER_DIRTY);
		this.title = title;
	}

	public String getDescription() {
		setSavedStatus(ER_DIRTY);
		return description;
	}

	public void setDescription(String description) {
		setSavedStatus(ER_DIRTY);
		this.description = description;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		setSavedStatus(ER_DIRTY);
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		setSavedStatus(ER_DIRTY);
		this.lon = lon;
	}
	
	public GeoPoint getPoint() {
		return new GeoPoint(this.lat,this.lon);
	}

	public int getErrorLevel() {
		return error_level;
	}

	public void setErrorLevel(int error_level) {
		setSavedStatus(ER_DIRTY);
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
		setSavedStatus(ER_DIRTY);
		this.is_closed = is_closed;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		setSavedStatus(ER_DIRTY);
		this.date = date;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		setSavedStatus(ER_DIRTY);
		this.link = link;
	}

	public void setPoint(GeoPoint p) {
		setSavedStatus(ER_DIRTY);
		setLat(p.getLatitudeE6()/ 1E6);
		setLon(p.getLongitudeE6()/ 1E6);
	}

	public void save() {
		if(error_platform != null && error_platform.canAdd()) {
			if(error_platform.createBug(this)) {
				setSavedStatus(ER_CLEAN);
			}
		}
	}

	public int getSavedStatus() {
		return saved_status;
	}

	public void setSavedStatus(int status) {
		this.saved_status = status;
	}

	public int getErrorStatus() {
		return err_status;
	}

	public void setErrorStatus(int err_status) {
		this.err_status = err_status;
	}
	
}
