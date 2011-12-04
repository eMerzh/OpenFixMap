package net.bmaron.openfixmap;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import net.bmaron.openfixmap.R;
import net.bmaron.openfixmap.ErrorParsers.ErrorPlatform;
import net.bmaron.openfixmap.ErrorParsers.KeepRight;
import net.bmaron.openfixmap.ErrorParsers.OpenStreetBugs;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.ScaleBarOverlay;

		
public class OpenFixMapActivity extends Activity {


    protected FixMapView mapView;
	
    private ScaleBarOverlay mScaleBarOverlay;  
    
    static final int DIALOG_ERROR_ID = 0;
    private ItemizedIconOverlay<OverlayErrorItem> pointOverlay; 
    private SharedPreferences sharedPrefs; 
	private Handler mHandler;
	private SharedPreferences settings;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(); 
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        /*
         * Set Default parser for First launch
         */
        if(sharedPrefs.getString("checkers", null) == null ){
        	SharedPreferences.Editor editor = sharedPrefs.edit();
        	editor.putString("checkers", "KeepRight");
        	editor.commit();
        }
        
        setContentView(R.layout.main);

        mapView = (FixMapView) findViewById(R.id.mapview);
        mapView.setup();


        /* Set position of last open or near Home :) */

        settings = getSharedPreferences("last_position", 0);
    	mapView.loadMapSource(settings.getInt("map_layer",1));

        int lat = settings.getInt("last_position_lat",50838599);
        int lon = settings.getInt("last_position_lon",4406551);
        int zoom = settings.getInt("last_position_zoom",16);
        
        mapView.getController().setZoom(zoom);
        mapView.getController().setCenter(new GeoPoint(lat, lon));

        mapView.getLocationOverlay().runOnFirstFix(new Runnable() {
            public void run() {
            	
            	mHandler.post(new Runnable() {
				    public void run() { 
		                ImageView image = (ImageView) findViewById(R.id.location_ico);
		                image.setImageResource(R.drawable.ic_menu_mylocation_on);
		            }
				}); 

            	if(sharedPrefs.getBoolean("go_to_first", false)) {
            		mapView.goToCurrentLocation();            		
            	}
            }
        });

        ImageButton location_but = (ImageButton) findViewById(R.id.location_ico);
        location_but.setOnClickListener(new View.OnClickListener(){        	
			@Override
			public void onClick(View v) {
				mapView.goToCurrentLocation();
			}
        	
        });
        Button refresh_but = (Button) findViewById(R.id.refresh);
        refresh_but.setOnClickListener(new View.OnClickListener(){        	
			@Override
			public void onClick(View v) {
		    	loadDataSource(); 
        	}
        });
        
        
        this.mScaleBarOverlay = new ScaleBarOverlay(this);                          
        this.mapView.getOverlays().add(mScaleBarOverlay);
        

    	class myItemGestureListener<T extends OverlayErrorItem> implements OnItemGestureListener<T> {
		    
    		private T it;
		    @Override
		    public boolean onItemSingleTapUp(int index, T item) {
		    	it = item;
		        mHandler.post(new Runnable() {
				    public void run() { 
				    	 ProblemDialog dialog = new ProblemDialog(OpenFixMapActivity.this,it.getError());
				    	 dialog.show();
				    	}
				}); 
		        return false;
		    }
		
		    @Override
		    public boolean onItemLongPress(int index, T item) { return false;}
    	}

        OnItemGestureListener<OverlayErrorItem> pOnItemGestureListener = new myItemGestureListener<OverlayErrorItem>();
       
        pointOverlay = new ItemizedIconOverlay<OverlayErrorItem>(this, new ArrayList<OverlayErrorItem>(), pOnItemGestureListener);
        this.mapView.getOverlays().add(pointOverlay);

