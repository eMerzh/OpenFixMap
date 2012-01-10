package net.bmaron.openfixmap;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.util.BoundingBoxE6;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import net.bmaron.openfixmap.ErrorParsers.ErrorPlatform;
import net.bmaron.openfixmap.ErrorParsers.KeepRight;
import net.bmaron.openfixmap.ErrorParsers.MapDust;
import net.bmaron.openfixmap.ErrorParsers.OpenStreetBugs;

public class PlatformManager {
	private static PlatformManager instance;

	private Bundle prefBndl;
	private List<ErrorPlatform> lPlatforms;
	private SharedPreferences sharedPrefs;
	private Context context;
	public PlatformManager(Context ctx, SharedPreferences prefs, Bundle appPrefs) {
		setContext(ctx);
		prefBndl = appPrefs;
		sharedPrefs = prefs;
		lPlatforms = new ArrayList<ErrorPlatform>();
		lPlatforms.add(new OpenStreetBugs(this));
		lPlatforms.add(new KeepRight(this));
		lPlatforms.add(new MapDust(this));
		instance=this;
	}
	
	public static PlatformManager getInstance() {
		/*if(instance == null) {
			instance = new PlatformManager(null, null, null); //Hum Hum not this!!
		}*/
		return instance;
	}
	
	public Bundle getPreferences() {
		return prefBndl;
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
	
	public List<ErrorPlatform> getActiveAllowAddPlatforms() {

		List<ErrorPlatform> addList = new ArrayList<ErrorPlatform>();

		for(ErrorPlatform temp :  getActivePlatforms()) {
			if(temp.canAdd()){
				addList.add(temp);
			}
		}
        return addList;
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
	public Context getContext() {
		return context;
	}
	public void setContext(Context context) {
		this.context = context;
	}
	public CharSequence[] getReportPtfms() {
		List<ErrorPlatform> activeList = getActiveAllowAddPlatforms();
		CharSequence[] platformList =  new CharSequence[activeList.size()];
		for(int i=0; i < activeList.size() ; i++) {
			platformList[i] = activeList.get(i).getName();
		}
		return platformList;
	}
}
