package net.bmaron.openfixmap;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
		
public class OpenFixMapActivity extends Activity {


    protected FixMapView mapView;
    
    static final int DIALOG_ERROR_ID = 0;
    private ItemizedIconOverlay<OverlayErrorItem> pointOverlay; 
    private SharedPreferences sharedPrefs; 
	private Handler mHandler;
	
	private PlatformManager plManager;
	private SharedPreferences settings;
    private ProgressDialog dialog;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler(); 
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplication());
    	ApplicationInfo ai = null;
		try {
			ai = getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		plManager = PlatformManager.getInstance();
		if(plManager == null) {
			plManager = new PlatformManager(getApplication(), sharedPrefs,ai.metaData);
			plManager.setOneCheckerOnFirstLoad();
		}
		
        setContentView(R.layout.main);

        mapView = (FixMapView) findViewById(R.id.mapview);
        mapView.setup(this);


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
        
       
        createPointOverlay(new ArrayList<OverlayErrorItem>());
        ItemizeOverlay();
        
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
				    	if(plManager.getActiveAllowAddPlatforms().size() !=0 ) {
				            Intent i = new Intent(OpenFixMapActivity.this, ReportActivity.class);
				            i.putExtra("p_lat", p.getLatitudeE6());
				            i.putExtra("p_lon", p.getLongitudeE6());
				            startActivity(i);
				    	} else {
                        	Toast toast = Toast.makeText(OpenFixMapActivity.this,
                        			getResources().getString(R.string.report_no_parser),
                        			Toast.LENGTH_SHORT);
                        	toast.show();
				    	}
				    	
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

    protected ItemizedIconOverlay<OverlayErrorItem> createPointOverlay(ArrayList<OverlayErrorItem> items) {

    	class myItemGestureListener<T extends OverlayErrorItem> implements OnItemGestureListener<T> {
		    @Override
		    public boolean onItemSingleTapUp(int index, T item) {
		    	//run.setInfo(OpenFixMapActivity.this, item.getError());
		        //mHandler.post(run);
	            Intent i = new Intent(OpenFixMapActivity.this, ErrorDetailsActivity.class);
	            i.putExtra("error_platform", item.getError().getPlatform().getName());
	            i.putExtra("error_id", (int)item.getError().getId());
	            startActivity(i);
		        return false;
		    }
		
		    @Override
		    public boolean onItemLongPress(int index, T item) { return false;}
    	}

        OnItemGestureListener<OverlayErrorItem> pOnItemGestureListener = new myItemGestureListener<OverlayErrorItem>();
       
        pointOverlay = new ItemizedIconOverlay<OverlayErrorItem>(getApplication(), items , pOnItemGestureListener);
        return pointOverlay;
    }
    
    protected  List<ErrorItem> fetchDatas()
    {
        boolean show_closed = sharedPrefs.getBoolean("show_closed", false);
    	int display_level = Integer.parseInt(sharedPrefs.getString("display_level", "1"));

    	plManager.fetchAllData(mapView.getBoundingBox(), display_level, show_closed);
    	return plManager.getAllItems();
    }
    
    protected void ItemizeOverlay()
    {
    	Resources res = getResources();
    	List<ErrorItem> itemList = plManager.getAllItems();
    	ArrayList<OverlayErrorItem> overlayList= new ArrayList<OverlayErrorItem>();
    	
        for(int i=0; i < itemList.size(); i++) {
        	ErrorItem item = itemList.get(i);
        	OverlayErrorItem oItem = new OverlayErrorItem(item);
        	oItem.setMarker( res.getDrawable(R.drawable.caution));
        	overlayList.add(oItem);
            //num_item = itemList.size();
        }
        mapView.getOverlays().remove(pointOverlay);
        createPointOverlay(overlayList);//Change pointOverlay
        mapView.getOverlays().add(pointOverlay);
    }
    
    protected void loadDataSource()
    {
    	if(isFinishing()) return;
        dialog = ProgressDialog.show(OpenFixMapActivity.this, "", 
        		getResources().getString(R.string.dialog_loading_message), true);

        new Thread() {
            public void run() {
            	try{
            		fetchDatas();
            		ItemizeOverlay();
            	} catch (Exception e) { 
            		e.printStackTrace();
            	}
                    
            	//Dismiss the Dialog
            	mHandler.post(new Runnable(){
            		public void run(){
            			if(isFinishing()) return;
            			mapView.invalidate();
                    	dialog.dismiss();
                        Toast toast = Toast.makeText(OpenFixMapActivity.this,
                        		getResources().getQuantityString(R.plurals.numberOfDownloadedItems,
                        			plManager.getAllItems().size(),
                        			plManager.getAllItems().size()),
                        		Toast.LENGTH_SHORT);
                        toast.show();
            		}
            	});
            }
        }.start();

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
        		    	dialog.dismiss();
        		    }
        		});
        		AlertDialog alert = builder.create();
        		alert.show();
        		return true;
        	case R.id.preferences: 
        		startActivityForResult(new Intent(this, Preferences.class), 1 /* CODE_RETOUR*/);
        		return true;
        	case R.id.menu_quit: 
        		finish();
        		return true;
        	default:
        		return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    protected void onPause() {
    	mapView.getLocationOverlay().disableMyLocation();
    	super.onPause();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	mapView.getLocationOverlay().enableMyLocation();

    }
    
    @Override
    protected void onDestroy() {
    	mapView.getLocationOverlay().disableMyLocation();
    	mapView.getTileProvider().clearTileCache();
    	System.gc();
    	super.onDestroy();
    }
}
