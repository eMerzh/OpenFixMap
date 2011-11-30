package net.bmaron.openfixmap.ErrorParsers;

import org.osmdroid.util.BoundingBoxE6;


public class KeepRight extends ErrorPlateform {
	public KeepRight() {
		super();
	}
	public KeepRight(BoundingBoxE6 bb, int display_level, boolean show_closed) {
		super(bb, display_level,show_closed);
	}

	@Override
	public void load() {
    	KeepRightCSVParser parser = new KeepRightCSVParser();
    	parser.parse(this.boundingBox, this.eLevel, this.show_closed);
    	lItems.addAll(parser.getItems());
	}
}
