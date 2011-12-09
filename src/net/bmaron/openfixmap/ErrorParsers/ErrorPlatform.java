package net.bmaron.openfixmap.ErrorParsers;

import java.util.ArrayList;
import java.util.List;


import net.bmaron.openfixmap.ErrorItem;
import net.bmaron.openfixmap.PlatformManager;

import org.osmdroid.util.BoundingBoxE6;

public abstract class ErrorPlatform {

	protected List<ErrorItem> lItems;
	protected BoundingBoxE6 boundingBox;
	protected int eLevel;
	protected boolean showClosed;
	protected boolean can_add;
	private PlatformManager mgr;

	public ErrorPlatform(PlatformManager mgr) {
		lItems = new ArrayList<ErrorItem>();
		setManager(mgr);
	}

	public ErrorPlatform(BoundingBoxE6 bb, int ErrorLevel, boolean show_closed) {
		eLevel = ErrorLevel;
		showClosed = show_closed;
		boundingBox = bb;
		lItems = new ArrayList<ErrorItem>();
	}
	
	public abstract void load();

	
	
	public List<ErrorItem> getItems()
	{
		org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ErrorPlatform.class);
        logger.info("getting items : # "+ lItems.size());
        
		return lItems;
	}

	public BoundingBoxE6 getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(BoundingBoxE6 boundingBox) {
		this.boundingBox = boundingBox;
	} 
	
	public boolean createBug(ErrorItem i) {
		return false;
	}
	
	public void setForFetch(BoundingBoxE6 bb, int ErrorLevel, boolean show_closed) {
		this.setBoundingBox(bb);
		this.setErrorLevel(ErrorLevel);
		this.setShowClosed(show_closed);
	}
	
	public void setErrorLevel(int level) {
		eLevel = level;
	}
	public void setShowClosed(boolean show) {
		showClosed = show;
	}
	public abstract int getIcon();
	public abstract String getName();
	public abstract boolean canAdd();
	
	
	public PlatformManager getManager() {
		return mgr;
	}
	public void setManager(PlatformManager mgr) {
		this.mgr = mgr;
	}
	
}
