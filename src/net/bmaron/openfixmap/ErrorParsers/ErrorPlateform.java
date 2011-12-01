package net.bmaron.openfixmap.ErrorParsers;

import java.util.ArrayList;
import java.util.List;


import net.bmaron.openfixmap.ErrorItem;
import org.osmdroid.util.BoundingBoxE6;

public abstract class ErrorPlateform {

	protected List<ErrorItem> lItems;
	protected BoundingBoxE6 boundingBox;
	protected int eLevel;
	protected boolean show_closed;
	
	public ErrorPlateform()
	{}

	public ErrorPlateform(BoundingBoxE6 bb, int ErrorLevel, boolean show_closed) {
		eLevel = ErrorLevel;
		this.show_closed = show_closed;
		boundingBox = bb;
		lItems = new ArrayList<ErrorItem>();
	}
	
	public abstract void load();

	
	
	public List<ErrorItem> getItems()
	{
		org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(KeepRightCSVParser.class);
        logger.info("getting items : # "+ lItems.size());
        
		return lItems;
	}

	public BoundingBoxE6 getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(BoundingBoxE6 boundingBox) {
		this.boundingBox = boundingBox;
	} 
	
	public abstract int getIcon();
	public abstract String getName();
	
}
