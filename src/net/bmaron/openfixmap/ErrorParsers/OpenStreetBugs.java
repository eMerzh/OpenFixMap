package net.bmaron.openfixmap.ErrorParsers;

import org.osmdroid.util.BoundingBoxE6;

public class OpenStreetBugs extends ErrorPlateform {

	public OpenStreetBugs() {
		super();
	}

	public OpenStreetBugs(BoundingBoxE6 bb, int ErrorLevel, boolean show_closed) {
		super(bb, ErrorLevel, show_closed);
	}
	
	public void load() {
		OpenStreetBugsGPX parser = new OpenStreetBugsGPX();
    	parser.parse(this.eLevel, this.show_closed, this.boundingBox);
    	lItems.addAll(parser.getItems());
	}

}