    	if(sharedPrefs.getBoolean("fetch_on_launch", false)) {
    		Runnable r=new Runnable() {
    		    public void run() {
    		    	loadDataSource();                       
    		    }
    		};
    		mHandler.postDelayed(r, 2000);    //Wait to Be Painted		   
    	}
    	
    	
    	

 
        mapView.setGestureDetector(new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
        	private GeoPoint p;
            @Override
            public void onLongPress(MotionEvent e) {
        		p = (GeoPoint) mapView.getProjection().fromPixels(e.getX(), e.getY());
		        mHandler.post(new Runnable() {
				    public void run() { 
				    	ReportDialog dialog = new ReportDialog(OpenFixMapActivity.this, p);
				    	 dialog.show();
				    	}
				}); 
            }
        }));
    	
    	
    }   
    
    
    @Override
    protected void onStop(){
       super.onStop();

      // We need an Editor object to make preference changes.
      // All objects are from android.context.Context
      SharedPreferences.Editor editor = settings.edit();
      GeoPoint bb = mapView.getBoundingBox().getCenter();
      editor.putInt("last_position_lat", bb.getLatitudeE6());
      editor.putInt("last_position_lon",  bb.getLongitudeE6());
      editor.putInt("last_position_zoom", mapView.getZoomLevel());

      // Commit the edits!
      editor.commit();
    }

    
    protected  List<ErrorItem> fetchDatas()
    {
        String [] checkers = MultiSelectListPreference.parseStoredValue(sharedPrefs.getString("checkers", "KeepRight"));
        boolean show_closed = sharedPrefs.getBoolean("show_closed", false);
        List<ErrorItem> items = new ArrayList<ErrorItem>();
        BoundingBoxE6 bb = mapView.getBoundingBox();

    	
    	int display_level = Integer.parseInt(sharedPrefs.getString("display_level", "1"));
    	ErrorPlatform bugPlateform;
        for(int i = 0; i < checkers.length; i++) {
        	
        	if(checkers[i].equals("OpenStreetBugs")) {
        		bugPlateform = new OpenStreetBugs(bb, display_level, show_closed);
            	
        	} else// if(checkers[i].equals("KeepRight")) 
        	{
        		bugPlateform = new KeepRight(bb, display_level, show_closed);
        	}
        	
        	bugPlateform.load();
        	items.addAll(bugPlateform.getItems());
        }
        
    	Toast toast = Toast.makeText(this,
    			getResources().getQuantityString(R.plurals.numberOfDownloadedItems,  items.size(),items.size()),
    			Toast.LENGTH_SHORT);
    	toast.show();
        return items;
    }
    
    protected void loadDataSource()
    {
    	Resources res = getResources();
    	pointOverlay.removeAllItems();
        List<ErrorItem> itemList = fetchDatas();
        
        for(int i=0; i < itemList.size(); i++) {
        	ErrorItem item = itemList.get(i);
        	OverlayErrorItem oItem = new OverlayErrorItem(item);
        	oItem.setMarker( res.getDrawable(R.drawable.caution));
            pointOverlay.addItem(oItem);
        }
        mapView.invalidate();

    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.map_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
    	switch (item.getItemId()) {
        	case R.id.switch_layer:                
        		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        		builder.setTitle(getResources().getString(R.string.dialog_switchlayer_title));
        		org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("LAYER");
                logger.info("switch default "+ settings.getInt("map_layer",1));
                
        		builder.setSingleChoiceItems(mapView.getLayers(), settings.getInt("map_layer",1), new DialogInterface.OnClickListener() {
        		    public void onClick(DialogInterface dialog, int item) {
        		    	
        				SharedPreferences.Editor editor = settings.edit();
        				editor.putInt("map_layer",item);
        				editor.commit();
        				
        		    	mapView.loadMapSource(item);
        		    }
        		});
        		AlertDialog alert = builder.create();
        		alert.show();
        		return true;
        	case R.id.preferences: 
        		startActivityForResult(new Intent(this, Preferences.class), 1 /* CODE_RETOUR*/);
        		return true;
        	default:
        		return super.onOptionsItemSelected(item);
        }
    }
}
