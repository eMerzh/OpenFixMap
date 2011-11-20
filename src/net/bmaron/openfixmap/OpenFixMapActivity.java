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
import android.os.Bundle;
import android.view.Gravity;
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

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapController;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.SimpleLocationOverlay;
import org.osmdroid.views.util.constants.MapViewConstants;

		
public class OpenFixMapActivity extends Activity {


    protected MapView mapView;
	
    private MapController mapController;
    private SimpleLocationOverlay mMyLocationOverlay;
    private ScaleBarOverlay mScaleBarOverlay;  
    
    static final int DIALOG_ERROR_ID = 0;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mapView = (MapView) findViewById(R.id.mapview);

        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setTileSource(TileSourceFactory.MAPQUESTOSM);

        /* Set position near etterbeek */
        mapController = this.mapView.getController();
        mapController.setZoom(16);
        GeoPoint p = new GeoPoint(50.838599, 4.406551);
        mapController.setCenter(p);


        this.mMyLocationOverlay = new SimpleLocationOverlay(this);                          
        this.mapView.getOverlays().add(mMyLocationOverlay);
       
        this.mScaleBarOverlay = new ScaleBarOverlay(this);                          
        this.mapView.getOverlays().add(mScaleBarOverlay);
        


        
    	class myItemGestureListener<T extends OverlayItem> implements OnItemGestureListener<T> {
       	 		protected Dialog dialog;
		    @Override
		    public boolean onItemSingleTapUp(int index, T item) {
		        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OpenFixMapActivity.class);
		        
		        logger.info("Hello 1 "+item.mDescription);
		        
		        //showDialog(DIALOG_ERROR_ID);
		        dialog = new Dialog(OpenFixMapActivity.this);

	            dialog.setContentView(R.layout.errordetail_dialog);
	            dialog.setTitle("Custom Dialog");

	            TextView text = (TextView) dialog.findViewById(R.id.text);
	            text.setText("Hello, this is a custom dialog!");
	            ImageView image = (ImageView) dialog.findViewById(R.id.image);
	            image.setImageResource(R.drawable.robot);
	            Button button = (Button) dialog.findViewById(R.id.close_button);
	            
	            button.setOnClickListener(new OnClickListener() {

	            	@Override
	            	public void onClick(View v) {
	            		//removeDialog(DIALOG_ERROR_ID);
	            		myItemGestureListener.this.dialog.cancel();
	            	}
	            });
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
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OpenFixMapActivity.class);



        
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
    
    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        switch(id) {
        case DIALOG_ERROR_ID:
            dialog = new Dialog(this);

            dialog.setContentView(R.layout.errordetail_dialog);
            dialog.setTitle("Custom Dialog");

            TextView text = (TextView) dialog.findViewById(R.id.text);
            text.setText("Hello, this is a custom dialog!");
            ImageView image = (ImageView) dialog.findViewById(R.id.image);
            image.setImageResource(R.drawable.robot);
            Button button = (Button) dialog.findViewById(R.id.close_button);
            
            button.setOnClickListener(new OnClickListener() {

            	@Override
            	public void onClick(View v) {
            		removeDialog(DIALOG_ERROR_ID);
            	}
            });
            break;
        default:
            dialog = null;
        }
        return dialog;
    }

}
