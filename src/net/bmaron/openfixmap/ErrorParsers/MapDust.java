package net.bmaron.openfixmap.ErrorParsers;

import org.osmdroid.util.BoundingBoxE6;

import android.os.Bundle;

import net.bmaron.openfixmap.R;

public class MapDust extends ErrorPlatform {


	public MapDust(Bundle prefs) {
		super(prefs);
	}
	public MapDust(BoundingBoxE6 bb, int display_level, boolean show_closed) {
		super(bb, display_level,show_closed);
	}

	@Override
	public void load() {
		
		MapDustParser parser = new MapDustParser(this);
    	parser.parse(this.boundingBox, this.eLevel, this.showClosed);
    	lItems.addAll(parser.getItems());
	}
	
	@Override
	public int getIcon() {
		return R.drawable.mapdust;
	}
	
	@Override
	public String getName() {
		return "MapDust";
	}
	@Override
	public boolean canAdd() {
		return false;
	}
}
