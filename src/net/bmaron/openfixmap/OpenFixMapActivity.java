package net.bmaron.openfixmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.tileprovider.tilesource.CloudmadeTileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.CloudmadeUtil;
import org.osmdroid.views.MapController;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.MyLocationOverlay;

		
public class OpenFixMapActivity extends Activity {


    protected MapView mapView;
	
    private MapController mapController;
    private MyLocationOverlay mMyLocationOverlay;
    private ScaleBarOverlay mScaleBarOverlay;  
    
    static final int DIALOG_ERROR_ID = 0;
    private ItemizedIconOverlay<OverlayItem> pointOverlay; 
    
	private Handler mHandler;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(); 

        setContentView(R.layout.main);

        mapView = (MapView) findViewById(R.id.mapview);

        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        loadMapSource(2);
        

        /* Set position near Home :) */
        mapController = this.mapView.getController();
        mapController.setZoom(16);
        GeoPoint p = new GeoPoint(50.838599, 4.406551);
        mapController.setCenter(p);

        this.mMyLocationOverlay = new MyLocationOverlay(this, this.mapView);                          
        this.mapView.getOverlays().add(mMyLocationOverlay);
        this.mMyLocationOverlay.enableMyLocation();
        //this.mMyLocationOverlay.enableCompass();

        mMyLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
            	goToCurrenLocation();
            }
        });
        
        this.mScaleBarOverlay = new ScaleBarOverlay(this);                          
        this.mapView.getOverlays().add(mScaleBarOverlay);
        

    	class myItemGestureListener<T extends OverlayItem> implements OnItemGestureListener<T> {
		    
    		private T it;
		    @Override
		    public boolean onItemSingleTapUp(int index, T item) {
		    	it = item;
		        mHandler.post(new Runnable() {
				    public void run() { 
				    	 ProblemDialog dialog = new ProblemDialog(OpenFixMapActivity.this, it.getTitle(), it.mDescription);
				    	 dialog.show();
				    	}
				}); 
		        return false;
		    }
		
		    @Override
		    public boolean onItemLongPress(int index, T item) { return false;}
    	}

        OnItemGestureListener<OverlayItem> pOnItemGestureListener = new myItemGestureListener<OverlayItem>();
       
        pointOverlay = new ItemizedIconOverlay<OverlayItem>(this, new ArrayList<OverlayItem>(), pOnItemGestureListener);
        this.mapView.getOverlays().add(pointOverlay);
        
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        
    	String [] checkers = MultiSelectListPreference.parseStoredValue(sharedPrefs.getString("checkers", "KeepRights"));

        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OpenFixMapActivity.class);

        logger.info("PREF: "+Arrays.toString(checkers));
        //loadDataSource();
    }
    
    
    protected  List<ErrorItem> fetchDatas()
    {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OpenFixMapActivity.class);
        logger.info("Start Fetching");
        mapView.getBoundingBox();
    	KeepRightCSVParser parser = new KeepRightCSVParser(mapView.getBoundingBox());
    	
    	BoundingBoxE6 bb = mapView.getBoundingBox();
    	Double t = (Double) (bb.getLatNorthE6() / 1E6);
    	logger.info("N: "+ String.valueOf(t)  + ", S " + bb.getLatSouthE6());
    	parser.parse();
    	
    	Toast toast = Toast.makeText(this, parser.getItems().size()+" items downloaded", Toast.LENGTH_SHORT);
    	toast.show();
        return parser.getItems();
    }
    
    protected void loadDataSource()
    {
    	pointOverlay.removeAllItems();
        List<ErrorItem> itemList = fetchDatas();
        
        for(int i=0; i < itemList.size(); i++) {
        	ErrorItem item = itemList.get(i);
            OverlayItem oItem = new OverlayItem(item.getTitle(),item.getDescription(),item.getPoint());
            pointOverlay.addItem(oItem);
        }
        mapView.invalidate();

    }
    
    protected void loadMapSource(int source)
    {
    	switch(source) {
    		case 1:
    			CloudmadeTileSource map_source_cloud = (CloudmadeTileSource)TileSourceFactory.CLOUDMADESTANDARDTILES;
    			map_source_cloud.setStyle(999);
    		    CloudmadeUtil.retrieveCloudmadeKey(this);
    	        mapView.setTileSource(map_source_cloud);
    			break;
    		case 2:
    			CloudmadeTileSource map_source_noname = (CloudmadeTileSource)TileSourceFactory.CLOUDMADESTANDARDTILES;
    			map_source_noname.setStyle(3);
    		    CloudmadeUtil.retrieveCloudmadeKey(this);
    	        mapView.setTileSource(map_source_noname);		
    			break;
    		case 3:
    			mapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
    			break;
    	        /*XYTileSource name = new XYTileSource("name", null, 0, 16, 256, ".png", "http://tilesserver.com/");
        		mapView.setTileSource(name); */
    		default:
    			break;
    	}

    }
    protected void goToCurrenLocation()
    {
    	mHandler.post(new Runnable() {
		    public void run() { 
		    	if(mMyLocationOverlay.getMyLocation() != null) {
		            // Make sure setZoom is before animateTo ==> may be a Go to sahara animateTo bug 
            		mapController.setZoom(17);
    		        mapController.animateTo(mMyLocationOverlay.getMyLocation());
            	}
		    }
    	});       
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
        	case R.id.gotolocation:
        		goToCurrenLocation();
        		return true;
        		
        	case R.id.refresh:
        		loadDataSource();
        		return true;
        	case R.id.preferences: 
        		startActivityForResult(new Intent(this, Preferences.class), 1 /* CODE_RETOUR*/);
        		return true;
        	default:
        		return super.onOptionsItemSelected(item);
        }
    }
}
