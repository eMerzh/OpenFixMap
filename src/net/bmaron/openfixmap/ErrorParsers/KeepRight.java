package net.bmaron.openfixmap.ErrorParsers;

import net.bmaron.openfixmap.PlatformManager;
import net.bmaron.openfixmap.R;

public class KeepRight extends ErrorPlatform {

	public KeepRight(PlatformManager mgr) {
		super(mgr);
	}
	
	@Override
	public void load() {
		lItems.clear();
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
