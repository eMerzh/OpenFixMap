package net.bmaron.openfixmap.ErrorParsers;

import net.bmaron.openfixmap.R;

import org.osmdroid.util.BoundingBoxE6;

public class OpenStreetBugs extends ErrorPlatform {

	public OpenStreetBugs() {
		super();
	}

	public OpenStreetBugs(BoundingBoxE6 bb, int ErrorLevel, boolean show_closed) {
		super(bb, ErrorLevel, show_closed);
	}
	
	@Override
	public void load() {
		OpenStreetBugsGPX parser = new OpenStreetBugsGPX(this);
    	parser.parse(this.eLevel, this.show_closed, this.boundingBox);
    	lItems.addAll(parser.getItems());
	}
	
	@Override
	public int getIcon() {
		return R.drawable.open_bug_marker;
	}

	@Override
	public String getName() {
		return "OpenStreetBugs";
	}
	
	@Override
	public boolean canAdd() {
		return true;
	}
	
}
