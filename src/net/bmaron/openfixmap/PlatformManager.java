package net.bmaron.openfixmap;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.util.BoundingBoxE6;

import android.content.SharedPreferences;

import net.bmaron.openfixmap.ErrorParsers.ErrorPlatform;
import net.bmaron.openfixmap.ErrorParsers.KeepRight;
import net.bmaron.openfixmap.ErrorParsers.OpenStreetBugs;

public class PlatformManager {


	
	private List<ErrorPlatform> lPlatforms;
	private SharedPreferences sharedPrefs;
	public PlatformManager(SharedPreferences prefs) {
		sharedPrefs = prefs;
		lPlatforms = new ArrayList<ErrorPlatform>();
		lPlatforms.add(new OpenStreetBugs());
		lPlatforms.add(new KeepRight());
		
	}
	
	public List<ErrorPlatform> getActivePlatforms() {
		List<ErrorPlatform> activeList = new ArrayList<ErrorPlatform>();
	
        String [] checkers = MultiSelectListPreference.parseStoredValue(sharedPrefs.getString("checkers", "KeepRight"));

        for(int i = 0; i < checkers.length; i++) {
        	for(ErrorPlatform temp : lPlatforms) {
            	if(temp.getName().equals(checkers[i])){
            		activeList.add(temp);
            		break;
            	}
            		
            }
        }
        return activeList;
	}
	
	public void fetchAllData(BoundingBoxE6 bb, int errorLevel, boolean show_closed)
	{
		for(ErrorPlatform temp : getActivePlatforms()) {
			temp.setForFetch(bb, errorLevel, show_closed);
			temp.load();
		}
	}
	
	public List<ErrorItem> getAllItems()
	{
		List<ErrorItem>  items = new ArrayList<ErrorItem>();
		for(ErrorPlatform temp : getActivePlatforms()) {
			items.addAll(temp.getItems());
		}
		return items;
	}

	/*
     * Set Default parser for First launch
     */
	public void setOneCheckerOnFirstLoad() {
        if(sharedPrefs.getString("checkers", null) == null ){
        	SharedPreferences.Editor editor = sharedPrefs.edit();
        	editor.putString("checkers", "KeepRight");
        	editor.commit();
        }		
	}
}
