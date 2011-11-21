package net.bmaron.openfixmap;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController.AnimationType;
import org.osmdroid.views.MapView;

import org.osmdroid.tileprovider.tilesource.CloudmadeTileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.util.CloudmadeUtil;
import org.osmdroid.views.MapController;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.util.constants.MapViewConstants;

		
public class OpenFixMapActivity extends Activity {


    protected MapView mapView;
	
    private MapController mapController;
    private MyLocationOverlay mMyLocationOverlay;
    private ScaleBarOverlay mScaleBarOverlay;  
    
    static final int DIALOG_ERROR_ID = 0;
    

    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OpenFixMapActivity.class);

        mapView = (MapView) findViewById(R.id.mapview);

        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        loadMapSource(2);
        

        /* Set position near etterbeek */
        mapController = this.mapView.getController();
        mapController.setZoom(16);
        GeoPoint p = new GeoPoint(50.838599, 4.406551);
        mapController.setCenter(p);
        

        this.mMyLocationOverlay = new MyLocationOverlay(this, this.mapView);                          
        this.mapView.getOverlays().add(mMyLocationOverlay);
        this.mMyLocationOverlay.enableMyLocation();
        this.mMyLocationOverlay.enableCompass();

        mMyLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
            	mapController.animateTo(mMyLocationOverlay.getMyLocation());
            	mapController.setZoom(17);
            }
        });
        
        this.mScaleBarOverlay = new ScaleBarOverlay(this);                          
        this.mapView.getOverlays().add(mScaleBarOverlay);
        

    	class myItemGestureListener<T extends OverlayItem> implements OnItemGestureListener<T> {
       	 		protected ProblemDialog dialog;
		    @Override
		    public boolean onItemSingleTapUp(int index, T item) {
		        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OpenFixMapActivity.class);
		        
		        logger.info("Hello 1 "+item.mDescription);
		        dialog = new ProblemDialog(OpenFixMapActivity.this, item.getTitle(), item.mDescription);

	            dialog.show();
		        return false;
		    }
		
	    @Override
		    public boolean onItemLongPress(int index, T item) {
	        	org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OpenFixMapActivity.class);
	    		logger.info("Hello 2");
		        return false;
		    }
    	}

        OnItemGestureListener<OverlayItem> pOnItemGestureListener = new myItemGestureListener<OverlayItem>();



        //http://keepright.ipax.at/points.php?lat=50.831871476664&lon=4.4058486757746&zoom=15&show_ign=1&show_tmpign=1&lang=en&ch=0,30,40,50,60,70,90,100,110,120,130,150,160,170,180,191,192,193,194,195,196,197,198,201,202,203,204,205,206,207,208,210,220,231,232,270,281,282,283,284,291,292,293,311,312,313,350,380,411,412,413

        String next[] = {};
        List<String[]> list = new ArrayList<String[]>();

        try {
            CSVReader reader = new CSVReader(new InputStreamReader(getAssets().open("points.csv")),'\t', '\0', 0);
            for(;;) {
                next = reader.readNext();
                if(next != null) {
                    list.add(next);
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        List<OverlayItem> pList = new ArrayList<OverlayItem>();
        for(int i=1; i<= 10; i++) {
        	logger.info("array is"+Arrays.toString(list.get(i)));
        	if(list.get(i).length < 12) continue;
        	logger.info("Hello "+list.get(i)[0]+" World "+list.get(i)[1]);
        	
        	GeoPoint point = new GeoPoint(Double.parseDouble(list.get(i)[0]), Double.parseDouble(list.get(i)[1]));
            OverlayItem myItem = new OverlayItem(list.get(i)[2],list.get(i)[10],point);
            pList.add(myItem);

        }

        ItemizedIconOverlay<OverlayItem> test = new ItemizedIconOverlay<OverlayItem>(this, pList, pOnItemGestureListener);
        this.mapView.getOverlays().add(test);
        
        logger.info("Hello World"+ this.mapView.getOverlays().size());
        
    }
   /* 
    @Override
    protected void onPause() {
        final SharedPreferences.Editor edit = mPrefs.edit();
        edit.putBoolean(PREFS_SHOW_LOCATION, mLocationOverlay.isMyLocationEnabled());
        edit.putBoolean(PREFS_SHOW_COMPASS, mLocationOverlay.isCompassEnabled());
        edit.commit();
 
        this.mLocationOverlay.disableMyLocation();
 
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (mPrefs.getBoolean(PREFS_SHOW_LOCATION, false)) {
            this.mLocationOverlay.enableMyLocation();
        }
        if (mPrefs.getBoolean(PREFS_SHOW_COMPASS, false)) {
            this.mLocationOverlay.enableCompass();
        }
    }*/
    
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
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.map_menu, menu);
        return true;
    }
}
