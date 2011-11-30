package net.bmaron.openfixmap;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;


public class OverlayErrorItem extends OverlayItem {

	protected ErrorItem item;
	
	public OverlayErrorItem(String aTitle, String aDescription,
			GeoPoint aGeoPoint) {
		super(aTitle, aDescription, aGeoPoint);
	}
	
	public OverlayErrorItem(ErrorItem i)
	{
		super(i.getTitle(), i.getDescription(), i.getPoint());
		item=i;
	}
	
	public ErrorItem getError() {
		return item;
	}
	
	public void setError(ErrorItem i)
	{
		item=i;
	}

}
