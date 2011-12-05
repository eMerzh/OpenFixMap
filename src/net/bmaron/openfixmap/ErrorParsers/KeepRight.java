package net.bmaron.openfixmap.ErrorParsers;

import net.bmaron.openfixmap.R;

import org.osmdroid.util.BoundingBoxE6;



public class KeepRight extends ErrorPlatform {
	public KeepRight() {
		super();
	}
	public KeepRight(BoundingBoxE6 bb, int display_level, boolean show_closed) {
		super(bb, display_level,show_closed);
	}

	@Override
	public void load() {
    	KeepRightCSVParser parser = new KeepRightCSVParser(this);
    	parser.parse(this.boundingBox, this.eLevel, this.showClosed);
    	lItems.addAll(parser.getItems());
	}
	
	@Override
	public int getIcon() {
		return R.drawable.keepright;
	}
	
	@Override
	public String getName() {
		return "KeepRight";
	}
	@Override
	public boolean canAdd() {
		return false;
	}
}
